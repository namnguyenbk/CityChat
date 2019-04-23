package btl.lapitchat.utility;

import android.app.ProgressDialog;

import btl.lapitchat.R;

public class ComonComponents {
    public static void showLoader(ProgressDialog loader, int title, String message, Boolean canceledTouchOut){
        loader.setTitle(title);
        loader.setMessage(message);
        loader.setCanceledOnTouchOutside(canceledTouchOut);
        loader.show();
    }
}
