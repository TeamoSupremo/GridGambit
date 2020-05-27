package com.example.gridgambit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.gridgambit.Player.PlayerInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SoundPool sp;
    PlayerInfo pi = new PlayerInfo();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create SoundPool
        sp = new SoundPool.Builder().setMaxStreams(5).build();
        // Create a file for player info
        File file = new File(this.getFilesDir(), "player_info");
        Level.LevelInfo.menuClickId = sp.load(this, R.raw.match_made, 1);
        FileInputStream is;
        // Import player info
        try {

            // Get file and store contents in string
            is = openFileInput(file.getName());
            StringBuffer fileContent = new StringBuffer();
            InputStreamReader isr = new InputStreamReader(is) ;
            BufferedReader buffReader = new BufferedReader(isr) ;
            String readString = buffReader.readLine() ;

            // While there's another line to read in file
            while (readString != null)
            {
                // Append line to buffer
                fileContent.append(readString);
                readString = buffReader.readLine();
            }

            String unfilteredPlayerData = String.valueOf(fileContent);
            String[] playerData = unfilteredPlayerData.split(",");

            // Assign loaded content to player variables
            PlayerInfo.level = Integer.parseInt(playerData[0]);
            PlayerInfo.levelHighest = Integer.parseInt(playerData[1]);

            // Load players achievements
            for(int i = 0; i < PlayerInfo.achievements.size(); i++){
                StringBuilder objectConverter = new StringBuilder();
                objectConverter.append(Objects.requireNonNull(PlayerInfo.achievements.keySet().toArray())[i]);
                String stringBoolean = String.valueOf(objectConverter);
                PlayerInfo.achievements.put(stringBoolean, Boolean.parseBoolean(playerData[i+2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChallengeMode(View view) {
        sp.play(Level.LevelInfo.menuClickId, PlayerInfo.soundLevel, PlayerInfo.soundLevel, 0, 0, 1);
        Player.PlayerInfo.isEndless = false;
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
    }

    public void loadAchievementsMenu(View view){
        sp.play(Level.LevelInfo.menuClickId, PlayerInfo.soundLevel, PlayerInfo.soundLevel, 0, 0, 1);
        Intent intent = new Intent(this, AchievementsScreen.class);
        startActivity(intent);
    }

    public void loadLevelMenuEasy(View view){
        sp.play(Level.LevelInfo.menuClickId, PlayerInfo.soundLevel, PlayerInfo.soundLevel, 0, 0, 1);
        Player.PlayerInfo.isEndless = false;
        Intent intent = new Intent(this, LevelMenu.class);
        startActivity(intent);
    }

    public void loadEndlessMode(View view) {
        sp.play(Level.LevelInfo.menuClickId, PlayerInfo.soundLevel, PlayerInfo.soundLevel, 0, 0, 1);
        Player.PlayerInfo.isEndless = true;
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void muteSound(View view){
        SoundManager.muteSound(view, this, getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onResume() {
        super.onResume();
        Button levelButton = findViewById(R.id.levelButton);
        // Display current level
        levelButton.setText(String.valueOf(Player.PlayerInfo.level + 1));
        Button soundButton = findViewById(R.id.sound_button);
        if (PlayerInfo.soundLevel == 1) {
            soundButton.setBackground(getDrawable(R.drawable.sound));

        } else {
            soundButton.setBackground(getDrawable(R.drawable.sound_muted));
        }
    }
}
