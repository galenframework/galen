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
package net.mindengine.galen.utils;

import java.awt.Dimension;
import java.io.File;
import java.net.URL;

public class GalenUtils {

    private static final String URL_REGEX = "[a-zA-Z0-9]+://.*";
    
    
    public static boolean isUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.matches(URL_REGEX);
    }
    
    public static String formatScreenSize(Dimension screenSize) {
        if (screenSize != null) {
            return String.format("%dx%d", screenSize.width, screenSize.height);
        }
        else return "0x0";
    }

    public static Dimension readSize(String sizeText) {
        if (!sizeText.matches("[0-9]+x[0-9]+")) {
            throw new RuntimeException("Incorrect screen size: " + sizeText);
        }
        else {
            String[] arr = sizeText.split("x");
            return new Dimension(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        }
    }

    public static File findFile(String specFile) {
        URL resource = GalenUtils.class.getResource(specFile);
        if (resource != null) {
            return new File(resource.getFile());
        }
        else return new File(specFile);
    }
    
}
