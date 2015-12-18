package com.ilenlab.ilentt.merrychristmas;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static int oneTimeOnly = 0;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static String FUTURE_DATE = "18/12/2015 22:30:00";
    private TextView txtDay, txtHour, txtMinute, txtSecond, txtEvent, txtRemind, txtTotal, txtSong;
    private Button btnPlay, btnPause;
    private MediaPlayer mediaPlayer;
    private double startTime;
    private double finalTime;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private ImageView img_animation;
    private float length;
    private RelativeLayout relativeRoot;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            txtRemind.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );
            seekBar.setProgress((int) startTime);
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.relative_container).setVisibility(View.GONE);

        Typeface tfTimer = Typeface.createFromAsset(this.getAssets(),"fonts/digital-7.ttf");
        Typeface tfEvent = Typeface.createFromAsset(this.getAssets(),"fonts/MerryChristmasFlake.ttf");

        length = (float) Resources.getSystem().getDisplayMetrics().widthPixels;
        img_animation = (ImageView) findViewById(R.id.img_animation);

        txtDay = (TextView) findViewById(R.id.textview_Day);
        txtDay.setTypeface(tfTimer);

        txtHour = (TextView) findViewById(R.id.textview_Hour);
        txtHour.setTypeface(tfTimer);

        txtMinute = (TextView) findViewById(R.id.textview_Minute);
        txtMinute.setTypeface(tfTimer);

        txtSecond = (TextView) findViewById(R.id.textview_Second);
        txtSecond.setTypeface(tfTimer);

        txtEvent = (TextView) findViewById(R.id.textview_Event);
        txtEvent.setTypeface(tfEvent);

        txtRemind = (TextView) findViewById(R.id.textview_Remind);
        txtTotal = (TextView) findViewById(R.id.textview_Total);
        txtSong = (TextView) findViewById(R.id.textview_Song);
        txtSong.setText("We wish you a merry christmas");

        btnPlay = (Button) findViewById(R.id.button_Play);
        btnPause = (Button) findViewById(R.id.button_Pause);

        mediaPlayer = mediaPlayer.create(this, R.raw.song);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setClickable(false);
        btnPause.setEnabled(false);

        relativeRoot = (RelativeLayout) findViewById(R.id.relative_root);

        countDownStart();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekBar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                txtRemind.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                txtTotal.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                seekBar.setProgress((int) startTime);
                handler.postDelayed(UpdateSongTime, 100);

                btnPlay.setEnabled(false);
                btnPause.setEnabled(true);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                btnPause.setEnabled(false);
                btnPlay.setEnabled(true);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void moveImage(long duration, float from, float to) {
        try {
            TranslateAnimation animation = new TranslateAnimation(from, to, 0.0f, 0.0f);
            animation.setDuration(duration);
            img_animation.startAnimation(animation);

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                float to = 0.0f;
                float s = 0.0f;
                handler.postDelayed(this, 1000);
                try {

                    Date futureDate = dateFormat.parse(FUTURE_DATE);
                    Date currentDate = new Date();

                    if(currentDate.before(futureDate)) {
                        long duration = futureDate.getTime() - currentDate.getTime();
                        long d = futureDate.getTime() - currentDate.getTime();

                        long days = duration / (24 * 60 * 60 * 1000);
                        duration -= days * (24 * 60 * 60 * 1000);

                        long hours = duration / (60 * 60 * 1000);
                        duration -= hours * (60 * 60 * 1000);

                        long minutes = duration / (60 * 1000);
                        duration -= minutes * (60 * 1000);

                        long seconds = duration / 1000;

                        txtDay.setText("" + String.format("%02d", days));
                        txtHour.setText("" + String.format("%02d", hours));
                        txtMinute.setText("" + String.format("%02d", minutes));
                        txtSecond.setText("" + String.format("%02d", seconds));


                        s += length/seconds;
                        if(to > length) {
                            to = length;
                        } else {
                            to += s;
                        }

                        moveImage(d, s, to);

                    } else {
                        performAction();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    public void performAction() {
        findViewById(R.id.textview_Day).setVisibility(View.GONE);
        findViewById(R.id.textview_Hour).setVisibility(View.GONE);
        findViewById(R.id.textview_Minute).setVisibility(View.GONE);
        findViewById(R.id.textview_Second).setVisibility(View.GONE);

        findViewById(R.id.textview_txtDay).setVisibility(View.GONE);
        findViewById(R.id.textview_txtHour).setVisibility(View.GONE);
        findViewById(R.id.textview_txtMinute).setVisibility(View.GONE);
        findViewById(R.id.textview_txtSecond).setVisibility(View.GONE);

        findViewById(R.id.img_animation).setVisibility(View.GONE);

        txtEvent.setVisibility(View.VISIBLE);
        txtEvent.setText("Merry Christmas!");
        findViewById(R.id.relative_container).setVisibility(View.VISIBLE);

        relativeRoot.setBackgroundResource(R.drawable.bg2);
        //btnPlay.performClick();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
