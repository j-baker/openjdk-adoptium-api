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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.google.common.base.Suppliers;
import com.google.common.collect.MoreCollectors;
import com.palantir.logsafe.SafeArg;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Scraper {
    private static final String HOMEPAGE = "https://jdk.java.net";
    private static final Pattern GA_RELEASE_PATTERN =
            Pattern.compile("^OpenJDK JDK ([0-9\\.]+) General-Availability Release$");
    private static final Pattern EA_RELEASE_PATTERN = Pattern.compile("^Build ([a-z0-9+\\-]+) \\([0-9/]+\\)$");

    private final Supplier<List<JavaRelease>> cachedReleases;

    public Scraper(Duration cacheFor) {
        this.cachedReleases =
                Suppliers.memoizeWithExpiration(this::loadUncached, cacheFor.toMillis(), TimeUnit.MILLISECONDS);
    }

    public List<JavaRelease> load() {
        return cachedReleases.get();
    }

    private List<JavaRelease> loadUncached() {
        try (WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(HOMEPAGE);
            return page.getAnchors().stream()
                    .filter(anchor -> isJavaVersion(anchor.getHrefAttribute()))
                    .map(Scraper::loadDownloads)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JavaRelease loadDownloads(HtmlAnchor link) {
        try {
            HtmlPage page = (HtmlPage) link.openLinkInNewWindow();
            String version = getVersion(page);
            List<HtmlTableRow> rows = page.getByXPath("//tr[contains(@class, 'build')]");
            Map<Architecture, URI> urls = rows.stream()
                    .map(row -> {
                        String architecture = row.getCell(0).getFirstChild().getNodeValue();
                        String downloadLink = ((HtmlAnchor) row.getCell(1).getFirstChild()).getHrefAttribute();
                        return Map.entry(Architecture.fromFancyName(architecture), URI.create(downloadLink));
                    })
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
            return new JavaRelease(version, urls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getVersion(HtmlPage page) {
        HtmlDivision main = (HtmlDivision) page.getElementById("main");
        String title = main.getChildNodes().stream()
                .filter(node -> node instanceof HtmlHeading1)
                .map(DomNode::asNormalizedText)
                .collect(MoreCollectors.onlyElement());
        Matcher gaMatcher = GA_RELEASE_PATTERN.matcher(title);
        if (gaMatcher.matches()) {
            return gaMatcher.group(1);
        }

        String buildTitle = main.getChildNodes().stream()
                .filter(node -> node instanceof HtmlHeading2)
                .map(DomNode::asNormalizedText)
                .filter(t -> t.startsWith("Build"))
                .collect(MoreCollectors.onlyElement());
        Matcher eaMatcher = EA_RELEASE_PATTERN.matcher(buildTitle);
        if (eaMatcher.matches()) {
            return eaMatcher.group(1);
        }
        throw new SafeIllegalArgumentException("unexpected page format", SafeArg.of("url", page.getUrl()));
    }

    /**
     * At time of writing, these heuristics are simple and complete.
     */
    private static boolean isJavaVersion(String href) {
        return !(href.contains("https") || href.contains("jmc") || href.contains("."));
    }
}
