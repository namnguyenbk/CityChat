package btl.lapitchat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import btl.lapitchat.chat.ChatsFragment;
import btl.lapitchat.chat.FriendsFragment;
import btl.lapitchat.chat.RequestsFragment;

public class SectionPageAdapter extends FragmentPagerAdapter {

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return  requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return  friendsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return  "REQUESTS";
            case 1:
                return  "CHATS";
            case 2:
                return  "FRIENDS";
        }
        return null;
    }
}