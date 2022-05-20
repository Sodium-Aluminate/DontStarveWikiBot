package com.NaAlOH4.PoParser;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class PoSorter {
    public static void main(String[] args) {
        TranslationMap translationMap = new TranslationMap();

        ReadFileToList:
        {
            File dstFile = new File("/tmp/dstScripts/scripts/languages/chinese_s.po");
            File dsFile = new File("/tmp/dstScripts/chinese_s.po");
            try (Source dsSource = Okio.source(dsFile);
                 BufferedSource dsBuffer = Okio.buffer(dsSource);
                 Source dstSource = Okio.source(dstFile);
                 BufferedSource dstBuffer = Okio.buffer(dstSource)
            ) {
                Translation dsCache = readFromBuffer(dsBuffer, false);
                Translation dstCache = readFromBuffer(dstBuffer, true);
                while (true) {
                    if(dsCache==null&&dstCache==null)break ;
                    int result = 0;
                    if(dsCache == null){
                        result = 1;
                    }
                    if(dstCache == null){
                        result=-1;
                    }
                    if(result == 0) result = Tools.stringComparator.compare(dsCache.msgctxt,dstCache.msgctxt);
                    if(result == 0){
                        translationMap.add(dstCache);
                        dstCache = readFromBuffer(dstBuffer, true);
                        dsCache = readFromBuffer(dsBuffer, false);
                    }else if(result<0){
                        translationMap.add(dsCache);
                        dsCache = readFromBuffer(dsBuffer, false);
                    }else {
                        translationMap.add(dstCache);
                        dstCache = readFromBuffer(dstBuffer, true);
                    }

                }
            } catch (IOException ignored) {
            }
        }

        System.out.println("Sorting");
        translationMap.sort();
        translationMap.writeMap("/tmp/dstwiki/");
    }

    private static @Nullable
    Translation readFromBuffer(BufferedSource buffer, boolean isDST) throws IOException {
        String s;
        while (true) {
            s = buffer.readUtf8Line();
            if (s == null) return null;
            if (s.startsWith("msgctxt")) break;
        }
        String msgctxt = s;
        String msgid = buffer.readUtf8Line();
        String msgstr = buffer.readUtf8Line();
        assert msgctxt.startsWith("msgctxt \"");
        assert msgid.startsWith("msgid \"");
        assert msgstr.startsWith("msgstr \"");
        assert msgctxt.endsWith("\"");
        assert msgid.endsWith("\"");
        assert msgstr.endsWith("\"");
        msgctxt = msgctxt.substring("msgctxt \"".length(), msgctxt.length() - 1);
        msgid = msgid.substring("msgid \"".length(), msgid.length() - 1);
        msgstr = msgstr.substring("msgstr \"".length(), msgstr.length() - 1);
        return new Translation(msgctxt,msgid,msgstr,isDST);
    }

}
