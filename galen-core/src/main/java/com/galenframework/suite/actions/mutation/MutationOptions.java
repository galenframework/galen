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
package com.galenframework.suite.actions.mutation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MutationOptions {
    private int positionOffset = 5;

    public int getPositionOffset() {
        return positionOffset;
    }

    public MutationOptions setPositionOffset(int positionOffset) {
        this.positionOffset = positionOffset;
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("positionOffset", positionOffset)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MutationOptions that = (MutationOptions) o;

        return new EqualsBuilder()
            .append(positionOffset, that.positionOffset)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(positionOffset)
            .toHashCode();
    }
}
