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
package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.reader.IncorrectSpecException;

public class PageSpecReaderException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1698658011707718651L;

    private String specFile;
    private int specLine;
    
    
    public PageSpecReaderException(Exception cause, String specFile, int specLine) {
        super(cause);
        this.specFile = specFile;
        this.specLine = specLine;
    }
    public String getSpecFile() {
        return specFile;
    }
    public int getSpecLine() {
        return specLine;
    }
    
    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (cause instanceof IncorrectSpecException) {
            return cause.getMessage();
        }
        return super.getMessage();
    }
}
