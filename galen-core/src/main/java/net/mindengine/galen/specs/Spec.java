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
package net.mindengine.galen.specs;

import java.util.Properties;

import net.mindengine.galen.specs.reader.Place;

public abstract class Spec {
    private String originalText;
    private Properties properties;
    private Place place;
    private boolean onlyWarn = false;
    private String alias;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String toText() {
        return originalText;
    }
    
    public Spec withOriginalText(String originalText) {
        setOriginalText(originalText);
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Place getPlace() {
        return this.place;
    }
    
    public void setPlace(Place place) {
        this.place = place;
    }
    
    public Spec withPlace(Place place) {
        setPlace(place);
        return this;
    }

    public boolean isOnlyWarn() {
        return onlyWarn;
    }

    public void setOnlyWarn(boolean onlyWarn) {
        this.onlyWarn = onlyWarn;
    }

    public Spec withOnlyWarn(boolean onlyWarn) {
        setOnlyWarn(onlyWarn);
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Spec withAlias(String alias) {
        setAlias(alias);
        return this;
    }
}
