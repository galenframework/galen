package com.galenframework.actions;

import com.galenframework.generator.PageSpecGenerationResult;
import com.galenframework.generator.SpecGenerator;
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
        PageSpecGenerationResult result = specGenerator.generate(GalenUtils.findFileOrResourceAsStream(generateArguments.getPath()));
        String text = SpecGenerator.generatePageSpec(result);
        File outputFile = new File(generateArguments.getExport());
        outputFile.createNewFile();
        FileUtils.writeStringToFile(outputFile, text);
    }

    public GalenActionGenerateArguments getGenerateArguments() {
        return generateArguments;
    }
}
