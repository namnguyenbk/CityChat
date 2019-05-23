package btl.lapitchat.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import btl.lapitchat.MainActivity;
import btl.lapitchat.R;
import btl.lapitchat.model.ReqUser;
import btl.lapitchat.model.User;
import btl.lapitchat.user.ProfileActivity;
import btl.lapitchat.user.UsersActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsFragment extends AppCompatActivity {

    private RecyclerView mrReqList;
    private static Context context;

    private DatabaseReference mReqDatabase;
    private DatabaseReference mUserDatabase;
    private Toolbar mToolbar;
    private  FirebaseAuth mAuth;
    private  FirebaseDatabase mData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_requests);
        mToolbar = findViewById(R.id.reqs_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Danh sách yêu cầu kết bạn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mrReqList = findViewById(R.id.request_list);
        mrReqList.setHasFixedSize(true);
        mrReqList.setLayoutManager( new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req").child(mAuth.getUid());
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        RequestsFragment.context = getApplicationContext();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ReqUser> options = new FirebaseRecyclerOptions.Builder<ReqUser>()
                .setQuery(mReqDatabase, ReqUser.class).build();
        FirebaseRecyclerAdapter<ReqUser, RequestsFragment.ReqViewHolder> firebaseApdater = new FirebaseRecyclerAdapter<ReqUser, ReqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ReqViewHolder holder, int position, @NonNull ReqUser model) {
                holder.setStatusView(model.getReq_type());
                final String userId = getRef(position).getKey();
                mUserDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("name").getValue().toString();
                        holder.setNameView(username);
                        String avatar = dataSnapshot.child("image").getValue().toString();
                        holder.setUserImage(avatar);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(RequestsFragment.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public ReqViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single, viewGroup, false);
                return new ReqViewHolder(view);
            }

        };
        firebaseApdater.startListening();
        mrReqList.setAdapter(firebaseApdater);
    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ReqViewHolder( View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setUserImage(String image){

            CircleImageView userImageView =  mView.findViewById(R.id.avatar_user_item);
            Picasso.with(RequestsFragment.context).load(image).placeholder(R.drawable.avatar_default).into(userImageView);

        }

        public void setNameView(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setStatusView( String status){
            TextView statusUserItem = mView.findViewById(R.id.status_item);
            if(status.equals("received")){
                statusUserItem.setText("Đã gửi cho bạn lời mời kết bạn!");
            }else {
                statusUserItem.setText("Bạn đã gửi lời mời kết bạn!");
            }
        }
    }

    public static Context getContext(){
        return RequestsFragment.context;
    }
}
