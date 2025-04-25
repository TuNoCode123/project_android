package com.example.formular_cookie.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.formular_cookie.helper.RecipeDbHelper;
import com.example.formular_cookie.model.Author;
import com.example.formular_cookie.model.Ingredient;
import com.example.formular_cookie.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Repository để quản lý dữ liệu công thức nấu ăn từ Firestore và SQLite.
 * Lớp này sử dụng singleton pattern để đảm bảo chỉ có một instance duy nhất trong toàn bộ ứng dụng.
 * Cung cấp các phương thức để lấy danh sách công thức, tìm kiếm công thức theo từ khóa,
 * Lấy chi tiết công thức theo ID, và xóa cache của công thức đã cũ.
 * Lấy thông tin tác giả cho một công thức.
 */

public class FirebaseRecipeRepository {
    private static final String TAG = "FirebaseRecipeRepo";
    private static final String RECIPES_COLLECTION = "recipes";
    private static final String USERS_COLLECTION = "users";
    private static final long CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1000; // 24 giờ

    private final FirebaseFirestore db;
    private final CollectionReference recipesCollection;
    private final CollectionReference usersCollection;
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
        db = FirebaseFirestore.getInstance();
        recipesCollection = db.collection(RECIPES_COLLECTION);
        usersCollection = db.collection(USERS_COLLECTION);
        dbHelper = RecipeDbHelper.getInstance(context);
    }

    // Check if recipe names are already loaded
    public boolean areRecipeNamesLoaded() {
        return recipeNamesLoaded;
    }

    // Lấy danh sách tên công thức và lưu vào SQLite.
    public void fetchAndStoreRecipeNames(final OnRecipeNamesLoadedListener listener) {
        // Skip if already loaded
        if (recipeNamesLoaded) {
            if (listener != null) {
                listener.onRecipeNamesLoaded(0); // We don't know the count anymore
            }
            return;
        }

        recipesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, String> recipeNames = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    String title = document.getString("title");

                    if (title != null) {
                        recipeNames.put(id, title);
                    }
                }

                // Lưu tên công thức vào SQLite
                dbHelper.insertRecipeNames(recipeNames);
                recipeNamesLoaded = true;

                if (listener != null) {
                    listener.onRecipeNamesLoaded(recipeNames.size());
                }
            } else {
                Log.e(TAG, "Error fetching recipe names: ", task.getException());
                if (listener != null) {
                    listener.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            }
        });
    }

    // Tìm kiếm công thức theo từ khóa.
    public void searchRecipes(String query, final OnRecipesLoadedListener listener) {
        // Tìm kiếm trong SQLite trước
        List<String> recipeIds = dbHelper.searchRecipeIds(query);

        if (recipeIds.isEmpty()) {
            if (listener != null) {
                listener.onRecipesLoaded(new ArrayList<>());
            }
            return;
        }

        // Lấy full thông tin công thức từ Firestore
        fetchRecipesByIds(recipeIds, listener);
    }

    // Lấy nhiều công thức theo danh sách ID
    private void fetchRecipesByIds(List<String> recipeIds, final OnRecipesLoadedListener listener) {
        final List<Recipe> recipes = new ArrayList<>();
        final int[] completedQueries = { 0 };
        final int totalQueries = recipeIds.size();

        for (String recipeId : recipeIds) {
            recipesCollection.document(recipeId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Recipe recipe = convertDocumentToRecipe(document);
                                    if (recipe != null) {
                                        // Lấy thông tin tác giả nếu có authorID
                                        if (recipe.getAuthorID() != null && !recipe.getAuthorID().isEmpty()) {
                                            fetchAuthorForRecipe(recipe, new OnAuthorLoadedListener() {
                                                @Override
                                                public void onAuthorLoaded(Author author) {
                                                    recipe.setAuthor(author);
                                                    recipes.add(recipe);
                                                    checkCompletion();
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    // Vẫn thêm công thức ngay cả khi không lấy được thông tin tác giả
                                                    recipes.add(recipe);
                                                    checkCompletion();
                                                }
                                            });
                                        } else {
                                            recipes.add(recipe);
                                            checkCompletion();
                                        }
                                    } else {
                                        checkCompletion();
                                    }
                                } else {
                                    checkCompletion();
                                }
                            } else {
                                Log.e(TAG, "Error fetching recipe: ", task.getException());
                                checkCompletion();
                            }
                        }

                        private void checkCompletion() {
                            completedQueries[0]++;

                            // Check if all queries are completed
                            if (completedQueries[0] >= totalQueries && listener != null) {
                                listener.onRecipesLoaded(recipes);
                            }
                        }
                    });
        }
    }

    // Lấy chi tiết công thức theo ID với caching.
    public void getRecipeById(String recipeId, final OnRecipeLoadedListener listener) {
        // Kiểm tra xem có trong cache không và có cần cập nhật không
        if (dbHelper.hasRecipeDetails(recipeId) && !dbHelper.isRecipeDetailsOutdated(recipeId, CACHE_TIMEOUT_MILLIS)) {
            // Lấy từ cache nếu có và còn mới
            Recipe cachedRecipe = dbHelper.getRecipeDetails(recipeId);
            if (cachedRecipe != null) {
                if (listener != null) {
                    listener.onRecipeLoaded(cachedRecipe);
                }
                return;
            }
        }

        // Nếu không có trong cache hoặc đã cũ, tải từ Firestore
        recipesCollection.document(recipeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recipe recipe = convertDocumentToRecipe(document);
                        if (recipe != null) {
                            // Lấy thông tin tác giả nếu có authorID
                            if (recipe.getAuthorID() != null && !recipe.getAuthorID().isEmpty()) {
                                fetchAuthorForRecipe(recipe, new OnAuthorLoadedListener() {
                                    @Override
                                    public void onAuthorLoaded(Author author) {
                                        recipe.setAuthor(author);

                                        // Lưu vào cache
                                        dbHelper.saveRecipeDetails(recipe);

                                        if (listener != null) {
                                            listener.onRecipeLoaded(recipe);
                                        }
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        // Vẫn trả về công thức ngay cả khi không lấy được thông tin tác giả
                                        // Lưu vào cache
                                        dbHelper.saveRecipeDetails(recipe);

                                        if (listener != null) {
                                            listener.onRecipeLoaded(recipe);
                                        }
                                    }
                                });
                            } else {
                                // Lưu vào cache
                                dbHelper.saveRecipeDetails(recipe);

                                if (listener != null) {
                                    listener.onRecipeLoaded(recipe);
                                }
                            }
                        } else if (listener != null) {
                            listener.onError("Failed to parse recipe data");
                        }
                    } else if (listener != null) {
                        listener.onError("Recipe not found");
                    }
                } else if (listener != null) {
                    listener.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            }
        });
    }

    // Xóa cache của công thức đã cũ.
    public void cleanupCache() {
        dbHelper.deleteOutdatedRecipeDetails(CACHE_TIMEOUT_MILLIS);
    }

    // Lấy thông tin tác giả cho một công thức.
    private void fetchAuthorForRecipe(Recipe recipe, final OnAuthorLoadedListener listener) {
        String authorId = recipe.getAuthorID();

        usersCollection.document(authorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            Author author = Author.fromMap(authorId, data);
                            if (listener != null) {
                                listener.onAuthorLoaded(author);
                            }
                        } else if (listener != null) {
                            listener.onError("Author data is null");
                        }
                    } else if (listener != null) {
                        listener.onError("Author not found");
                    }
                } else if (listener != null) {
                    listener.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            }
        });
    }

    // Helper method chuyển DocumentSnapshot thành Recipe.
    private Recipe convertDocumentToRecipe(DocumentSnapshot document) {
        Recipe recipe = new Recipe();

        recipe.setId(document.getId());
        recipe.setTitle(document.getString("title"));
        recipe.setImageUrl(document.getString("imageUrl"));
        recipe.setAuthorID(document.getString("authorID"));
        recipe.setSummary(document.getString("description"));

        // recipe.setCategory(document.getString("category"));
        // recipe.setAuthorImageUrl(document.getString("authorImageUrl"));

        // lấy ds nguyên liệu
        List<Map<String, Object>> ingredientsList = (List<Map<String, Object>>) document.get("ingredients");
        if (ingredientsList != null) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredientMap : ingredientsList) {
                Ingredient ingredient = Ingredient.fromMap(ingredientMap);
                ingredients.add(ingredient);
            }
            recipe.setIngredients(ingredients);
        }

        // Lấy ds hướng dẫn
        List<String> steps = (List<String>) document.get("steps");
        if (steps != null) {
            recipe.setSteps(steps);
        }

        return recipe;
    }

    // Callback interfaces
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

    public interface OnAuthorLoadedListener {
        void onAuthorLoaded(Author author);

        void onError(String errorMessage);
    }
}