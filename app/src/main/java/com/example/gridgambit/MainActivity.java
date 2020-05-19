package com.example.gridgambit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.gridgambit.Player.PlayerInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadChallengeMode(View view) {
        Player.PlayerInfo.isEndless = false;
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
    }

    public void loadAchievementsMenu(View view){
        Intent intent = new Intent(this, AchievementsScreen.class);
        startActivity(intent);
    }

    public void loadLevelMenuEasy(View view){
        Player.PlayerInfo.isEndless = false;
        Intent intent = new Intent(this, LevelMenu.class);
        startActivity(intent);
    }

    public void loadEndlessMode(View view) {
        Player.PlayerInfo.isEndless = true;
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void muteSound(View view){

        Button soundButton = findViewById(R.id.sound_button);

        if(PlayerInfo.soundLevel == 1) {
            PlayerInfo.soundLevel = 0;
            soundButton.setBackground(getDrawable(R.drawable.sound_muted));
        }
        else{
            PlayerInfo.soundLevel = 1;
            soundButton.setBackground(getDrawable(R.drawable.sound));
        }
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
