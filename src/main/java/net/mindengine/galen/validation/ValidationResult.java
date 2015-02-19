/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.validation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ValidationResult {

    private List<ValidationObject> validationObjects = new LinkedList<ValidationObject>();
    private ValidationError error;

    public ValidationResult(List<ValidationObject> validationObjects) {
        this.validationObjects = validationObjects;
    }

    public ValidationResult() {
    }

    public ValidationResult(List<ValidationObject> validationObjects, ValidationError validationError) {
        this.validationObjects = validationObjects;
        this.error = validationError;
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

    public static boolean doesNotHaveErrors(List<ValidationResult> validationResults) {
        for (ValidationResult result : validationResults) {
            if (result.getError() != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(validationObjects)
                .append(error)
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
                .isEquals();

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("objects", validationObjects)
                .append("error", error)
                .toString();
    }

    public static List<ValidationResult> filterOnlyErrorResults(List<ValidationResult> results) {
        List<ValidationResult> filtered = new LinkedList<ValidationResult>();

        for (ValidationResult result : results) {
            filtered.add(result);
        }

        return filtered;
    }
}
