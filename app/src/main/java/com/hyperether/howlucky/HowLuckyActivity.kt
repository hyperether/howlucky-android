package com.hyperether.howlucky

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hyperether.util.MessageDialogFragment
import com.hyperether.util.ResultDialogFragment
import java.text.DateFormat
import java.util.*

/**
 * Game application that determines your luck today
 *
 * @author dusan
 * @version November 22, 2013
 */
class HowLuckyActivity : FragmentActivity() {
    private var mRandomNumber: Long = 0
    private var mRandomIndex = 0
    private var mButton1: ImageButton? = null
    private var mButton2: ImageButton? = null
    private var mButton3: ImageButton? = null
    private var mButton4: ImageButton? = null
    private var mButton5: ImageButton? = null
    private var mButton6: ImageButton? = null
    private var mButton7: ImageButton? = null
    private var mButton8: ImageButton? = null
    var mClickCounter = 0
    var avgLuck = 0f

    val REQUEST_CODE_SIGN_IN = 9001 // Request code for Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("871148509842-dba2idm4u5rj10mvgm67u4nhiuhhg4ln.apps.googleusercontent.com") // From google-services.json
        .requestEmail()
        .build()

    // Sound effects
    private var effectPool: SoundPool? = null
    private var badSound = 0
    private var goodSound = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_lucky)

        // Get the comment text entered in EditTextHandler
        val settings = getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()

        // Get the current date
        val df = DateFormat.getDateInstance()
        val currDate = df.format(Date())

        // Get the saved date
        val savedDate = settings.getString("currDate", "Missing")

        // Check if the game was already played today
        //if(currDate.compareTo(savedDate) != 0) { // If No, save the date and continue
        if (true) {
            editor.putString("currDate", currDate)
            editor.apply()
        } else { // If Yes then notify used and quit
            editor.apply()
            val dialog = MessageDialogFragment()
            dialog.isCancelable = false
            dialog.setmMessage(DIALOG_MESSAGE_PLAYED)
            dialog.show(supportFragmentManager, "Game played")
        }

        // Generate a random number
        mRandomNumber = randomGenerator()
        // Message array index
        mRandomIndex = mRandomNumber.toInt() % 3

        // Initialize sound effects
        initSounds()

        // Initialize buttons
        initButtons()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account.email?.let { Log.d("GOOGLE SIGN IN success, email: ", it) }
        } catch (e: ApiException) {
            e.status.statusCode.toString()?.let { Log.d("GOOGLE SIGN IN error: ", it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.how_lucky, menu);
        return true
    }

    // Save layout before stopping the activity
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("random number", mRandomNumber)
        outState.putInt("random index", mRandomIndex)
        outState.putInt("click count", mClickCounter)
        outState.putFloat("avg luck", avgLuck)
        super.onSaveInstanceState(outState)
    }

    // Restore layout after interruption
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mRandomNumber = savedInstanceState.getLong("random number")
        mRandomIndex = savedInstanceState.getInt("random index")
        mClickCounter = savedInstanceState.getInt("click count")
        avgLuck = savedInstanceState.getFloat("avg luck")
    }

    // Button 1 listener
    private val mButton1Listener = View.OnClickListener { processButton(mButton1, 0) }

    // Button 2 listener
    private val mButton2Listener = View.OnClickListener { processButton(mButton2, 1) }

    // Button 3 listener
    private val mButton3Listener = View.OnClickListener { processButton(mButton3, 2) }

    // Button 4 listener
    private val mButton4Listener = View.OnClickListener { processButton(mButton4, 3) }

    // Button 5 listener
    private val mButton5Listener = View.OnClickListener { processButton(mButton5, 4) }

    // Button 6 listener
    private val mButton6Listener = View.OnClickListener { processButton(mButton6, 5) }

    // Button 7 listener
    private val mButton7Listener = View.OnClickListener { processButton(mButton7, 6) }

    // Button 8 listener
    private val mButton8Listener = View.OnClickListener { processButton(mButton8, 7) }

    /**
     * Method that generates a pseudo-random number between 0 and 7
     *
     * @return pseudo random number between 0 and 7
     */
    private fun randomGenerator(): Long {
        var out: Long
        var random = Math.random()
        random *= 10.0
        out = Math.round(random)
        out %= 8
        return out
    }

    /**
     * Method that initializes buttons
     */
    private fun initButtons() {
        mButton1 = findViewById<View>(R.id.button1) as ImageButton
        // Register the onClick listener with the implementation above
        mButton1!!.setOnClickListener(mButton1Listener)
        mButton2 = findViewById<View>(R.id.button2) as ImageButton
        // Register the onClick listener with the implementation above
        mButton2!!.setOnClickListener(mButton2Listener)
        mButton3 = findViewById<View>(R.id.button3) as ImageButton
        // Register the onClick listener with the implementation above
        mButton3!!.setOnClickListener(mButton3Listener)
        mButton4 = findViewById<View>(R.id.button4) as ImageButton
        // Register the onClick listener with the implementation above
        mButton4!!.setOnClickListener(mButton4Listener)
        mButton5 = findViewById<View>(R.id.button5) as ImageButton
        // Register the onClick listener with the implementation above
        mButton5!!.setOnClickListener(mButton5Listener)
        mButton6 = findViewById<View>(R.id.button6) as ImageButton
        // Register the onClick listener with the implementation above
        mButton6!!.setOnClickListener(mButton6Listener)
        mButton7 = findViewById<View>(R.id.button7) as ImageButton
        // Register the onClick listener with the implementation above
        mButton7!!.setOnClickListener(mButton7Listener)
        mButton8 = findViewById<View>(R.id.button8) as ImageButton
        // Register the onClick listener with the implementation above
        mButton8!!.setOnClickListener(mButton8Listener)
    }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    /**
     * React on user dialog confirmation by closing the main activity
     */
    fun onUserConfirm() {
        // Close activity
        finish()
    }

    /**
     * Process a button pressed
     *
     * @param button - reference to the button that was pressed
     * @param number - button code
     */
    private fun processButton(button: ImageButton?, number: Long) {
        mClickCounter++
        if (mRandomNumber == number) {
            luck
        } else {
//            Toast.makeText(getApplicationContext(), WRONG_BUTTON, Toast.LENGTH_SHORT).show();
            //Disable the button
            button!!.isEnabled = false
            // Change the color of the button
            val drawableRedButton = resources.getDrawable(R.drawable.hat_cloud)
            button.setImageDrawable(drawableRedButton)
            // Play the "smoke from the hat" sound
            effectPool!!.play(badSound, EFFECT_VOLUME_HIGHER, EFFECT_VOLUME_HIGHER, 0, 0, 1f)
        }
    }// Play the winning sound

    /**
     * Calculate luck for that day
     */
    private val luck: Unit
        private get() {
            val outLuck: Int
            val type: Int
            val message: String
            if (mClickCounter == 1) outLuck = 100 else {
                avgLuck = mClickCounter.toFloat() / TOTAL_BUTTONS
                outLuck = (100 - avgLuck * 100).toInt()
            }
            if (mClickCounter < 2) {
                message = mMessagesGood[mRandomIndex]
                type = ResultDialogFragment.CLOVER
            } else if (mClickCounter < 4) {
                message = mMessagesMiddle[mRandomIndex]
                type = ResultDialogFragment.HORSESHOE
            } else {
                message = mMessagesBad[mRandomIndex]
                type = ResultDialogFragment.RABBIT
            }
            val dialog = ResultDialogFragment.newInstance(type, outLuck, message)
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "Result")

            // Play the winning sound
            effectPool!!.play(goodSound, EFFECT_VOLUME, EFFECT_VOLUME, 0, 0, 1f)
        }

    /**
     * Method that initializes sound effects
     */
    private fun initSounds() {
        effectPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        badSound = effectPool!!.load(applicationContext, R.raw.lose, 1)
        // random good sound
        val random0_7 = randomGenerator().toInt()
        when (random0_7) {
            0, 1 -> goodSound = effectPool!!.load(applicationContext, R.raw.win, 1)
            2, 3 -> goodSound = effectPool!!.load(applicationContext, R.raw.hooraay, 1)
            4, 5 -> goodSound = effectPool!!.load(applicationContext, R.raw.woohoo, 1)
            6, 7 -> goodSound = effectPool!!.load(applicationContext, R.raw.yoohoo, 1)
        }
    }

    companion object {
        private val mMessagesGood = arrayOf(
            "You're infected by a virus of good luck!",
            "Your 6th sense is working!",
            "Impossible, you must be a clairvoyant!"
        )
        private val mMessagesMiddle = arrayOf(
            "Feeling lucky?", "Almost perfect!",
            "The goddess of fortune guides your hand!"
        )
        private val mMessagesBad = arrayOf(
            "Don't play lottery today!", "Better luck next " +
                    "time.",
            "Sometimes life is rough, don't despair!"
        )
        private const val TOTAL_BUTTONS = 8
        private const val DIALOG_MESSAGE_PLAYED = "Game already played today. Try again " +
                "tomorrow."
        private const val WRONG_BUTTON = "Wrong choice! Try another one."
        const val PREF_FILE = "HowLuckyDataPref.txt"
        private const val EFFECT_VOLUME = 0.2f
        private const val EFFECT_VOLUME_HIGHER = 0.9f
    }
}