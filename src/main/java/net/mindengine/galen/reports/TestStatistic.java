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
package net.mindengine.galen.reports;

public class TestStatistic {

    private int passed = 0;
    private int errors = 0;
    private int warnings = 0;
    private int total = 0;
    
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
}
