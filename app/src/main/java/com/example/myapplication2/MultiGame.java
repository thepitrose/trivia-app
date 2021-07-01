package com.example.myapplication2;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MultiGame {
    public String gameId;
    public String firstPlayerId;
    public String secPlayerId;
    public String category;
    public int firstPlayerScore = 0;
    public int secPlayerScore = 0;
    public int player1PrevQuestion = -1;
    public int player2PrevQuestion = -1;
    public GameStatus gameStatus;

    public MultiGame(String id, String player1Id, String player2Id, String category) {
        this.firstPlayerId = player1Id;
        this.secPlayerId = player2Id;
        this.gameId = id;
        this.gameStatus = GameStatus.pendingApproval;
        this.category = category;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("gameId", this.gameId);
        result.put("firstPlayerId", this.firstPlayerId);
        result.put("category", this.category);
        result.put("secPlayerId", this.secPlayerId);
        result.put("firstPlayerScore", this.firstPlayerScore);
        result.put("secPlayerScore", this.secPlayerScore);
        result.put("gameStatus", this.gameStatus);
        result.put("player1PrevQuestion", this.player1PrevQuestion);
        result.put("player2PrevQuestion", this.player2PrevQuestion);
        return result;
    }

}
