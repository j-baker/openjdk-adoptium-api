/*
 * (c) Copyright 2022 James Baker. All rights reserved.&#10;&#10;Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);&#10;you may not use this file except in compliance with the License.&#10;You may obtain a copy of the License at&#10;&#10;    http://www.apache.org/licenses/LICENSE-2.0&#10;&#10;Unless required by applicable law or agreed to in writing, software&#10;distributed under the License is distributed on an &quot;AS IS&quot; BASIS,&#10;WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#10;See the License for the specific language governing permissions and&#10;limitations under the License.
 */

package io.jbaker.oai.scrape;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScraperTest {
    /**
     * This test will break whenever Java 17 goes out of LTS. Of course, the website will probably be dead by that
     * point.
     */
    @Test
    public void scraperTest() {
        List<JavaRelease> releases = new Scraper(Duration.ofMillis(1)).load();
        assertThat(releases)
                .as("java 17 exists in the list")
                .anyMatch(release -> release.version().startsWith("17.")
                        && release.versions().containsKey(Architecture.LINUX_X64));
    }
}
