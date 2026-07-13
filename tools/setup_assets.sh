#!/usr/bin/env bash
# Downloads the vanilla 1.21.11 client jar, extracts its embedded resources
# onto the classpath, and fetches the asset-index objects (sounds, langs).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

JAR_URL='https://piston-data.mojang.com/v1/objects/ba2df812c2d12e0219c489c4cd9a5e1f0760f5bd/client.jar'
JAR_SHA1='ba2df812c2d12e0219c489c4cd9a5e1f0760f5bd'
INDEX_URL='https://piston-meta.mojang.com/v1/packages/c71d40f506849d05487327dcede7c984f7becbf1/29.json'

mkdir -p "$ROOT/game"
JAR="$ROOT/game/client-1.21.11.jar"

if ! echo "$JAR_SHA1  $JAR" | sha1sum -c --status 2>/dev/null; then
	echo "Downloading client jar..."
	curl -fSL -o "$JAR" "$JAR_URL"
	echo "$JAR_SHA1  $JAR" | sha1sum -c
fi

echo "Extracting embedded resources to src/game/resources..."
mkdir -p "$ROOT/src/game/resources"
unzip -oq "$JAR" -d "$ROOT/src/game/resources" -x '*.class' 'META-INF/*'

echo "Fetching asset index 29..."
mkdir -p "$ROOT/game/assets/indexes" "$ROOT/game/assets/objects"
curl -fsSL -o "$ROOT/game/assets/indexes/29.json" "$INDEX_URL"

echo "Downloading asset objects (skips existing)..."
python3 -c '
import json
with open("'"$ROOT"'/game/assets/indexes/29.json") as f:
    for o in json.load(f)["objects"].values():
        print(o["hash"])' | sort -u | while read -r h; do
	f="$ROOT/game/assets/objects/${h:0:2}/$h"
	[ -f "$f" ] || printf 'url = https://resources.download.minecraft.net/%s/%s\noutput = %s\n' "${h:0:2}" "$h" "$f"
done | curl -fsS --create-dirs --parallel --parallel-max 16 --config -

echo "Done."
