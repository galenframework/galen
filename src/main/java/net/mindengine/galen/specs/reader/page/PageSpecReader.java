package net.mindengine.galen.specs.reader.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageSpecReader {

    public PageSpec read(File file) throws IOException {
        return read(new FileInputStream(file));
    }

    public PageSpec read(InputStream inputStream) throws IOException {
        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        String line = bufferedReader.readLine();
        while(line != null){
            lineProcessor.processLine(line);
            line = bufferedReader.readLine();
        }

        return lineProcessor.buildPageSpec();
    }

}
