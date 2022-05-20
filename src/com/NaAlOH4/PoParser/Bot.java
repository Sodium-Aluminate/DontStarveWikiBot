package com.NaAlOH4.PoParser;

import okhttp3.HttpUrl;
import org.fastily.jwiki.core.Wiki;

public class Bot {
    public static void main(String[] args) {
        Wiki wiki = new Wiki.Builder()
                .withApiEndpoint(HttpUrl.parse("https://dontstarve.fandom.com/zh/api.php"))
                //.withDomain("dontstarve.fandom.com/zh")
                .withLogin("NaAlOH4","10c7f8fbf8")
                .build();
    }
}
