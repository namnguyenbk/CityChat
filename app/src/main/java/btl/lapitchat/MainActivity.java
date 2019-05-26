package btl.lapitchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import btl.lapitchat.chat.ChatActivity;
import btl.lapitchat.chat.Friends;
import btl.lapitchat.chat.RequestsFragment;
import btl.lapitchat.user.ProfileActivity;
import btl.lapitchat.user.RssActivity;
import btl.lapitchat.user.SettingActivity;
import btl.lapitchat.user.StartActivity;
import btl.lapitchat.user.UsersActivity;
import btl.lapitchat.utility.ComonComponents;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private String mCurrent_user_id;

    private Toolbar mToolbar;
    private EditText searchEditText;
    private Button searchButton;
    private TextView noFriends;
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mData;

    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();

        searchEditText = findViewById(R.id.search_friends_input);
        searchButton = findViewById(R.id.search_friends_button);
        noFriends = findViewById(R.id.no_friends_label);

        // Toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("City Chat");
        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(MainActivity.this, UsersActivity.class);
                profile.putExtra("user_search", searchEditText.getText().toString().toUpperCase());
                startActivity(profile);
            }
        });
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if ( currentUser == null){
            gotoStartView();
        } else {
            mUserRef.child("online").setValue("true");
            mFriendsList = findViewById(R.id.friends_list);
            mAuth = FirebaseAuth.getInstance();

            mCurrent_user_id = mAuth.getCurrentUser().getUid();

            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user_id);
            FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        System.out.println("Khong co ban");
                        noFriends.setVisibility(View.VISIBLE);
//                        noFriends.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
//                                startActivity(usersIntent);
//                            }
//                        });
                    }
                    else {
                        System.out.println("Co ban");
                        noFriends.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mFriendsDatabase.keepSynced(true);
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mUsersDatabase.keepSynced(true);

            mFriendsList.setHasFixedSize(true);
            mFriendsList.setLayoutManager(new LinearLayoutManager(this));
            FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                    .setQuery(mFriendsDatabase, Friends.class).build();
            FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {
                    friendsViewHolder.setDate(friends.getDate());
                    final String list_user_id = getRef(position).getKey();
                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("image").getValue().toString();
                            if(dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                friendsViewHolder.setUserOnline(userOnline);
                            }
                            friendsViewHolder.setName(userName);
                            friendsViewHolder.setUserImage(userThumb, MainActivity.this);
                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]{"Xem trang cá nhân", "Gửi tin nhắn"};
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Tuỳ chọn");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //Click Event for each item.
                                            if(i == 0){
                                                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                startActivity(profileIntent);
                                            }
                                            if(i == 1){
                                                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("user_id", list_user_id);
                                                chatIntent.putExtra("user_name", userName);
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @NonNull
                @Override
                public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single, viewGroup, false);
                    return new FriendsViewHolder(view);
                }
            };
            friendsRecyclerViewAdapter.startListening();
            mFriendsList.setAdapter(friendsRecyclerViewAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if ( item.getItemId() == R.id.main_logout_btn){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            mAuth.signOut();
            gotoStartView();
        }
        if( item.getItemId() == R.id.request_friends){
            Intent settingIntent = new Intent(MainActivity.this, RequestsFragment.class);
            startActivity(settingIntent);
        }
        if( item.getItemId() == R.id.main_setting_btn){
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        }
        if (item.getItemId() == R.id.main_users_btn){
            Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }if (item.getItemId() == R.id.rss_feed_btn){
            Intent usersIntent = new Intent(MainActivity.this, RssActivity.class);
            startActivity(usersIntent);
        }
         return true;
    }

    private void gotoStartView( ) {
        Intent startIntent = new Intent( MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }
    public static FirebaseDatabase getmData() {
        return mData;
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue("true");

        }

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.status_item);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.avatar_user_item);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.avatar_default).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView =  mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

    }


}
