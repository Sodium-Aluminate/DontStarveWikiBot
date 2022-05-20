package com.NaAlOH4.PoParser;

import com.NaAlOH4.IO.WriteFile;
import kotlin.Pair;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;


public class Main {

    public static class Translation{

        @Override
        public String toString() {
            return "Translation{" +
                    "msgctxt='" + getTrueMsgctxt() + '\'' +
                    ", msgid='" + msgid + '\'' +
                    ", msgstr='" + msgstr + '\'' +
                    '}';
        }

        @Nullable
        private String msgctxt_;
        public String msgctxt; // code
        public String msgid; // eng
        public String msgstr; // chs
        public boolean isDst; // is this dst or ds
        public Translation(String msgctxt,String msgid,String msgstr, boolean dst){
            {
                Pair<String, String> s = parseCharacterStrings(parse(msgctxt, "msgctxt"));
                this.msgctxt = s.getFirst();
                msgctxt_ = s.getSecond();
            }

            this.msgid=parse(msgid,"msgid");
            this.msgstr=parse(msgstr,"msgstr");
            isDst = dst;
        }
        String getTrueMsgctxt(){
            return msgctxt_==null? msgctxt: msgctxt_;
        }
        private static String parse(String s, String type){
            assert s.startsWith(type+" \"");
            assert s.endsWith("\"");
            return s.substring(type.length()+" \"".length(), s.length()-1);
        }


        private static final String[] charaCodes = {
                "WALTER",
                "WANDA",
                "WARLY",
                "WATHGRITHR",
                "WAXWELL",
                "WEBBER",
                "WENDY",
                "WICKERBOTTOM",
                "WILLOW",
                "WILSON",
                "WINONA",
                "WOLFGANG",
                "WOODIE",
                "WORMWOOD",
                "WORTOX",
                "WURT",
                "WX78"};

        /**
         * @param msgctxt
         * @return
         * first string: msgctxt that used for sort;
         * second string: original string, null if the first one is original.
         */
        private static Pair<String, String> parseCharacterStrings(String msgctxt) {
            String s1 = "STRINGS.CHARACTERS.";
            if (!msgctxt.startsWith(s1)) return new Pair<>(msgctxt, null);
            int l1 = "STRINGS.CHARACTERS.".length();

            // STRINGS.CHARACTERS.  WENDY  .  DESCRIBE.BACKPACK
            // |<       1       >|  |<2>|     |<      3      >|
            String s23 = msgctxt.substring(l1);

            boolean b = false;
//            for (String code : charaCodes) {
//                if (s23.startsWith(code)) b = true;
//            }
//            if (!b) return new Pair<>(msgctxt, null);

            int l2 = s23.indexOf(".");
            String s2 = s23.substring(0, l2);
            String s3 = s23.substring(l2 + 1);

            return new Pair<>(s1 + s3 + "." + s2, msgctxt);
        }

    }
    public static class TranslationManager{
        private List<Pair<String, List<Translation>>> codeMap = null;
        private List<Pair<String, List<Translation>>> enMap = null;
        Translation[] totalEnList = new Translation[200000];
        Translation[] totalCodeList = new Translation[200000];
        private int length = 0;
        private int enLength = 0;

