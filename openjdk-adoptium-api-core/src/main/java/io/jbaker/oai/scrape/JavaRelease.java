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

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record JavaRelease(String version, Map<Architecture, URI> versions) {

    // 19-loom+5-429
    // 19-ea+18
    private static final Pattern EXPERIMENT_VERSION_PATTERN = Pattern.compile("[0-9]+-(?<experiment>[a-z]+).*");

    public Optional<String> getExperimentName() {
        Matcher matcher = EXPERIMENT_VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(matcher.group("experiment"));
    }

    public int getLangLevel() {
        return Integer.parseInt(version.substring(0, version.indexOf('-')));
    }
}
