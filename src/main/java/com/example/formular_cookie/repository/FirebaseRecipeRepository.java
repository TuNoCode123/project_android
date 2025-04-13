package com.example.formular_cookie.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.formular_cookie.helper.RecipeDbHelper;
import com.example.formular_cookie.model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRecipeRepository {
    private static final String TAG = "FirebaseRecipeRepo";
    private static final String RECIPES_REF = "recipes";

    private final DatabaseReference databaseRef;
    private final RecipeDbHelper dbHelper;
    private boolean recipeNamesLoaded = false;

    // Singleton instance
    private static FirebaseRecipeRepository instance;

    public static synchronized FirebaseRecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseRecipeRepository(context.getApplicationContext());
        }
        return instance;
    }

    private FirebaseRecipeRepository(Context context) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        dbHelper = RecipeDbHelper.getInstance(context);
    }

    // Check xem recipe names đã được tải về chưa
    public boolean areRecipeNamesLoaded() {
        return recipeNamesLoaded;
    }

    // Lấy tên recipe từ Firebase và lưu vào SQLite
    public void fetchAndStoreRecipeNames(final OnRecipeNamesLoadedListener listener) {
        // Bỏ qua nếu đã tải về trước đó
        if (recipeNamesLoaded) {
            if (listener != null) {
                listener.onRecipeNamesLoaded(0); // We don't know the count anymore
            }
            return;
        }

        databaseRef.child(RECIPES_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> recipeNames = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);

                    if (title != null) {
                        recipeNames.put(id, title);
                    }
                }

                // Lưu tên recipe vào SQLite
                dbHelper.insertRecipeNames(recipeNames);
                recipeNamesLoaded = true;

                if (listener != null) {
                    listener.onRecipeNamesLoaded(recipeNames.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching recipe names: " + databaseError.getMessage());
                if (listener != null) {
                    listener.onError(databaseError.getMessage());
                }
            }
        });
    }

    // Tìm tên recipe trên SQLite trước, sau đó lấy chi tiết từ Firebase
    public void searchRecipes(String query, final OnRecipesLoadedListener listener) {
        // Tìm trên SQLite trước
        List<String> recipeIds = dbHelper.searchRecipeIds(query);

        if (recipeIds.isEmpty()) {
            if (listener != null) {
                listener.onRecipesLoaded(new ArrayList<>());
            }
            return;
        }

        // Lấy chi tiết recipe từ Firebase
        fetchRecipesByIds(recipeIds, listener);
    }

    // Lấy chi tiết recipe từ Firebase theo danh sách ID
    private void fetchRecipesByIds(List<String> recipeIds, final OnRecipesLoadedListener listener) {
        final List<Recipe> recipes = new ArrayList<>();
        final int[] completedQueries = {0};
        final int totalQueries = recipeIds.size();

        for (String recipeId : recipeIds) {
            databaseRef.child(RECIPES_REF).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Recipe recipe = dataSnapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            recipe.setId(dataSnapshot.getKey());
                            recipes.add(recipe);
                        }
                    }

                    completedQueries[0]++;

                    // Kiểm tra xem tất cả các truy vấn đã hoàn thành chưa
                    if (completedQueries[0] >= totalQueries && listener != null) {
                        listener.onRecipesLoaded(recipes);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching recipe: " + databaseError.getMessage());
                    completedQueries[0]++;

                    // Check if all queries are completed
                    if (completedQueries[0] >= totalQueries && listener != null) {
                        listener.onRecipesLoaded(recipes);
                    }
                }
            });
        }
    }

    // Lấy chi tiết recipe theo ID
    public void getRecipeById(String recipeId, final OnRecipeLoadedListener listener) {
        databaseRef.child(RECIPES_REF).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setId(dataSnapshot.getKey());

                        if (listener != null) {
                            listener.onRecipeLoaded(recipe);
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("Failed to parse recipe data");
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Recipe not found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching recipe: " + databaseError.getMessage());
                if (listener != null) {
                    listener.onError(databaseError.getMessage());
                }
            }
        });
    }

    public interface OnRecipeNamesLoadedListener {
        void onRecipeNamesLoaded(int count);
        void onError(String errorMessage);
    }

    public interface OnRecipesLoadedListener {
        void onRecipesLoaded(List<Recipe> recipes);
        void onError(String errorMessage);
    }

    public interface OnRecipeLoadedListener {
        void onRecipeLoaded(Recipe recipe);
        void onError(String errorMessage);
    }
}
