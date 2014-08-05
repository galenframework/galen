/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.reports;

import net.mindengine.galen.reports.LayoutReportNode;
import net.mindengine.galen.reports.TestStatistic;
import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;
import net.mindengine.galen.validation.ValidationError;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LayoutReportNodeTest {

    @Test
    public void should_fetchStatistics_properly() {
        LayoutReport report = createSampleLayoutReport();
        TestStatistic statistics = new TestStatistic();
        new LayoutReportNode(report, "Layout check").fetchStatistic(statistics);

        assertThat(statistics.getPassed(), is(1));
        assertThat(statistics.getErrors(), is(3));
        assertThat(statistics.getWarnings(), is(2));
    }

    @Test
    public void shouldReturn_errorsAndWarnings_properly() {
        LayoutReport report = createSampleLayoutReport();

        assertThat(report.errors(), is(3));
        assertThat(report.warnings(), is(2));
    }

    private LayoutReport createSampleLayoutReport() {
        LayoutReport report = new LayoutReport();
        List<ValidationError> list = new LinkedList<ValidationError>();
        report.setValidationErrors(list);

        list.add(new ValidationError().withOnlyWarn(true));
        list.add(new ValidationError());
        list.add(new ValidationError());
        list.add(new ValidationError().withOnlyWarn(true));
        list.add(new ValidationError());


        List<LayoutSection> sections = new LinkedList<LayoutSection>();
        LayoutSection section = new LayoutSection();
        sections.add(section);
        report.setSections(sections);


        List<LayoutObject> objects = new LinkedList<LayoutObject>();
        section.setObjects(objects);
        LayoutObject object = new LayoutObject();
        objects.add(object);


        List<LayoutSpec> specs = new LinkedList<LayoutSpec>();
        object.setSpecs(specs);

        specs.add(passedSpec());
        specs.add(failedSpec());
        specs.add(failedSpec());
        specs.add(failedSpec());
        specs.add(warnSpec());
        specs.add(warnSpec());



        return report;
    }

    private LayoutSpec warnSpec() {
        LayoutSpec spec = new LayoutSpec();
        spec.setFailed(true);
        spec.setOnlyWarn(true);
        return spec;
    }

    private LayoutSpec failedSpec() {
        LayoutSpec spec = new LayoutSpec();
        spec.setFailed(true);
        spec.setOnlyWarn(false);
        return spec;
    }

    private LayoutSpec passedSpec() {
        LayoutSpec spec = new LayoutSpec();
        spec.setFailed(false);
        return spec;
    }


}
