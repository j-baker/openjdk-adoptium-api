/*
 * (c) Copyright 2022 James Baker. All rights reserved.&#10;&#10;Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);&#10;you may not use this file except in compliance with the License.&#10;You may obtain a copy of the License at&#10;&#10;    http://www.apache.org/licenses/LICENSE-2.0&#10;&#10;Unless required by applicable law or agreed to in writing, software&#10;distributed under the License is distributed on an &quot;AS IS&quot; BASIS,&#10;WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#10;See the License for the specific language governing permissions and&#10;limitations under the License.
 */

package io.jbaker.oai.endpoint;

import com.palantir.logsafe.Preconditions;
import com.palantir.logsafe.SafeArg;
import io.jbaker.oai.scrape.Architecture;
import io.jbaker.oai.scrape.JavaRelease;
import io.jbaker.oai.scrape.Scraper;
import java.net.URI;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public final class AdoptiumApiEndpoint {
    private final Scraper scraper;

    public AdoptiumApiEndpoint(Scraper scraper) {
        this.scraper = scraper;
    }

    @GET
    @Path("{experimentName}/v3/binary/latest/{language}/{releaseState}/{os}/{arch}/jdk/hotspot/normal/{org}")
    public Response download(
            @PathParam("experimentName") String experimentName,
            @PathParam("language") int languageVersion,
            @PathParam("releaseState") String _releaseState,
            @PathParam("os") String os,
            @PathParam("arch") String arch,
            @PathParam("org") String _unusedOrg) {
        List<JavaRelease> releases = scraper.load();
        Architecture actualArch = Architecture.fromOsAndMicroArch(os, arch);
        for (JavaRelease release : releases) {
            if (release.getExperimentName().isPresent()
                    && release.getExperimentName().get().equals(experimentName)
                    && release.getLangLevel() == languageVersion) {
                URI downloadUrl = release.versions().get(actualArch);
                Preconditions.checkArgument(
                        downloadUrl != null, "architecture not found", SafeArg.of("arch", actualArch));
                return Response.seeOther(downloadUrl).build();
            }
        }
        throw new NotFoundException();
    }
}
