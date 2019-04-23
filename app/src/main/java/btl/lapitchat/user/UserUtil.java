package btl.lapitchat.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import btl.lapitchat.MainActivity;

public class UserUtil {
    public static DatabaseReference mDataUser = MainActivity.getmData().getReference();
    public static FirebaseAuth mAuth = MainActivity.getmAuth();

    public static DatabaseReference getUserData(String id){
        return mDataUser.child("users").child(id);
    }
}
