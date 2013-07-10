package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.page.PageSection;

public abstract class State {

    public abstract void process(String line);

    public boolean isObjectDefinition() {
        return this instanceof StateObjectDefinition;
    }

    public static State objectDefinition(PageSpec pageSpec) {
        return new StateObjectDefinition(pageSpec);
    }

    public static State startedSection(PageSection section) {
        return new StateDoingSection(section);
    }

}
