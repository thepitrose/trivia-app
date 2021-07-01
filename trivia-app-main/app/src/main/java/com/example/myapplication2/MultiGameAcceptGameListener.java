package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;

public class MultiGameAcceptGameListener implements GameEventsListener {
    private Activity activity;

    public MultiGameAcceptGameListener(Activity activity) {
        this.activity = activity;
    }
    public void acceptGame(String gameKey) {
        Intent intent = new Intent(activity,  MultiQuesActivity.class);
        intent.putExtra("gameId", gameKey);
        intent.putExtra("isFirstPlayer", true);

        // start game
        this.activity.startActivity(intent);
    }

    @Override
    public void cancelGame() {
        Intent intent = new Intent(activity,  GameType.class);

        // go back to main screen
        this.activity.startActivity(intent);
    }

}
