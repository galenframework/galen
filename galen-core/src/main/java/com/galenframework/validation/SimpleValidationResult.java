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
package com.galenframework.validation;

import com.galenframework.reports.model.LayoutMeta;

public class SimpleValidationResult {
    private final String error;
    private final LayoutMeta meta;


    private SimpleValidationResult(String error, LayoutMeta meta) {
        this.error = error;
        this.meta = meta;
    }

    public static SimpleValidationResult success(LayoutMeta meta) {
        return new SimpleValidationResult(null, meta);
    }

    public static SimpleValidationResult error(String error, LayoutMeta meta) {
       if (error == null)  {
           throw new IllegalArgumentException("error should not be null");
       }

       if (error.trim().isEmpty()) {
           throw new IllegalArgumentException("error should not be empty");
       }
       return new SimpleValidationResult(error, meta);
    }

    public LayoutMeta getMeta() {
        return meta;
    }


    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }
}
