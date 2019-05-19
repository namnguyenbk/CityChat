//package btl.lapitchat.chat;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import btl.lapitchat.R;
//import btl.lapitchat.model.User;
//import btl.lapitchat.user.ProfileActivity;
//import btl.lapitchat.user.UsersActivity;
//import de.hdodenhof.circleimageview.CircleImageView;
//
//import static com.firebase.ui.auth.AuthUI.getApplicationContext;
//
//
//public class FriendsFragment extends AppCompatActivity {
//
//    private RecyclerView mFriendsList;
//
//    private DatabaseReference mFriendsDatabase;
//    private DatabaseReference mUsersDatabase;
//
//    private FirebaseAuth mAuth;
//
//    private String mCurrent_user_id;
//
//    public FriendsFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mFriendsList = findViewById(R.id.friends_list);
//        mAuth = FirebaseAuth.getInstance();
//
//        mCurrent_user_id = mAuth.getCurrentUser().getUid();
//
//        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user_id);
//        mFriendsDatabase.keepSynced(true);
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
//        mUsersDatabase.keepSynced(true);
//
//        mFriendsList.setHasFixedSize(true);
//        mFriendsList.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
//                .setQuery(mFriendsDatabase, Friends.class).build();
//        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {
//                friendsViewHolder.setDate(friends.getDate());
//                final String list_user_id = getRef(position).getKey();
//                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        final String userName = dataSnapshot.child("name").getValue().toString();
//                        String userThumb = dataSnapshot.child("image").getValue().toString();
//                        if(dataSnapshot.hasChild("online")) {
//                            String userOnline = dataSnapshot.child("online").getValue().toString();
//                            friendsViewHolder.setUserOnline(userOnline);
//                        }
//                        friendsViewHolder.setName(userName);
//                        friendsViewHolder.setUserImage(userThumb, FriendsFragment.this);
//                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
//                                final AlertDialog.Builder builder = new AlertDialog.Builder(FriendsFragment.this);
//                                builder.setTitle("Select Options");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        //Click Event for each item.
//                                        if(i == 0){
//                                            Intent profileIntent = new Intent(FriendsFragment.this, ProfileActivity.class);
//                                            profileIntent.putExtra("user_id", list_user_id);
//                                            startActivity(profileIntent);
//                                        }
//                                        if(i == 1){
//                                            Intent chatIntent = new Intent(FriendsFragment.this, ChatActivity.class);
//                                            chatIntent.putExtra("user_id", list_user_id);
//                                            chatIntent.putExtra("user_name", userName);
//                                            startActivity(chatIntent);
//                                        }
//                                    }
//                                });
//                                builder.show();
//                            }
//                        });
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single, viewGroup, false);
//                return new FriendsViewHolder(view);
//            }
//        };
//        friendsRecyclerViewAdapter.startListening();
//        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
//    }
//
//    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public FriendsViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//
//        }
//
//        public void setDate(String date){
//
//            TextView userStatusView = (TextView) mView.findViewById(R.id.status_item);
//            userStatusView.setText(date);
//
//        }
//
//        public void setName(String name){
//
//            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
//            userNameView.setText(name);
//
//        }
//
//        public void setUserImage(String thumb_image, Context ctx){
//
//            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.avatar_user_item);
//            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.avatar_default).into(userImageView);
//
//        }
//
//        public void setUserOnline(String online_status) {
//
//            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);
//
//            if(online_status.equals("true")){
//                userOnlineView.setVisibility(View.VISIBLE);
//            } else {
//                userOnlineView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//    }
//
//}
