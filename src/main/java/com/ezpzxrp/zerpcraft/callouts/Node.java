package com.ezpzxrp.zerpcraft.callouts;

import org.json.simple.parser.JSONParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Node {

    public static JSONArray staticResponse() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("http://172.29.25.5:8080")
                .build();
        try {

            Response response = client.newCall(request).execute();
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(response.body().string());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
