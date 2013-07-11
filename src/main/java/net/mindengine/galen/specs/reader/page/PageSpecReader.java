package net.mindengine.galen.specs.reader.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageSpecReader {
    
    public PageSpec read(File file) throws IOException {
        return read(new FileInputStream(file), file.getAbsolutePath());
    }

    public PageSpec read(InputStream inputStream) throws IOException {
        return read(inputStream, "<unknown location>");
    }
    
    
    public PageSpec read(InputStream inputStream, String fileLocation) throws IOException {
        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        String line = bufferedReader.readLine();
        
        int lineNumber = 1;
        try {
            while(line != null){
                lineProcessor.processLine(line);
                line = bufferedReader.readLine();
                lineNumber++;
            }
        }
        catch (Exception exception) {
            throw new PageSpecReaderException(exception, fileLocation, lineNumber);
        }

        return lineProcessor.buildPageSpec();
    }
    
    

}
