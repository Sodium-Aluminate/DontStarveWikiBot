package com.NaAlOH4.PoParser;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class Tools {
    public static final Comparator<String> stringComparator = (o1, o2)-> {
        byte[] a = o1.getBytes(StandardCharsets.UTF_8);
        byte[] b = o2.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            if(a[i]==b[i]) continue;
            return a[i]-b[i];
        }
        return a.length - b.length;
    };
}
