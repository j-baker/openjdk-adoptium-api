/*
 * (c) Copyright 2022 James Baker. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
