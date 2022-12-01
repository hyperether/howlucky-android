package com.hyperether.howlucky;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.hyperether.util.MessageDialogFragment;
import com.hyperether.util.ResultDialogFragment;

import java.text.DateFormat;
import java.util.Date;


/**
 * Game application that determines your luck today
 *
 * @author dusan
 * @version November 22, 2013
 */

public class HowLuckyActivity extends FragmentActivity {

    private long mRandomNumber;
    private int mRandomIndex;
    private final static String[] mMessagesGood = {"You're infected by a virus of good luck!",
            "Your 6th sense is working!",
            "Impossible, you must be a clairvoyant!"};
    private final static String[] mMessagesMiddle = {"Feeling lucky?", "Almost perfect!",
            "The goddess of fortune guides your hand!"};
    private final static String[] mMessagesBad = {"Don't play lottery today!", "Better luck next " +
            "time.",
            "Sometimes life is rough, don't despair!"};
    private final static int TOTAL_BUTTONS = 8;
    private final static String DIALOG_MESSAGE_PLAYED = "Game already played today. Try again " +
            "tomorrow.";
    private final static String WRONG_BUTTON = "Wrong choice! Try another one.";
    private ImageButton mButton1, mButton2, mButton3, mButton4, mButton5, mButton6, mButton7,
            mButton8;

    public static final String PREF_FILE = "HowLuckyDataPref.txt";

    int mClickCounter = 0;
    float avgLuck = 0;

