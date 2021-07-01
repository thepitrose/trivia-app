package com.example.myapplication2;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class BaseTriviaService {
    protected ArrayList<String> availableQuestions = new ArrayList<>(); // all the game questions which not been seen by the user
    protected String correctAnswer;
    protected int totalPoints = 0;
    protected GameQuestionsListener gameQuestionsListener;
    protected ArrayList<String> answersList = new ArrayList<>();        // hold the answers to the question which appears
    protected ArrayList<String> currentQuestionList = new ArrayList<>();        //hold the question the appears
    protected int currentQuestionIndex = -1;
    protected DatabaseReference gameDBRef;
    private static BaseTriviaService singleton;

    public static BaseTriviaService getSingletonInstance() {
        if (singleton == null) {
            singleton = new BaseTriviaService();
        }

        return singleton;
    }


    protected BaseTriviaService() {
        this.gameDBRef = FirebaseDatabase.getInstance().getReference();
    }

    public void addGameListener(GameQuestionsListener listener) {
        this.gameQuestionsListener = listener;
    }

    public void handleEndOfRound() {
        this.saveUserScore(this.totalPoints);
    }

    public void startGame(String root , String category, String ques) {



        if (root.equals("PersonalQuestions"))
        {

            DatabaseReference reference =
                    this.gameDBRef.child(root).child(category).child(ques); // get all the questions from the chosen category


            reference.addListenerForSingleValueEvent(new ValueEventListener() {  // "Listener" that communicate with database
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    prepareGameData(dataSnapshot);
                    getNextQuestion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else
            {
            DatabaseReference reference =
                    this.gameDBRef.child(root).child(category); // get all the questions from the chosen category
            reference.addListenerForSingleValueEvent(new ValueEventListener() {  // "Listener" that communicate with database
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    prepareGameData(dataSnapshot);
                    getNextQuestion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void prepareGameData(DataSnapshot gameDataSnapshot) {
        availableQuestions.clear();
        for (DataSnapshot snapshot : gameDataSnapshot.getChildren()) {                  // go over the questions list
            availableQuestions.add(snapshot.getValue().toString());
            Collections.sort(availableQuestions); //Make sure the data theat received will be correct TODO: why we need this?
        }

        //Collections.shuffle(availableQuestions);  // mix the arrangement of questions

        setQuestionsAsArrayString(availableQuestions);
    }

    public boolean chooseAnswer(int chosenAnswerIndex, int roundPoints) {
        boolean isWinRound = false;
        String chosenAnswer = this.answersList.get(chosenAnswerIndex);

        // Check if the answer is correct
        if (chosenAnswer.equals(correctAnswer))
        {
            isWinRound = true;
            this.totalPoints = totalPoints + roundPoints;
        }

        // Remove current question
        availableQuestions.remove(0);

        return isWinRound;
    }

    public boolean isNextQuestionExist() {
        return !availableQuestions.isEmpty();
    }

    public boolean getNextQuestion(){
        boolean isNextQuestionExist = !availableQuestions.isEmpty();

        // Checks if the questions are over in the round
        if (isNextQuestionExist) {
            this.setQuestionsAsArrayString(availableQuestions);
            this.currentQuestionList.add(answersList.get(0));
            this.answersList.remove(0);
            this.correctAnswer = answersList.get(0);
            Collections.shuffle(answersList);
            this.currentQuestionIndex = this.currentQuestionIndex + 1;

            // return possible answers for the first question
            gameQuestionsListener.newQuestion(currentQuestionList, answersList, this.currentQuestionIndex);
        }
        else {
            this.handleEndOfRound();
        }

        return isNextQuestionExist;
    }

    public boolean skipToNextQuestion () {
        availableQuestions.remove(0);
        boolean isNextQuestionExist = !availableQuestions.isEmpty();

        if (isNextQuestionExist) {
            getNextQuestion();
        }

        return isNextQuestionExist;
    }

    private void saveUserScore(int newpoint)        // add the point to the user database
    {
        String userId = UsersService.getUserId();
        UsersService.addToUserTotalScore(userId, newpoint);
    }

    public int getUserPoints() {
        return this.totalPoints;
    }

    /*
    Because different devices receive the question and answer from the Database In a different order ,this function is intended to:
    Cut the String that received from the server, arrange it in the correct order
    Question as a head, The correct answer follows and then three incorrect answers
    Then add it to the arraylist the show
 */
    private void setQuestionsAsArrayString(ArrayList<String> list)
    {
        ArrayList<String> templist = new ArrayList<>();
        String temp;
        String temp2 = list.get(0);         //get The question and the answers
        int a = 0;
        int b = 0;

        for (int i = 1; i < temp2.length() - 1; i++) {
            if (temp2.charAt(i) == 'Q' || temp2.charAt(i) == 'X') //A question in the  Database begins with the letter Q - and answer begins with and X with a follow letter A,B,C,D
            {
                a = i ;
            }

            if (temp2.charAt(i) == 44) {        // all and with a ',' 44 is the ascii of ','
                b = i;
            }

            if (a != 0 && b != 0) {
                templist.add(temp2.substring(a, b));        // add it to tke list
                a = 0;
                b = 0;
            }

        }

        templist.add(temp2.substring(a, temp2.length()-1)); //add the last one , dont end with a ','

        Collections.sort(templist);                 // sort the array

        //-----------------to cut
        answersList.clear();
        currentQuestionList.clear();

        temp =String.valueOf(templist);

        int x = 0;
        int y = 0;
        for (int i = 0; i < temp.length(); i++) {       //cut the The initial character ,They all start with '='
            if (temp.charAt(i) == 61) {                 //61 is the ascii of '='
                x = i + 1;
            }

            if (temp.charAt(i) == 44) {
                y = i;
            }

            if (x != 0 && y != 0) {
                answersList.add(temp.substring(x, y));
                x = 0;
                y = 0;
            }

        }
        answersList.add(temp.substring(x, temp.length()-1));
        templist.clear();
        //----------------end cut
    }

}
