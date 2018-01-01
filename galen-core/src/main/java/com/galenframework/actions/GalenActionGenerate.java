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
package com.galenframework.actions;

import com.galenframework.generator.PageSpecGenerationResult;
import com.galenframework.generator.SpecGenerator;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.utils.GalenUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintStream;

public class GalenActionGenerate extends GalenAction {

    private final GalenActionGenerateArguments generateArguments;

    public GalenActionGenerate(String[] arguments, PrintStream outStream, PrintStream errStream) {
        super(arguments, outStream, errStream);
        this.generateArguments = GalenActionGenerateArguments.parse(arguments);
    }

    @Override
    public void execute() throws Exception {
        SpecGenerator specGenerator = new SpecGenerator();
        SpecGeneratorOptions specGeneratorOptions = new SpecGeneratorOptions();
        specGeneratorOptions.setUseGalenExtras(generateArguments.isUseGalenExtras());

        PageSpecGenerationResult result = specGenerator.generate(GalenUtils.findFileOrResourceAsStream(generateArguments.getPath()), specGeneratorOptions);
        String text = SpecGenerator.generatePageSpec(result, specGeneratorOptions);
        File outputFile = new File(generateArguments.getExport());
        outputFile.createNewFile();
        FileUtils.writeStringToFile(outputFile, text);
    }

    public GalenActionGenerateArguments getGenerateArguments() {
        return generateArguments;
    }
}
