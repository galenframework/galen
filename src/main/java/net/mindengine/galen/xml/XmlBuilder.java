/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.xml;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;


public class XmlBuilder {
    
    public static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static String INDENTATION = "    ";
    
    public static enum XmlNodeType {
        NODE, TEXT, TEXT_UNESCAPED
    }
    
    public static class XmlNode {
        private XmlNode parent;
        private XmlNodeType type = XmlNodeType.NODE;
        private List<Pair<String, String>> attributes = new LinkedList<Pair<String,String>>();
        private String name;
        private List<XmlNode> childNodes = new LinkedList<XmlBuilder.XmlNode>();
        
        public XmlNode(String name) {
            this.setName(name);
        }
        public XmlNode withAttribute(String name, String value) {
            this.getAttributes().add(Pair.of(name, value));
            return this;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public XmlNode getParent() {
            return parent;
        }
        public void setParent(XmlNode parent) {
            this.parent = parent;
        }
        public void add(XmlNode childNode) {
            childNode.parent = this;
            this.childNodes.add(childNode);
        }
        
        public void toXml(String indentation, StringWriter sw) {
            if (type == XmlNodeType.TEXT) {
                sw.append(StringEscapeUtils.escapeXml(name));
            }
            else if (type == XmlNodeType.TEXT_UNESCAPED) {
                sw.append(name);
            }
            else {
                if (parent != null) {
                    sw.append("\n");
                }
                sw.append(indentation);
                sw.append("<");
                sw.append(name);
                writeAttributes(sw);
                sw.append(">");
                
                writeChildren(indentation + INDENTATION, sw);
                
                if (childNodes != null && childNodes.size() > 0 && !containsOnlyText()) {
                    sw.append("\n");
                    sw.append(indentation);
                }
                
                sw.append("</");
                sw.append(name);
                sw.append(">");
            }
        }
        
        private boolean containsOnlyText() {
            return childNodes.size() == 1 
                    && (childNodes.get(0).getType() == XmlNodeType.TEXT 
                        || childNodes.get(0).getType() == XmlNodeType.TEXT_UNESCAPED);
        }
        private void writeChildren(String indentation, StringWriter sw) {
            for (XmlNode childNode : childNodes) {
                childNode.toXml(indentation, sw);
            }
        }
        private void writeAttributes(StringWriter sw) {
            for(Pair<String, String> attribute : getAttributes()){
                sw.append(' ');
                sw.append(attribute.getLeft());
                sw.append('=');
                sw.append('"');
                sw.append(StringEscapeUtils.escapeXml(attribute.getRight()));
                sw.append('"');
            }
        }
        public List<Pair<String, String>> getAttributes() {
            return attributes;
        }
        public void setAttributes(List<Pair<String, String>> attributes) {
            this.attributes = attributes;
        }
        public XmlNodeType getType() {
            return type;
        }
        public void setType(XmlNodeType type) {
            this.type = type;
        }
        
        public XmlNode asTextNode() {
            setType(XmlNodeType.TEXT);
            return this;
        }
        public XmlNode withChildren(XmlNode...nodes) {
            for (XmlNode node : nodes) {
                add(node);
            }
            return this;
        }
        
        public XmlNode withText(String text) {
            add(node(text).asTextNode());
            return this;
        }
        public XmlNode withUnescapedText(String text) {
            add(node(text).asUnescapedTextNode());
            return this;
        }
        private XmlNode asUnescapedTextNode() {
            setType(XmlNodeType.TEXT_UNESCAPED);
            return this;
        }
        
    }

    private XmlNode rootNode;
    private String firstLine;

    public XmlBuilder(String firstLine, XmlNode rootNode) {
        this.firstLine = firstLine;
        this.rootNode = rootNode;
    }

    public static XmlNode node(String name) {
        return new XmlNode(name);
    }

    public String build() {
        StringWriter sw = new StringWriter();
        
        if (firstLine != null) {
            sw.append(firstLine);
        }
        rootNode.toXml("", sw);
        
        return sw.toString();
    }

    public static XmlNode textNode(String text) {
        return new XmlNode(text).asTextNode();
    }
}
