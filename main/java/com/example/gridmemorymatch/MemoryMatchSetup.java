package com.example.gridmemorymatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MemoryMatchSetup extends Activity {
    final static String[] participants = { "1", "2", "3", "4", "5", "6", "7", "8" };
    final static String[] dim = { "2x4", "2x7" }; // NOTE: do not change strings
    final static String[] imageText = { "Images", "Text" };

    Spinner spinParticpants, spinDimension, spinImageText;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setup);

        spinParticpants = (Spinner) findViewById(R.id.paramPart);
        ArrayAdapter<CharSequence> adapter0 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, participants);
        spinParticpants.setAdapter(adapter0);

        spinDimension = (Spinner) findViewById(R.id.paramDim);
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, dim);
        spinDimension.setAdapter(adapter1);

        spinImageText = (Spinner) findViewById(R.id.paramTextImage);
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, imageText);
        spinImageText.setAdapter(adapter2);

    }



    // called when the "OK" button is tapped
    public void clickOK(View view)
    {
        // get user's choices...
        String participant = (String) spinParticpants.getSelectedItem();
        String dimension = (String) spinDimension.getSelectedItem();
        String imageOrText = (String) spinImageText.getSelectedItem();

        // bundle up parameters to pass on to activity
        Bundle b = new Bundle();
        b.putString("participant", participant);
        b.putString("dimension", dimension);
        b.putString("imageOrText", imageOrText);

        // start experiment activity
        Intent i = new Intent(getApplicationContext(), GridMemoryMatch.class);
        i.putExtras(b);
        startActivity(i);

    }

    /** Called when the "Exit" button is pressed. */
    public void clickExit(View view)
    {
        super.onDestroy(); // cleanup
        this.finish(); // terminate
    }
}
