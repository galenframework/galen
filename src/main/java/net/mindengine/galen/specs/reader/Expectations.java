package net.mindengine.galen.specs.reader;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.Range;

public class Expectations {

    
    public static List<Expectation<?>> expectThese(Expectation<?>...expectations) {
        return Arrays.asList(expectations);
    }

    public static Expectation<List<Side>> sides() {
        return new ExpectSides();
    }

    public static Expectation<Range> range() {
        return new ExpectRange();
    }

    public static Expectation<String> objectName() {
        return new ExpectWord();
    }
    
    public static boolean isDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t';
    }
    
    public static boolean isWordDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t' || symbol == ',';
    }
    
    public static boolean isNumeric(char symbol) {
        return symbol == '-' || (symbol >= '0' && symbol <= '9');
    }

    public static Expectation<List<Location>> locations() {
        return new ExpectLocations();
    }

    
}
