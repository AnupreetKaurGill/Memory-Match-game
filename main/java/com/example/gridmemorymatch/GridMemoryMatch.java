package com.example.gridmemorymatch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GridMemoryMatch extends Activity implements AdapterView.OnItemClickListener {

    String dims;
    String imageText;
    String participant;

    ImageView currentView;
    private int countPair;
    Integer [] drawable;
    static Integer [] pos;
    int currentPos;
    Handler delay;
    Handler timer;
    ImageView first;
    ImageView last;
    int firstPos;
    int lastPos;

    public final String WORK_DIR = "MatchProgramResults";
    private final int WRITE_PERM = 100;
    boolean permAccepted;

    int timeInSecs; //VALUE USED FOR TIMER
    int attemptTotal; //VALUE USED FOR ATTEMPT COUNT
    boolean isSolved; //CHECK FOR IF ALL MATCHES FOUND

    ImageAdapter adapt;

    GridView grid;
    TextView timeText;
    TextView attemptCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERM);

        Bundle b = getIntent().getExtras();

        participant = b.getString("participant");
        dims = b.getString("dimension");
        imageText = b.getString("imageOrText");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int columnWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels / 3 - 12
                : dm.heightPixels / 3 - 12;

        if (dims.equals("2x4")) {
            setContentView(R.layout.grid_2x4);
            grid = (GridView) findViewById(R.id.gridview2x4);
        }
        else if (dims.equals("2x7")){
            //setContentView(R.layout.grid_7x2);
            setContentView(R.layout.grid_2x4);
            grid = (GridView) findViewById(R.id.gridview2x4);
            columnWidth = columnWidth / 2;
        }

        timeText = (TextView)findViewById(R.id.textTimer);
        attemptCount = (TextView)findViewById(R.id.attemptCount);
        init();
        attemptCount.setText("Attempts: " + attemptTotal);

        adapt = new ImageAdapter(this);
        grid.setAdapter(adapt);
        adapt.setColumnWidth(columnWidth);
        grid.setColumnWidth(columnWidth);
        grid.setOnItemClickListener(this);

    }
    private void init() {
        attemptTotal = 0;
        isSolved = false;
        currentView = null;
        countPair = 0;
        timeInSecs = 0;
        if (dims.equals("2x4") && imageText.equals("Images")) {
            drawable = new Integer [] {R.drawable.sample_0, R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3};
            pos = new Integer[]{0, 1, 2, 3, 0, 1, 2, 3};
        }
        else if (dims.equals("2x4") && imageText.equals("Text")){
            drawable = new Integer [] {R.drawable.w1, R.drawable.w2, R.drawable.w3, R.drawable.w4};
            pos = new Integer[]{0, 1, 2, 3, 0, 1, 2, 3};
        }
        else if (dims.equals("2x7") && imageText.equals("Text")){
            drawable = new Integer [] {R.drawable.w1, R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5, R.drawable.w6, R.drawable.w7};
            pos = new Integer[]{0, 1, 2, 3, 4, 5, 6, 0, 1, 2, 3, 4, 5, 6};
        }
        else if (dims.equals("2x7") && imageText.equals("Images")){
            drawable = new Integer [] {R.drawable.sample_0, R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5, R.drawable.sample_6};
            pos = new Integer[]{0, 1, 2, 3, 4, 5, 6, 0, 1, 2, 3, 4, 5, 6};
        }
        List<Integer> array = new ArrayList<Integer>(Arrays.asList(pos));
        Collections.shuffle(array);
        pos = array.toArray(new Integer[array.size()]);
        currentPos = -1;
        delay = new Handler();
        timer = new Handler();
        Timer();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (currentPos < 0) {
            //CODE FOR WHEN PLAYER CLICKS ON FIRST CARD IN ATTEMPT
            currentPos = i;
            currentView = (ImageView) view;
            first = (ImageView)view;
            cardFlip(adapterView, first, drawable[pos[i]]);
        }
        else {
            if (currentPos == i) {
                //CODE FOR IF PLAYER CLICKS ON THE SAME CARD AGAIN
                cardFlip(adapterView, first, R.drawable.cover);
                attemptTotal++;
                attemptCount.setText("Attempts: " + attemptTotal);
                //((ImageView)view).setImageResource(R.drawable.cover);
            }
            else if (pos[currentPos] != pos[i]) {
                //CODE FOR IF PLAYER CLICKS ON NEW IMAGE THAT IS NOT A MATCH
                last = (ImageView)view;
                cardFlip(adapterView, last, drawable[pos[i]]);

                delay.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardFlip(adapterView, last, R.drawable.cover);
                        cardFlip(adapterView, currentView, R.drawable.cover);
                    }
                }, 1000);
                attemptTotal++;
                attemptCount.setText("Attempts: " + attemptTotal);
            }
            else {
                //CODE FOR IF PLAYER FINDS A MATCH
                last = (ImageView)view;
                cardFlip(adapterView, last, drawable[pos[i]]);
                delay.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        first.setVisibility(View.INVISIBLE);
                        last.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
                attemptTotal++;
                attemptCount.setText("Attempts: " + attemptTotal);
                countPair++;

                if (countPair == pos.length / 2) {
                    //THIS IS THE WIN CONDITION, ADD CODE HERE FOR ANYTHING REGARDING WIN
                    isSolved = true;
                    try {
                        if (permAccepted) {
                            Toast.makeText(getApplicationContext(), "You Win, results saved to " + Environment.getExternalStorageDirectory().toString() + "/" + WORK_DIR, Toast.LENGTH_LONG).show();
                            writeToFile(timeInSecs, attemptTotal, imageText, dims);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "You Win", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }
            currentPos = -1;
        }
    }

    public void cardFlip(AdapterView<?> view, ImageView card, Integer img) {
        //Code used from Stack Overflow post at <https://stackoverflow.com/questions/37028694/flipping-and-changing-image-in-imageview>
        view.setEnabled(false);
        card.setRotationY(0f);
        card.animate().rotationY(90f).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                card.setImageResource(img);
                card.setRotationY(270f);
                card.animate().rotationY(360f).setListener(null);
                view.setEnabled(true);
            }
        } );
    }

    public void Timer() {
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeInSecs++;
                timeText.setText("Timer: " + timeInSecs + "s");
                if (isSolved) {
                    timer.removeCallbacks(this::run);
                }
                else {
                    timer.postDelayed(this::run, 1000);
                }
            }
        }, 0);
    }

    public void writeToFile(int time, int attempts, String imgOrTex, String cardNum) throws IOException {

        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;

        //if MatchProgramResults folder doesn't exist in directory, makes one
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/" + WORK_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File results = new File(dir.getAbsolutePath(), dateFormat.format(date) + ".txt") ;
        Log.i(new String("DEBUGGER"), results.getAbsolutePath());
        BufferedWriter out = new BufferedWriter(new FileWriter(results));
        out.write("Participant Number: " + participant + "\n");
        out.write("Images or Text Used for Matching: " + imgOrTex + "\n");
        out.write("Number of Cards Used (ColumnsxRows): " + cardNum + "\n");
        out.write("Total Time Taken: " + time + "s \n");
        out.write("Total Number of Attempts: " + attempts + "\n");
        out.flush();
        out.close();

        //Makes file visible to explorer
        MediaScannerConnection.scanFile(this, new String[]{results.getAbsolutePath()}, null, null);

    }

    public void onRequestPermissionsResult(int req, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (req == WRITE_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //checks to see if permission is granted
                permAccepted = true;
            }
            else {
                permAccepted = false;
            }
        }
    }

}