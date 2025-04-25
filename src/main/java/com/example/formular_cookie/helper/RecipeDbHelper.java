package com.example.formular_cookie.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.formular_cookie.model.Ingredient;
import com.example.formular_cookie.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *Helper class để quản lý cơ sở dữ liệu SQLite cho công thức nấu ăn.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng recipes - bảng chính
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";

    // Bảng recipe_details - bảng chi tiết
    public static final String TABLE_RECIPE_DETAILS = "recipe_details";
    public static final String COLUMN_RECIPE_ID = "recipe_id"; // foreign key đến recipes.id
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String COLUMN_AUTHOR_IMAGE_URL = "author_image_url";
    public static final String COLUMN_LAST_UPDATED = "last_updated"; // timestamp cho caching

    // Bảng recipe_ingredients - bảng cho nguyên liệu
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
    public static final String COLUMN_INGREDIENT_NAME = "name";
    public static final String COLUMN_INGREDIENT_AMOUNT = "amount";
    public static final String COLUMN_INGREDIENT_UNIT = "unit";

    // Bảng recipe_steps - bảng cho các bước
    public static final String TABLE_RECIPE_STEPS = "recipe_steps";
    public static final String COLUMN_STEP_ID = "step_id";
    public static final String COLUMN_STEP_NUMBER = "step_number";
    public static final String COLUMN_STEP_DESCRIPTION = "description";

    // SQL tạo bảng recipes
    private static final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
            COLUMN_ID + " TEXT PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT NOT NULL)";

    // SQL xoá bảng recipes
    private static final String SQL_DELETE_RECIPES_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECIPES;

    // SQL tạo bảng recipe_details
    private static final String SQL_CREATE_RECIPE_DETAILS_TABLE = "CREATE TABLE " + TABLE_RECIPE_DETAILS + " (" +
            COLUMN_RECIPE_ID + " TEXT PRIMARY KEY, " +
            COLUMN_IMAGE_URL + " TEXT, " +
            COLUMN_SUMMARY + " TEXT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_AUTHOR_ID + " TEXT, " +
            COLUMN_AUTHOR_IMAGE_URL + " TEXT, " +
            COLUMN_LAST_UPDATED + " INTEGER, " +
            "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
            TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL tạo bảng recipe_ingredients
    private static final String SQL_CREATE_RECIPE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
            +
            COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_ID + " TEXT NOT NULL, " +
            COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
            COLUMN_INGREDIENT_AMOUNT + " TEXT, " +
            COLUMN_INGREDIENT_UNIT + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
            TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL tạo bảng recipe_steps
    private static final String SQL_CREATE_RECIPE_STEPS_TABLE = "CREATE TABLE " + TABLE_RECIPE_STEPS + " (" +
            COLUMN_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_ID + " TEXT NOT NULL, " +
            COLUMN_STEP_NUMBER + " INTEGER NOT NULL, " +
            COLUMN_STEP_DESCRIPTION + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
            TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

    // SQL xoá các bảng chi tiết
    private static final String SQL_DELETE_RECIPE_DETAILS_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECIPE_DETAILS;
    private static final String SQL_DELETE_RECIPE_INGREDIENTS_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_RECIPE_INGREDIENTS;
    private static final String SQL_DELETE_RECIPE_STEPS_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECIPE_STEPS;

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

    // Tạo các bảng trong cơ sở dữ liệu.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RECIPES_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_INGREDIENTS_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_STEPS_TABLE);
    }

    // Nâng cấp cơ sở dữ liệu khi thay đổi phiên bản.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cơ sở dữ liệu này chỉ là bộ nhớ đệm cho dữ liệu trực tuyến, vì vậy nâng cấp
        // của nó
        // là chỉ cần loại bỏ dữ liệu và bắt đầu lại từ đầu
        db.execSQL(SQL_DELETE_RECIPES_TABLE);
        db.execSQL(SQL_DELETE_RECIPE_DETAILS_TABLE);
        db.execSQL(SQL_DELETE_RECIPE_INGREDIENTS_TABLE);
        db.execSQL(SQL_DELETE_RECIPE_STEPS_TABLE);
        onCreate(db);
    }

    // Chèn tên công thức vào cơ sở dữ liệu.
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

    // Tìm kiếm ID công thức theo tên.
    public List<String> searchRecipeIds(String query) {
        List<String> recipeIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = new String[] { "%" + query + "%" };

        Cursor cursor = db.query(
                TABLE_RECIPES,
                new String[] { COLUMN_ID },
                selection,
                selectionArgs,
                null,
                null,
                null);

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

    // Lưu chi tiết công thức vào cơ sở dữ liệu.
    public void saveRecipeDetails(Recipe recipe) {
        if (recipe == null || recipe.getId() == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            // 1. Lưu thông tin chi tiết cơ bản
            ContentValues detailValues = new ContentValues();
            detailValues.put(COLUMN_RECIPE_ID, recipe.getId());
            detailValues.put(COLUMN_IMAGE_URL, recipe.getImageUrl());
            detailValues.put(COLUMN_SUMMARY, recipe.getSummary());
            detailValues.put(COLUMN_CATEGORY, recipe.getCategory());
            detailValues.put(COLUMN_AUTHOR_ID, recipe.getAuthorID());
            detailValues.put(COLUMN_AUTHOR_IMAGE_URL, recipe.getAuthorImageUrl());
            detailValues.put(COLUMN_LAST_UPDATED, System.currentTimeMillis());

            // Xóa chi tiết cũ nếu có
            db.delete(TABLE_RECIPE_DETAILS, COLUMN_RECIPE_ID + " = ?", new String[] { recipe.getId() });
            db.insert(TABLE_RECIPE_DETAILS, null, detailValues);

            // 2. Lưu danh sách nguyên liệu
            db.delete(TABLE_RECIPE_INGREDIENTS, COLUMN_RECIPE_ID + " = ?", new String[] { recipe.getId() });
            if (recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put(COLUMN_RECIPE_ID, recipe.getId());
                    ingredientValues.put(COLUMN_INGREDIENT_NAME, ingredient.getName());
                    ingredientValues.put(COLUMN_INGREDIENT_AMOUNT, ingredient.getAmount());
                    ingredientValues.put(COLUMN_INGREDIENT_UNIT, ingredient.getUnit());
                    db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
                }
            }

            // 3. Lưu các bước thực hiện
            db.delete(TABLE_RECIPE_STEPS, COLUMN_RECIPE_ID + " = ?", new String[] { recipe.getId() });
            if (recipe.getSteps() != null) {
                int stepNumber = 0;
                for (String step : recipe.getSteps()) {
                    ContentValues stepValues = new ContentValues();
                    stepValues.put(COLUMN_RECIPE_ID, recipe.getId());
                    stepValues.put(COLUMN_STEP_NUMBER, stepNumber++);
                    stepValues.put(COLUMN_STEP_DESCRIPTION, step);
                    db.insert(TABLE_RECIPE_STEPS, null, stepValues);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Kiểm tra xem chi tiết công thức có tồn tại không.
    public boolean hasRecipeDetails(String recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RECIPE_DETAILS,
                new String[] { COLUMN_RECIPE_ID },
                COLUMN_RECIPE_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                null);

        boolean hasDetails = false;
        try {
            hasDetails = cursor.getCount() > 0;
        } finally {
            cursor.close();
        }
        return hasDetails;
    }

    // Kiểm tra xem chi tiết công thức có cần cập nhật không.
    public boolean isRecipeDetailsOutdated(String recipeId, long cacheTimeoutMillis) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RECIPE_DETAILS,
                new String[] { COLUMN_LAST_UPDATED },
                COLUMN_RECIPE_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                null);

        try {
            if (cursor.moveToFirst()) {
                long lastUpdated = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_UPDATED));
                long currentTime = System.currentTimeMillis();
                return (currentTime - lastUpdated) > cacheTimeoutMillis;
            }
            return true; // Không có dữ liệu, cần cập nhật
        } finally {
            cursor.close();
        }
    }

    // Lấy chi tiết công thức theo ID.
    public Recipe getRecipeDetails(String recipeId) {
        Recipe recipe = null;
        SQLiteDatabase db = getReadableDatabase();

        // 1. Lấy thông tin từ bảng recipes (bảng chính)
        Cursor recipeCursor = db.query(
                TABLE_RECIPES,
                new String[] { COLUMN_ID, COLUMN_TITLE },
                COLUMN_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                null);

        try {
            if (recipeCursor.moveToFirst()) {
                recipe = new Recipe();
                recipe.setId(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COLUMN_ID)));
                recipe.setTitle(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            } else {
                return null; // Không tìm thấy công thức cơ bản
            }
        } finally {
            recipeCursor.close();
        }

        // 2. Lấy thông tin chi tiết từ bảng recipe_details
        Cursor detailCursor = db.query(
                TABLE_RECIPE_DETAILS,
                new String[] { COLUMN_IMAGE_URL, COLUMN_SUMMARY, COLUMN_CATEGORY,
                        COLUMN_AUTHOR_ID, COLUMN_AUTHOR_IMAGE_URL },
                COLUMN_RECIPE_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                null);

        try {
            if (detailCursor.moveToFirst()) {
                recipe.setImageUrl(detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                recipe.setSummary(detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_SUMMARY)));
                recipe.setCategory(detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                recipe.setAuthorID(detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_AUTHOR_ID)));
                recipe.setAuthorImageUrl(
                        detailCursor.getString(detailCursor.getColumnIndexOrThrow(COLUMN_AUTHOR_IMAGE_URL)));
            }
        } finally {
            detailCursor.close();
        }

        // 3. Lấy danh sách nguyên liệu
        List<Ingredient> ingredients = new ArrayList<>();
        Cursor ingredientsCursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                new String[] { COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_AMOUNT, COLUMN_INGREDIENT_UNIT },
                COLUMN_RECIPE_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                null);

        try {
            while (ingredientsCursor.moveToNext()) {
                String name = ingredientsCursor
                        .getString(ingredientsCursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME));
                String amount = ingredientsCursor
                        .getString(ingredientsCursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_AMOUNT));
                String unit = ingredientsCursor
                        .getString(ingredientsCursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_UNIT));

                Ingredient ingredient = new Ingredient(name, amount, unit);
                ingredients.add(ingredient);
            }
            recipe.setIngredients(ingredients);
        } finally {
            ingredientsCursor.close();
        }

        // 4. Lấy các bước thực hiện
        List<String> steps = new ArrayList<>();
        Cursor stepsCursor = db.query(
                TABLE_RECIPE_STEPS,
                new String[] { COLUMN_STEP_DESCRIPTION },
                COLUMN_RECIPE_ID + " = ?",
                new String[] { recipeId },
                null,
                null,
                COLUMN_STEP_NUMBER + " ASC" // Sắp xếp theo thứ tự bước
        );

        try {
            while (stepsCursor.moveToNext()) {
                String step = stepsCursor.getString(stepsCursor.getColumnIndexOrThrow(COLUMN_STEP_DESCRIPTION));
                steps.add(step);
            }
            recipe.setSteps(steps);
        } finally {
            stepsCursor.close();
        }

        return recipe;
    }

    // Lấy danh sách các công thức đã lưu chi tiết
    public List<String> getCachedRecipeIds() {
        List<String> recipeIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_DETAILS,
                new String[] { COLUMN_RECIPE_ID },
                null,
                null,
                null,
                null,
                null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                recipeIds.add(id);
            }
        } finally {
            cursor.close();
        }

        return recipeIds;
    }

    // Xóa chi tiết của một công thức
    public void deleteRecipeDetails(String recipeId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RECIPE_DETAILS, COLUMN_RECIPE_ID + " = ?", new String[] { recipeId });
        db.delete(TABLE_RECIPE_INGREDIENTS, COLUMN_RECIPE_ID + " = ?", new String[] { recipeId });
        db.delete(TABLE_RECIPE_STEPS, COLUMN_RECIPE_ID + " = ?", new String[] { recipeId });
    }

    // Xóa chi tiết công thức cũ hơn một thời gian nhất định.
    public int deleteOutdatedRecipeDetails(long cacheTimeoutMillis) {
        long oldestAllowedTimestamp = System.currentTimeMillis() - cacheTimeoutMillis;
        SQLiteDatabase db = getWritableDatabase();

        // Lấy danh sách các ID công thức cần xóa
        Cursor cursor = db.query(
                TABLE_RECIPE_DETAILS,
                new String[] { COLUMN_RECIPE_ID },
                COLUMN_LAST_UPDATED + " < ?",
                new String[] { String.valueOf(oldestAllowedTimestamp) },
                null,
                null,
                null);

        List<String> recipeIdsToDelete = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                recipeIdsToDelete.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)));
            }
        } finally {
            cursor.close();
        }

        // Xóa chi tiết của mỗi công thức
        int deletedCount = 0;
        for (String recipeId : recipeIdsToDelete) {
            deleteRecipeDetails(recipeId);
            deletedCount++;
        }

        return deletedCount;
    }
}