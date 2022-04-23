package com.muhibbin.memes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "MemesDB";
    private static String TABLE_NAME = "favoriteTable";
    public static String KEY_ID = "id";
    public static String IMAGE_URL = "ImageURL";
    public static String LIKES = "Likes";
    public static String DISLIKES = "DisLikes";
    public static String FAVORITE = "fStatus";
    private static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+IMAGE_URL+" VARCHAR(500),"+LIKES+" INTEGER,"+DISLIKES+" INTEGER, "+FAVORITE+" INTEGER );";

    private Context context;
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(CREATE_TABLE);
        }
        catch (Exception e)
        {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(String imageUrls, int likes, int dislikes, int favorite)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_URL,imageUrls);
        contentValues.put(LIKES,likes);
        contentValues.put(DISLIKES,dislikes);
        contentValues.put(FAVORITE,favorite);
        long rowId = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return rowId;
    }

    public Cursor read_all_data(String imageUrl) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+ " WHERE "+IMAGE_URL+"='"+imageUrl+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }

    public void updateData(String imageUrls, int likes, int dislikes, int favorite)
    {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_URL,imageUrls);
        contentValues.put(LIKES,likes);
        contentValues.put(DISLIKES,dislikes);
        contentValues.put(FAVORITE,favorite);
        sqLiteDatabase.update(TABLE_NAME,contentValues,IMAGE_URL+" = ?",new String[]{imageUrls});
    }

    public Cursor read_all_favorites() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+ " WHERE "+FAVORITE+"=1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }

}
