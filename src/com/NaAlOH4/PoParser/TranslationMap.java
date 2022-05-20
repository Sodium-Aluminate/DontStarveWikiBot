package com.NaAlOH4.PoParser;

import com.NaAlOH4.IO.WriteFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TranslationMap {
    private static final int MAX_CHUNK_SIZE = 2000;
    private final List<Translation> translationList;

    public TranslationMap() {
        translationList = new ArrayList<>();
    }

    public void add(Translation t){
        this.translationList.add(t);
    }


    public static final Comparator<Translation> translationComparator = (o1, o2) -> {
        byte[] a = o1.reArrangedMsgctxt.getBytes(StandardCharsets.UTF_8);
        byte[] b = o2.reArrangedMsgctxt.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            if(a[i]==b[i]) continue;
            return a[i]-b[i];
        }
        return a.length - b.length;
    };

    public void sort() {

        translationList.sort(translationComparator);

        var tmp = translationList.get(2000);

         tmp = translationList.get(3000);

    }

    public void writeMap(String dist) {
        ArrayList<List<Translation>> transChunks = new ArrayList<>();
        MakingChunk:
        {
            List<Translation> transChunk = new ArrayList<>();
            for (int i = 0; i < translationList.size(); i++) {
                transChunk.add(translationList.get(i));
                if(transChunk.size() > MAX_CHUNK_SIZE)
                    if( translationList.get(i).isNotSameQuote(translationList.get(i+1))){
                    transChunks.add(transChunk);
                    transChunk = new ArrayList<>();
                }
            }
            transChunks.add(transChunk);
        }

        WriteFile.writeFile(dist+"Map.lua", printMap(transChunks));
        WriteFile.writeFile(dist+"Map", printMetaMap(transChunks));

        for (int i = 0; i < transChunks.size(); i++) {
            List<Translation> transChunk = transChunks.get(i);
            WriteFile.writeFile(dist+"MapChunk_"+i+".lua", printChunk(transChunk));
        }

    }

    private static String printChunk(List<Translation> transChunk) {
        StringBuilder stringBuilder = new StringBuilder("return {");
        for (Translation t:transChunk) {
            stringBuilder.append("\n\t{\n");
            stringBuilder.append("\t\tmsgctxt=\"");
            stringBuilder.append(t.msgctxt);
            stringBuilder.append("\",\n");

            stringBuilder.append("\t\tmsgid=\"");
            stringBuilder.append(t.msgid);
            stringBuilder.append("\",\n");

            stringBuilder.append("\t\tmsgstr=\"");
            stringBuilder.append(t.msgstr);
            stringBuilder.append("\",\n\t},\n");
        }
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    private static String printMap(ArrayList<List<Translation>> m){
        StringBuilder map = new StringBuilder("return {\n");
        for (int i = 0; i < m.size(); i++) {
            map.append("\t{\"");
            map.append(m.get(i).get(0).reArrangedMsgctxt);
            map.append("\", \"");
            map.append("MapChunk_"+i);
            map.append("\"},\n");
        }
        map.append("}");
        return map.toString();
    }
    private static String printMetaMap(ArrayList<List<Translation>> m) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < m.size(); i++) {
            s.append("MapChunk_"+i+"\n");
        }
        return s.toString();
    }
}
