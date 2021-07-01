package com.example.myapplication2;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private ArrayList<String> data;

    public Question(ArrayList<String> data) {
        this.data = new ArrayList<>(data);
    }

    public Question(String questionString, String rightAnswer, List<String> wrongAnswers) {
        this.data = new ArrayList<>();

        int i = 0;
        data.add(i++, questionString);
        data.add(i++, rightAnswer);

        for (String wAns : wrongAnswers) {
            data.add(i++, wAns);
        }
    }

    public String getQuestionString() {
        return data.get(0);
    }

    public String getRightAnswer() {
        return data.get(1);
    }

    public List<String> getWrongAnswers() {
        return data.subList(2, data.size());
    }
}

