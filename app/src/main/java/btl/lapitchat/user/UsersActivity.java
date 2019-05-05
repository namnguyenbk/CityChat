package btl.lapitchat.user;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import btl.lapitchat.R;
import btl.lapitchat.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mDataBaseUser;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        UsersActivity.context = getApplicationContext();

        mDataBaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersList = findViewById(R.id.list_users_view);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager( new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(mDataBaseUser, User.class).build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseApdater = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.setNameView(model.getName());
                holder.setStatusView(model.getStatus());
                holder.setAvatarView(model.getImage());
                final String userId = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single, viewGroup, false);
                return new UsersViewHolder(view);
            }

        };
        firebaseApdater.startListening();
        mUsersList.setAdapter(firebaseApdater);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder( View itemView){
            super(itemView);
            mView = itemView;
        }

        public  void setNameView(String name) {
            TextView nameUserItem = mView.findViewById(R.id.name_user_item);
            nameUserItem.setText(name);
        }

        public void setStatusView( String status){
            TextView statusUserItem = mView.findViewById(R.id.status_item);
            statusUserItem.setText(status);
        }

        public void setAvatarView( String url){
            CircleImageView avatarUserItem = mView.findViewById(R.id.avatar_user_item);
            Picasso.with(UsersActivity.getContext()).load(url).placeholder(R.drawable.avatar_default).into(avatarUserItem);
        }
    }

    public static Context getContext(){
        return UsersActivity.context;
    }
}
