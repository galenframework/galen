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
package net.mindengine.galen.specs;


import net.mindengine.galen.parser.SyntaxException;

public class SpecText extends Spec {
    public enum Type {
        IS, CONTAINS, STARTS, ENDS, MATCHES;

        public static Type fromString(String typeString) {
            if (typeString.equals("is")) {
                return SpecText.Type.IS;
            }
            else if (typeString.equals("contains")) {
                return SpecText.Type.CONTAINS;
            }
            else if (typeString.equals("starts")) {
                return SpecText.Type.STARTS;
            }
            else if (typeString.equals("ends")) {
                return SpecText.Type.ENDS;
            }
            else if (typeString.equals("matches")) {
                return SpecText.Type.MATCHES;
            }
            else throw new SyntaxException("Unknown validation type: " + typeString);
        }
    }
    
    private Type type;
    private String text;

    public SpecText(Type type, String text) {
        this.setType(type);
        this.setText(text);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
}
