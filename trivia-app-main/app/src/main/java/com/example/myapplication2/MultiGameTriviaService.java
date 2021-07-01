package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MultiGameTriviaService extends BaseTriviaService {
    static MultiGameTriviaService singleton;
    protected String currentGameKey;
    protected boolean isFirstPlayer = true;
    private boolean isWaitingToOtherPlayer = false;

    protected MultiGameTriviaService() {
        super();
    }

    public static MultiGameTriviaService getSingletonInstance() {
        if (singleton == null) {
            singleton = new MultiGameTriviaService();
        }

        return singleton;
    }
    public void startGame(String gameKey) {
        DatabaseReference multiGameRef = this.gameDBRef.child("MultiGame");
        DatabaseReference gameReference = multiGameRef.child(gameKey);
        this.currentGameKey = gameKey;
        gameReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Map<String, String> gameData = (Map<String, String>) task.getResult().getValue();
                // get game category
                String category = gameData.get("category");

                // if game status is active
                GameStatus gameStatus = GameStatus.valueOf(gameData.get("gameStatus"));
                if (gameStatus == GameStatus.active) {

                    // set if player is the first player or the second one
                    String firstPlayerId = (String)gameData.get("firstPlayerId");
                    isFirstPlayer = firstPlayerId.equals(UsersService.getUserId());

                    // start game
                    MultiGameTriviaService.super.startGame("Questions",category,"Filling");
                }
            }
        });
    }

    public void listenToGameInvitations(InvitationToGameListener invitationToGameListener) {
        String userId = UsersService.getUserId();
        Query query =
                this.gameDBRef.child("MultiGame").orderByChild("secPlayerId").equalTo(userId).limitToLast(1);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Object> game = (HashMap<String, Object>)snapshot.getValue();
                String gameStatus = (String) game.get("gameStatus");
                String gameId = (String) game.get("gameId");
                if (gameId != null && GameStatus.valueOf(gameStatus) == GameStatus.pendingApproval) {
                    String category = (String) game.get("category");
                    String otherPlayerId = (String) game.get("firstPlayerId");
                    UsersService.getUserById(otherPlayerId, new getDataListener() {
                        @Override
                        public void getData(HashMap<String, Object> data) {
                            String otherPlayerName = (String) data.get("username");
                            invitationToGameListener.handleMultiGameInvitation(gameId, category, otherPlayerName);
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addChildEventListener(childEventListener);
    }

    public String createNewMultiGame(String player1Id, String player2Id, String category, GameEventsListener acceptGameListener) {
        DatabaseReference reference = this.gameDBRef.child("MultiGame");
        DatabaseReference multiGamesRef = reference.push();
        String key = multiGamesRef.getKey();
        MultiGame game = new MultiGame(key, player1Id, player2Id, category);
        multiGamesRef.setValue(game.toMap());
        this.listenToGameUpdates(key, acceptGameListener);
        return key;
    }

    public void listenToGameUpdates(String gameKey, GameEventsListener acceptGameListener) {
        DatabaseReference givenGameRef = this.gameDBRef.child("MultiGame").child(gameKey);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> game = (HashMap<String, Object>)dataSnapshot.getValue();
                String newStatus = (String) game.get("gameStatus");
                // if the update was the cancel the game invite
                if (GameStatus.valueOf(newStatus) == GameStatus.cancel) {
                    acceptGameListener.cancelGame();
                }
                // if the game is active
                else if (GameStatus.valueOf(newStatus) == GameStatus.active){
                    long player1QuestionIndex = (long)game.get("player1PrevQuestion");
                    long player2QuestionIndex = (long)game.get("player2PrevQuestion");

                    // if the update was the second player accept the game invite
                    if (player1QuestionIndex == -1 && player2QuestionIndex == -1){
                        acceptGameListener.acceptGame(gameKey);
                    }
                    else if (player1QuestionIndex == player2QuestionIndex){
                        handleEndOfQuestion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                acceptGameListener.cancelGame();
            }
        };
        givenGameRef.addValueEventListener(valueEventListener);
    }

    public void acceptMultiGameRequest(String gameKey) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("gameStatus", GameStatus.active.toString());
        this.updateGameData(gameKey, updates);

    }

    public void cancelMultiGameRequest(String gameKey) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("gameStatus", GameStatus.cancel.toString());
        this.updateGameData(gameKey, updates);
    }

    protected void updateGameData(String gameKey, HashMap<String, Object> fields) {
        // Update multi game data in the database
        DatabaseReference multiGameRef = this.gameDBRef.child("MultiGame");
        Map<String, Object> multiGamesUpdates = new HashMap<>();

        for (String fieldName : fields.keySet()) {
            multiGamesUpdates.put("/" + gameKey + "/" + fieldName, fields.get(fieldName));
        }
        multiGameRef.updateChildren(multiGamesUpdates);
    }

    public boolean chooseAnswer(int chosenAnswerIndex, int roundPoints) {
        boolean isWin = false;
        if (!isWaitingToOtherPlayer) {
            isWaitingToOtherPlayer = true;
            isWin = super.chooseAnswer(chosenAnswerIndex, roundPoints);

            this.updateMultigameUserScore(isWin ? roundPoints : 0);
        }
        return isWin;
    }

    public boolean getNextQuestion() {
        isWaitingToOtherPlayer = false;
        return super.getNextQuestion();
    }

    protected void updateMultigameUserScore(int roundScore) {
        String playerField = isFirstPlayer ? "firstPlayerScore" : "secPlayerScore";
        String playerQuestionIndexField = isFirstPlayer ? "player1PrevQuestion" : "player2PrevQuestion";
        HashMap<String, Object> updates = new HashMap<>();
        Integer newScore = this.totalPoints + roundScore;
        updates.put(playerField, newScore);
        updates.put(playerQuestionIndexField, currentQuestionIndex);
        this.updateGameData(this.currentGameKey, updates);
    }

    public void handleEndOfQuestion() {
        MidGameStateData lastRoundData = new MidGameStateData();
        lastRoundData.player1Name = "You";
        lastRoundData.player2Name = "Other Player";
        DatabaseReference multiGameRef = this.gameDBRef.child("MultiGame");
        DatabaseReference gameReference = multiGameRef.child(this.currentGameKey);
        gameReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Map<String, Object> gameData = (Map<String, Object>) task.getResult().getValue();
                long player1Score;
                long player2Score;
                if (isFirstPlayer) {
                    player1Score = (long)gameData.get("firstPlayerScore");
                    player2Score = (long)gameData.get("secPlayerScore");
                } else {
                    player1Score = (long)gameData.get("secPlayerScore");
                    player2Score = (long)gameData.get("firstPlayerScore");
                }
                lastRoundData.player1Score = player1Score;
                lastRoundData.player2Score = player2Score;

                gameQuestionsListener.endOfCurrentQuestion(lastRoundData);
            }
        });
    }

    public void waitToBothPlayersFinishAnswerQuestion() {

    }

    public boolean skipToNextQuestion () {
        this.updateMultigameUserScore(0);
        boolean retval = isNextQuestionExist();
        if (retval) {
            availableQuestions.remove(0);
        }
        return retval;
    }
}
