package btl.lapitchat.user;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import btl.lapitchat.model.User;

public class UserHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="citychat.db";
    private static final int SCHEMA_VERSION=1;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (_id INTEGER PRIMARY KEY AUTOINCREMENT,userId TEXT, name TEXT, email TEXT, status TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public UserHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    public void insert(String userId, String name, String email, String status) {
//        ContentValues cv=new ContentValues();
//        cv.put("userId", userId);
//        cv.put("name", name);
//        cv.put("email", email);
//        cv.put("status", status);
//        getWritableDatabase().insert("user", "userId", cv);
    }
    public void getUser( String userId ){
//        User user = new User();
//        Cursor cursor = getReadableDatabase()
//                .rawQuery("SELECT _id, userId, name, email, status FROM user WHERE userId=" + userId , null);
//        user.setName(cursor.getString(2));
//        user.setStatus(cursor.getString(4));
//        return user;
    }

}
