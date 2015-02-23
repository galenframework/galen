package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.page.ConditionalBlock;
import net.mindengine.galen.specs.page.PageSection;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/23.
 */
public class TaggedPageSection extends PageSection {
    private List<String> tags;
    private List<ConditionalBlock> conditionalBlocks;

    public List<ConditionalBlock> getConditionalBlocks() {
        return this.conditionalBlocks;
    }

    public void setConditionalBlocks(List<ConditionalBlock> conditionalBlocks) {
        this.conditionalBlocks = conditionalBlocks;
    }

    public void addConditionalBlock(ConditionalBlock conditionalBlock) {
        if (conditionalBlocks == null) {
            conditionalBlocks = new LinkedList<ConditionalBlock>();
        }
        conditionalBlocks.add(conditionalBlock);
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean appliesToTags(List<String> includedTags) {
        return tags.contains("*") || hasAnyTag(includedTags);
    }

    public boolean hasAnyTag(List<String> includedTags) {
        if (includedTags != null && includedTags.size() > 0) {
            if (tags != null) {
                for (String tag : includedTags) {
                    if (tags.contains(tag)) {
                        return true;
                    }
                }
            }
            return false;
        }
        else return true;
    }


}
