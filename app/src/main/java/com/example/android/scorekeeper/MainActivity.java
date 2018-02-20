package com.example.android.scorekeeper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private int scoreA = 0;
    private int yellowCardsForA = 0;
    private int redCardsForA = 0;
    private int scoreB = 0;
    private int yellowCardsForB = 0;
    private int redCardsForB = 0;

    // all needed to set the date
    Context context = this;
    EditText dateField;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "dd.MM.yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

    // variables to save after rotation
    EditText nameA;
    EditText nameB;
    TextView scoreTeamA;
    TextView yellowTeamA;
    TextView redTeamA;
    TextView scoreTeamB;
    TextView yellowTeamB;
    TextView redTeamB;

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

        setContentView(R.layout.activity_main);

        displayScoreForA(0);
        displayScoreForB(0);
        displayYellowCardsForA(0);
        displayYellowCardsForB(0);
        displayRedCardsForA(0);
        displayRedCardsForB(0);

        // find all values which can be changed in the app to manipulate them later
        dateField = findViewById(R.id.date);
        nameA = findViewById(R.id.team_a_name);
        nameB = findViewById(R.id.team_b_name);
        scoreTeamA = findViewById(R.id.team_a_score);
        yellowTeamA = findViewById(R.id.team_a_yellowcards);
        redTeamA = findViewById(R.id.team_a_redcards);
        scoreTeamB = findViewById(R.id.team_b_score);
        yellowTeamB = findViewById(R.id.team_b_yellowcards);
        redTeamB = findViewById(R.id.team_b_redcards);

        // find all the sounds to play
        soundGoal = MediaPlayer.create(this, R.raw.goalsound);
        soundYellow = MediaPlayer.create(this, R.raw.shortwhistle);
        soundRed = MediaPlayer.create(this, R.raw.refereewhistle);

        if (savedInstanceState != null) {
            CharSequence savedScoreA = savedInstanceState.getCharSequence(SCORE_A);
            scoreTeamA.setText(savedScoreA);
            CharSequence savedYellowA = savedInstanceState.getCharSequence(YELLOW_A);
            yellowTeamA.setText(savedYellowA);
            CharSequence savedRedA = savedInstanceState.getCharSequence(RED_A);
            redTeamA.setText(savedRedA);
            CharSequence savedScoreB = savedInstanceState.getCharSequence(SCORE_B);
            scoreTeamB.setText(savedScoreB);
            CharSequence savedYellowB = savedInstanceState.getCharSequence(YELLOW_B);
            yellowTeamB.setText(savedYellowB);
            CharSequence savedRedB = savedInstanceState.getCharSequence(RED_B);
            redTeamB.setText(savedRedB);
        }

        // The below code which sets the current date and allows to pick another date written with help:
        // http://www.moo-code.me/en/2017/04/16/how-to-popup-datepicker-calendar/

        // calendar on create - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);
        dateField.setText(dateString);

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
        dateField.setOnClickListener(new View.OnClickListener() {
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
        dateField.setText(sdf.format(myCalendar.getTime()));
    }

    // saving number of goals and cards before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SCORE_A, scoreTeamA.getText());
        outState.putCharSequence(YELLOW_A, yellowTeamA.getText());
        outState.putCharSequence(RED_A, redTeamA.getText());
        outState.putCharSequence(SCORE_B, scoreTeamB.getText());
        outState.putCharSequence(YELLOW_B, yellowTeamB.getText());
        outState.putCharSequence(RED_B, redTeamB.getText());
    }

    // restoring number of goals and cards after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        scoreTeamA.setText(savedInstanceState.getString(SCORE_A));
        yellowTeamA.setText(savedInstanceState.getString(YELLOW_A));
        redTeamA.setText(savedInstanceState.getString(RED_A));
        scoreTeamB.setText(savedInstanceState.getString(SCORE_B));
        yellowTeamB.setText(savedInstanceState.getString(YELLOW_B));
        redTeamB.setText(savedInstanceState.getString(RED_B));
    }

    /**
     * Below methods add goals to score and cards to cards counter.
     */
    public void addGoal(View v){
        soundGoal.start();
        String goalsA = scoreTeamA.getText().toString();
        scoreA = Integer.parseInt(goalsA);
        String goalsB = scoreTeamB.getText().toString();
        scoreB = Integer.parseInt(goalsB);
        switch (v.getId()) {
            case R.id.goalA_button:
                scoreA += 1;
                displayScoreForA(scoreA);
                break;
            case R.id.goalB_button:
                scoreB += 1;
                displayScoreForB(scoreB);
                break;
        }
    }

    public void addYellowCardForA(View v) {
        soundYellow.start();
        String yellowCardsA = yellowTeamA.getText().toString();
        yellowCardsForA = Integer.parseInt(yellowCardsA);
        yellowCardsForA += 1;
        displayYellowCardsForA(yellowCardsForA);
    }

    public void addRedCardForA(View v) {
        soundRed.start();
        String redCardsA = redTeamA.getText().toString();
        redCardsForA = Integer.parseInt(redCardsA);
        redCardsForA += 1;
        displayRedCardsForA(redCardsForA);
    }

    public void addYellowCardForB(View v) {
        soundYellow.start();
        String yellowCardsB = yellowTeamB.getText().toString();
        yellowCardsForB = Integer.parseInt(yellowCardsB);
        yellowCardsForB += 1;
        displayYellowCardsForB(yellowCardsForB);
    }

    public void addRedCardForB(View v) {
        soundRed.start();
        String redCardsB = redTeamB.getText().toString();
        redCardsForB = Integer.parseInt(redCardsB);
        redCardsForB += 1;
        displayRedCardsForB(redCardsForB);
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayScoreForA(int score) {
        scoreTeamA = findViewById(R.id.team_a_score);
        scoreTeamA.setText(String.valueOf(score));
    }

    /**
     * Displays the yellow cards for Team A.
     */
    public void displayYellowCardsForA(int score) {
        yellowTeamA = findViewById(R.id.team_a_yellowcards);
        yellowTeamA.setText(String.valueOf(score));
    }

    /**
     * Displays the red cards for Team A.
     */
    public void displayRedCardsForA(int score) {
        redTeamA = findViewById(R.id.team_a_redcards);
        redTeamA.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayScoreForB(int score) {
        scoreTeamB = findViewById(R.id.team_b_score);
        scoreTeamB.setText(String.valueOf(score));
    }

    /**
     * Displays the yellow cards for Team B.
     */
    public void displayYellowCardsForB(int score) {
        yellowTeamB = findViewById(R.id.team_b_yellowcards);
        yellowTeamB.setText(String.valueOf(score));
    }

    /**
     * Displays the yellow cards for Team B.
     */
    public void displayRedCardsForB(int score) {
        redTeamB = findViewById(R.id.team_b_redcards);
        redTeamB.setText(String.valueOf(score));
    }

    /**
     * This method is called when the send e-mail button is clicked.
     */
    public void sendMail(View view) {
        // Get all the needed information to strings.
        String date = dateField.getText().toString();
        String teamA = nameA.getText().toString();
        if (teamA.equals("")) {
            teamA = getString(R.string.team_a);
        }
        String teamB = nameB.getText().toString();
        if (teamB.equals("")) {
            teamB = getString(R.string.team_b);
        }
        String scoreA = scoreTeamA.getText().toString();
        String scoreB = scoreTeamB.getText().toString();
        String redA = redTeamA.getText().toString();
        String yellowA = yellowTeamA.getText().toString();
        String redB = redTeamB.getText().toString();
        String yellowB = yellowTeamB.getText().toString();
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

    /**
     * This method creates match summary.
     */
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
            nameA.getText().clear();
            nameB.getText().clear();
            resetButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            resetClick = false;
        }
    }

    public void displayResetWarning(String warningReset) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, warningReset, duration);
        toast.show();
    }
}