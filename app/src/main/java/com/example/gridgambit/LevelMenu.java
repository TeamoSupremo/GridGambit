package com.example.gridgambit;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.example.gridgambit.Player.PlayerInfo;
import com.example.gridgambit.R;

public class LevelMenu extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_menu);
        String json = null;

        // Load the levels from levels.json
        try
        {
            InputStream is = getAssets().open("levels.json");
            int size = is.available();
            byte[] buffer  = new byte[size];
            int numBytes = is.read(buffer);
            System.out.println(numBytes);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        // if error, print
        catch (IOException e)
        {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray difficulty = null;

        if (jsonObject != null) {
            try {
                difficulty = jsonObject.getJSONArray("challenge");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int levelsCompleted = 0;

        // get the player's current progress
        if(!PlayerInfo.isEndless) {
            levelsCompleted = PlayerInfo.levelHighest + 1;
        }

        final LinearLayout levelLayout = findViewById(R.id.level_layout);

        // Create the level layout
        assert difficulty != null;
        for(int i = 0; i < difficulty.length(); i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final Button levelButton = new Button(this);
            levelButton.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.level_text), (i+1)));
            levelButton.setId(i);
            levelButton.setLayoutParams(params);
            levelButton.setBackground(getDrawable(R.drawable.text_list_layout));
            levelButton.setTextColor(Color.parseColor("#dddddd"));
            levelButton.setTextSize(30);
            levelLayout.addView(levelButton);

            // Allow only completed/current level to be clickable
            if(i > levelsCompleted){
                levelButton.setClickable(false);
                levelButton.setBackground(getDrawable(R.drawable.unclickable_button_nocolour));
            }
            else{
                final JSONArray finalDifficulty = difficulty;
                levelButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        if(!PlayerInfo.isEndless) {
                            if (PlayerInfo.level < finalDifficulty.length()) {
                                Button tempButton = (Button) levelLayout.getChildAt(PlayerInfo.level);
                                tempButton.setBackgroundTintList(null);
                                System.out.println(tempButton.getText());
                            }
                            PlayerInfo.level = v.getId();
                            v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
                        }
                    }
                });
            }

            if(!PlayerInfo.isEndless) {
                if(i == PlayerInfo.level){
                    levelButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
                }
            }
        }
    }
}