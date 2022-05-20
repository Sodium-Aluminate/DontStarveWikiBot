package com.NaAlOH4.IO;

import com.NaAlOH4.PoParser.Tools;
import com.NaAlOH4.PoParser.Translation;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;

public class ReadFile {
    public static String ReadFile(String path){
        File file = new File(path);
        StringBuilder stringBuilder = new StringBuilder();
        try (Source source = Okio.source(file);
             BufferedSource buffer = Okio.buffer(source);
        ) {
            while (true){
                String s = buffer.readUtf8Line();
                if(s==null)break;
                stringBuilder.append(s);
                stringBuilder.append("\n");
            }


        } catch (IOException ignored) {
        }
        return stringBuilder.toString();
    }
}
