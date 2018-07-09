package com.example.android.scorekeeper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
    private int scoreA = 0;
    private int yellowCardsForA = 0;
    private int redCardsForA = 0;
    private int scoreB = 0;
    private int yellowCardsForB = 0;
    private int redCardsForB = 0;

    // all needed to set the date
    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "dd.MM.yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

    //sounds
    MediaPlayer soundGoal;
    MediaPlayer soundYellow;
    MediaPlayer soundRed;

    private static String SCORE_A = "goalsA";
    private static String YELLOW_A = "yellowA";
    private static String RED_A = "redA";
    private static String SCORE_B = "goalsB";
    private static String YELLOW_B = "yellowB";
    private static String RED_B = "redB";

    boolean resetClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        displayScoreForA(0);
        displayScoreForB(0);
        displayYellowCardsForA(0);
        displayYellowCardsForB(0);
        displayRedCardsForA(0);
        displayRedCardsForB(0);

        // find all the sounds to play
        soundGoal = MediaPlayer.create(this, R.raw.goalsound);
        soundYellow = MediaPlayer.create(this, R.raw.shortwhistle);
        soundRed = MediaPlayer.create(this, R.raw.refereewhistle);

        if (savedInstanceState != null) {
            CharSequence savedScoreA = savedInstanceState.getCharSequence(SCORE_A);
            bind.teamAScore.setText(savedScoreA);
            CharSequence savedYellowA = savedInstanceState.getCharSequence(YELLOW_A);
            bind.teamAYellowcards.setText(savedYellowA);
            CharSequence savedRedA = savedInstanceState.getCharSequence(RED_A);
            bind.teamARedcards.setText(savedRedA);
            CharSequence savedScoreB = savedInstanceState.getCharSequence(SCORE_B);
            bind.teamBScore.setText(savedScoreB);
            CharSequence savedYellowB = savedInstanceState.getCharSequence(YELLOW_B);
            bind.teamBYellowcards.setText(savedYellowB);
            CharSequence savedRedB = savedInstanceState.getCharSequence(RED_B);
            bind.teamBRedcards.setText(savedRedB);
        }

        // The below code which sets the current date and allows to pick another date written with help:
        // http://www.moo-code.me/en/2017/04/16/how-to-popup-datepicker-calendar/

        // calendar on create - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);
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
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // the keyboard doesn't show up just after launching the app
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void updateDate() {
        bind.date.setText(sdf.format(myCalendar.getTime()));
    }

    // saving number of goals and cards before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SCORE_A, bind.teamAScore.getText());
        outState.putCharSequence(YELLOW_A, bind.teamAYellowcards.getText());
        outState.putCharSequence(RED_A, bind.teamARedcards.getText());
        outState.putCharSequence(SCORE_B, bind.teamBScore.getText());
        outState.putCharSequence(YELLOW_B, bind.teamBYellowcards.getText());
        outState.putCharSequence(RED_B, bind.teamBRedcards.getText());
    }

    // restoring number of goals and cards after rotation
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

    public void addGoal(View v){
        soundGoal.start();
        switch (v.getId()) {
            case R.id.goalA_button:
                String goalsA = bind.teamAScore.getText().toString();
                scoreA = Integer.parseInt(goalsA);
                scoreA += 1;
                displayScoreForA(scoreA);
                break;
            case R.id.goalB_button:
                String goalsB = bind.teamBScore.getText().toString();
                scoreB = Integer.parseInt(goalsB);
                scoreB += 1;
                displayScoreForB(scoreB);
                break;
        }
    }

    public void addYellowCard(View v) {
        soundYellow.start();
        switch (v.getId()) {
            case R.id.yellowA_button:
                String yellowCardsA = bind.teamAYellowcards.getText().toString();
                yellowCardsForA = Integer.parseInt(yellowCardsA);
                yellowCardsForA += 1;
                displayYellowCardsForA(yellowCardsForA);
                break;
            case R.id.yellowB_button:
                String yellowCardsB = bind.teamBYellowcards.getText().toString();
                yellowCardsForB = Integer.parseInt(yellowCardsB);
                yellowCardsForB += 1;
                displayYellowCardsForB(yellowCardsForB);
                break;
        }
    }

    public void addRedCard(View v) {
        soundRed.start();
        switch (v.getId()) {
            case R.id.redA_button:
                String redCardsA = bind.teamARedcards.getText().toString();
                redCardsForA = Integer.parseInt(redCardsA);
                redCardsForA += 1;
                displayRedCardsForA(redCardsForA);
                break;
            case R.id.redB_button:
                String redCardsB = bind.teamBRedcards.getText().toString();
                redCardsForB = Integer.parseInt(redCardsB);
                redCardsForB += 1;
                displayRedCardsForB(redCardsForB);
                break;
        }
    }

    public void displayScoreForA(int score) {
        bind.teamAScore.setText(String.valueOf(score));
    }
    public void displayYellowCardsForA(int score) {
        bind.teamAYellowcards.setText(String.valueOf(score));
    }
    public void displayRedCardsForA(int score) {
        bind.teamARedcards.setText(String.valueOf(score));
    }
    public void displayScoreForB(int score) {
        bind.teamBScore.setText(String.valueOf(score));
    }
    public void displayYellowCardsForB(int score) {
        bind.teamBYellowcards.setText(String.valueOf(score));
    }
    public void displayRedCardsForB(int score) {
        bind.teamBRedcards.setText(String.valueOf(score));
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
        String scoreMessage = createMatchSummary(date, teamA, teamB, scoreA, scoreB, redA, yellowA, redB, yellowB);

        // Create e-mail and set intent to mail app.
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, teamA + getString(R.string.versus) + teamB + getString(R.string.emdash) + getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, scoreMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String createMatchSummary(String date, String nameA, String nameB, String goalsA, String goalsB, String redCardsA, String yellowCardsA, String redCardsB, String yellowCardsB) {
        String matchSummary = nameA + getString(R.string.emdash) + nameB;
        matchSummary += "\n" + goalsA + getString(R.string.emdash) + goalsB;
        matchSummary += "\n";
        matchSummary += "\n" + getString(R.string.red_cards_info) + nameA + ": " + redCardsA;
        matchSummary += "\n" + getString(R.string.yellow_cards_info) + nameA + ": " + yellowCardsA;
        matchSummary += "\n";
        matchSummary += "\n" + getString(R.string.red_cards_info) + nameB + ": " + redCardsB;
        matchSummary += "\n" + getString(R.string.yellow_cards_info) + nameB + ": " + yellowCardsB;
        matchSummary += "\n";
        matchSummary += "\n" + getString(R.string.match_time_info) + date + ".";
        return matchSummary;
    }

    public void resetScore(View v) {
        Button resetButton = findViewById(R.id.reset_button);
        if (!resetClick) {
            displayResetWarning(getString(R.string.reset_warning));
            resetButton.setBackgroundColor(getResources().getColor(R.color.colorButton));
            resetClick = true;
        } else {
            scoreA = 0;
            yellowCardsForA = 0;
            redCardsForA = 0;
            scoreB = 0;
            yellowCardsForB = 0;
            redCardsForB = 0;
            displayScoreForA(scoreA);
            displayYellowCardsForA(yellowCardsForA);
            displayRedCardsForA(redCardsForA);
            displayScoreForB(scoreB);
            displayYellowCardsForB(yellowCardsForB);
            displayRedCardsForB(redCardsForB);
            bind.teamAName.getText().clear();
            bind.teamBName.getText().clear();
            resetButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            resetClick = false;
        }
    }

    public void displayResetWarning(String warningReset) {
        Toast.makeText(getApplicationContext(), warningReset, Toast.LENGTH_SHORT).show();
    }
}