package thall59.blackjack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import java.text.NumberFormat; // currency formatting

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity; // base class for activities
import android.os.Bundle; // saving state information
import android.text.Editable; // for EditText event handling
import android.text.InputFilter;
import android.text.TextWatcher; // EditText listener
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // for buyin amount input
import android.widget.ImageButton;
import android.widget.TextView; // displaying text
import android.widget.Toast;

public class BlackjackMenu extends AppCompatActivity {

    // currency formatter
    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();

    // declarations for variables
    private ImageButton newgameButton;
    private Button rulesButton;
    private TextView buyinAmountTextView; //shows formatted bill amount
    private EditText buyinAmount;
    double buyinMoney = 0.0;
    Intent newGameIntent;
    int num1;
    CharSequence text = "Value must be between $10 - $2000";
    int duration = Toast.LENGTH_SHORT;
    MediaPlayer mp;



    // called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call superclass's version
        setContentView(R.layout.blackjack_mainmenu); // inflate the GUI
        getSupportActionBar().setDisplayShowHomeEnabled(true); // display icon on actionbar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher); // icon



        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);

            if (gameSound) {
                mp = MediaPlayer.create(BlackjackMenu.this, R.raw.selectscreen);
                mp.start();
            }
        }

        // Locate the button in activity_display_mesage.xml
        newgameButton = (ImageButton) findViewById(R.id.newgameButton);
        buyinAmount = (EditText) findViewById(R.id.buyinAmount);



        // Capture button clicks
        newgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.stop();

                // Start GroupAssignment2.class
                if (buyinMoney < 10 || buyinMoney > 2000){

                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    recreate();

                }else{


                    Intent newGameIntent = new Intent(BlackjackMenu.this, BlackjackActivity.class);

                    newGameIntent.putExtra("buyin", buyinMoney);

                    startActivity(newGameIntent);
                }
            }
        }); // end newgameButton OnClickListener


        // get reference to the TextViews
        // that the craps game interacts with programmatically
        buyinAmountTextView =
                (TextView) findViewById(R.id.amount_display_textview);

        // update GUI based on buyinAmount
        buyinAmountTextView.setText(
                currencyFormat.format(buyinMoney));



        // set buyinAmount edit text's TextWatcher
        EditText buyinAmountEditText =
                (EditText) findViewById(R.id.buyinAmount);
        buyinAmountEditText.addTextChangedListener(buyinAmountEditTextWatcher);


    } // end method onCreate




    // event-handling object that responds to buyinAmountEditText's events
    private TextWatcher buyinAmountEditTextWatcher =
            new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count){
                    // convert buyinAmountEditText's into a double
                    try{
                        buyinMoney = Double.parseDouble(s.toString()) / 100.0;
                    } // end try

                    catch (NumberFormatException e){

                        buyinMoney = 0.0; // default if an exception occurs
                    } // end catch

                    // display currency formatted bill amount
                    buyinAmountTextView.setText(currencyFormat.format(buyinMoney));

                } // end method onTextChanged

                @Override
                public void afterTextChanged(Editable s){
                } // end afterTextChanged

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after){
                } // end method beforeTextChanged

            }; // end buyinAmountEditTextWatcher



    // shows the dialog for the rules
    public void rulesClick(){
        AlertDialog.Builder rules = new AlertDialog.Builder(BlackjackMenu.this);
        rules.setCancelable(false);
        rules.setTitle("RULES");
        rules.setMessage("Rules of the Game: "
                + "\n"
                + "\n1. Minimum you can buy-in is $10"
                + "\n2. Maximum that you can buy-in is $1000"
                + "\n3. The amount for each chip is 5, 25, & 100."
                + "\n4. The basic premise of the game is that you"
                +"\n     want to have a hand value that is closer to"
                +"\n     21 than that of the dealer, without going"
                + "\n    over 21."
                + "\n5. Player has to stand to end their turn even if" +
                "\n        they hit 21. This includes after every hit."
                +"\n6.  The value of a hand is simply the sum of"
                + "\n     the point counts of each card in the hand."
                + "\n     For example, a hand containing (5, 7, 8)"
                + "\n     has value of 20."
                + "\n7. Ace will be counted as 1 only if you are"
                + "\n     going over 21 or there are two Aces in the"
                + "\n     hand."
                + "\n8. For example, a hand containing (A, 7) has"
                + "\n     the value of 18. A hand containing (A, 7, 8)"
                + "\n      has the value of 16. Here Ace is counted as" +
                "\n       1."
                +"\n9.  When a player has hit 21 they hit Blackjack," +
                "\n        thus they will get 2x the amount. If the " +
                "\n        dealer has hit Blackjack the player loses." +
                "\n 10.  When a player ties with the dealer the " +
                "\n        player does not lose the amount they " +
                "\n        bet. This includes if dealer and player" +
                "\n        both get 21(Blackjack)."
                + "\n* Hit - Draw another card."
                + "\n* Stand - End turn."
                + "\n* Double - Double wager, take a single card"
                + "\n    and finish."
                + "\n* Surrender - Give up a half-bet and retire"
                + "\n    from game."
                + "\n"
                + "\n Values of the Cards:"
                + "\n* An Ace can count as either 1 or 11."
                + "\n* The cards from 2 through 9 are valued"
                + "\n   at their face value."
                + "\n* The 10, Jack, Queen, and King are all valued"
                + "\n   at 10.");
        rules.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = rules.create();
        alert.show();
    }

    // when button is clicked it shows the rules dialog
    public void rulesButton (View view){


        rulesClick();
    }

    public void exitClick(View view){
        mp.stop();

        Intent intent = new Intent(BlackjackMenu.this, BlackjackHome.class);
        startActivity(intent);
    }



}

