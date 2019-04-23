package btl.lapitchat.user;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import btl.lapitchat.R;
import btl.lapitchat.utility.ComonComponents;

public class StatusActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserData;

    private Toolbar mToolbar;
    private EditText mStatusInput;
    private Button mSaveBtn;
    private  ProgressDialog mProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        mUserData = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatusInput = findViewById(R.id.status_input);
        String uid = mAuth.getCurrentUser().getUid();
        mUserData = mUserData.child("users").child(uid);
        mUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(StatusActivity.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                mStatusInput.setHint(dataSnapshot.child("status").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mStatusInput.setHint(UserUtil.getUserData(uid));
        mSaveBtn = findViewById(R.id.save_status_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcess = new ProgressDialog(StatusActivity.this);
                ComonComponents.showLoader(mProcess, R.string.upload, "Updating status in progress!", false);
                String status = mStatusInput.getText().toString();
                mUserData.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProcess.dismiss();
                        }else {
                            mProcess.hide();
                            Toast.makeText( getApplicationContext(),"CANN'T UPDATE THIS CHANGES", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void onSaveStatus( String status){

    }
}
