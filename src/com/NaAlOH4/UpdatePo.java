package com.NaAlOH4;

import okhttp3.HttpUrl;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.fastily.jwiki.core.Wiki;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpdatePo {
    public static String REASON = "update po";

    public static void main(String[] args) {
        Wiki wiki = new Wiki.Builder().withApiEndpoint(HttpUrl.parse("https://dontstarve.fandom.com/zh/api.php")).build();

        Map<String, String> env = System.getenv();
        boolean isLogin = wiki.login(env.get("WIKI_USERNAME"), env.get("WIKI_PASSWD"));

        if (!isLogin) return;
        System.out.println(readFile("/tmp/dstwiki/Map.lua"));
//        sleep(5000);
        List<String> files = readFileAsArray("/tmp/dstwiki/Map");
        for (String s : files) {
            wiki.edit("Module:" + s, readFile("/tmp/dstwiki/" + s + ".lua"), REASON);
            // sleep(2000);
        }

        wiki.edit("Module:Map", readFile("/tmp/dstwiki/Map.lua"), REASON);

    }

    private static String readFile(String path){
        File file = new File(path);
        StringBuilder stringBuilder = new StringBuilder();
        try (Source source = Okio.source(file);
             BufferedSource buffer = Okio.buffer(source)) {
            while (true) {
                String s = buffer.readUtf8Line();
                if (s == null) break;
                stringBuilder.append(s).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            sleep(3000);
        }
        return stringBuilder.toString();
    }

    private static List<String> readFileAsArray(String path){
        File file = new File(path);
        LinkedList<String> strings = new LinkedList<>();
        try (Source source = Okio.source(file);
             BufferedSource buffer = Okio.buffer(source)) {
            while (true) {
                String s = buffer.readUtf8Line();
                if (s == null) break;
                strings.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
            sleep(3000);
        }
        return strings;
    }



    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
