#!/usr/bin/env python3
"""Convert CFR's illegal `class X extends Record` output into real records.

CFR 0.152 cannot resugar records: it emits a class extending java.lang.Record
with explicit fields, a canonical constructor, accessors, and
toString/hashCode/equals bodies that call ObjectMethods.bootstrap (illegal
source). The bootstrap string literal gives the authoritative component order;
field declarations give the types.

Files where a record deviates from the mechanical pattern are left untouched
and reported for manual fixing.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src/game/java"

BOOTSTRAP_RE = re.compile(
    r'ObjectMethods\.bootstrap\("(?:toString|hashCode|equals)", new MethodHandle\[\]\{(\w+)\.class, "([^"]*)"'
)
# a method whose body is a single ObjectMethods.bootstrap return
OM_METHOD_RE = re.compile(
    r'\n[ \t]*@Override\n'
    r'[ \t]*public final (?:String toString\(\)|int hashCode\(\)|boolean equals\(Object \w+\)) \{\n'
    r'[ \t]*return [^\n]*ObjectMethods\.bootstrap[^\n]*;\n'
    r'[ \t]*\}\n'
)


def find_class_span(text, name):
    """Return (header_match, body_start, body_end) for `class <name> ... extends Record`."""
    hdr = re.search(
        r'((?:^[ \t]*@[\w.]+(?:\([^)]*\))?\n)*)'          # annotations
        r'^([ \t]*)((?:(?:public|protected|private|static|final|abstract)\s+)*)'
        r'class\s+' + re.escape(name) + r'(<(?:[^<>{]|<(?:[^<>{]|<[^<>{]*>)*>)*>)?'  # generics, 3 levels
        r'([^{]*?)\bextends\s+Record\b([^{]*)\{',
        text, re.M | re.S)
    if not hdr:
        return None
    depth = 1
    i = hdr.end()
    while depth and i < len(text):
        c = text[i]
        if c == '{':
            depth += 1
        elif c == '}':
            depth -= 1
        i += 1
    return hdr, hdr.end(), i - 1


def component_decl(body, comp):
    """Find the field declaration line for a component; return (line_re_match, type, annotations)."""
    m = re.search(
        r'^[ \t]*((?:@[\w.]+(?:\([^)]*\))?\s+)*)'
        r'((?:private\s+|protected\s+|public\s+)?final\s+)'
        r'((?:@[\w.]+(?:\([^)]*\))?\s+)*)'
        r'([@\w.$<>\[\],\s?&]+?)\s+' + re.escape(comp) + r'\s*;\n',
        body, re.M)
    return m


def process_class(text, name, comps):
    span = find_class_span(text, name)
    if not span:
        # CFR sometimes resugars the record but leaves the ObjectMethods bodies
        if re.search(r'\brecord\s+' + re.escape(name) + r'\b', text):
            om_named = re.compile(OM_METHOD_RE.pattern.replace(
                r'ObjectMethods\.bootstrap',
                r'ObjectMethods\.bootstrap\("\w+", new MethodHandle\[\]\{' + re.escape(name) + r'\.class'))
            new, n = om_named.subn('\n', text, count=3)
            if 1 <= n <= 3:
                return new, None
            return None, f"{name}: already record, but matched {n} ObjectMethods methods"
        return None, f"{name}: class header not found"
    hdr, bstart, bend = span
    body = text[bstart:bend]

    # 1. components: gather types from field decls
    parts = []
    for comp in comps:
        m = component_decl(body, comp)
        if not m:
            return None, f"{name}: field for component '{comp}' not found"
        ann = (m.group(1).strip() + ' ' + m.group(3).strip()).strip()
        typ = m.group(4).strip()
        if 'static' in typ.split():
            return None, f"{name}: static field matched for '{comp}'"
        parts.append((f"{ann} " if ann else "") + f"{typ} {comp}")
        body = body[:m.start()] + body[m.end():]

    # 2. drop the three ObjectMethods methods
    body, n = OM_METHOD_RE.subn('\n', body)
    if not 1 <= n <= 3:
        return None, f"{name}: expected 1-3 ObjectMethods methods, matched {n}"

    # 3. canonical constructor: delete if trivial (assignments in any order, one per component)
    ctor = re.search(
        r'^([ \t]*)(?:(?:public|protected|private)\s+)?' + re.escape(name) +
        r'\(([^)]*)\)\s*\{\n((?:[^{}]*\n)*?)\1\}\n',
        body, re.M)
    if ctor:
        params = [p.strip() for p in ctor.group(2).split(',')] if ctor.group(2).strip() else []
        if len(params) == len(comps):
            stmts = [s.strip() for s in ctor.group(3).split('\n') if s.strip()]
            assign_re = re.compile(r'^this\.(\w+)\s*=\s*[\w$]+\s*;$')
            targets = []
            for s in stmts:
                am = assign_re.match(s)
                if not am:
                    targets = None
                    break
                targets.append(am.group(1))
            if targets is not None and sorted(targets) == sorted(comps):
                body = body[:ctor.start()] + body[ctor.end():]
            else:
                return None, f"{name}: non-trivial canonical constructor"
        # different arity -> secondary constructor, leave it

    # 4. rebuild header
    mods = hdr.group(3).replace('final ', '').replace('abstract ', '')
    generics = hdr.group(4) or ''
    tail = (hdr.group(5) + ' ' + hdr.group(6)).replace('\n', ' ')
    tail = re.sub(r'\s+', ' ', tail).strip()  # e.g. "implements Foo"
    if tail and not tail.startswith('implements'):
        return None, f"{name}: unexpected header tail '{tail}'"
    new_hdr = (hdr.group(1) + hdr.group(2) + mods + 'record ' + name + generics +
               '(' + ', '.join(parts) + ')' + ((' ' + tail) if tail else '') + ' {')
    return text[:hdr.start()] + new_hdr + body + text[bend:], None


def process_file(path):
    text = path.read_text()
    classes = dict()
    for m in BOOTSTRAP_RE.finditer(text):
        name, comps = m.group(1), m.group(2)
        classes[name] = [c for c in comps.split(';') if c]
    problems = []
    changed = False
    for name, comps in classes.items():
        new, err = process_class(text, name, comps)
        if err:
            problems.append(err)
        else:
            text = new
            changed = True
    if changed:
        # drop now-unused imports if no other references remain
        for imp in ('import java.lang.runtime.ObjectMethods;\n',
                    'import java.lang.invoke.MethodHandle;\n'):
            sym = imp.split('.')[-1].rstrip(';\n')
            if text.count(sym) == 1:
                text = text.replace(imp, '')
        path.write_text(text)
    return changed, problems


def main():
    files = sorted(set(p for p in ROOT.rglob('*.java')
                       if 'ObjectMethods.bootstrap' in p.read_text()))
    fixed, manual = 0, []
    for p in files:
        changed, problems = process_file(p)
        if changed:
            fixed += 1
        for prob in problems:
            manual.append(f"{p.relative_to(ROOT)}: {prob}")
    print(f"files touched: {fixed}/{len(files)}")
    if manual:
        print(f"\nneeds manual fix ({len(manual)}):")
        print('\n'.join(manual))


if __name__ == '__main__':
    sys.exit(main())
