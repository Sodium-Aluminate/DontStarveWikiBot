package com.NaAlOH4;

import com.NaAlOH4.IO.ReadFile;
import okhttp3.HttpUrl;
import org.fastily.jwiki.core.Wiki;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.NaAlOH4.Log.log;

public class UpdateTemplateSmallIconAndText {

    private static Wiki wiki;

    public static void main(String[] args) {

        wiki = new Wiki.Builder().withApiEndpoint(HttpUrl.parse("https://dontstarve.fandom.com/zh/api.php")).build();
        Map<String, String> env = System.getenv();
        boolean isLogin = wiki.login(env.get("WIKI_USERNAME"), env.get("WIKI_PASSWD"));
        if (!isLogin) {
            log("login failed, exit");
            System.exit(1);
            return;
        }





        while (true) {
            String nameTemplateCodes = ReadFile.ReadFile("/tmp/nameTemplate-Codes.txt");

            for (String s:nameTemplateCodes.split("\n")) {
                applyNameModule(s);
            }

            String nameTemplateReq = wiki.getPageText("User:NaAlOH4/NameTemplateReq");
            //System.out.println(nameTemplateReq);
            String[] splitPage = nameTemplateReq.split("= ?请求 ?=");
            if (splitPage.length != 2) {
                log("找不到请求标签，检查页面是否坏掉了？");
            } else {
                boolean nothingTodo = true;
                for (String[] s : readSourceString(splitPage[1])) {
                    applyNameModule(s);
                    nothingTodo = false;
                }

                if (nothingTodo) {
                    log("nothing to do");
                } else {
                    String toOverride = splitPage[0] + "= 请求 =" + deleteSourceString(splitPage[1]);
                    // System.out.println(toOverride);
                    wiki.edit("User:NaAlOH4/NameTemplateReq", toOverride, "相应请求");
                }
            }

            sleep(10000);
        }


    }




    /**
     * @param s code
     */
    private static void applyNameModule(String s) {
        applyNameModule(new String[]{s,null,null});
    }

    /**
     * @param s s[0] code
     *          s[1] en
     *          s[2] zh
     */
    private static void applyNameModule(String[] s) {

        assert s.length == 3;
        Log.flush();
        String msgctxt = s[0].toLowerCase();
        String en = s[1];
        String zh = s[2];
        log("appling: ", s);
        if (wiki.exists("模板:" + msgctxt)) {
            log("exist, pass");
        } else {
            String text = "{{SmallIconAndText|" + msgctxt + "}}";
            log("apply: " + text);
            boolean b = wiki.edit("模板:" + msgctxt, text, "自动套用模板");
            if (b) {
                log("success: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + msgctxt);
            } else {
                log("failed: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + msgctxt);
            }
        }
        if(en!=null) {
            System.out.println("appling: " + en);
            if (wiki.exists("模板:" + en)) {
                log("exist, pass");
            } else {
                String text = "#重定向 [[" + "模板:" + msgctxt + "]]";
                log("apply: " + text);
                boolean b = wiki.edit("模板:" + en, text, "自动套用模板");
                if (b) {
                    log("success: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + en);
                } else {
                    log("failed: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + en);
                }
            }
        }
        if(zh!=null) {
            System.out.println("appling: " + zh);
            if (wiki.exists("模板:" + zh)) {
                log("exist, pass");
            } else {
                String text = "#重定向 [[" + "模板:" + msgctxt + "]]";
                log("apply: " + text);
                boolean b = wiki.edit("模板:" + zh, text, "自动套用模板");
                if (b) {
                    log("success: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + zh);
                } else {
                    log("failed: https://dontstarve.fandom.com/zh/wiki/" + "模板:" + zh);
                }
            }
        }
        Log.flush();
    }

    private static List<String[]> readSourceString(String source) {

        ArrayList<String[]> result = new ArrayList<>();
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String s = lines[i];
            if (s.startsWith("msgctxt \"STRINGS.NAMES.") && s.endsWith("\"")) {
                String[] cache = new String[3];
                s = s.replaceAll(".*NAMES\\.", "").replaceAll("\"", "");
                cache[0] = s;
                i++;
                s = lines[i];
                if (!(s.startsWith("msgid \"") && s.endsWith("\""))) {
                    log("fount a error format: ");
                    log(lines[i - 1], lines[i], lines[i + 1]);
                    continue;
                }
                s = s.replaceAll("msgid \"", "").replaceAll("\"", "");
                cache[1] = s;
                i++;
                s = lines[i];
                if (!(s.startsWith("msgstr \"") && s.endsWith("\""))) {
                    log("fount a error format: ");
                    log(lines[i - 2], lines[i - 1], lines[i]);
                    continue;
                }
                s = s.replaceAll("msgstr \"", "").replaceAll("\"", "");
                cache[2] = s;
                result.add(cache);
            }
        }
        return result;
    }
    private static String deleteSourceString(String source) {
        return source
                .replaceAll("msgctxt \".*\"\\nmsgid \".*\"\\nmsgstr \".*\"","")
                .replaceAll("\n{2,}", "\n\n");

    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

}