package com.example.android.scorekeeper;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.android.scorekeeper.databinding.ActivityMainBinding;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding bind;
    ScoreViewModel viewModel;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    // all needed to set the date
    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "dd.MM.yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

    private static String SCORE_A = "goalsA";
    private static String YELLOW_A = "yellowA";
    private static String RED_A = "redA";
    private static String SCORE_B = "goalsB";
    private static String YELLOW_B = "yellowB";
    private static String RED_B = "redB";

    boolean resetClick = false;

    AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlayer.start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mediaPlayer.stop();
                    releaseMediaPlayer();
                    break;
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);

        displayScoreForA(viewModel.getScoreA());
        displayScoreForB(viewModel.getScoreB());
        displayYellowCardsForA(viewModel.getYellowA());
        displayYellowCardsForB(viewModel.getYellowB());
        displayRedCardsForA(viewModel.getRedA());
        displayRedCardsForB(viewModel.getRedB());

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (savedInstanceState != null) {
            bind.teamAScore.setText(savedInstanceState.getString(SCORE_A));
            bind.teamAYellowcards.setText(savedInstanceState.getString(YELLOW_A));
            bind.teamARedcards.setText(savedInstanceState.getString(RED_A));
            bind.teamBScore.setText(savedInstanceState.getString(SCORE_B));
            bind.teamBYellowcards.setText(savedInstanceState.getString(YELLOW_B));
            bind.teamBRedcards.setText(savedInstanceState.getString(RED_B));
        }

        // Set the current date and allows to pick another date
        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        bind.date.setText(dateString);

        // change the date and update dateField
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        // when dateField clicked, calendar shows up
        bind.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // the keyboard doesn't show up just after launching the app
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void updateDate() {
        bind.date.setText(sdf.format(myCalendar.getTime()));
    }

    // saving number of goals and cards before going to background
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SCORE_A, bind.teamAScore.getText().toString());
        outState.putString(YELLOW_A, bind.teamAYellowcards.getText().toString());
        outState.putString(RED_A, bind.teamARedcards.getText().toString());
        outState.putString(SCORE_B, bind.teamBScore.getText().toString());
        outState.putString(YELLOW_B, bind.teamBYellowcards.getText().toString());
        outState.putString(RED_B, bind.teamBRedcards.getText().toString());
    }

    // restoring number of goals and cards after coming back from background
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        bind.teamAScore.setText(savedInstanceState.getString(SCORE_A));
        bind.teamAYellowcards.setText(savedInstanceState.getString(YELLOW_A));
        bind.teamARedcards.setText(savedInstanceState.getString(RED_A));
        bind.teamBScore.setText(savedInstanceState.getString(SCORE_B));
        bind.teamBYellowcards.setText(savedInstanceState.getString(YELLOW_B));
        bind.teamBRedcards.setText(savedInstanceState.getString(RED_B));
    }

    public void addGoal(View v) {
        createMediaPlayer(R.raw.goalsound);
        switch (v.getId()) {
            case R.id.goalA_button:
                displayScoreForA(viewModel.getScoreA() + 1);
                break;
            case R.id.goalB_button:
                displayScoreForB(viewModel.getScoreB() + 1);
                break;
        }
    }

    public void addYellowCard(View v) {
        createMediaPlayer(R.raw.shortwhistle);
        switch (v.getId()) {
            case R.id.yellowA_button:
                displayYellowCardsForA(viewModel.getYellowA() + 1);
                break;
            case R.id.yellowB_button:
                displayYellowCardsForB(viewModel.getYellowB() + 1);
                break;
        }
    }

    public void addRedCard(View v) {
        createMediaPlayer(R.raw.refereewhistle);
        switch (v.getId()) {
            case R.id.redA_button:
                displayRedCardsForA(viewModel.getRedA() + 1);
                break;
            case R.id.redB_button:
                displayRedCardsForB(viewModel.getRedB() + 1);
                break;
        }
    }

    public void displayScoreForA(int scoreA) {
        viewModel.setScoreA(scoreA);
        bind.teamAScore.setText(String.valueOf(scoreA));
    }

    public void displayYellowCardsForA(int yellowA) {
        viewModel.setYellowA(yellowA);
        bind.teamAYellowcards.setText(String.valueOf(yellowA));
    }

    public void displayRedCardsForA(int redA) {
        viewModel.setRedA(redA);
        bind.teamARedcards.setText(String.valueOf(redA));
    }

    public void displayScoreForB(int scoreB) {
        viewModel.setScoreB(scoreB);
        bind.teamBScore.setText(String.valueOf(scoreB));
    }

    public void displayYellowCardsForB(int yellowB) {
        viewModel.setYellowB(yellowB);
        bind.teamBYellowcards.setText(String.valueOf(yellowB));
    }

    public void displayRedCardsForB(int redB) {
        viewModel.setRedB(redB);
        bind.teamBRedcards.setText(String.valueOf(redB));
    }

    public void sendMail(View view) {
        // Get all the needed information to strings.
        String date = bind.date.getText().toString();
        String teamA = bind.teamAName.getText().toString();
        if (teamA.equals("")) {
            teamA = getString(R.string.team_a);
        }
        String teamB = bind.teamBName.getText().toString();
        if (teamB.equals("")) {
            teamB = getString(R.string.team_b);
        }
        String scoreA = bind.teamAScore.getText().toString();
        String scoreB = bind.teamBScore.getText().toString();
        String redA = bind.teamARedcards.getText().toString();
        String yellowA = bind.teamAYellowcards.getText().toString();
        String redB = bind.teamBRedcards.getText().toString();
        String yellowB = bind.teamBYellowcards.getText().toString();

        // Create e-mail and set intent to mail app.
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, teamA + getString(R.string.versus) + teamB + getString(R.string.emdash) + getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, createMatchSummary(date, teamA, teamB, scoreA, scoreB, redA, yellowA, redB, yellowB));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String createMatchSummary(String date, String nameA, String nameB, String goalsA, String goalsB, String redCardsA, String yellowCardsA, String redCardsB, String yellowCardsB) {
        return nameA + getString(R.string.emdash) + nameB + "\n" +
                goalsA + getString(R.string.emdash) + goalsB + "\n\n" +
                getString(R.string.red_cards_info) + nameA + ": " + redCardsA + "\n" +
                getString(R.string.yellow_cards_info) + nameA + ": " + yellowCardsA + "\n\n" +
                getString(R.string.red_cards_info) + nameB + ": " + redCardsB + "\n" +
                getString(R.string.yellow_cards_info) + nameB + ": " + yellowCardsB + "\n\n" +
                getString(R.string.match_time_info) + date + ".";
    }

    public void resetScore(View v) {
        Button resetButton = findViewById(R.id.reset_button);
        if (!resetClick) {
            displayResetWarning(getString(R.string.reset_warning));
            resetButton.setBackgroundColor(getResources().getColor(R.color.colorButton));
            resetClick = true;
        } else {
            displayScoreForA(0);
            displayYellowCardsForA(0);
            displayRedCardsForA(0);
            displayScoreForB(0);
            displayYellowCardsForB(0);
            displayRedCardsForB(0);
            bind.teamAName.getText().clear();
            bind.teamBName.getText().clear();
            resetButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            resetClick = false;
        }
    }

    public void displayResetWarning(String warningReset) {
        Toast.makeText(getApplicationContext(), warningReset, Toast.LENGTH_SHORT).show();
    }

    private void createMediaPlayer(int soundId) {
        int result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer.create(this, soundId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(completionListener);
        }
    }
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }
}