package com.example.russianroulette;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.AnimationDrawable;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    private SoundPool sounds;
    private TextView id_pulls;
    private TextView id_shots;
    private int pulls;
    private int shots;
    private int sound_shot;
    private int sound_shot_false;
    private int sound_baraban;
    private ImageView gun;
    private int on_shot = 3;
    private int max_number = 10;
    private int random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSoundPool();
        loadSounds();
        init();

    }
    protected void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
    }
    private void loadSounds()
    {
        sound_shot = sounds.load(this,R.raw.revolver_shot, 1);
        sound_shot_false = sounds.load(this,R.raw.gun_false, 1);
        sound_baraban = sounds.load(this,R.raw.revolver_baraban, 1);

    }

    private void init()
    {
        gun = findViewById(R.id.gun);
        id_pulls = findViewById(R.id.id_pulls);
        id_shots = findViewById(R.id.id_shots);
        pulls=0;
        shots=0;
        gun.setBackgroundResource(R.drawable.baraban_animation);
        AnimationDrawable gunBaraban = (AnimationDrawable) gun.getBackground();
        gun.postDelayed(new Runnable() {
            public void run() {
                gunBaraban.start();
            }
        }, 200);
        random = new Random().nextInt(max_number);
    }

    public void onClickMic(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
        {
            switch (requestCode)
            {
                case 10:
                    ArrayList<String> text =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    switch(text.get(0)){
                        case "огонь":
                            if(random == on_shot )
                            {
                                gun.setBackgroundResource(R.drawable.fire_animation);
                                AnimationDrawable gunFire = (AnimationDrawable) gun.getBackground();
                                gun.postDelayed(new Runnable() {
                                    public void run() {
                                        if (gunFire.isRunning())
                                            gunFire.stop();
                                        gunFire.start();
                                    }
                                }, 200);
                                sounds.play(sound_shot,1.0f,1.0f,1,0,1);
                                pulls=0;
                                shots=shots+1;
                            }
                            else
                            {
                                gun.setBackgroundResource(R.drawable.pull_animation);
                                AnimationDrawable gunPull = (AnimationDrawable) gun.getBackground();
                                gun.postDelayed(new Runnable() {
                                    public void run() {
                                        if (gunPull.isRunning())
                                            gunPull.stop();
                                        gunPull.start();
                                    }
                                }, 200);
                                sounds.play(sound_shot_false,1.0f,1.0f,1,0,1);
                                pulls=pulls+1;
                            }
                            random++;
                            if (random>max_number) {
                                random=0;
                            }
                            id_pulls.setText("Pulls: "+pulls);
                            id_shots.setText("Shots: "+shots);
                            break;
                        case "барабан":
                            gun.setBackgroundResource(R.drawable.baraban_animation);
                            AnimationDrawable gunBaraban = (AnimationDrawable) gun.getBackground();
                            gun.postDelayed(new Runnable() {
                                public void run()
                                {
                                    if (gunBaraban.isRunning())
                                        gunBaraban.stop();
                                    gunBaraban.start();
                                }
                            }, 200);
                            sounds.play(sound_baraban,1.0f,1.0f,1,0,1);
                            random = new Random().nextInt(max_number);
                            break;
                    }
            }
        }
    }

    public void onClickReference(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help")
                .setMessage("Commands: \"барабан\" - put random value in current_slot. \n \"огонь\" - pull the trigger and increase current_slot by 1 (if it>max_value it'll go back to zero). If current_slot equals certain value it will make shot.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}