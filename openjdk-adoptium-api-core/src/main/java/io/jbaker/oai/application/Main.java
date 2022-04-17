/*
 * (c) Copyright 2022 James Baker. All rights reserved.&#10;&#10;Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);&#10;you may not use this file except in compliance with the License.&#10;You may obtain a copy of the License at&#10;&#10;    http://www.apache.org/licenses/LICENSE-2.0&#10;&#10;Unless required by applicable law or agreed to in writing, software&#10;distributed under the License is distributed on an &quot;AS IS&quot; BASIS,&#10;WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#10;See the License for the specific language governing permissions and&#10;limitations under the License.
 */

package io.jbaker.oai.application;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.jbaker.oai.endpoint.AdoptiumApiEndpoint;
import io.jbaker.oai.scrape.Scraper;
import java.time.Duration;

public final class Main extends Application<Configuration> {
    private Main() {}

    public static void main(String[] args) throws Exception {
        new Main().run("server", "openjdk-adoptium-api-core/var/conf/install.yml");
    }

    @Override
    public void run(Configuration _configuration, Environment environment) {
        environment.jersey().register(new AdoptiumApiEndpoint(new Scraper(Duration.ofHours(1))));
    }
}
