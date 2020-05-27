package com.example.gridgambit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.gridgambit.Level.LevelInfo;
import com.example.gridgambit.Player.PlayerInfo;
import org.json.JSONException;

import java.util.Locale;

public class GameScreen extends AppCompatActivity {

    Level levelUI;
    private static final float ITEM_MARGIN_MODIFIER = 0.05f;
    private static final float ITEM_MARGIN_MODIFIER_TOP = 0.1f;
    private static final float GAME_MARGIN_MODIFIER = 0.025f;
    private Activity gs;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gs = this;
        super.onCreate(savedInstanceState);

        // Create object for playing sounds
        LevelInfo.soundPool = new SoundPool.Builder().setMaxStreams(5).build();

        //Load sounds from resource IDs
        LevelInfo.matchMadeSoundId = LevelInfo.soundPool.load(this, R.raw.match_made, 1);
        LevelInfo.gridItemStaticFailSoundId = LevelInfo.soundPool.load(this, R.raw.match_two, 1);
        LevelInfo.powerActivatedSoundId = LevelInfo.soundPool.load(this, R.raw.power_up_two_trimmed, 1);
        LevelInfo.powerDeactivatedSoundId = LevelInfo.soundPool.load(this, R.raw.power_up_three_trimmed, 1);
        LevelInfo.powerReadySoundId = LevelInfo.soundPool.load(this, R.raw.power_up_ready, 1);
        LevelInfo.powerUsedSoundId = LevelInfo.soundPool.load(this, R.raw.match_three, 1);
        LevelInfo.winSoundId = LevelInfo.soundPool.load(this, R.raw.win_one, 1);
        LevelInfo.loseSoundId = LevelInfo.soundPool.load(this, R.raw.lose, 1);

        // Reset power-ups charge
        Player.PlayerInfo.powerCharge = 0;

        // Reset players score
        Level.LevelInfo.currentScore = 0;

        // Assign UI references
        levelUI = new Level();
        setContentView(R.layout.activity_game_screen);
        levelUI.gameLayout = findViewById(R.id.game_layout);
        levelUI.infoLayout = findViewById(R.id.info_ui_layout);
        levelUI.turnsText = findViewById(R.id.turns_text);
        levelUI.levelText = findViewById(R.id.level_text);
        levelUI.scoreText = findViewById(R.id.score_text);
        levelUI.targetText = findViewById(R.id.target_text);
        levelUI.powerChargeBar = findViewById(R.id.charge_progress_bar);
        levelUI.turnsLevelRow = (LinearLayout) levelUI.turnsText.getParent();
        levelUI.scoreRow = (LinearLayout) levelUI.scoreText.getParent();
        levelUI.soundButton = findViewById(R.id.sound_button);

        // Draw sound icon as regular or muted
        // Regular/muted depends on player preference "sound level"
        if (PlayerInfo.soundLevel == 1) {
            levelUI.soundButton.setBackground(getDrawable(R.drawable.sound));
        } else {
            levelUI.soundButton.setBackground(getDrawable(R.drawable.sound_muted));
        }

        // Remove UI elements if endless mode is on
        if(Player.PlayerInfo.isEndless){
            Player.PlayerInfo.level = 0;
            levelUI.turnsLevelRow.removeView(levelUI.turnsText);
            levelUI.turnsLevelRow.removeView(levelUI.levelText);
            levelUI.scoreRow.removeView(levelUI.targetText);
        }

        // Load information relating to current level being played.
        levelUI.data = DataManager.loadLevels(getApplicationContext());

        try {
            // Assign level info from JSON
            Level.LevelInfo.grid = levelUI.data.getJSONArray("value");
            Level.LevelInfo.currentTurns = levelUI.data.getInt("turns");
            Level.LevelInfo.targetScore = levelUI.data.getInt("score");
            GridUtil.updateUI(levelUI, getApplicationContext());

            // Font used for grid items
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "font/akashi.ttf");

            // If the square root of the total number of values is an integer
            if (Math.sqrt(Level.LevelInfo.grid.length()) ==
            (int) Math.sqrt(Level.LevelInfo.grid.length()))
            {
                // Assign square root as grid size
                Level.LevelInfo.gridSize = (int) Math.sqrt(Level.LevelInfo.grid.length());
            }

            // Get display metrics for dynamic grid sizing
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            // Set grid items to be 80% of screen width
            int newWidth = (int) (width * 0.8) / Level.LevelInfo.gridSize;
            int newHeight = (int) (width * 0.8) / Level.LevelInfo.gridSize;
            int textSize = (int) (newWidth / 5);


            // Assign info UI params
            levelUI.infoLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(newWidth, newHeight);

