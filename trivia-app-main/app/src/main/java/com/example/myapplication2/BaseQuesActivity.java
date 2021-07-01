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

public class BaseQuesActivity extends AppCompatActivity {

    private static final long DURATION = TimeUnit.SECONDS.toMillis(10);      // the set the TIMER by seconds
    private GameQuestionsListener gameQuestionsListener;
    private BaseTriviaService triviaService;
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
    private int roundPoints = 0;   //point per timer
    private ArrayAdapter adapter;        //what array list will show in  the  listView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = getIntent().getExtras();
        String qtype = bundle.getString("key");
        String qtypeques = bundle.getString("Ques");
        String roottype = bundle.getString("type");
        getSupportActionBar().setTitle(qtype);

        stop = findViewById(R.id.stop);             //Link user interface to XML
        listView = findViewById(R.id.listView);       //Link user interface to XML
        timerView = findViewById(R.id.timerView);         //Link user interface to XML
        button1 = findViewById(R.id.button1);             //Link user interface to XML
        button2 = findViewById(R.id.button2);             //Link user interface to XML
        button3 = findViewById(R.id.button3);                 //Link user interface to XML
        button4 = findViewById(R.id.button4);                 //Link user interface to XML


        BaseQuesActivity self = this;
        gameQuestionsListener = new GameQuestionsListener() {

            @Override
            public void endOfCurrentQuestion(MidGameStateData midGameStateData) {

            }

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
                    public void onFinish() {            //If time is run out - go to the next question
                        boolean isNextQuestionExist = triviaService.skipToNextQuestion();
                        numQ--;
                        if (isNextQuestionExist) {
                            this.start();
                        }
                        else  {
                            this.cancel();
                            handleEndOfRound();
                        }
                    }
                }.start();
            }
        };
        this.triviaService = new BaseTriviaService();
        this.triviaService.addGameListener(gameQuestionsListener);
        this.triviaService.startGame(roottype,qtype,qtypeques);

        // to stop the round
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numQ=-1;
                handleEndOfRound();
            }
        });

        // show the questions in the listView
        listView.setAdapter(adapter);
    }

    private void handleEndOfRound() {
        Intent intent = new Intent(BaseQuesActivity.this, EndSession.class);
        intent.putExtra("score", this.triviaService.getUserPoints());
        startActivity(intent);
        finish();
    }

    private void onButtonClick(int buttonIndex) {
        this.timer.cancel();
        boolean isCorrect = triviaService.chooseAnswer(buttonIndex, this.roundPoints);
        if (isCorrect) {
            Toast.makeText(BaseQuesActivity.this , "point " + roundPoints, Toast.LENGTH_SHORT).show();    //show it on the bottom of the screen
        }

        boolean isRoundOver = !(triviaService.getNextQuestion());

        // If user finished finished this round
        if (isRoundOver) {
            // To update the timer that a question pass - Prevents the timer from continuing to run after the end of the round
            numQ--;
            handleEndOfRound();
        }
    }

}