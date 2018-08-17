/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class PageItemNode {
    private final PageItem pageItem;
    private List<PageItemNode> children = new LinkedList<>();

    @JsonIgnore
    private PageItemNode parent = null;
    private int minimalPaddingLeft = -100;
    private int minimalPaddingRight = -100;
    private int minimalPaddingTop = -100;
    private int minimalPaddingBottom = -100;

    public PageItemNode(PageItem pageItem) {
        this.pageItem = pageItem;
    }

    public PageItem getPageItem() {
        return pageItem;
    }

    public List<PageItemNode> getChildren() {
        return children;
    }

    public void setChildren(List<PageItemNode> children) {
        this.children = children;
    }

    public void moveToParent(PageItemNode pinB) {
        pinB.addChild(this);
        this.parent = pinB;
    }

    private void addChild(PageItemNode pin) {
        if (!children.contains(pin)) {
            children.add(pin);
        }
        pin.parent = this;
    }

    public void printTree() {
        printTree("");
    }

    private void printTree(String indentation) {
        System.out.print(indentation);
        System.out.println(pageItem.getName());
        for (PageItemNode childPin: children) {
            childPin.printTree(indentation + "    ");
        }
    }

    public PageItemNode getParent() {
        return parent;
    }

    public void visitTree(Consumer<PageItemNode> consumer) {
        consumer.accept(this);
        for (PageItemNode pin: children) {
            pin.visitTree(consumer);
        }
    }

    public int getMinimalPaddingLeft() {
        return minimalPaddingLeft;
    }

    public void updateMinimalPaddingLeft(int padding) {
        this.minimalPaddingLeft = smallestValue(this.minimalPaddingLeft, padding);
    }

    public int getMinimalPaddingRight() {
        return minimalPaddingRight;
    }

    public void updateMinimalPaddingRight(int padding) {
        this.minimalPaddingRight = smallestValue(this.minimalPaddingRight, padding);
    }

    public int getMinimalPaddingTop() {
        return minimalPaddingTop;
    }

    public void updateMinimalPaddingTop(int padding) {
        this.minimalPaddingTop = smallestValue(this.minimalPaddingTop, padding);
    }

    public int getMinimalPaddingBottom() {
        return minimalPaddingBottom;
    }

    public void updateMinimalPaddingBottom(int padding) {
        this.minimalPaddingBottom = smallestValue(this.minimalPaddingBottom, padding);
    }

    private int smallestValue(int original, int newValue) {
        if (original < 0 || original > newValue) {
            return newValue;
        } else {
            return original;
        }
    }

    public PageItemNode findParentOrSelf() {
        return parent != null ? parent : this;
    }
}
