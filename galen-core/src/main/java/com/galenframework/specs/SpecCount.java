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
package com.galenframework.specs;

public class SpecCount extends Spec {
    private String pattern;
    private Range amount;

    private FetchType fetchType = FetchType.ANY;

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }


    public static enum FetchType {
        ANY, VISIBLE, ABSENT;

        public static FetchType parse(String name) {
            return FetchType.valueOf(name.toUpperCase());
        }
    }

    public SpecCount(FetchType fetchType, String pattern, Range amount) {
        setFetchType(fetchType);
        setPattern(pattern);
        setAmount(amount);
    }

    public Range getAmount() {
        return amount;
    }

    public void setAmount(Range amount) {
        this.amount = amount;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
