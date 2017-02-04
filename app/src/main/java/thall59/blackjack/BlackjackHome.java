package thall59.blackjack;

import android.content.Intent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity; // base class for activities
import android.os.Bundle; // saving state information
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // for buyin amount input
import android.widget.ImageButton;
import android.widget.TextView; // displaying text


public class BlackjackHome extends AppCompatActivity{

    // declarations for variables
    private ImageButton newgameButton;
    Intent mainmenuIntent;
    MediaPlayer mp;


    // called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call superclass's version
        setContentView(R.layout.activity_blackjack); // inflate the GUI
        getSupportActionBar().setDisplayShowHomeEnabled(true); // display icon on actionbar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher); // icon
        // Locate the button in activity_display_message.xml
        newgameButton = (ImageButton) findViewById(R.id.play_now_button);


        // Capture button clicks
        newgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start BlackjackActivity.class
                Intent mainmenuIntent = new Intent(BlackjackHome.this, BlackjackMenu.class);

                startActivity(mainmenuIntent);
                recreate();
            }

        }); // end newgameButton OnClickListener

    } // end method onCreate


}
