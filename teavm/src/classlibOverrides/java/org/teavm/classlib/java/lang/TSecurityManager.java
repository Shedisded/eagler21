/*
 *  Copyright 2022 ulugbek.abdullaev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;

// Note: this class is left empty on purpose -
//  functionality is intended to be added as need arises
public class TSecurityManager {

    public TSecurityManager() {
    }

    // patched: slf4j's ClassContextSecurityManager probes the call stack,
    // which does not exist under TeaVM
    protected Class<?>[] getClassContext() {
        return new Class<?>[0];
    }

    public ThreadGroup getThreadGroup() {
        return null;
    }
}
