package com.NaAlOH4;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Log {
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    static boolean logServerStarted = false;
    static void startLogServer() {
        assert !logServerStarted;
        new Thread(() -> {
            long time = System.currentTimeMillis();
            if ((time - updateTime > 5000 && !logs.isEmpty()) || needFlush) {
                updateTime = time;
                needFlush = false;
                printLog();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }).start();

        logServerStarted = true;
    }

    private static void printLog() {
        if(logs.isEmpty())return;
        StringBuilder stringBuilder = new StringBuilder(logs.poll());
        while (!logs.isEmpty()){
            stringBuilder.append('\n');
            stringBuilder.append(logs.poll());
        }
        String s = stringBuilder.toString();

    }

    static BlockingQueue<String> logs = new LinkedBlockingQueue<>();
    static long updateTime = 0;
    static boolean needFlush = false;
    public static void log(String s) {
        if (s == null) {
            log("null");
            return;
        }
        Log:
        {
            System.out.println(s);
//            if(!logServerStarted)
//                startLogServer();
//            System.out.println(s);
//            logs.add(s);
//            updateTime = System.currentTimeMillis();
        }
    }


    public static void log(Object ... o){
        log(new Gson().toJson(o));
    }

    public static void flush() {
        needFlush = true;
    }
}

