package net.mindengine.galen.suite.reader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mindengine.galen.suite.GalenSuite;

public class GalenSuiteLineProcessor {

    private RootNode rootNode = new RootNode();
    private Node<?> currentNode = rootNode;
    
    private static final int INDENTATION = 4;

    public void processLine(String line) {
        if (!isBlank(line) && !isCommented(line) && !isSeparator(line)) {
            
            int level = indentationLevel(line);
            
            Node<?> processingNode = currentNode.findProcessingNodeByLevel(level);
            Node<?> newNode = processingNode.processNewNode(line);
            currentNode = newNode;
        }
    }
    
    public List<GalenSuite> buildSuites() {
        //TODO rearrange all nodes based on parameterization
        
        return rootNode.build(new Context());
    }

    private int indentationLevel(String line) {
        int spacesCount = 0;
        for (int i=0; i<line.length(); i++) {
            if (line.charAt(i) == ' ') {
                spacesCount++;
            }
            else {
                return (int) Math.floor(spacesCount / INDENTATION);
            }
        }
        return 0;
    }

    private boolean isSeparator(String line) {
        line = line.trim();
        if (line.length() > 3) {
            char ch = line.charAt(0);
            for (int i = 1; i < line.length(); i++) {
                if (ch != line.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        else return false;
    }

    private boolean isBlank(String line) {
        return line.trim().isEmpty();
    }

    private boolean isCommented(String line) {
        return line.trim().startsWith("#");
    }
    
}
