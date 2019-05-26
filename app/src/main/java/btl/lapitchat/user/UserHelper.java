package btl.lapitchat.user;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import btl.lapitchat.model.User;

public class UserHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="User";
    private static final int SCHEMA_VERSION=1;


    public UserHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "Create Table If not exists User (_id Integer Primary Key Autoincrement,userId TEXT, name TEXT, email TEXT, status TEXT)";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    public boolean insert(String userId, String name, String email, String status) {
        SQLiteDatabase db= this.getWritableDatabase();
        String selectQuery = "Select * from User where email like '%" + email + "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            db.close();
            return false; // da co trong database
        }
        else {
            ContentValues cv=new ContentValues();
            cv.put("userId", userId);
            cv.put("name", name);
            cv.put("email", email);
            cv.put("status", status);
            db.insert("User", null, cv);
            db.close();
            return true;
        }
    }
//    public void getUser( String userId ){
////        User user = new User();
////        Cursor cursor = getReadableDatabase()
////                .rawQuery("SELECT _id, userId, name, email, status FROM user WHERE userId=" + userId , null);
////        user.setName(cursor.getString(2));
////        user.setStatus(cursor.getString(4));
////        return user;
//    }

    public ArrayList<String> getAllUserEmail() {
        ArrayList<String> emailList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT * FROM User";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                // Thêm vào danh sách.
                emailList.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        // return note list
        return emailList;
    }

}
