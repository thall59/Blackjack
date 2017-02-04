// BlackjackActivity.java
// To play the game Blackjack
package thall59.blackjack;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;


public class BlackjackActivity extends Activity{

    // format currency
    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();

    // layout used defined here
    public static LinearLayout layout;

    // All elements used are defined here
    public static TextView bank_Amount, dealerScore, playerScore, tvBet, tvHighestScore;
    public ImageView dealerCard1, dealerCard2, dealerCard3, dealerCard4,
            dealerCard5;
    public ImageView playerCard1, playerCard2, playerCard3, playerCard4, playerCard5;
    public ImageButton btn5bet, btn25bet, btn100bet;
    private Button btnHit, btnStand, btnDouble, btnGame, btnSurrender, btnQuit;

    // All local variables here
    double bet = 0;
    int dealersScore = 0, playersScore = 0;
    int dealerCardNumber = 0, playerCardNumber = 0;
    int randomNumber;
    static double total = 0;
    double highestScore;
    int j;
    MediaPlayer mp;

    // To make sure no card comes twice
    ArrayList<Integer> _alCardsTracking = new ArrayList<Integer>();

    // Dealer and Player Aces Check
    char[] _dealerCardArray = new char[]{'0', '0', '0', '0', '0'};
    char[] _playerCardArray = new char[]{'0', '0', '0', '0', '0'};

    // Dealer and Player Score Count
    int[] _dealerScoreCount = new int[]{0, 0, 0, 0, 0};
    int[] _playerScoreCount = new int[]{0, 0, 0, 0, 0};

    // Internal Storage
    private final String saveFileName = "savingHighScoreOfBlackJack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blackjack_game);


        // displays an add in the app
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

        // loads an ad into the adview
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupVariables(); // set up variables/cards for player

        disableButtons(); // disables the buttons for the player to play

        loadingHighScore(); // loads highest score

        bank_Amount = (TextView) findViewById(R.id.player_bank);
        btn5bet = (ImageButton) findViewById(R.id.five_BetAmount);
        btn25bet = (ImageButton) findViewById(R.id.twentyfive_BetAmount);
        btn100bet = (ImageButton) findViewById(R.id.hundred_BetAmount);
        btnDouble = (Button) findViewById(R.id.double_button);
        btnHit = (Button) findViewById(R.id.hit_button);
        btnStand = (Button) findViewById(R.id.stand_button);
        btnGame = (Button) findViewById(R.id.game_button);
        btnSurrender = (Button) findViewById(R.id.surrender_button);
        dealerCard1 = (ImageView) findViewById(R.id.dealer_card1);
        tvBet = (TextView) findViewById(R.id.player_bet);
        btnQuit = (Button) findViewById(R.id.quit_button);


        // gets the buyin on the home screen and puts it in the bank
        Intent bankAmountIntent = getIntent();
        Bundle bundle = bankAmountIntent.getExtras();
        double buyinMoney = bundle.getDouble("buyin", 0);
        // shows in textview bankAmount in currency format
        total = buyinMoney;
        bank_Amount.setText(String.valueOf(currencyFormat.format(buyinMoney)));

        // Capture button clicks, when quit is clicked
        // user is taken back to the menu page
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savingHighScore(); // saves high score

