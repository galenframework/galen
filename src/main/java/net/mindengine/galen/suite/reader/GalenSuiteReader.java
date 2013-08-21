package net.mindengine.galen.suite.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.mindengine.galen.suite.GalenSuite;


public class GalenSuiteReader {

    public List<GalenSuite> read(File file) throws IOException {
        return read(new FileInputStream(file));
    }
    
    public List<GalenSuite> read(InputStream inputStream) throws IOException {
        GalenSuiteLineProcessor lineProcessor = new GalenSuiteLineProcessor();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        String line = bufferedReader.readLine();
        
        while(line != null){
            lineProcessor.processLine(line);
            line = bufferedReader.readLine();
        }
        return lineProcessor.buildSuites();
    }

}
