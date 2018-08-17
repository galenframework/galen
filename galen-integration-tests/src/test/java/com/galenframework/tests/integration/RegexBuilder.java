/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.integration;

public class RegexBuilder {
    StringBuilder builder = new StringBuilder();

    public RegexBuilder exact(String string) {
        builder.append("\\Q").append(string).append("\\E");
        return this;
    }


    @Override
    public String toString() {
        return builder.toString();
    }

    public RegexBuilder digits() {
        builder.append("[0-9]+");
        return this;
    }

    public RegexBuilder digits(int times) {
        builder.append("[0-9]{").append(times).append("}");
        return this;
    }

    public static RegexBuilder regex() {
        return new RegexBuilder();
    }
}
