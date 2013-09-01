package net.mindengine.galen.suite.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenSuite;


public class GalenSuiteReader {

    public List<GalenSuite> read(File file) throws IOException {
        return read(new FileInputStream(file), file.getAbsolutePath());
    }
    public List<GalenSuite> read(InputStream inputStream) throws IOException {
        return read(inputStream, "< unknown file >");
    }
    
    private List<GalenSuite> read(InputStream inputStream, String filePath) throws IOException {
        try {
            GalenSuiteLineProcessor lineProcessor = new GalenSuiteLineProcessor();
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            
            String line = bufferedReader.readLine();
            int lineNumber = 0;
            while(line != null){
                lineNumber++;
                lineProcessor.processLine(line, lineNumber);
                line = bufferedReader.readLine();
            }
            return lineProcessor.buildSuites();
        }
        catch (SyntaxException e) {
            
            int lineNumber = -1;
            if (e.getLine() != null) {
                lineNumber = e.getLine().getNumber();
            }
            throw new FileSyntaxException(e, filePath, lineNumber);
        }
    }

}
