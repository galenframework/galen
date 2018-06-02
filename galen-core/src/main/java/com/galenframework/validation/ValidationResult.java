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
package com.galenframework.validation;

import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Spec;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ValidationResult {

    private Spec spec;
    private List<ValidationObject> validationObjects = new LinkedList<>();
    private ValidationError error;
    private List<ValidationResult> childValidationResults;
    private List<LayoutMeta> meta;

    public ValidationResult(Spec spec, List<ValidationObject> validationObjects) {
        this.spec = spec;
        this.validationObjects = validationObjects;
    }

    public ValidationResult(Spec spec) {
        this.spec = spec;
    }

    public ValidationResult(Spec spec, List<ValidationObject> validationObjects, ValidationError validationError, List<LayoutMeta> meta) {
        this.spec = spec;
        this.validationObjects = validationObjects;
        this.error = validationError;
        this.meta = meta;
    }

    public List<ValidationObject> getValidationObjects() {
        return validationObjects;
    }

    public void setValidationObjects(List<ValidationObject> validationObjects) {
        this.validationObjects = validationObjects;
    }

    public ValidationError getError() {
        return error;
    }

    public void setError(ValidationError error) {
        this.error = error;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(validationObjects)
                .append(error)
                .append(childValidationResults)
                .append(meta)
                .toHashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ValidationResult))
            return false;

        ValidationResult rhs = (ValidationResult)obj;
        return new EqualsBuilder()
                .append(this.error, rhs.error)
                .append(this.validationObjects, rhs.validationObjects)
                .append(this.childValidationResults, rhs.childValidationResults)
                .append(this.meta, rhs.meta)
                .isEquals();

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("objects", validationObjects)
                .append("error", error)
                .append("childValidationResults", childValidationResults)
                .append("meta", meta)
                .toString();
    }

    public List<ValidationResult> getChildValidationResults() {
        return childValidationResults;
    }

    public void setChildValidationResults(List<ValidationResult> childValidationResults) {
        this.childValidationResults = childValidationResults;
    }

    public ValidationResult withObjects(List<ValidationObject> objects) {
        setValidationObjects(objects);
        return this;
    }
    public ValidationResult withError(ValidationError error) {
        setError(error);
        return this;
    }
    public ValidationResult withChildValidationResults(List<ValidationResult> childValidationResults) {
        setChildValidationResults(childValidationResults);
        return this;
    }

    public Spec getSpec() {
        return spec;
    }

    public List<LayoutMeta> getMeta() {
        return meta;
    }

    public void setMeta(List<LayoutMeta> meta) {
        this.meta = meta;
    }
}
