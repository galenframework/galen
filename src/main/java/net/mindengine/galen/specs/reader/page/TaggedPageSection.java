/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.page.ConditionalBlock;
import net.mindengine.galen.specs.page.PageSection;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Created by ishubin on 2015/02/23.
 */
public class TaggedPageSection extends PageSection {
    private List<String> tags;
    private List<ConditionalBlock> conditionalBlocks;

    public List<ConditionalBlock> getConditionalBlocks() {
        return this.conditionalBlocks;
    }

    public void setConditionalBlocks(final List<ConditionalBlock> conditionalBlocks) {
        this.conditionalBlocks = conditionalBlocks;
    }

    public void addConditionalBlock(final ConditionalBlock conditionalBlock) {
        if (conditionalBlocks == null) {
            conditionalBlocks = new LinkedList<ConditionalBlock>();
        }
        conditionalBlocks.add(conditionalBlock);
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public boolean appliesToTags(final List<String> includedTags) {
        return tags.contains("*") || hasAnyTag(includedTags);
    }

    public boolean hasAnyTag(final List<String> includedTags) {
        if (CollectionUtils.isNotEmpty(includedTags)) {
            if (CollectionUtils.isNotEmpty(tags)) {
                for (final String tag : includedTags) {
                    if (tags.contains(tag)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

}
