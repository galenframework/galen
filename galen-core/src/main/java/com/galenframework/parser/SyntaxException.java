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


import com.galenframework.specs.Place;

public class SyntaxException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 421348434010710101L;
    private static final Place NULL_PLACE = null;

    private Place place;
    
    public SyntaxException(Place place) {
        super();
        this.place = place;
    }

    public SyntaxException(Place place, String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        this.place = place;
    }

    public SyntaxException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public SyntaxException(Place place, String paramString) {
        super(paramString);
        this.place = place;
    }

    public SyntaxException(Place place, Throwable paramThrowable) {
        super(null, paramThrowable);
        this.place = place;
    }
    
    public SyntaxException(String message) {
		this(NULL_PLACE, message);
	}

    public SyntaxException(StructNode originNode, String message) {
        this(originNode.getPlace(), message);
    }

    public SyntaxException(StructNode originNode, String message, Throwable cause) {
        this(originNode.getPlace(), message, cause);
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        StringBuilder builder = new StringBuilder();
        if (message != null) {
            builder.append(message);
            if (place != null) {
                builder.append("\n    ");
            }
        }
        if (place != null) {
            builder.append(place.toExceptionMessage());
        }

        return builder.toString();
    }
}