            // Create grid rows
            for (int i = 0; i < Level.LevelInfo.gridSize; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(params);
                row.setOrientation(LinearLayout.HORIZONTAL);

                // Fill grid rows
                for (int j = 0; j < Level.LevelInfo.gridSize; j++) {
                    GridTextView gridItem = new GridTextView(this);

                    // Set text from data loaded in JSON file
                    gridItem.setText(String.format(
                            Locale.getDefault(),
                            "%d",
                            Level.LevelInfo.grid.getInt(j + (i * Level.LevelInfo.gridSize)))
                    );
                    gridItem.setTextColor(Color.parseColor("#1f1f2a"));
                    // Center text
                    gridItem.setGravity(Gravity.CENTER);
                    gridItem.setTextSize(textSize);

                    // TODO: fix text size using "setAutoSizeTextTypeUniformWithConfiguration"
                    //gridItem.setAutoSizeTextTypeUniformWithConfiguration(textSize / 3, textSize, 15, TypedValue.COMPLEX_UNIT_PX);

                    // Stop numbers from appearing on a new line
                    gridItem.setMaxLines(1);

                    // Set font to Akashi
                    gridItem.setTypeface(face);

                    // Set spacing between each grid item
                    gridItem.setLayoutParams(itemParams);
                    itemParams.leftMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / Level.LevelInfo.gridSize);
                    itemParams.rightMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / Level.LevelInfo.gridSize);
                    itemParams.topMargin = (int) ((width * ITEM_MARGIN_MODIFIER_TOP) / Level.LevelInfo.gridSize);
                    gridItem.setBackground(getDrawable(R.drawable.grid_item));

                    // Add colour overlay for changing grid item colour
                    // Colour will change based on how high value of grid item is
                    gridItem.getBackground().setColorFilter(
                            Color.rgb(
                                    gridItem.redInBackground,
                                    gridItem.greenInBackground,
                                    gridItem.blueInBackground
                            ),
                            PorterDuff.Mode.OVERLAY
                    );

                    // Set grid item position
                    gridItem.xLocation = j;
                    gridItem.yLocation = i;

                    // Set grid item listeners
                    gridItem.setOnTouchListener(new gridTouchListener());
                    gridItem.setOnDragListener(new gridDragListener());

