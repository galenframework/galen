package net.mindengine.galen.specs;

public class Range {

    public Range(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }
    private Integer from;
    private Integer to;
    public Integer getFrom() {
        return from;
    }
    public Integer getTo() {
        return to;
    }
    public static Range exact(int number) {
        return new Range(number, null);
    }
    public static Range between(int from, int to) {
        return new Range(from, to);
    }
}
