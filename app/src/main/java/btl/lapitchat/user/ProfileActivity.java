package btl.lapitchat.user;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import btl.lapitchat.R;
import btl.lapitchat.utility.ComonComponents;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplaynameProfile;
    private ImageView mAvatarProfile;
    private TextView mStatusProfile;
    private TextView mTotalFriendProfile;
    private Button mSendReqFriendBtnProfile;
    private Button mSendDeclineFriendBtnProfile;
    private ProgressDialog mProgress;
    private DatabaseReference mUserData;
    private String mCurrentState;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userId = getIntent().getStringExtra("user_id");
        mAuth = FirebaseAuth.getInstance();
        mUserData = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mAvatarProfile = findViewById(R.id.profile_avatar);
        mAvatarProfile.setImageResource(R.drawable.avatar_default);
        mStatusProfile = findViewById(R.id.profile_status);
        mTotalFriendProfile = findViewById(R.id.profile_total_friend);
        mSendReqFriendBtnProfile = findViewById(R.id.profile_req_friend_btn);
        mSendDeclineFriendBtnProfile = findViewById(R.id.profile_decline_friend_btn);
        mDisplaynameProfile = findViewById(R.id.profile_displayname);
        mProgress = new ProgressDialog(ProfileActivity.this);
//        mCurrentState = mFriendReqDatabase.child();
        mCurrentState = "not_friend";
        mFriendsDatabase.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)){
                    mCurrentState = "friend";
                    mSendReqFriendBtnProfile.setText(getString(R.string.unfriend));
                    mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#E71313"));
                    mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                    mSendDeclineFriendBtnProfile.setEnabled(false);
                    mUserData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mProgress.dismiss();
                            String displayName = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();

                            Picasso.with(ProfileActivity.this).load(image).into(mAvatarProfile);
                            mDisplaynameProfile.setText(displayName);
                            mStatusProfile.setText(status);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }else {
                    mUserData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String displayName = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();

                            Picasso.with(ProfileActivity.this).load(image).into(mAvatarProfile);
                            mDisplaynameProfile.setText(displayName);
                            mStatusProfile.setText(status);
                            // Friend list
                            mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userId)) {
                                        String req_type = dataSnapshot.child(userId).child("req_type").getValue().toString();
                                        if (req_type.equals("received")) {
                                            mCurrentState = "req_received";
                                            mSendReqFriendBtnProfile.setText(getString(R.string.accept_friend_req));
                                            mSendDeclineFriendBtnProfile.setVisibility(View.VISIBLE);
                                            mSendDeclineFriendBtnProfile.setEnabled(true);
                                        } else {
                                            if (req_type.equals("sent")) {
                                                mCurrentState = "req_sent";
                                                mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#E71313"));
                                                mSendReqFriendBtnProfile.setText(getString(R.string.cancel_req_firend));
                                                mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                mSendDeclineFriendBtnProfile.setEnabled(false);
                                            }
                                        }
                                        mProgress.dismiss();
                                    } else {
                                        mFriendsDatabase.child(mCurrentUser.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.hasChild(userId)) {
                                                            mCurrentState = "friend";
                                                            mSendReqFriendBtnProfile.setText(getString(R.string.unfriend));
                                                            mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#E71313"));
                                                            mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                            mSendDeclineFriendBtnProfile.setEnabled(false);
                                                        }
                                                        mProgress.dismiss();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        mProgress.dismiss();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        ComonComponents.showLoader(mProgress, R.string.wait_mess, "Xin vui lòng chờ", false);
        if(!mCurrentState.equals("friend")){

        }

        mSendDeclineFriendBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mSendReqFriendBtnProfile.setEnabled(true);
                                                mCurrentState = "not_friend";
                                                mSendReqFriendBtnProfile.setText(getString(R.string.send_firend_req));
                                                mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#419ADF"));
                                                Toast.makeText(ProfileActivity.this, "Từ chối kết bạn", Toast.LENGTH_LONG).show();
                                                mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                mSendDeclineFriendBtnProfile.setEnabled(false);
                                            }
                                        });
                            }
                        });
            }
        });

        mSendReqFriendBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendReqFriendBtnProfile.setEnabled(false);
                // not friend
                if(mCurrentState.equals("not_friend")){
//                    mSendDeclineFriendBtnProfile.setText("");
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).child("req_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if( task.isSuccessful()){
                                        mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).child("req_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#E71313"));
                                                        mCurrentState = "req_sent";
                                                        mSendReqFriendBtnProfile.setText(getString(R.string.cancel_req_firend));
                                                        Toast.makeText(ProfileActivity.this, "Gửi lời mời kết bạn thành công!", Toast.LENGTH_LONG).show();
                                                        mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                        mSendDeclineFriendBtnProfile.setEnabled(false);
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(ProfileActivity.this, "Không thể gửi lời mời kết bạn!", Toast.LENGTH_LONG).show();
                                    }
                                    mSendReqFriendBtnProfile.setEnabled(true);
                                }
                            });
                }
                // not friend

                // cancel request friend
                if (mCurrentState.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mSendReqFriendBtnProfile.setEnabled(true);
                                                    mCurrentState = "not_friend";
                                                    mSendReqFriendBtnProfile.setText(getString(R.string.send_firend_req));
                                                    mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#419ADF"));
                                                    Toast.makeText(ProfileActivity.this, "Huỷ lời mời kết bạn", Toast.LENGTH_LONG).show();
                                                    mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                    mSendDeclineFriendBtnProfile.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }

                // REQ received
                if (mCurrentState.equals("req_received")) {
                    final String createdDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDatabase.child(mCurrentUser.getUid()).child(userId).child("date").setValue(createdDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendsDatabase.child(userId).child(mCurrentUser.getUid()).child("date").setValue(createdDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mSendReqFriendBtnProfile.setEnabled(true);
                                                                    mCurrentState = "friend";
                                                                    mSendReqFriendBtnProfile.setText(getString(R.string.unfriend));
                                                                    mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#E71313"));

                                                                    mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                                    mSendDeclineFriendBtnProfile.setEnabled(false);
//                                                                    Toast.makeText(ProfileActivity.this, "Cancel request successfully", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userId).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
//                                                                    Toast.makeText(ProfileActivity.this, "Cancel request successfully", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                }

//                if(mCurrentState.equals("friend")){
//                    Map unfriendMap = new HashMap()
//                }
                if (mCurrentState.equals("friend")) {
                    mFriendsDatabase.child(mCurrentUser.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendsDatabase.child(userId).child(mCurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mSendReqFriendBtnProfile.setEnabled(true);
                                                    mCurrentState = "not_friend";
                                                    mSendReqFriendBtnProfile.setText(getString(R.string.send_firend_req));
                                                    mSendReqFriendBtnProfile.setBackgroundColor(Color.parseColor("#419ADF"));
                                                    Toast.makeText(ProfileActivity.this, "Đã huỷ kết bạn", Toast.LENGTH_LONG).show();
                                                    mSendDeclineFriendBtnProfile.setVisibility(View.INVISIBLE);
                                                    mSendDeclineFriendBtnProfile.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }
}