    // Sound effects
    private SoundPool effectPool;
    private int badSound, goodSound;
    private final static float EFFECT_VOLUME = 0.2f;
    private final static float EFFECT_VOLUME_HIGHER = 0.9f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_lucky);

        // Get the comment text entered in EditTextHandler
        SharedPreferences settings = getSharedPreferences(PREF_FILE, 0);
        Editor editor = settings.edit();

        // Get the current date
        DateFormat df = DateFormat.getDateInstance();
        String currDate = df.format(new Date());

        // Get the saved date
        String savedDate = settings.getString("currDate", "Missing");

        // Check if the game was already played today
        //if(currDate.compareTo(savedDate) != 0) { // If No, save the date and continue
        if (true) {
            editor.putString("currDate", currDate);
            editor.apply();
        } else { // If Yes then notify used and quit
            editor.apply();

            MessageDialogFragment dialog = new MessageDialogFragment();
            dialog.setCancelable(false);
            dialog.setmMessage(DIALOG_MESSAGE_PLAYED);
            dialog.show(getSupportFragmentManager(), "Game played");
        }

        // Generate a random number
        mRandomNumber = randomGenerator();
        // Message array index
        mRandomIndex = (int) mRandomNumber % 3;

        // Initialize sound effects
        initSounds();

        // Initialize buttons
        initButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.how_lucky, menu);
        return true;
    }

    // Save layout before stopping the activity
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("random number", mRandomNumber);
        outState.putInt("random index", mRandomIndex);
        outState.putInt("click count", mClickCounter);
        outState.putFloat("avg luck", avgLuck);
        super.onSaveInstanceState(outState);
    }

    // Restore layout after interruption
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRandomNumber = savedInstanceState.getLong("random number");
        mRandomIndex = savedInstanceState.getInt("random index");
        mClickCounter = savedInstanceState.getInt("click count");
        avgLuck = savedInstanceState.getFloat("avg luck");
    }

    // Button 1 listener
    private final OnClickListener mButton1Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton1, 0);
        }
    };

    // Button 2 listener
    private final OnClickListener mButton2Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton2, 1);
        }
    };

    // Button 3 listener
    private final OnClickListener mButton3Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton3, 2);
        }
    };

    // Button 4 listener
    private final OnClickListener mButton4Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton4, 3);
        }
    };

    // Button 5 listener
    private final OnClickListener mButton5Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton5, 4);
        }
    };

    // Button 6 listener
    private final OnClickListener mButton6Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton6, 5);
        }
    };

    // Button 7 listener
    private final OnClickListener mButton7Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton7, 6);
        }
    };

    // Button 8 listener
    private final OnClickListener mButton8Listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            processButton(mButton8, 7);
        }
    };

    /**
     * Method that generates a pseudo-random number between 0 and 7
     *
     * @return pseudo random number between 0 and 7
     */
    private long randomGenerator() {
        long out;
        double random = Math.random();
        random *= 10;
        out = Math.round(random);
        out %= 8;
        return out;
    }

    /**
     * Method that initializes buttons
     */
    private void initButtons() {
        mButton1 = (ImageButton) findViewById(R.id.button1);
        // Register the onClick listener with the implementation above
        mButton1.setOnClickListener(mButton1Listener);

        mButton2 = (ImageButton) findViewById(R.id.button2);
        // Register the onClick listener with the implementation above
        mButton2.setOnClickListener(mButton2Listener);

        mButton3 = (ImageButton) findViewById(R.id.button3);
        // Register the onClick listener with the implementation above
        mButton3.setOnClickListener(mButton3Listener);

        mButton4 = (ImageButton) findViewById(R.id.button4);
        // Register the onClick listener with the implementation above
        mButton4.setOnClickListener(mButton4Listener);

        mButton5 = (ImageButton) findViewById(R.id.button5);
        // Register the onClick listener with the implementation above
        mButton5.setOnClickListener(mButton5Listener);

        mButton6 = (ImageButton) findViewById(R.id.button6);
        // Register the onClick listener with the implementation above
        mButton6.setOnClickListener(mButton6Listener);

        mButton7 = (ImageButton) findViewById(R.id.button7);
        // Register the onClick listener with the implementation above
        mButton7.setOnClickListener(mButton7Listener);

        mButton8 = (ImageButton) findViewById(R.id.button8);
        // Register the onClick listener with the implementation above
        mButton8.setOnClickListener(mButton8Listener);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * React on user dialog confirmation by closing the main activity
     */
    public void onUserConfirm() {
        // Close activity
        finish();
    }

    /**
     * Process a button pressed
     *
     * @param button - reference to the button that was pressed
     * @param number - button code
     */
    private void processButton(ImageButton button, long number) {

        mClickCounter++;

        if (mRandomNumber == number) {
            getLuck();
        } else {
//            Toast.makeText(getApplicationContext(), WRONG_BUTTON, Toast.LENGTH_SHORT).show();
            //Disable the button
            button.setEnabled(false);
            // Change the color of the button
            Drawable drawableRedButton = getResources().getDrawable(R.drawable.hat_cloud);
            button.setImageDrawable(drawableRedButton);
            // Play the "smoke from the hat" sound
            effectPool.play(badSound, EFFECT_VOLUME_HIGHER, EFFECT_VOLUME_HIGHER, 0, 0, 1);
        }
    }

    /**
     * Calculate luck for that day
     */
    private void getLuck() {
        int outLuck;
        int type;
        String message;

        if (mClickCounter == 1)
            outLuck = 100;
        else {
            avgLuck = (float) mClickCounter / TOTAL_BUTTONS;
            outLuck = (int) (100 - (avgLuck * 100));
        }

        if (mClickCounter < 2) {
            message = mMessagesGood[mRandomIndex];
            type = ResultDialogFragment.CLOVER;
        } else if (mClickCounter < 4) {
            message = mMessagesMiddle[mRandomIndex];
            type = ResultDialogFragment.HORSESHOE;
        } else {
            message = mMessagesBad[mRandomIndex];
            type = ResultDialogFragment.RABBIT;
        }

        ResultDialogFragment dialog = ResultDialogFragment.newInstance(type, outLuck, message);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "Result");

        // Play the winning sound
        effectPool.play(goodSound, EFFECT_VOLUME, EFFECT_VOLUME, 0, 0, 1);
    }

    /**
     * Method that initializes sound effects
     */
    private void initSounds() {

        effectPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        badSound = effectPool.load(getApplicationContext(), R.raw.lose, 1);
        // random good sound
        int random0_7 = (int) randomGenerator();
        switch (random0_7) {
            case 0:
            case 1:
                goodSound = effectPool.load(getApplicationContext(), R.raw.win, 1);
                break;
            case 2:
            case 3:
                goodSound = effectPool.load(getApplicationContext(), R.raw.hooraay, 1);
                break;
            case 4:
            case 5:
                goodSound = effectPool.load(getApplicationContext(), R.raw.woohoo, 1);
                break;
            case 6:
            case 7:
                goodSound = effectPool.load(getApplicationContext(), R.raw.yoohoo, 1);
                break;
        }
    }
}
