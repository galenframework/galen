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
package com.galenframework.actions;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class GalenActionConfig extends GalenAction {
    public GalenActionConfig(String[] arguments, PrintStream outStream, PrintStream errStream) {
        super(arguments, outStream, errStream);
    }

    @Override
    public void execute() throws IOException {
        File file = new File("galen.config");

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Could not create file: " + file.getAbsolutePath());
            }
            FileOutputStream fos = new FileOutputStream(file);

            StringWriter writer = new StringWriter();
            IOUtils.copy(getClass().getResourceAsStream("/config-template.conf"), writer, "UTF-8");
            IOUtils.write(writer.toString(), fos, "UTF-8");
            fos.flush();
            fos.close();
            outStream.println("Created config file");
        } else {
            errStream.println("Config file already exists");
        }
    }
}
