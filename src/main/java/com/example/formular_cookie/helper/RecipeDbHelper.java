package com.example.formular_cookie.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";

    // Create table
    private static final String SQL_CREATE_RECIPES_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT NOT NULL)";

    // Drop table
    private static final String SQL_DELETE_RECIPES_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_RECIPES;

    // Singleton instance
    private static RecipeDbHelper instance;

    public static synchronized RecipeDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cơ sở dữ liệu này chỉ là bộ nhớ đệm cho dữ liệu trực tuyến, vì vậy nâng cấp của nó
        // là chỉ cần loại bỏ dữ liệu và bắt đầu lại từ đầu
        db.execSQL(SQL_DELETE_RECIPES_TABLE);
        onCreate(db);
    }

    // Insert tên recipe với IDs
    public void insertRecipeNames(Map<String, String> recipeNames) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            // Xóa tất cả dữ liệu cũ
            db.delete(TABLE_RECIPES, null, null);

            // Chèn dữ mới
            for (Map.Entry<String, String> entry : recipeNames.entrySet()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, entry.getKey());
                values.put(COLUMN_TITLE, entry.getValue());

                db.insert(TABLE_RECIPES, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Tìm recipe ID theo tên (substring matching)
    public List<String> searchRecipeIds(String query) {
        List<String> recipeIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = db.query(
                TABLE_RECIPES,
                new String[]{COLUMN_ID},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    recipeIds.add(id);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return recipeIds;
    }
}
