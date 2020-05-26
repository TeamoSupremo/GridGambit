package com.example.gridgambit;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataManager {
    // method to update player info
    public static void savePlayerInfo(Context context){
        FileOutputStream out;
        // get the current player data
        File file = new File(context.getFilesDir(), "/" + "player_info");
        StringBuilder achievements = new StringBuilder();
        // update player file with new values
        try {
            out = new FileOutputStream(file, false);
            for(int i = 0; i < Player.PlayerInfo.achievements.size(); i++){
                achievements.append(",");
                achievements.append(Player.PlayerInfo.achievements.values().toArray()[i]);
            }
            String fileContents = Player.PlayerInfo.level + "," +
                    Player.PlayerInfo.levelHighest +
                    achievements;

            byte[] contents = fileContents.getBytes();
            out.write(contents);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to load the levels from json file
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
                // load challenge mode if endless has not been selected
                if(!Player.PlayerInfo.isEndless) {
                    Level.LevelInfo.levelArray = jsonObject.getJSONArray("challenge");
                }
                else{
                    Level.LevelInfo.levelArray = jsonObject.getJSONArray("endless");
                }
                // level info cannot be null
                assert Level.LevelInfo.levelArray != null;
                levelJSON = Level.LevelInfo.levelArray.getJSONObject(Player.PlayerInfo.level);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return levelJSON;
    }
}
