package net.mindengine.galen.specs.reader.page;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.page.PageSection;


public class PageSpecLineProcessor {

    private static final String TAG = "@";
    private static final String COMMENT = "#";
    PageSpec pageSpec = new PageSpec();
    
    
    private State state;
    
    public PageSpecLineProcessor() {
        startNewSection("");
    }

    public void processLine(String line) {
        if (!isCommentedOut(line) && !isEmpty(line)) {
            if (isObjectSeparator(line)) {
                switchObjectDefinitionState();
            }
            else if (line.startsWith(TAG)) {
                startNewSection(line.substring(1));
            }
            else if (isSectionSeparator(line)) {
                //Do nothing
            }
            else state.process(line);
        }
    }
    
    public PageSpec buildPageSpec() {
        Iterator<PageSection> it = pageSpec.getSections().iterator();
        while(it.hasNext()) {
            if (it.next().getObjects().size() == 0) {
                it.remove();
            }
         }
        return this.pageSpec;
    }

    private void switchObjectDefinitionState() {
        if (state.isObjectDefinition()) {
            startNewSection("");
        }
        else state = State.objectDefinition(pageSpec);
    }

    private boolean isSectionSeparator(String line) {
        line = line.trim();
        return !containsAnyLetters(line);
    }

    private boolean isObjectSeparator(String line) {
        return containsOnly(line.trim(), '=');
    }

    private void startNewSection(String tags) {
        PageSection section = new PageSection();
        section.setTags(readTags(tags));
        pageSpec.addSection(section);
        state = State.startedSection(section);
    }

    private List<String> readTags(String tagsText) {
        List<String> tags = new LinkedList<String>();
        for (String tag : tagsText.split(",")) {
            tag = tag.trim();
            if(!tag.isEmpty()) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private boolean isEmpty(String line) {
        return line.trim().isEmpty();
    }

    private boolean isCommentedOut(String line) {
        return line.trim().startsWith(COMMENT);
    }

    private boolean containsOnly(String line, char c) {
        if (line.length() > 1) {
            for (int i=0; i<line.length(); i++) {
                if (line.charAt(i) != c) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean containsAnyLetters(String line) {
        if (line.length() > 0) {
            for (int i=0; i<line.length(); i++) {
                char symbol = line.charAt(i);
                if ((symbol >= 'A' && symbol <= 'Z') || (symbol >= 'a' && symbol <= 'z')) {
                    return true;
                }
            }
        }
        return false;
    }
}