        private int compareStr(String s1,String s2){
            byte[] a = s1.getBytes(StandardCharsets.UTF_8);
            byte[] b = s2.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < Math.min(a.length, b.length); i++) {
                if(a[i]==b[i]) continue;
                return a[i]-b[i];
            }
            return a.length - b.length;
        }
        private int compareMapStr(String s1,String s2){
            byte[] a = s1.getBytes(StandardCharsets.UTF_8);
            byte[] b = s2.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < Math.min(a.length, b.length); i++) {
                if(a[i]==b[i]) continue;
                return a[i]-b[i];
            }
            return b.length - a.length;

        }


        public void sort(){
            totalEnList = Arrays.copyOfRange(totalEnList, 0, enLength);
            totalCodeList = Arrays.copyOfRange(totalCodeList, 0, length);
            sortSingleList(totalEnList, a->a.msgid);
            sortSingleList(totalCodeList, a->a.msgctxt);
            totalCodeList = margeTranslation(totalCodeList);
            totalEnList = margeTranslation(totalEnList);
        }

        private void sortSingleList(Translation[] list, Function<Translation, String> stringGetter){
            Arrays.sort(list, (a,b)-> compareStr(
                    stringGetter.apply(a),
                    stringGetter.apply(b)
            ));
        }

        private Translation[] margeTranslation(Translation[] l){
            Translation[] tmp = new Translation[l.length];
            int delCount = 0;
            for (int i = 0; i < l.length; i++) {
                tmp[i-delCount] = l[i];
                if(l.length-1 == i)break;

                if(l[i].getTrueMsgctxt().equals(l[i + 1].getTrueMsgctxt())) {
                    if(!l[i].isDst){
                        tmp[i-delCount]=l[i+1];
                    }

                    if (!l[i].msgid.equals(l[i + 1].msgid)
                            || !l[i].msgstr.equals(l[i + 1].msgstr)) {
                                System.err.println("found diff: \n"+l[i]+"\n"+l[i+1]);
                            }
                    i++;
                    delCount ++;
                }
            }

            Translation[] ts = new Translation[l.length - delCount];
            System.arraycopy(tmp, 0 ,ts , 0, ts.length);
            return ts;
        }

        private static String getSameStart(String a,String b){
            int sameCount = 0;
            int limit = Math.min(a.length(), b.length());

            while (sameCount<limit && a.charAt(sameCount)==b.charAt(sameCount)) {
                    sameCount ++;
            }
            return a.substring(0,sameCount);
        }

        public void map(){

            this.codeMap = singleMap(totalCodeList,a->a.msgctxt);
            margeMap(codeMap);
            printMap(codeMap);
            //this.enMap = singleMap(totalEnList, a->a.msgid);
            //margeMap(enMap);
            //printMap(enMap);
        }

        private void printMap(List<Pair<String, List<Translation>>> map){
            List<Pair<String,String>> mapList = new LinkedList<>(){
                @Override
                public String toString() {
                    StringBuilder stringBuilder = new StringBuilder("return {\n");
                    this.sort((a,b)->compareMapStr(a.getFirst(),b.getFirst()));
                    for (Pair<String, String> s_:this) {
                        String s = s_.getSecond();
                        String filename = s.endsWith("_")?
                                s.substring(0,s.length()-1).replace('.','_'):
                                s.replace('.','_');
                        stringBuilder.append("\t{\""+s_.getFirst()+"\",\""+filename+"\"},\n");
                    }
                    stringBuilder.append("}");
                    return stringBuilder.toString();
                }
            };
            for (Pair<String, List<Translation>> p:map) {

                String s = p.getFirst();
                mapList.add(new Pair<>( p.getSecond().get(0).msgctxt,s));
                String filename = s.endsWith("_")?
                        s.substring(0,s.length()-1).replace('.','_'):
                        s.replace('.','_');

                String filePath = "/tmp/dstwiki/"+filename+".lua";
                StringBuilder stringBuilder = new StringBuilder("return {\n");

                for (Translation t:p.getSecond()) {

                    stringBuilder.append("\n\t{\n");
                    stringBuilder.append("\t\tmsgctxt=\"");
                    stringBuilder.append(t.getTrueMsgctxt());
                    stringBuilder.append("\",\n");

                    stringBuilder.append("\t\tmsgid=\"");
                    stringBuilder.append(t.msgid);
                    stringBuilder.append("\",\n");

                    stringBuilder.append("\t\tmsgstr=\"");
                    stringBuilder.append(t.msgstr);
                    stringBuilder.append("\",\n\t},\n");

                }
                stringBuilder.append("\n}");
                WriteFile.writeFile(filePath, stringBuilder.toString());
            }
            WriteFile.writeFile("/tmp/dstwiki/Map.lua", mapList.toString());
            WriteFile.writeFile("/tmp/dstwiki/Map", mapList, p->{
                String s = p.getSecond();
                return s.endsWith("_")?
                        s.substring(0,s.length()-1).replace('.','_'):
                        s.replace('.','_');
            });
        }
        private static final int SINGLE_LIST_MAX = 3000;
        private List<Pair<String, List<Translation>>> singleMap(Translation[] from, Function<Translation, String> stringGetter){
            List<Pair<String, List<Translation>>> toAdd = new ArrayList<>();
            int pointStart = 0;
            while (true){
                int pointCurr = pointStart;
                String curr = stringGetter.apply(from[pointCurr]);

                int pointLast = -1;
                String last = null;

                while (true){
                    String tmp = getSameStart(curr, stringGetter.apply(from[pointCurr+1]));
                    if(!tmp.equals(curr)){
                        pointLast = pointCurr;
                        last = curr;
                        curr = tmp;
                    }
                    pointCurr++;

                    if(pointCurr-pointStart > SINGLE_LIST_MAX){
                        assert last!=null;
                        assert pointLast>0;
                        List<Translation> list = Arrays.asList(
                                Arrays.copyOfRange(from, pointStart, pointLast + 1)
                        );
                        toAdd.add(new Pair<>(last,list));
                        pointStart = pointLast + 1;
                        System.out.println(last);
                        break;
                    }

                    if(pointCurr == from.length -1){
                        List<Translation> list = Arrays.asList(
                                Arrays.copyOfRange(from, pointLast, pointCurr)
                        );
                        toAdd.add(new Pair<>(last,list));
                        return toAdd;
                    }
                }
            }
        }

        private void margeMap(List<Pair<String, List<Translation>>> map){
            for (int i = 0; i < map.size() - 1; i++) {
                Pair<String, List<Translation>> curr = map.get(i);
                Pair<String, List<Translation>> next = map.get(i + 1);
                if(curr.getSecond().size()+next.getSecond().size()>SINGLE_LIST_MAX) continue;
                map.remove(next);
                map.remove(curr);
                map.add(i,marge(curr,next));
            }
        }
        private Pair<String, List<Translation>> marge(Pair<String, List<Translation>> a, Pair<String, List<Translation>> b) {
            ArrayList<Translation> l = new ArrayList<>();
            var f = a.getSecond();
            var s = b.getSecond();
            l.addAll(f);
            l.addAll(s);

            return new Pair<>(a.getFirst(), l);
        }

        public void add(Translation t){
            totalCodeList[length]=t;
            length++;

            if(t.msgid.length()>0) {
                totalEnList[enLength] = t;
                enLength++;
            }
        }
        public int length(){
            return length;
        }
    }
    @Deprecated
    public static void main(String[] args){
        throw new RuntimeException("Deprecated, use PoSorter.");
        /**
        TranslationManager translationManager = new TranslationManager();
        File dstFile = new File("/tmp/dstScripts/scripts/languages/chinese_s.po");
        File dsFile = new File("/tmp/dstScripts/chinese_s.po");

        for (File file:new File[]{dsFile,dstFile}) {

            try (Source source = Okio.source(file);
                 BufferedSource buffer = Okio.buffer(source)) {
                while (true) {
                    String s = buffer.readUtf8Line();
                    if (s == null) break;
                    if (s.startsWith("msgctxt")) {
                        String msgctxt = s;
                        String msgid = buffer.readUtf8Line();
                        String msgstr = buffer.readUtf8Line();
                        Translation translation = new Translation(msgctxt, msgid, msgstr, file.equals(dstFile));
                        translationManager.add(translation);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("got "+translationManager.length()+" translations, sorting...");
        translationManager.sort();
        System.out.println("mapping...");

        translationManager.map();
         **/
    }

}
