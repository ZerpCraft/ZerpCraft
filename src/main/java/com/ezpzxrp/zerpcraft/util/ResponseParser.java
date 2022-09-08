package com.ezpzxrp.zerpcraft.util;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

import javax.validation.constraints.Null;


public class ResponseParser {

    public static JSONObject stringToJSON(String object)  {

        JSONObject json = null;
        try {

            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(object);
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return json;
    }

    public static String getValueFromJsonKeypath(JSONObject object, String path) {

        String[] pathValues = path.split("\\.");
        String value = null;
        try {

            for (int i = 0; i < pathValues.length - 1; i++) {

                object = (JSONObject) object.get(pathValues[i]);
            }
            value = (String) object.get(pathValues[pathValues.length - 1]);
        }
        catch (NullPointerException e) {

            return null;
        }

        return value;
    }

    public static boolean getBooleanFromJsonKeypath(JSONObject object, String path) {

        String[] pathValues = path.split("\\.");
        for (int i = 0; i < pathValues.length - 1; i++) {

            object = (JSONObject) object.get(pathValues[i]);
        }
        boolean value = (Boolean) object.get(pathValues[pathValues.length - 1]);

        return value;
    }

    public static JSONArray getArrayFromJsonKeypath(JSONObject object, String path) {

        String[] pathValues = path.split("\\.");
        for (int i = 0; i < pathValues.length - 1; i++) {

            object = (JSONObject) object.get(pathValues[i]);
        }
        JSONArray value = (JSONArray) object.get(pathValues[pathValues.length - 1]);

        return value;
    }

    public static long getNumberFromJsonKeypath(JSONObject object, String path) {

        String[] pathValues = path.split("\\.");
        for (int i = 0; i < pathValues.length - 1; i++) {

            object = (JSONObject) object.get(pathValues[i]);
        }
        long value = (long) object.get(pathValues[pathValues.length - 1]);

        return value;
    }

    public static String decodeDomain(JSONObject object) {

        String domain = getValueFromJsonKeypath(object, "result.account_data.Domain");
        byte[] bytes = new byte[0];
        String decodedDomain = "";
        try {

            bytes = Hex.decodeHex(domain.toCharArray());
            decodedDomain = new String(bytes, "UTF-8");
        }
        catch (Exception e) {

            System.out.println("No domain for this player");
        }
        return decodedDomain;
    }
}
