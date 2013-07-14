/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.specs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Range {

    public Range(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }
    private Integer from;
    private Integer to;
    public Integer getFrom() {
        return from;
    }
    public Integer getTo() {
        return to;
    }
    public static Range exact(int number) {
        return new Range(number, null);
    }
    public static Range between(int from, int to) {
        return new Range(Math.min(from, to), Math.max(from, to));
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19).append(from).append(to).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range rhs = (Range)obj;
        return new EqualsBuilder().append(from, rhs.from).append(to, rhs.to).isEquals();
    }
    
    @Override
    public String toString() {
        return "Range(" + from + ", " + to + ")";
    }
}
