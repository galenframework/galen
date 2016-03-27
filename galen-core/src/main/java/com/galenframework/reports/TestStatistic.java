/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TestStatistic {

    private int passed = 0;
    private int errors = 0;
    private int warnings = 0;
    private int total = 0;

    public TestStatistic() {
    }

    public TestStatistic(int passed, int errors, int warnings, int total) {
        this.passed = passed;
        this.errors = errors;
        this.warnings = warnings;
        this.total = total;
    }

    public int getPassed() {
        return passed;
    }
    public void setPassed(int passed) {
        this.passed = passed;
    }
    public int getErrors() {
        return errors;
    }
    public void setErrors(int errors) {
        this.errors = errors;
    }
    public int getWarnings() {
        return warnings;
    }
    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public void add(TestStatistic statistic) {
        this.total += statistic.getTotal();
        this.passed += statistic.getPassed();
        this.errors += statistic.getErrors();
        this.warnings += statistic.getWarnings();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TestStatistic)) {
            return false;
        }

        TestStatistic rhs = (TestStatistic) obj;
        return new EqualsBuilder()
                .append(this.passed, rhs.passed)
                .append(this.errors, rhs.errors)
                .append(this.warnings, rhs.warnings)
                .append(this.total, rhs.total)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(passed)
                .append(errors)
                .append(warnings)
                .append(total)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("passed", passed)
                .append("errors", errors)
                .append("warnings", warnings)
                .append("total", total)
                .toString();
    }
}
