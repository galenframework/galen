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
package net.mindengine.galen.utils;

import java.util.List;

import org.openqa.selenium.Dimension;

public class TestDevice {
    private final String name;
    private final Dimension screenSize;
    private final List<String> tags;

    public TestDevice(String name, Dimension screenSize, List<String> tags) {
        this.name = name;
        this.screenSize = screenSize;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public List<String> getTags() {
        return tags;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestDevice [");
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (screenSize != null) {
            builder.append("screenSize=");
            builder.append(screenSize);
            builder.append(", ");
        }
        if (tags != null) {
            builder.append("tags=");
            builder.append(tags);
        }
        builder.append("]");
        return builder.toString();
    }
}
