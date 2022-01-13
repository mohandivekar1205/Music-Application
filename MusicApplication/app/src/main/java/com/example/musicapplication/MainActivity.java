package com.example.musicapplication;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView Title, Artist, textcurrenttime, texttotalduration;
    SeekBar playseekBar;
    ImageView imageplaypause, previousbtn, nextbtn, Poster,Shuffle,repeat;
    MediaPlayer mediaPlayer;
    String audioUrl;
    int playposition;
    int count=0;
    Handler handler = new Handler();
    FirebaseDatabase getImagefirebaseDatabase, getTitlefirebaseDatabase, getartistfirebaseDatabase, getaudiofirebaseDatabase;
    DatabaseReference getImage, getTitle, getartist, getaudio, databaseReference;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Poster = findViewById(R.id.Poster);

        //set Height and width of cover
        Poster.getLayoutParams().height = 840;
        Poster.getLayoutParams().width = 840;

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        Title = findViewById(R.id.Title);
        Artist = findViewById(R.id.Artist);
        playseekBar = findViewById(R.id.seekBar);
        textcurrenttime = findViewById(R.id.starttime);
        texttotalduration = findViewById(R.id.stoptime);
        imageplaypause = findViewById(R.id.PlauPauseButton);
        previousbtn = findViewById(R.id.Priviosbtn);
        nextbtn = findViewById(R.id.nextbtn);
        Shuffle=findViewById(R.id.Shuffle);
        repeat=findViewById(R.id.repeat);


        getImagefirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = getImagefirebaseDatabase.getReference();
        getImage = databaseReference.child("Song1").child("Cover");

        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String link = dataSnapshot.getValue(String.class);
                Picasso.get().load(link).into(Poster);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(MainActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });





        getTitlefirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = getTitlefirebaseDatabase.getReference();
        getTitle = databaseReference.child("Song1").child("Title");

        getTitle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.getValue(String.class);
                Title.setText(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error Loading Title", Toast.LENGTH_SHORT).show();
            }
        });


        getartistfirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = getartistfirebaseDatabase.getReference();
        getartist = databaseReference.child("Song1").child("Artist");

        getartist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String artist = snapshot.getValue(String.class);
                Artist.setText(artist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error Loading Artist Name", Toast.LENGTH_SHORT).show();
            }
        });

        getaudiofirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = getaudiofirebaseDatabase.getReference();
        getaudio = databaseReference.child("Song1").child("Audio");

        getaudio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                audioUrl = snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error Loading Audio", Toast.LENGTH_SHORT).show();
            }
        });


        //playseekBar.setMax(100);

        imageplaypause.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
                imageplaypause.setImageResource(R.drawable.baseline_play_circle_filled_white_20);
            } else {
                mediaPlayer.start();
                imageplaypause.setImageResource(R.drawable.baseline_pause_circle_filled_white_20);
                updateSeekbar();
            }
        });






         Shuffle.setOnClickListener(new View.OnClickListener() {
            boolean Shufflebtn;
            @Override
            public void onClick(View view) {
                if(!Shufflebtn){
                    Shufflebtn=true;
                    Shuffle.setImageResource(R.drawable.baseline_shuffle_on_white_18);
                    Snackbar.make(view, "Shuffle On", Snackbar.LENGTH_LONG).show();
                }else{
                    Shufflebtn=false;
                    Shuffle.setImageResource(R.drawable.baseline_shuffle_white_18);
                    Snackbar.make(view, "Shuffle Off", Snackbar.LENGTH_LONG).show();
                }
            }
        });

           nextbtn.setOnClickListener(view -> {
               count++;
               databaseReference.orderByChild("Title").equalTo(count).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       String title = snapshot.getValue(String.class);
                       Title.setText(title);
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       Toast.makeText(MainActivity.this, "Error Loading changing Audio", Toast.LENGTH_SHORT).show();
                   }
               });
           });




        previousbtn.setOnClickListener(view -> {
            count--;
            databaseReference.orderByChild("Title").equalTo(count).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = snapshot.getValue(String.class);
                    Title.setText(title);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error Loading changing Audio", Toast.LENGTH_SHORT).show();
                }
            });
        });


         playseekBar.setOnTouchListener((view, motionEvent) -> {
            SeekBar seekBar = (SeekBar) view;
            playposition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
            mediaPlayer.seekTo(playposition);
            textcurrenttime.setText(milliSecondToTiming(mediaPlayer.getCurrentPosition()));
            return false;
        });



        preparedMediaPlayer();

         mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> playseekBar.setSecondaryProgress(i));




         mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            playseekBar.setProgress(0);
            imageplaypause.setImageResource(R.drawable.baseline_play_circle_filled_white_20);
            textcurrenttime.setText(R.string.zero1);
            texttotalduration.setText(R.string.zero2);
            mediaPlayer.reset();
            preparedMediaPlayer();
        });
    }


    public void preparedMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            texttotalduration.setText(milliSecondToTiming(mediaPlayer.getDuration()));
        } catch (Exception exception) {
            //Snackbar.make(coordinatorLayout, "Error Are Occured", Snackbar.LENGTH_LONG).show();
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }




    public Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textcurrenttime.setText(milliSecondToTiming(currentDuration));
        }
    };



    public void updateSeekbar() {
        if (mediaPlayer.isPlaying()) {
            playseekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) + 100));
            handler.postDelayed(updater, 100);
        }
    }



    public String milliSecondToTiming(long millingSecond) {
        String timerString = "";
        String secondsString;

        int hourse = ((int) (millingSecond / (1000 * 60 * 60)));
        int minutes = ((int) (millingSecond % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = ((int) (millingSecond % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hourse > 0) {
            timerString = hourse + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }







    //back Button code
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}