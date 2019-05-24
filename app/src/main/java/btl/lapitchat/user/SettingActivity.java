package btl.lapitchat.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import btl.lapitchat.R;
import btl.lapitchat.model.User;
import btl.lapitchat.utility.ComonComponents;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private DatabaseReference mDataUser;
    private FirebaseUser mCurrentUser;

    private Toolbar mToolbar;
    private ProgressDialog mLoader;
    private CircleImageView mAvatar;
    private TextView mName;
    private TextView mEmail;
    private Button mChangeAvtarBtn;
    private Button mChangeStatusBtn;
    private Button mChangeNameBtn;
    private StorageReference mStorageRef;
    private  ProgressDialog mProcess;
    UserHelper helper;

    private static  final  int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        helper = new UserHelper(this);
        mToolbar = findViewById(R.id.setting_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAvatar = findViewById(R.id.avatar_setting);
        mName = findViewById(R.id.display_name_setting);
        mEmail = findViewById(R.id.email_setting);

        mChangeAvtarBtn = findViewById(R.id.change_avatar_btn);
        mChangeStatusBtn = findViewById(R.id.change_status_btn);
        mChangeNameBtn = findViewById(R.id.change_displayname_btn);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = mCurrentUser.getUid();
        mDataUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mDataUser.keepSynced(true);
        mDataUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(SettingActivity.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
//                User currentUser = helper.getUser(uid);
                mName.setText(name);
//                mName.setText(currentUser.getName());
                mEmail.setText(mCurrentUser.getEmail());
                if (image.contains("default")){
                    mAvatar.setImageResource(R.drawable.avatar_default);
                }else {
//                    Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.avatar_default).into(mAvatar);
                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(mAvatar);
//                    mAvatar.setImageURI(Uri.parse(image));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChangeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(SettingActivity.this, StatusActivity.class);
                startActivity(statusIntent);
            }
        });

        mChangeAvtarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode,resultCode ,data);
       if ( requestCode == GALLERY_PICK && resultCode == RESULT_OK){
           Uri imageUri = data.getData();
           CropImage.activity(imageUri)
                   .setAspectRatio(1,1)
                   .start(this);
       }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference filePath = mStorageRef.child("avatar_user").child(mCurrentUser.getUid());
                mProcess = new ProgressDialog(this);
                ComonComponents.showLoader(mProcess, R.string.upload, "Upload image in progress!", false);
                filePath.putFile(resultUri).
                continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            mProcess.dismiss();
                            String avatarUrl = task.getResult().toString();
                            mDataUser.child("image").setValue(avatarUrl);
                            Toast.makeText(SettingActivity.this, "UPDATING AVATAR : DONE !", Toast.LENGTH_LONG).show();
                        }else {
                            mProcess.hide();
                            Toast.makeText(SettingActivity.this, "UPDATING AVATAR : FAIL !", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String randomName() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(16);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

}