                    GridUtil.updateGridItemColour(gridItem);
                    row.addView(gridItem);
                    row.setBaselineAligned(false);
                }
                levelUI.gameLayout.addView(row);
            }

            // Set margin and padding for container of grid items
            ConstraintLayout.LayoutParams paramsGameLayout =
                    (ConstraintLayout.LayoutParams) levelUI.gameLayout.getLayoutParams();

            paramsGameLayout.leftMargin = (int) (width * GAME_MARGIN_MODIFIER);
            paramsGameLayout.rightMargin = (int) (width * GAME_MARGIN_MODIFIER);
            paramsGameLayout.bottomMargin = (int) (width * GAME_MARGIN_MODIFIER);

            levelUI.gameLayout.setPadding(
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER));

            levelUI.gameLayout.setLayoutParams(paramsGameLayout);

            // Catch and print problems with reading JSON file
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get reference to charge bar and parent
        LinearLayout chargeBar = findViewById(R.id.charge_bar_layout);
        LinearLayout chargeBarParent = (LinearLayout) chargeBar.getParent();

        // Levels < 4 do not require charge bar, so charge bar is hidden to reduce complexity
        if(Player.PlayerInfo.level < 4 && !Player.PlayerInfo.isEndless) {
            chargeBarParent.removeView(chargeBar);
        }
        //if player is passed level 3 then add the charge bar back
        if(Player.PlayerInfo.level >= 3 && chargeBar.getParent() == null){
            chargeBarParent.addView(chargeBar);
        }
    }

    // On touch listener for grid item
    private static final class gridTouchListener implements View.OnTouchListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint({"ClickableViewAccessibility"})
        @Override

        // On touching grid item
        public boolean onTouch(View v, MotionEvent event) {
            // If the event is touching down on grid item
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Start drag event
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        }
    }

    private final class gridDragListener implements View.OnDragListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onDrag(View v, DragEvent event) {

            // grid item being dragged
            GridTextView movingSquare = (GridTextView) event.getLocalState();

            // grid item being dragged over
            GridTextView goalSquare;
            final int action = event.getAction();

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // If this is the first time the event is running for the drag
                    if (event.getClipDescription().hasMimeType(
                            ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // Play sound and update background
                        LevelInfo.soundPool.play(LevelInfo.gridItemStaticSoundId, PlayerInfo.soundLevel, PlayerInfo.soundLevel, 0, 0, 1);
                        updateBackgroundAndFilter(getDrawable(R.drawable.grid_item_good), movingSquare);
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Assign reference to view being dragged over
                    goalSquare = (GridTextView) v;
                    // Take not that an extra grid item is now being matched with
                    int nextMatchNumber = movingSquare.matches + 1;
                    switch (movingSquare.matches) {
                        case 0:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare,
                                    goalSquare,
                                    nextMatchNumber
                            )) {
                                // Add green border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_good),
                                        movingSquare
                                );
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_good),
                                        goalSquare
                                );

                                // References to matching items
                                movingSquare.firstMatch = goalSquare.getValue();
                                movingSquare.firstMatchObject = goalSquare;
                                movingSquare.matches++;

                                // Play matching sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                                return true;
                            } else {
                                // If the first grid item being matched is not the dragging item
                                if (goalSquare != movingSquare) {

                                    // Add red border to grid item
                                    updateBackgroundAndFilter(
                                            getDrawable(R.drawable.grid_item_bad),
                                            goalSquare
                                    );

                                    LevelInfo.soundPool.play(LevelInfo.gridItemStaticFailSoundId,
                                            PlayerInfo.soundLevel,
                                            PlayerInfo.soundLevel,
                                            0,
                                            0,
                                            1
                                    );
                                }
                            }
                            break;
                        case 1:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare.firstMatchObject,
                                    goalSquare,
                                    nextMatchNumber
                            )) {
                                // Add green border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_good),
                                        goalSquare
                                );

                                // References to matching items
                                movingSquare.secondMatch = goalSquare.getValue();
                                movingSquare.secondMatchObject = goalSquare;
                                movingSquare.matches++;

                                // Play matching sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                                return true;
                                // Else match cannot be made
                            } else {
                                // Remove green border
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item)
                                        , movingSquare.firstMatchObject
                                );
                                // Add red border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_bad),
                                        goalSquare
                                );
                                movingSquare.matches--;

                                // Play match fail sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticFailSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                            }
                            break;
                        case 2:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare.secondMatchObject,
                                    goalSquare,
                                    nextMatchNumber
                            )) {
                                // Add green border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_good),
                                        goalSquare
                                );
                                // References to matching items
                                movingSquare.thirdMatch = goalSquare.getValue();
                                movingSquare.thirdMatchObject = goalSquare;
                                movingSquare.matches++;

                                // Play matching sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                                return true;
                            } else {

                                // Remove green border
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item),
                                        movingSquare.firstMatchObject
                                );

                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item),
                                        movingSquare.secondMatchObject
                                );

                                // Add red border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_bad),
                                        goalSquare
                                );
                                movingSquare.matches--;

                                // Play matching failed sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticFailSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                            }
                            break;
                        case 3:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare.thirdMatchObject,
                                    goalSquare,
                                    nextMatchNumber
                            )) {
                                // Add green border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_good),
                                        goalSquare
                                );
                                movingSquare.matches++;

                                // Play matching sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                                return true;
                            } else {
                                // Remove green border
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item),
                                        movingSquare.firstMatchObject
                                );
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item),
                                        movingSquare.secondMatchObject
                                );
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item),
                                        movingSquare.thirdMatchObject
                                );

                                // Add red border to grid items being matched
                                updateBackgroundAndFilter(
                                        getDrawable(R.drawable.grid_item_bad),
                                        goalSquare
                                );
                                movingSquare.matches--;

                                // Play matching failed sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.gridItemStaticFailSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );
                            }
                            return true;
                    }
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                    goalSquare = (GridTextView) v;

                    // if player is not dragging back towards matching items
                    if (goalSquare != movingSquare.firstMatchObject &&
                            goalSquare != movingSquare.secondMatchObject &&
                            goalSquare != movingSquare.thirdMatchObject
                    ) {
                        // Remove green border
                        updateBackgroundAndFilter(
                                getDrawable(R.drawable.grid_item),
                                goalSquare
                        );
                    }
                    return true;

                case DragEvent.ACTION_DROP:
                    goalSquare = (GridTextView) v;

                    switch (movingSquare.matches) {
                        case 1:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare,
                                    goalSquare))
                            {
                                // The player has used a turn
                                Level.LevelInfo.currentTurns--;

                                // Play match made sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.matchMadeSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );

                                // Total value for element and score
                                int sequence = movingSquare.getValue() + movingSquare.firstMatch;
                                String sequenceString = sequence + "";

                                // Change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;

                                // Update grid item colour
                                GridUtil.updateUI(levelUI, getApplicationContext());
                                GridUtil.updateGridItemColour(movingSquare);
                                GridUtil.updateGridItemColour(goalSquare);
                            }
                            break;
                        case 2:
                            // If match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare.firstMatchObject,
                                    goalSquare
                            )) {
                                // Player has used a turn
                                Level.LevelInfo.currentTurns--;

                                // Play match made sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.matchMadeSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );

                                // Total value for element and score
                                int sequence = movingSquare.getValue() +
                                        movingSquare.firstMatch +
                                        movingSquare.secondMatch;

                                String sequenceString = sequence + "";
                                // Change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;

                                // Update grid items colour
                                GridUtil.updateUI(levelUI, getApplicationContext());
                                GridUtil.updateGridItemColour(goalSquare);
                                GridUtil.updateGridItemColour(movingSquare);
                                GridUtil.updateGridItemColour(movingSquare.firstMatchObject);
                            }
                            break;

                        case 3:
                            // If a match can be made
                            if (GridUtil.matchCanBeMadeWith(
                                    movingSquare,
                                    movingSquare.secondMatchObject,
                                    goalSquare
                            )) {
                                // Player has used a turn
                                Level.LevelInfo.currentTurns--;

                                // Play match made sound
                                LevelInfo.soundPool.play(
                                        LevelInfo.matchMadeSoundId,
                                        PlayerInfo.soundLevel,
                                        PlayerInfo.soundLevel,
                                        0,
                                        0,
                                        1
                                );

                                // Total value for element and score
                                int sequence = movingSquare.getValue() +
                                        movingSquare.firstMatch +
                                        movingSquare.secondMatch +
                                        movingSquare.thirdMatch;
                                String sequenceString = sequence + "";

                                // Change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;

                                // Update grid items colour
                                GridUtil.updateUI(levelUI, getApplicationContext());
                                GridUtil.updateGridItemColour(goalSquare);
                                GridUtil.updateGridItemColour(movingSquare);
                                GridUtil.updateGridItemColour(movingSquare.firstMatchObject);
                                GridUtil.updateGridItemColour(movingSquare.secondMatchObject);
                            }
                            break;
                    }
                    // If charge is full and player hasn't activated power-up
                    if (levelUI.powerChargeBar.getProgress() == 100 && !Player.PlayerInfo.powerActivated) {
                        // Switch background
                        Button powerButton = findViewById(R.id.power_charge_button);
                        powerButton.setBackground(getDrawable(R.drawable.charge_button_drawable));
                        // Play sound if sound hasn't been played yet
                        if (!LevelInfo.powerSoundPlayed) {
                            LevelInfo.powerSoundPlayed = true;
                            LevelInfo.soundPool.play(LevelInfo.powerReadySoundId, (PlayerInfo.soundLevel * 0.8f), (PlayerInfo.soundLevel * 0.8f), 0, 0, 1);
                        }
                    }
                    // If power is active and player has made a match
                    if (Player.PlayerInfo.powerActivated && movingSquare.matches != 0) {
                        // Deactivate power up and reduce charge to 0
                        Player.PlayerInfo.powerActivated = false;
                        Player.PlayerInfo.powerCharge = 0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            levelUI.powerChargeBar.setProgress(Player.PlayerInfo.powerCharge, true);
                        }
                        // Switch UI back to normal
                        Button powerButton = findViewById(R.id.power_charge_button);
                        powerButton.setBackground(getDrawable(R.drawable.noncharge_button_drawable));
                        LinearLayout chargeLayout = findViewById(R.id.charge_bar_layout);
                        chargeLayout.setBackground(getDrawable(R.drawable.value_text_background));
                        LevelInfo.powerSoundPlayed = false;
                    }
                    // Remove any references to older matches
                    movingSquare.matches = 0;
                    movingSquare.firstMatchObject = null;
                    movingSquare.secondMatchObject = null;
                    movingSquare.thirdMatchObject = null;
                    GridUtil.winCheck(getApplicationContext(), levelUI, gs);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    goalSquare = (GridTextView) v;
                    goalSquare.setBackground(getDrawable(R.drawable.grid_item));
                    GridUtil.updateGridItemColour(goalSquare);
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void executeCharge(View view){
        GridUtil.executeCharge(view, levelUI, getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void muteSound(View view){
        SoundManager.muteSound(view, gs, getApplicationContext());
    }

    void updateBackgroundAndFilter(Drawable background, GridTextView gridItem){
        gridItem.setBackground(background);
        gridItem.getBackground().setColorFilter(Color.rgb(gridItem.redInBackground, gridItem.greenInBackground, gridItem.blueInBackground), PorterDuff.Mode.OVERLAY);
    }
}
