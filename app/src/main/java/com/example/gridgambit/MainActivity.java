package com.example.gridgambit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    public void onResume() {
        super.onResume();
        Button levelButton = findViewById(R.id.levelButton);
        // Display current level
        levelButton.setText(String.valueOf(Player.PlayerInfo.level + 1));
        // TODO: Add sound button info
    }
}
