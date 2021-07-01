package com.example.myapplication2;

import java.util.ArrayList;

public interface GameQuestionsListener {
    void newQuestion(ArrayList<String> question, ArrayList<String> answers,  int questionIndex);
    void endOfCurrentQuestion(MidGameStateData midGameStateData);
}
