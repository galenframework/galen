package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.isDelimeter;

public class ExpectWord implements Expectation<String> {

    @Override
    public String read(StringCharReader reader) {
        boolean started = false;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            char symbol = reader.next();
            if(isDelimeter(symbol)) {
                if (started) {
                    break;
                }
            }
            else {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }

    
}
