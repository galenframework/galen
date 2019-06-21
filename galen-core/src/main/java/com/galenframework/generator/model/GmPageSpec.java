/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package com.galenframework.generator.model;

import com.galenframework.generator.PageItemNode;
import com.galenframework.generator.PageSpecGenerationResult;
import com.galenframework.generator.SpecStatement;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class GmPageSpec {
    private List<GmPageSection> sections = new LinkedList<>();

    public static GmPageSpec create(PageSpecGenerationResult result) {
        GmPageSpec pageSpec = new GmPageSpec();

        GmPageSection skeletonSection = pageSpec.createNewSection("Skeleton");

        Map<PageItemNode, GmPageSection> pinPageSections = new HashMap<>();


        result.getObjects().forEach(rootObject -> rootObject.getChildren().forEach(bigPin -> {
            GmPageSection pageSection = pageSpec.createNewSection(bigPin.getPageItem().getName() + " elements");
            bigPin.visitTree(p -> {
                if (p == bigPin) {
                    pinPageSections.put(p, skeletonSection);
                } else {
                    pinPageSections.put(p, pageSection);
                }
            });
        }));


        Map<String, List<SpecStatement>> generatedRules = result.getSuggestionResults().getGeneratedRules();
        Map<String, List<SpecStatement>> generatedObjectSpecs = result.getSuggestionResults().getGeneratedObjectSpecs();

        result.getObjects().forEach(p -> p.visitTree(pin -> {
            GmPageSection pageSection = pinPageSections.get(pin);

            if (generatedRules != null) {
                List<SpecStatement> rules = generatedRules.get(pin.getPageItem().getName());
                if (rules != null) {
                    rules.forEach((rule) -> pageSection.getRules().add(new GmSpecRule(rule.getStatement())));
                }
            }

            if (generatedObjectSpecs != null && !generatedObjectSpecs.isEmpty()) {
                List<SpecStatement> specs = generatedObjectSpecs.get(pin.getPageItem().getName());
                if (specs != null && !specs.isEmpty()) {
                    GmObjectSpecs objectSpecs = new GmObjectSpecs(pin.getPageItem().getName());
                    pageSection.getObjectSpecs().add(objectSpecs);

                    specs.forEach(spec -> objectSpecs.getSpecs().add(new GmSpec(spec.getStatement())));
                }
            }
        }));

        pageSpec.setSections(pageSpec.getSections().stream().map(GmPageSection::optimizeSection).collect(toList()));

        pinPageSections.values().forEach(section ->
            section.getObjectSpecs().forEach(objectSpecs ->
                Collections.sort(objectSpecs.getSpecs(), bySpecStatement())
            )
        );
        return pageSpec;
    }

    private static Comparator<GmSpec> bySpecStatement() {
        return (a, b) -> a.getStatement().compareTo(b.getStatement());
    }

    private GmPageSection createNewSection(String name) {
        GmPageSection pageSection = new GmPageSection(name);
        getSections().add(pageSection);
        return pageSection;
    }

    public List<GmPageSection> getSections() {
        return sections;
    }

    public void setSections(List<GmPageSection> sections) {
        this.sections = sections;
    }

    public String render() {
        Configuration freemarkerConfiguration = new Configuration();

        Map<String, Object> model = new HashMap<>();
        model.put("pageSpec", this);

        try {
            Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/generator/page-spec.gspec.ftl")), freemarkerConfiguration);
            StringWriter sw = new StringWriter();
            template.process(model, sw);
            sw.flush();
            sw.close();
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error rendering template", ex);
        }
    }
}
