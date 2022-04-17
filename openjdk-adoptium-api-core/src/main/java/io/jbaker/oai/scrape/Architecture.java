/*
 * (c) Copyright 2022 James Baker. All rights reserved.&#10;&#10;Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);&#10;you may not use this file except in compliance with the License.&#10;You may obtain a copy of the License at&#10;&#10;    http://www.apache.org/licenses/LICENSE-2.0&#10;&#10;Unless required by applicable law or agreed to in writing, software&#10;distributed under the License is distributed on an &quot;AS IS&quot; BASIS,&#10;WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#10;See the License for the specific language governing permissions and&#10;limitations under the License.
 */

package io.jbaker.oai.scrape;

import com.palantir.logsafe.SafeArg;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;

public enum Architecture {
    LINUX_AARCH64("Linux/AArch64"),
    LINUX_X64("Linux/x64"),
    ALPINE_LINUX_X64("Alpine Linux/x64"),
    MACOS_AARCH64("macOS/AArch64"),
    MACOS_X64("macOS/x64"),
    WINDOWS_X64("Windows/x64");

    private final String fancyName;

    Architecture(String fancyName) {
        this.fancyName = fancyName;
    }

    public static Architecture fromFancyName(String maybeFancyName) {
        for (Architecture arch : values()) {
            if (normalize(arch.fancyName).equals(normalize(maybeFancyName))) {
                return arch;
            }
        }
        throw new SafeIllegalArgumentException(
                "fancy name did not correspond to an architecture", SafeArg.of("architecture", maybeFancyName));
    }

    private static String normalize(String architecture) {
        // the website has annoying unicode hair spaces
        return architecture.replaceAll("\\W", "");
    }
}
