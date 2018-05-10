package com.example.android.sltmachin;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private ImageView ch;
    private ImageView st;
    private ImageView gra;
    private Handler handler;
    private Drawable[] a;
    private TextView collected;
    private Button startStop;
    private Wheel wheel;  //wheel for each pic
    private Wheel wheel2;
    private Wheel wheel3;
    private boolean isStarted;
    private SeekBar seek;
    private Update i;
    private Update i2;
    private Update i3;

    public static final Random RANDOM = new Random();

    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        ch = findViewById(R.id.chery);
        st = findViewById(R.id.strwbery);
        gra = findViewById(R.id.grape);
        startStop = findViewById(R.id.Sbutton);
        collected = findViewById(R.id.PC);


        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStarted){
                    wheel.stopWheel();
                    wheel2.stopWheel();
                    wheel3.stopWheel();

                    if (wheel.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex) {
                        collected.setText("Awesome we have a winner!! you won $15K");
                    } else if (wheel.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex
                            || wheel.currentIndex == wheel3.currentIndex) {
                        collected.setText("you won nothing but 2 fruits!!");
                    } else {
                        collected.setText("play as profissional or leave don't waste my energy!!");
                    }
                    startStop.setText("Start");
                    isStarted = false;

                } else {

                    wheel = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ch.setImageResource(img);
                                }
                            });
                        }
                    }, 200, randomLong(0, 200));

                    wheel.start();

                    wheel2 = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gra.setImageResource(img);
                                }
                            });
                        }
                    }, 200, randomLong(150, 400));

                    wheel2.start();

                    wheel3 = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    st.setImageResource(img);
                                }
                            });
                        }
                    }, 200, randomLong(150, 400));

                    wheel3.start();

                    startStop.setText("Stop");
                    collected.setText("");
                    isStarted = true;
                }
            }




        });

        if(savedInstanceState != null) {
            collected.setText(savedInstanceState.getString("collected points"));
        }

        seek = findViewById(R.id.seek);
        seek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(startStop.getText().toString().equals("STOP")) { // then the slots are spinning
                            i.modifyRate(progress);
                            i2.modifyRate(progress);
                            i3.modifyRate(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        a = new Drawable[3];
        a[0] = getResources().getDrawable(R.drawable.cherry);
        a[1] = getResources().getDrawable(R.drawable.grape);
        a[2] = getResources().getDrawable(R.drawable.strawberry);

        if(savedInstanceState != null) {
            ch.setImageDrawable(a[Integer.parseInt(savedInstanceState.getString("first"))]);
            st.setImageDrawable(a[Integer.parseInt(savedInstanceState.getString("second"))]);
            gra.setImageDrawable(a[Integer.parseInt(savedInstanceState.getString("thrd"))]);
        } else {
            ch.setImageDrawable(a[0]);
            st.setImageDrawable(a[1]);
            gra.setImageDrawable(a[2]);
        }


        i = new Update(ch, 0);
        i2 = new Update(st, 1);
        i3 = new Update(gra, 2);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("collected points", collected.getText().toString());
        savedInstanceState.putString("first", i.getCurrent() + "");
        savedInstanceState.putString("second", i2.getCurrent() + "");
        savedInstanceState.putString("thrd", i3.getCurrent() + "");
    }


    public class Update implements Runnable {

        private ImageView i;
        private int current;
        private int rate;
        private int initRate;

        public Update(ImageView i, int current) {
            this.i = i;
            this.current = current;

            Random r = new Random();
            rate = (r.nextInt(101) + 100);
            initRate = rate;

            rate += (2 - seek.getProgress()) * 200;

        }

        public void run() {
            current = (current + 1) % 4;
            i.setImageDrawable(a[current]);
            handler.postDelayed(this, rate);
        }

        public int getCurrent() {
            return this.current;
        }

        public void modifyRate(int x) {
            this.rate = initRate + (2 - x) * 200;

        }


    }

    public void startSpinning(View v) {
        if(startStop.getText().toString().equals("START")) {
            updateScore(0); // resets the score back to 0.
            handler.post(i);
            handler.post(i2);
            handler.post(i3);
            startStop.setText("STOP");
        } else {
            handler.removeCallbacksAndMessages(null);
            startStop.setText("START");
            updateScore();

        }


    }

    public void updateScore() {
        int points = 0;
        Update[] a = {i, i2, i3};
        if(a[0].getCurrent() == 0 && a[1].getCurrent() == 0 && a[2].getCurrent() == 0) {
            points = 200;
        } else {
            for(int i = 0; i < a.length; i++) {
                if (a[i].getCurrent() == 0) {
                    points -= 50;
                } else if (a[i].getCurrent() == 1) {
                    points += 25;
                } else if (a[i].getCurrent() == 2) {
                    points += 30;
                } else {
                    points += 40;
                }
            }

        }

        if(points >= 100) {
            Toast.makeText(this, "YOU WIN!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "don't just quit the game just started", Toast.LENGTH_LONG).show();
        }
        collected.setText("points: " + points);
    }

    public void updateScore(int x) {
        collected.setText("points: " + 0);
    }


}