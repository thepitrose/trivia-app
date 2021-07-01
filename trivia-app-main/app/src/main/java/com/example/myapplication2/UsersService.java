package com.example.myapplication2;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UsersService {
    private static String OFFLINE_STATE = "OFFLINE";
    private static String ONLINE_STATE = "ONLINE";
    private static DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static String getUserId() {
        return auth.getUid();
    }

    public static void registerUser(Activity activity , String email, String password, String username, OnCompleteListener<AuthResult> onCompleteListener) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {        // If everything is correct - add user to the Database Authentication
                if (task.isSuccessful())
                {
                    addNewUserData(auth.getUid(), email, username);
                    if (onCompleteListener != null) {
                        onCompleteListener.onComplete(task);
                    }
                }
                else
                {
                    Toast.makeText(activity, "faile to register ,Invalid email address Or the email is already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void addNewUserData(String id, String email, String username) {
        HashMap<String , Object> userData = new HashMap<>();
        userData.put("userMail", email);
        userData.put("username", username);
        userData.put("status", OFFLINE_STATE);
        userData.put("score", 0);
        userData.put("id", id);

        HashMap<String , Object> updatesMap = new HashMap<String, Object>() {{
            put(id, userData);
        }};

        userInfoRef.updateChildren(updatesMap);
    }

    public static void loginUser(Activity activity ,String email, String password, OnCompleteListener onCompleteListener) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addConnectStateToUser(auth.getUid());
                    onCompleteListener.onComplete(task);
                }
                else
                {
                    Toast.makeText(activity, "Failed to login, check email Or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void addConnectStateToUser(String userId) {
        // Create a reference to this user's specific status node.
        // This is where we will store data about being online/offline
        DatabaseReference userStatusDatabaseRef =
                userInfoRef.child(userId);

        // Create a reference to the special '.info/connected' path in
        // Realtime Database. This path returns `true` when connected
        // and `false` when disconnected.
        DatabaseReference firebaseConnectionRef= FirebaseDatabase.getInstance().getReference(".info/connected");
        firebaseConnectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                //only if the user is connected (not connected users will be handled in the disconnect event listener
                if (connected) {
                    // If we are currently connected, then use the 'onDisconnect()'
                    // method to add a set which will only trigger once this
                    // client has disconnected by closing the app,
                    // losing internet, or any other means.
                    HashMap<String, Object> offlineUpdateMap = new HashMap<String, Object>() {{
                        put("status", OFFLINE_STATE);
                    }};
                    userStatusDatabaseRef.onDisconnect().updateChildren(offlineUpdateMap);


                    // We can now safely set ourselves as 'online' knowing that the
                    // server will mark us as offline once we lose connection.
                    HashMap<String, Object> onlineUpdateMap = new HashMap<String, Object>() {{
                        put("status", ONLINE_STATE);
                    }};
                    userStatusDatabaseRef.updateChildren(onlineUpdateMap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getUserById(String id, getDataListener getDataListener) {
        userInfoRef.orderByChild("id").equalTo(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getValue();
                HashMap<String, Object> userData = (HashMap<String, Object>)data.get(id);
                if (userData!=null) {
                    getDataListener.getData(userData);
                }
            }
        });
    }

    public static void addToUserTotalScore(String userId, int newScore) {
        userInfoRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, Object> userData = (HashMap<String, Object>) task.getResult().getValue();
                Long userScore = (Long) userData.get("score");
                long newUserScore = userScore + newScore;
                HashMap<String, Object> updates = new HashMap<>();
                updates.put("score", newUserScore);
                userInfoRef.child(userId).updateChildren(updates);
            }
        });
    }

    public static void getUsersData(getDataListener usersScoreListener) {
        userInfoRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, Object> usersScore = (HashMap<String, Object>) task.getResult().getValue();
                usersScoreListener.getData(usersScore);
            }
        });
    }

    public static void getConnectedUsers(getDataListener connectUsersListener) {
        userInfoRef.orderByChild("status").equalTo(ONLINE_STATE).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, Object> connectedUsers = (HashMap<String, Object>) task.getResult().getValue();
                connectUsersListener.getData(connectedUsers);
            }
        });
    }
}
