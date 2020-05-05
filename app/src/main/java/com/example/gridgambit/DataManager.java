package com.example.gridgambit;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class DataManager {
    public static JSONObject loadLevels(Context context){

        String json = null;

        try {
            InputStream is = context.getAssets().open("levels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            System.out.println(bytesRead);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        JSONObject levelJSON = null;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            try {
                if(!Player.PlayerInfo.isEndless) {
                    Level.LevelInfo.levelArray = jsonObject.getJSONArray("challenge");
                }
                else{
                    Level.LevelInfo.levelArray = jsonObject.getJSONArray("endless");
                }

                assert Level.LevelInfo.levelArray != null;
                levelJSON = Level.LevelInfo.levelArray.getJSONObject(Player.PlayerInfo.level);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return levelJSON;
    }
}
