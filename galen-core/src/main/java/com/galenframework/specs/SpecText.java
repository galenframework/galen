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
package com.galenframework.specs;


import com.galenframework.parser.SyntaxException;

import java.util.List;

public class SpecText extends Spec {
    private List<String> operations;

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public Spec withOperations(List<String> operations) {
        setOperations(operations);
        return this;
    }

    public enum Type {
        IS("is"), CONTAINS("contains"), STARTS("starts"), ENDS("ends"), MATCHES("matches");

        private final String operationName;
        private Type(String operationName) {
            this.operationName = operationName;
        }

        public static Type fromString(String typeString) {
            for (Type type : Type.values()) {
                if (type.operationName.equals(typeString)) {
                    return type;
                }
            }
            throw new SyntaxException("Unknown validation type: " + typeString);
        }

        public static boolean isValid(String typeString) {
            for (Type type : Type.values()) {
                if (type.operationName.equals(typeString)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private Type type;
    private String text;

    public SpecText(Type type, String text) {
        this.setType(type);
        this.setText(text);
    }

    public SpecText(Type type, String text, List<String> operations) {
        this.setType(type);
        this.setText(text);
        this.setOperations(operations);
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
