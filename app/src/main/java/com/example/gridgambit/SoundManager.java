package com.example.gridgambit;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

public class SoundManager {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void muteSound(View view, Activity activity, Context context){

        Button soundButton = activity.findViewById(R.id.sound_button);

        System.out.println("test");
        // mute sound if sound is on, otherwise play sound
        if(Player.PlayerInfo.soundLevel == 1) {
            Player.PlayerInfo.soundLevel = 0;
            soundButton.setBackground(context.getDrawable(R.drawable.sound_muted));
        }
        else{
            Player.PlayerInfo.soundLevel = 1;
            soundButton.setBackground(context.getDrawable(R.drawable.sound));
        }
    }
}

