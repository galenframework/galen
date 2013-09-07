package net.mindengine.galen.specs;



public class SpecText extends Spec {
    public enum Type {
        IS, CONTAINS, STARTS, ENDS, MATCHES
    }
    
    private Type type;
    private String text;

    public SpecText(Type type, String text) {
        this.setType(type);
        this.setText(text);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
}
