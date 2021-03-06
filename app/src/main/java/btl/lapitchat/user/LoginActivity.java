package btl.lapitchat.user;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import btl.lapitchat.MainActivity;
import btl.lapitchat.R;
import btl.lapitchat.utility.ComonComponents;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private Toolbar loginToolbar;
    private ProgressDialog mLoader;

    private AutoCompleteTextView loginEmail;
    private TextInputEditText loginPass;
    UserHelper helper;
    private Button loginBtn;
    private ArrayList<String> users = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new UserHelper(this);
        users =  helper.getAllUserEmail();
        setContentView(R.layout.activity_login);
        mAuth = MainActivity.getmAuth();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mLoader = new ProgressDialog(this);
        // Toolbar
        loginToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle(R.string.login_label);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginEmail = findViewById(R.id.login_email);
        ArrayAdapter<String> adapterUser = new ArrayAdapter(this,android.R.layout.simple_list_item_1, users);
        loginEmail.setAdapter(adapterUser);
        loginEmail.setThreshold(1);
        loginPass = findViewById(R.id.login_password);

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPass.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    ComonComponents.showLoader(mLoader, R.string.login_label, "Waiting", false);
                    loginAccount(email, pass);
                }
            }
        });
    }

    private void loginAccount(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mLoader.dismiss();
                        if (task.isSuccessful()) {
                            final String currentUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String name = mUserDatabase.child(currentUserId).child("name").toString();
                            String status = mUserDatabase.child(currentUserId).child("status").toString();
                            helper.insert(currentUserId, name, email,status );
                            mUserDatabase.child(currentUserId).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mUserDatabase.child(currentUserId).child("online").setValue("true")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            gotoStartView();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            mLoader.hide();
                            Toast.makeText(LoginActivity.this, R.string.cannot_login, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void gotoStartView( ) {
        Intent startIntent = new Intent( LoginActivity.this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //helper.close();
    }


}
