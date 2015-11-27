package com.galenframework.speclang2.reader.pagespec;

import java.util.List;

public interface FilterFunction<T> {
    T filter (List<T> list);
}
