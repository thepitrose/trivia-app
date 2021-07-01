package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MultiQuesActivity extends AppCompatActivity {

    private static final long DURATION = TimeUnit.SECONDS.toMillis(10);      // the set the TIMER by seconds
    private GameQuestionsListener gameQuestionsListener;
    private String gameKey;
    private MultiGameTriviaService triviaService;
    private boolean isWaitToNewQuestion = false;
    private Button stop;              //Statement for user interfaces
    private Button button1;           //Statement for user interfaces
    private Button button2;           //Statement for user interfaces
    private Button button3;            //Statement for user interfaces
    private Button button4;           //Statement for user interfaces
    private ListView listView;       //Statement for user interfaces

    private CountDownTimer timer;
    private TextView timerView;     //Statement for user interfaces
    private int numQ =0;        // for the Timer
    private int tempQ;          // for the Timer
    private int totalPoints = 0;
    private int roundPoints = 0;   //point per timer
    private ArrayAdapter adapter;        //what array list will show in  the  listView

    @Override
    protected void onResume() {
        super.onResume();

        // if we did not arrive to this event after onCreate
        if (isWaitToNewQuestion) {
            triviaService.getNextQuestion();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = getIntent().getExtras();
        String category = bundle.getString("key");
        this.gameKey = bundle.getString("gameId");
        getSupportActionBar().setTitle(category);

        stop = findViewById(R.id.stop);             //Link user interface to XML
        listView = findViewById(R.id.listView);       //Link user interface to XML
        timerView = findViewById(R.id.timerView);         //Link user interface to XML
        button1 = findViewById(R.id.button1);             //Link user interface to XML
        button2 = findViewById(R.id.button2);             //Link user interface to XML
        button3 = findViewById(R.id.button3);                 //Link user interface to XML
        button4 = findViewById(R.id.button4);                 //Link user interface to XML

        MultiQuesActivity self = this;

        gameQuestionsListener = new GameQuestionsListener() {
            @Override
            public void newQuestion(ArrayList<String>question, ArrayList<String> answers , int questionIndex) {
                if (adapter == null) {
                    adapter = new ArrayAdapter<String>(self, R.layout.show, question);          //what array list will show in  the  listView
                    listView.setAdapter(adapter);                                                                //show the questions in the listView
                }
                adapter.notifyDataSetChanged();     //Refreshes the view of the question
                button1.setText(answers.get(0));      //Each button gets an answer
                button2.setText(answers.get(1));
                button3.setText(answers.get(2));
                button4.setText(answers.get(3));

                numQ=questionIndex;        // Gets the number of questions in the round
                tempQ=numQ;                 //To update the timer if we passed a question

                // register buttons event listeners
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(0);
                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(1);
                    }
                });
                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(2);
                    }
                });
                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(3);
                    }
                });


                timer = new CountDownTimer(DURATION, 1000) {   //the timer

                    @Override
                    public void onTick(long l) {

                        String sduration = String.format(Locale.ENGLISH,"%02d : %02d"               //set the view of the timer
                                , TimeUnit.MILLISECONDS.toMinutes(l)
                                , TimeUnit.MILLISECONDS.toSeconds(l) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                        roundPoints = (int) (TimeUnit.MILLISECONDS.toSeconds(l) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                        timerView.setText(sduration);                                                       //show it on the screen

                        if (tempQ>numQ)                 // If a question has passed, without the timer update the amount of questions
                        {
                            tempQ=numQ;
                            this.start();
                        }

                        if (numQ<0)
                        {
                            this.cancel();      //Prevents the timer from continuing to run after the end of the round
                        }
                    }

                    @Override
                    public void onFinish() {
                       boolean isNextQuestionExist = triviaService.skipToNextQuestion();
                    }
                }.start();
            }

            public void endOfCurrentQuestion(MidGameStateData midGameStateData) {
                handleEndOfCurrentQuestion(midGameStateData);
            }
        };

        this.triviaService = MultiGameTriviaService.getSingletonInstance();
        this.triviaService.addGameListener(gameQuestionsListener);
        this.triviaService.startGame(gameKey);

        // to stop the round
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numQ=-1;
                startActivity(new Intent(MultiQuesActivity.this, GameType.class));
            }
        });

        // show the questions in the listView
        listView.setAdapter(adapter);
    }

    private void onButtonClick(int buttonIndex) {
        this.timer.cancel();
        boolean isCorrect = triviaService.chooseAnswer(buttonIndex, this.roundPoints);
        if (isCorrect) {
            Toast.makeText(MultiQuesActivity.this , "point " + roundPoints, Toast.LENGTH_SHORT).show();    //show it on the bottom of the screen
        }
    }

    private void handleEndOfCurrentQuestion(MidGameStateData midGameStateData) {
        // To update the timer that a question pass - Prevents the timer from continuing to run after the end of the round
        numQ--;
        boolean isNextQuestionExist = triviaService.isNextQuestionExist();

        Intent intent = new Intent(MultiQuesActivity.this, ScoresStateActivity.class);
        intent.putExtra("Player1Name", midGameStateData.player1Name);
        intent.putExtra("Player2Name", midGameStateData.player2Name);
        intent.putExtra("Player1Score", midGameStateData.player1Score);
        intent.putExtra("isEndGame", !isNextQuestionExist);
        intent.putExtra("Player2Score", midGameStateData.player2Score);

        // display the next question
        startActivity(intent);
        isWaitToNewQuestion = true;
    }

}