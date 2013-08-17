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
        NODE, TEXT
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
            else {
                sw.append("\n");
                sw.append(indentation);
                sw.append("<");
                sw.append(name);
                writeAttributes(sw);
                sw.append(">");
                
                writeChildren(indentation + INDENTATION, sw);
                
                if (!containsOnlyText()) {
                    sw.append("\n");
                    sw.append(indentation);
                }
                
                sw.append("</");
                sw.append(name);
                sw.append(">");
            }
        }
        
        private boolean containsOnlyText() {
            return childNodes.size() == 1 && childNodes.get(0).getType() == XmlNodeType.TEXT;
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
            childNodes = new LinkedList<XmlBuilder.XmlNode>();
            for (XmlNode node : nodes) {
                childNodes.add(node);
            }
            return this;
        }
        
        public XmlNode withText(String text) {
            childNodes = new LinkedList<XmlBuilder.XmlNode>();
            childNodes.add(node(text).asTextNode());
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
