package btl.lapitchat.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import btl.lapitchat.MainActivity;
import btl.lapitchat.R;
import btl.lapitchat.utility.ComonComponents;


public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText mDisplayName;
    private TextInputEditText mEmail;
    private TextInputEditText mPasword;
    private Button mCreateBtbn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private ProgressDialog mRegProgress;
    private DatabaseReference mUserDatabase;
    UserHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helper = new UserHelper(this);
        // Toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.create_account_label);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Loading effect
        mRegProgress = new ProgressDialog(this);

        // Main elements
        mAuth = MainActivity.getmAuth();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPasword = findViewById(R.id.reg_password);
        mCreateBtbn = findViewById(R.id.reg_create_btn);
        mCreateBtbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPasword.getText().toString();
                if ( !TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    ComonComponents.showLoader(mRegProgress, R.string.reg_user, "Waiting", false);
                    registerUser(displayName, email, password);
                }
            }
        });
    }

    private void registerUser(final String displayName, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = MainActivity.getmAuth().getCurrentUser();
                            String uid = currentUser.getUid();
                            helper.insert(uid, displayName, email,"ACTIVATE" );
                            DatabaseReference mDataReg = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> userData = new HashMap<>();
                            userData.put("name", displayName);
                            userData.put("status", "ACTIVATE");
                            userData.put("image", "default");
                            userData.put("thumbnail", "default");
                            mDataReg.setValue(userData);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            mUserDatabase.child(uid).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mRegProgress.dismiss();
                                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    });
                        } else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, R.string.cannot_sign_in, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