                // Start DisplayMesageActivity.class
                Intent homePage = new Intent(BlackjackActivity.this, BlackjackMenu.class);
                startActivity(homePage);
            }
        });

    }

    // disables hit, stand, surrender, and double
    private void disableButtons() {

        Button btnHit = (Button) findViewById(R.id.hit_button);
        btnHit.setEnabled(false);

        Button btnStand = (Button) findViewById(R.id.stand_button);
        btnStand.setEnabled(false);

        Button btnSurrender = (Button) findViewById(R.id.surrender_button);
        btnSurrender.setEnabled(false);

        Button btnDouble = (Button) findViewById(R.id.double_button);
        btnDouble.setEnabled(false);

    }

    // enables hit, stand, surrender, and double
    private void enableButtons() {

        Button btnHit = (Button) findViewById(R.id.hit_button);
        btnHit.setEnabled(true);

        Button btnStand = (Button) findViewById(R.id.stand_button);
        btnStand.setEnabled(true);

        Button btnSurrender = (Button) findViewById(R.id.surrender_button);
        btnSurrender.setEnabled(true);

        Button btnDouble = (Button) findViewById(R.id.double_button);
        btnDouble.setEnabled(true);

    }


    // after player places their bets they will click Start Game
    public void setBtnGame(View view) {
        // will check if the player has placed a bet
        if (bet == 0) {
            // shows toast if the player has not placed a bet and clicked start
            Toast toast = Toast.makeText(BlackjackActivity.this, "You must place a bet first",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        // will not allow player to play if bet is lower than total
        else if (bet > total){
            bet = 0;
            tvBet.setText("Bet: $0");
            Toast toast = Toast.makeText(BlackjackActivity.this, "You do not have enough money. You bet " +
                    "higher then your bank total.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else if (bet > 0) { // will start game
            total = total - (bet);
            bank_Amount.setText(String.valueOf((currencyFormat.format(total))));
            tvBet.setText(String.valueOf(currencyFormat.format(bet)));


            cardImageSwitcher(); // displays card images

            enableButtons();
            gameStart();
            btnGame.setVisibility(View.INVISIBLE);
        }
    }

    // player will hit to draw another card
    public void btnHit(View view) {
        playerCall();
        calculatePlayerScore();

        // Aces count as 1
        if (playersScore > 21) {
            for (int i = 0; i < 5; i++) {
                if (_playerCardArray[i] == 'A' && _playerScoreCount[i] == 11) {
                    _playerScoreCount[i] = 1;
                    Toast toast = Toast.makeText(BlackjackActivity.this, "Ace will be counted as 1",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
            }
            calculatePlayerScore();
        }
        // player will bust if they go over 21
        if (playersScore > 21) {
            bet = 0;

            Toast toast = Toast.makeText(getApplicationContext(), "Player BUSTS! You lose!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            disableButtons();


            alertBox(); // displays next hand alert box
        }
        // where computer will play
        if (playerCardNumber >= 5 && playersScore < 22) {
            btnStandClick();
            alertBox();
        }

    }

    public void btnStandClick() {
        // Computer play here
        do {
            //Opening 1 card of dealer
            dealerCall();
            calculateDealerScore();

            // Aces count as 1
            if (dealersScore > 21) {
                for (int i = 0; i < 5; i++) {
                    if (_dealerCardArray[i] == 'A' && _dealerScoreCount[i] == 11) {
                        _dealerScoreCount[i] = 1;
                        break;
                    }
                }
                calculateDealerScore();
            }
        } while (dealersScore < 17 && dealersScore <= playersScore && dealerCardNumber < 5);

        results(); // shows results of game
    }


    // player will stand if they do not want to draw another card, double, or surrender
    public void btnStand(View view) {
        // dealer's turn to play
        do {
            // opening 1 card of dealer
            dealerCall();
            calculateDealerScore();

            // Aces count as 1
            if (dealersScore > 21) {
                for (int i = 0; i < 5; i++) {
                    if (_dealerCardArray[i] == 'A' && _dealerScoreCount[i] == 11) {
                        _dealerScoreCount[i] = 1;
                        break;
                    }
                }
                calculateDealerScore();
            }
        } while (dealersScore < 17 && dealersScore <= playersScore && dealerCardNumber < 5);

        // if player hits 21 it's Blackjack they win
        if (playersScore == 21) {
            blackJack();

        } else if (dealersScore == 21) { // if dealer hits blackjack dealer wins
            dealerblackJack();


        }else if (dealersScore > 21) { // dealer will bust
            total = total + (bet * 2);
            Toast toast = Toast.makeText(getApplicationContext(), "Dealer BUSTS! You win!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            disableButtons();

            alertBox(); // displays alert box

        }else results();

    }


    // when player hits surrender they get
    // half of their bet back
    public void btnSurrender(View view){
        total += (bet/2);
        bet = 0;
        Toast toast = Toast.makeText(BlackjackActivity.this, "You surrendered, you got half " +
                        "of your bet back.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        alertBox();

        bank_Amount.setText(String.valueOf((currencyFormat.format(total))));
        tvBet.setText(String.valueOf(currencyFormat.format(bet)));

    }

    // if player wants to double their current bet and receive one additional card
    public void btnDouble(View view){
        bet = bet + bet;
        playerCall();
        calculatePlayerScore();

        // Aces count as 1
        if (playersScore > 21){
            for (int i = 0; i < 5; i++){
                if (_playerCardArray[i] == 'A' && _playerScoreCount[i] == 11){
                    _playerScoreCount[i]= 1;
                    Toast toast = Toast.makeText(BlackjackActivity.this, "Ace will be counted as 1",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
            }
            calculatePlayerScore();
        }
        if (playersScore > 21) { // player busts
            bet = 0;

            Toast toast = Toast.makeText(getApplicationContext(), "Player BUSTS! You lose!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            disableButtons();


            alertBox();
        }

        bank_Amount.setText(String.valueOf(currencyFormat.format(total)));
        tvBet.setText(String.valueOf(currencyFormat.format(bet)));

    }

    // when player clicks 25 chip btn
    public void btn25bet(View view){

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);

            if (gameSound) {
                mp = MediaPlayer
                        .create(BlackjackActivity.this, R.raw.chip);
                mp.start();
            }
        }
        // checks to see if there is enough money
        if (total >= 25) {

            bet = bet + 25.00;
            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
        }else{
            Toast toast = Toast.makeText(BlackjackActivity.this, "You do not have enough money.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    // when player clicks 5 chip btn
    public void btn5bet(View view){

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);

            if (gameSound) {
                mp = MediaPlayer
                        .create(BlackjackActivity.this, R.raw.chip);
                mp.start();
            }
        }
        // checks to see if there is enough money
        if ( total >= 5) {

            bet = bet + 5.00;
            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
        }else{
            Toast toast = Toast.makeText(BlackjackActivity.this, "You do not have enough money.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    // when player clicks 100 chip btn
    public void btn100bet(View view){

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);

            if (gameSound) {
                mp = MediaPlayer
                        .create(BlackjackActivity.this, R.raw.chip);
                mp.start();
            }
        }
        // checks to see if there is enough money
        if (total >= 100) {

            bet = bet + 100.00;
            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
        }else{
            Toast toast = Toast.makeText(BlackjackActivity.this, "You do not have enough money.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    // shows back images of cards for dealer and player
    private void cardImageSwitcher() {

        dealerCard1.setImageResource(R.drawable.cardback);
        dealerCard2.setImageResource(R.drawable.cardback);
        dealerCard3.setImageResource(R.drawable.cardback);
        dealerCard4.setImageResource(R.drawable.cardback);
        dealerCard5.setImageResource(R.drawable.cardback);

        playerCard1.setImageResource(R.drawable.cardback);
        playerCard2.setImageResource(R.drawable.cardback);
        playerCard3.setImageResource(R.drawable.cardback);
        playerCard4.setImageResource(R.drawable.cardback);
        playerCard5.setImageResource(R.drawable.cardback);
    }

    // starts the game; dealer with one card showing and player with two
    private void gameStart() {
        btn5bet.setEnabled(false);
        btn25bet.setEnabled(false);
        btn100bet.setEnabled(false);

        // Opening 1 Card of Dealer
        dealerCall();
        calculateDealerScore();

        // Opening 2 Cards of Player
        playerCall();


        // Opening 2 Cards of Player
        playerCall();
        calculatePlayerScore();

        // Looking for BlackJack
        if (playersScore == 21) {
            if ((_playerCardArray[0] == 'A' && _playerCardArray[1] == 'J')
                    || ((_playerCardArray[0] == 'J' && _playerCardArray[1] == 'A'))) {
                blackJack();
            }
        }
    }

    // when player clicks stand dealer will draw cards
    private int dealerNumber;
    public void dealerCall() {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(randomNumber));
        _alCardsTracking.add(randomNumber);

        switch (dealerCardNumber) {
            case 0:
                _dealerCardArray[dealerCardNumber] = cardsCalling(randomNumber,
                        dealerCard1);
                break;
            case 1:
                _dealerCardArray[dealerCardNumber] = cardsCalling(randomNumber,
                        dealerCard2);
                break;
            case 2:
                _dealerCardArray[dealerCardNumber] = cardsCalling(randomNumber,
                        dealerCard3);
                break;
            case 3:
                _dealerCardArray[dealerCardNumber] = cardsCalling(randomNumber,
                        dealerCard4);
                break;
            case 4:
                _dealerCardArray[dealerCardNumber] = cardsCalling(randomNumber,
                        dealerCard5);
                break;
        }
        dealerNumber = randomNumber;
        _dealerScoreCount[dealerCardNumber] = getIntValueFromCard(_dealerCardArray[dealerCardNumber]);
        dealerCardNumber++;

    }

    // draws the card when player hits
    public void playerCall() {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(randomNumber));
        _alCardsTracking.add(randomNumber);

        switch (playerCardNumber) {
            case 0:
                _playerCardArray[playerCardNumber] = cardsCalling(randomNumber,
                        playerCard1);
                break;
            case 1:
                _playerCardArray[playerCardNumber] = cardsCalling(randomNumber,
                        playerCard2);
                break;
            case 2:
                _playerCardArray[playerCardNumber] = cardsCalling(randomNumber,
                        playerCard3);
                break;
            case 3:
                _playerCardArray[playerCardNumber] = cardsCalling(randomNumber,
                        playerCard4);
                break;
            case 4:
                _playerCardArray[playerCardNumber] = cardsCalling(randomNumber,
                        playerCard5);
                break;
        }
        // Very Important
        _playerScoreCount[playerCardNumber] = getIntValueFromCard(_playerCardArray[playerCardNumber]);
        playerCardNumber++;
    }

    // calculates the dealers score
    public void calculateDealerScore() {

        j = 0;
        for (int i = 0; i < 5; i++) {
            j += _dealerScoreCount[i];
        }
        dealersScore = j;
        dealerScore.setText("Dealer's Score : " + dealersScore);

    }

    // calculates the players score
    public void calculatePlayerScore() {

        j = 0;
        for (int i = 0; i < 5; i++) {
            j += _playerScoreCount[i];
        }
        playersScore = j;
        playerScore.setText("Your Score : " + playersScore);

    }


    // shows the results of the game and whether the player loses or wins
    public void results(){
        calculateDealerScore();
        calculatePlayerScore();

        if (playersScore > dealersScore && playersScore <= 21) { // displays that player wins

            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
            Toast toast=Toast.makeText(getApplicationContext(),"You win!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            total = total + (bet * 2);


            alertBox();


        }else if (dealersScore > playersScore && dealersScore <= 21){ // displays that player loses
            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
            Toast toast=Toast.makeText(getApplicationContext(),"You lose!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            bet = 0;

            alertBox();


        }else if(playersScore == dealersScore){ // result is a tie
            total = total + bet;
            bank_Amount.setText(String.valueOf(currencyFormat.format(total)));


            tvBet.setText(String.valueOf(currencyFormat.format(bet)));
            Toast toast=Toast.makeText(getApplicationContext(),"Tie!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            alertBox();

        }
    }

    // Check to see if user has hit Blackjack
    public void blackJack(){
        total = total + (bet * 3);
        Toast toast = Toast.makeText(BlackjackActivity.this, "Congrats! You Hit BLACKJACK",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        disableButtons();
        alertBox();
    }

    // Check to see if dealer has hit Blackjack
    public void dealerblackJack(){
        bet = 0;

        Toast toast = Toast.makeText(BlackjackActivity.this, "Dealer has BLACKJACK. You lose!",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        disableButtons();
        alertBox();
    }

    // display card images
    public char cardsCalling(int cardNumberFromRandom, ImageView imageView) {

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);

            if (gameSound) {
                mp = MediaPlayer
                        .create(BlackjackActivity.this, R.raw.card_slide);
                mp.start();
            }

            // Club,Diamond,Hearts,Spades Sequence--(c,d,h,s)
            // Number sequence A,2,3,4,5,6,7,8,9,10,J,Q,K
            // Card Names : A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, 2

            switch (cardNumberFromRandom) {
                // Clubs
                case 0:
                    imageView.setImageResource(R.drawable.c1);
                    return 'A';
                case 1:
                    imageView.setImageResource(R.drawable.c2);
                    return '2';
                case 2:
                    imageView.setImageResource(R.drawable.c3);
                    return '3';
                case 3:
                    imageView.setImageResource(R.drawable.c4);
                    return '4';
                case 4:
                    imageView.setImageResource(R.drawable.c5);
                    return '5';
                case 5:
                    imageView.setImageResource(R.drawable.c6);
                    return '6';
                case 6:
                    imageView.setImageResource(R.drawable.c7);
                    return '7';
                case 7:
                    imageView.setImageResource(R.drawable.c8);
                    return '8';
                case 8:
                    imageView.setImageResource(R.drawable.c9);
                    return '9';
                case 9:
                    imageView.setImageResource(R.drawable.c10);
                    return 'T';
                case 10:
                    imageView.setImageResource(R.drawable.c11);
                    return 'J';
                case 11:
                    imageView.setImageResource(R.drawable.c12);
                    return 'Q';
                case 12:
                    imageView.setImageResource(R.drawable.c13);
                    return 'K';

                // Diamonds

                case 13:
                    imageView.setImageResource(R.drawable.d1);
                    return 'A';
                case 14:
                    imageView.setImageResource(R.drawable.d2);
                    return '2';
                case 15:
                    imageView.setImageResource(R.drawable.d3);
                    return '3';
                case 16:
                    imageView.setImageResource(R.drawable.d4);
                    return '4';
                case 17:
                    imageView.setImageResource(R.drawable.d5);
                    return '5';
                case 18:
                    imageView.setImageResource(R.drawable.d6);
                    return '6';
                case 19:
                    imageView.setImageResource(R.drawable.d7);
                    return '7';
                case 20:
                    imageView.setImageResource(R.drawable.d8);
                    return '8';
                case 21:
                    imageView.setImageResource(R.drawable.d9);
                    return '9';
                case 22:
                    imageView.setImageResource(R.drawable.d10);
                    return 'T';
                case 23:
                    imageView.setImageResource(R.drawable.d11);
                    return 'J';
                case 24:
                    imageView.setImageResource(R.drawable.d12);
                    return 'Q';
                case 25:
                    imageView.setImageResource(R.drawable.d13);
                    return 'K';

                // Hearts

                case 26:
                    imageView.setImageResource(R.drawable.h1);
                    return 'A';
                case 27:
                    imageView.setImageResource(R.drawable.h2);
                    return '2';
                case 28:
                    imageView.setImageResource(R.drawable.h3);
                    return '3';
                case 29:
                    imageView.setImageResource(R.drawable.h4);
                    return '4';
                case 30:
                    imageView.setImageResource(R.drawable.h5);
                    return '5';
                case 31:
                    imageView.setImageResource(R.drawable.h6);
                    return '6';
                case 32:
                    imageView.setImageResource(R.drawable.h7);
                    return '7';
                case 33:
                    imageView.setImageResource(R.drawable.h8);
                    return '8';
                case 34:
                    imageView.setImageResource(R.drawable.h9);
                    return '9';
                case 35:
                    imageView.setImageResource(R.drawable.h10);
                    return 'T';
                case 36:
                    imageView.setImageResource(R.drawable.h11);
                    return 'J';
                case 37:
                    imageView.setImageResource(R.drawable.h12);
                    return 'Q';
                case 38:
                    imageView.setImageResource(R.drawable.h13);
                    return 'K';

                // Spades

                case 39:
                    imageView.setImageResource(R.drawable.s1);
                    return 'A';
                case 40:
                    imageView.setImageResource(R.drawable.s2);
                    return '2';
                case 41:
                    imageView.setImageResource(R.drawable.s3);
                    return '3';
                case 42:
                    imageView.setImageResource(R.drawable.s4);
                    return '4';
                case 43:
                    imageView.setImageResource(R.drawable.s5);
                    return '5';
                case 44:
                    imageView.setImageResource(R.drawable.s6);
                    return '6';
                case 45:
                    imageView.setImageResource(R.drawable.s7);
                    return '7';
                case 46:
                    imageView.setImageResource(R.drawable.s8);
                    return '8';
                case 47:
                    imageView.setImageResource(R.drawable.s9);
                    return '9';
                case 48:
                    imageView.setImageResource(R.drawable.s10);
                    return 'T';
                case 49:
                    imageView.setImageResource(R.drawable.s11);
                    return 'J';
                case 50:
                    imageView.setImageResource(R.drawable.s12);
                    return 'Q';
                case 51:
                    imageView.setImageResource(R.drawable.s13);
                    return 'K';

                default:

                    return 0;
            }

        }
    }

    // gets the int value from the card
    public int getIntValueFromCard(char card) {

        switch (card) {
            case 'A':
                return 11;
            case 'K':
                return 10;
            case 'Q':
                return 10;
            case 'J':
                return 10;
            case 'T':
                return 10;
            case '9':
                return 9;
            case '8':
                return 8;
            case '7':
                return 7;
            case '6':
                return 6;
            case '5':
                return 5;
            case '4':
                return 4;
            case '3':
                return 3;
            case '2':
                return 2;
            default:
                return 0;

        }
    }

    // setupVariables
    private void setupVariables() {

        layout = (LinearLayout) findViewById(R.id.parentLayout);

        bank_Amount = (TextView) findViewById(R.id.buyinAmount);
        dealerScore = (TextView) findViewById(R.id.dealer_score);
        playerScore = (TextView) findViewById(R.id.player_score);
        tvBet = (TextView) findViewById(R.id.player_bet);
        tvHighestScore = (TextView) findViewById(R.id.highest_score);

        dealerCard1 = (ImageView) findViewById(R.id.dealer_card1);
        dealerCard2 = (ImageView) findViewById(R.id.dealer_card2);
        dealerCard3 = (ImageView) findViewById(R.id.dealer_card3);
        dealerCard4 = (ImageView) findViewById(R.id.dealer_card4);
        dealerCard5 = (ImageView) findViewById(R.id.dealer_card5);

        playerCard1 = (ImageView) findViewById(R.id.player_card1);
        playerCard2 = (ImageView) findViewById(R.id.player_card2);
        playerCard3 = (ImageView) findViewById(R.id.player_card3);
        playerCard4 = (ImageView) findViewById(R.id.player_card4);
        playerCard5 = (ImageView) findViewById(R.id.player_card5);

    }

    // resets the game
    private void resetEveryThing() {

        dealerCard1.setImageResource(R.drawable.cardback);
        dealerCard2.setImageResource(R.drawable.cardback);
        dealerCard3.setImageResource(R.drawable.cardback);
        dealerCard4.setImageResource(R.drawable.cardback);
        dealerCard5.setImageResource(R.drawable.cardback);

        playerCard1.setImageResource(R.drawable.cardback);
        playerCard2.setImageResource(R.drawable.cardback);
        playerCard3.setImageResource(R.drawable.cardback);
        playerCard4.setImageResource(R.drawable.cardback);
        playerCard5.setImageResource(R.drawable.cardback);

        disableButtons();
        dealerCardNumber = playerCardNumber = 0;
        dealersScore = playersScore = 0;
        bet = 0;
        _alCardsTracking.clear();

        for (int i = 0; i < 5; i++) {
            _dealerCardArray[i] = '0';
            _dealerScoreCount[i] = 0;
            _playerCardArray[i] = '0';
            _playerScoreCount[i] = 0;
        }

        showTextViews();

        // Setting up high score
        highScoreCompare(); // determines which score is higher and saves highest one

        if (total <= 0){
            playAllOverAgainAlertBox(); // play again alert box
        }

    }

    // alertbox that displays if the player wants to play again
    public void playAllOverAgainAlertBox(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);

        Thread t = new Thread(){
            public void run(){

                try{
                    sleep(1500);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // go back to buy in menu
                        alert.setMessage("Play Again");
                        alert.setPositiveButton("Get more money to play!",
                                new DialogInterface.OnClickListener(){

                                    public void onClick(DialogInterface dialog,
                                                        int which){
                                        Intent intent = new Intent(BlackjackActivity.this, BlackjackMenu.class);
                                        startActivity(intent);
                                    }
                                });

                        alert.setNegativeButton("Quit Game", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which){
                                // go back to home page
                                Intent intent = new Intent(BlackjackActivity.this, BlackjackHome.class);
                                startActivity(intent);
                            }
                        });

                        alert.show(); // alert box
                    }
                });
            }
        };
        t.start();
        //thread ends here
    }

    // clears values and sets the total
    private void showTextViews() {

        bank_Amount.setText(" $ " + total);
        bank_Amount.setText(String.valueOf(currencyFormat.format(total)));
        tvHighestScore.setText("Highest: " + highestScore);

        tvBet.setText("Bet: $ " + 0);
        dealerScore.setText("Dealer's Score : " + 0);
        playerScore.setText("Your Score : " + 0);

    }

    // displays alert for player to deal next hand
    public void alertBox() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        String message = "Play Again";
        String positive = "Next Hand";
        final String finalMessage = message;
        final String finalPositive = positive;
        Thread t = new Thread() {
            public void run() {

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        alert.setMessage(finalMessage);
                        alert.setNeutralButton(finalPositive,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        enableChips();
                                        btnGame.setVisibility(View.VISIBLE);
                                        resetEveryThing();
                                    }
                                });
                        alert.show();
                    }
                });
            }
        };
        t.start();
        // Thread ends here
    }

    // enables chips
    private void enableChips(){
        ImageButton btn5bet = (ImageButton)findViewById(R.id.five_BetAmount);
        btn5bet.setEnabled(true);

        ImageButton btn25bet = (ImageButton)findViewById(R.id.twentyfive_BetAmount);
        btn25bet.setEnabled(true);

        ImageButton btn100bet = (ImageButton)findViewById(R.id.hundred_BetAmount);
        btn100bet.setEnabled(true);
    }


    // Setting up high score
    private void highScoreCompare() {
        if (total > highestScore) {
            highestScore = total;
            tvHighestScore.setText("Highest: " + highestScore);
        }
    }

    // Saving highScore
    private void savingHighScore() {

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(saveFileName, MODE_PRIVATE)));
            writer.write("" + highestScore);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Loading high score
    private void loadingHighScore() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput(saveFileName)));
            String highScore = reader.readLine();
            highestScore = Double.parseDouble(highScore);
            reader.close();
            tvHighestScore.setText("Highest: " +highestScore);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // shows the dialog for the rules
    public void rulesClick(){
        AlertDialog.Builder rules = new AlertDialog.Builder(BlackjackActivity.this);
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



}