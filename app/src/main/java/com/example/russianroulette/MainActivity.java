package com.example.russianroulette;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    private SoundPool sounds;
    private int sound_shot;
    private int sound_shot_false;
    private int sound_baraban;
    private ImageView blood_image;
    private int on_shot = 3;
    private int max_number = 5;
    private int random = 0;

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
        blood_image = findViewById(R.id.image_blood);

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
                                sounds.play(sound_shot,1.0f,1.0f,1,0,1);
                                blood_image.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                sounds.play(sound_shot_false,1.0f,1.0f,1,0,1);
                            }
                            break;
                        case "барабан":
                            sounds.play(sound_baraban,1.0f,1.0f,1,0,1);
                            blood_image.setVisibility(View.GONE);
                            random = new Random().nextInt(max_number);
                            break;
                    }
            }
        }
    }
}