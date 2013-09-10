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
package net.mindengine.galen.specs.reader;

public class StringCharReader {

    private String text;
    private int length;
    private int cursor = 0;
    

    public StringCharReader(String text) {
        this.text = text;
        this.length = text.length();
    }
    
    public void back() {
        cursor--;
        if (cursor < 0) {
            cursor = 0;
        }
    }

    public boolean hasMore() {
        return cursor < length;
    }

    public char next() {
        if(cursor == length) {
            throw new IndexOutOfBoundsException();
        }
        char symbol = text.charAt(cursor);
        cursor++;
        return symbol;
    }

    public char currentSymbol() {
        if (cursor < length) {
            return text.charAt(cursor);
        }
        else return text.charAt(length - 1);
    }

    public String getTheRest() {
        if (cursor < length) {
            return text.substring(cursor);
        }
        else return ""; 
    }

    public char firstNonWhiteSpaceSymbol() {
        for (int i = cursor; i < length; i++) {
            char symbol = text.charAt(i);
            if (symbol != ' ' && symbol != '\t') {
                return symbol;
            }
        }
        return 0;
    }

}
