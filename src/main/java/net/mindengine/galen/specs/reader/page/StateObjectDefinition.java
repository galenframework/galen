package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.ExpectWord;
import net.mindengine.galen.specs.reader.IncorrectSpecException;
import net.mindengine.galen.specs.reader.StringCharReader;

public class StateObjectDefinition extends State {

    private PageSpec pageSpec;

    public StateObjectDefinition(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    @Override
    public void process(String line) {
        StringCharReader reader = new StringCharReader(line);
        
        String objectName = expectWord(reader, "Object name");
        
        try {
            String locatorType = expectWord(reader, "Locator type");
            
            String value = reader.getTheRest().trim();
            if (value.isEmpty()) {
                throw new IncorrectSpecException(String.format("The locator for object '%s' is not defined correctly", objectName));
            }
            pageSpec.addObject(objectName, new Locator(locatorType, value));
        }
        catch (Exception e) {
            throw new IncorrectSpecException("Object \"" + objectName + "\" has incorrect locator", e);
        }
    }

    private String expectWord(StringCharReader reader, String what) {
        String word = new ExpectWord().read(reader).trim();
        if (word.isEmpty()) {
            throw new IncorrectSpecException(what + " is not defined correctly");
        }
        return word;
    }

}
