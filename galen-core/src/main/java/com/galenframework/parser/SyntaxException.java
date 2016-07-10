/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.parser;

import com.galenframework.suite.reader.Line;

public class SyntaxException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 421348434010710101L;
    private static final Line NULL_LINE = null;

    private Line line;
    
    public SyntaxException(Line line) {
        super();
        this.line = line; 
    }

    public SyntaxException(Line line, String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        this.line = line;
    }

    public SyntaxException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public SyntaxException(Line line, String paramString) {
        super(paramString);
        this.line = line;
    }

    public SyntaxException(Line line, Throwable paramThrowable) {
        super(null, paramThrowable);
        this.line = line;
    }
    
    public SyntaxException(String message) {
		this(NULL_LINE, message);
	}

    public SyntaxException(StructNode originNode, String message) {
        this(originNode.getLine(), message);
    }

    public SyntaxException(StructNode originNode, String message, Throwable cause) {
        this(originNode.getLine(), message, cause);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        StringBuilder builder = new StringBuilder();
        if (message != null) {
            builder.append(message);
            if (line != null) {
                builder.append("\n    ");
            }
        }
        if (line != null) {
            builder.append(line.toString());
        }

        return builder.toString();
    }
}
