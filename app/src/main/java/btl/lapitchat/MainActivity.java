package btl.lapitchat;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import btl.lapitchat.chat.ChatsFragment;
import btl.lapitchat.chat.FriendsFragment;
import btl.lapitchat.chat.RequestsFragment;
import btl.lapitchat.user.StartActivity;

public class MainActivity extends AppCompatActivity implements
        FriendsFragment.OnFragmentInteractionListener,
        RequestsFragment.OnFragmentInteractionListener,
        ChatsFragment.OnFragmentInteractionListener {
    private Toolbar mToolbar;
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mData;

    private TabLayout mTabMain;
    private ViewPager mViewPager;
    private SectionPageAdapter mSectionPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();

        // Toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lapit Chat");

        // Tabs
        mTabMain = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.main_pager);
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPageAdapter);
        mTabMain.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if ( currentUser == null){
            gotoStartView();
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
            mAuth.signOut();
            gotoStartView();
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
    public void onFragmentInteraction(Uri uri) {
    }
}
