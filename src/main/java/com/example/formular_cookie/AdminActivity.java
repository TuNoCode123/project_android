package com.example.formular_cookie;

import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private List<Recipe> recipeListApprove;
    private List<Recipe> recipeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        bottomNavigationView = findViewById(R.id.admin_nav_view);
        recipeListApprove = getDummyRecipes();// TODO: lấy dữ liệu công thức chờ duyệt từ database
        recipeList = getDummyRecipes();// TODO: lấy dữ liệu công thức đã có từ database
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                Bundle bundle = new Bundle();
                int itemId = item.getItemId();

                if (itemId == R.id.nav_approve_recipes) {
                    fragment = new ApproveRecipesFragment();
                    bundle.putSerializable("recipeListApprove", (Serializable) recipeListApprove);
                } else if (itemId == R.id.nav_post_recipe) {
                    fragment = new PostRecipeFragment();
                } else if (itemId == R.id.nav_recipe_list) {
                    bundle.putSerializable("recipeList", (Serializable) recipeList);
                    fragment = new RecipeListFragment();
                } else if (itemId == R.id.nav_account) {
                    fragment = new AccountFragment();
                }
                if (fragment != null) {
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.admin_fragment_container, fragment)
                            .commit();

                    return true;
                }

                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_approve_recipes);
    }
    private List<Recipe> getDummyRecipes() {
        List<Recipe> list = new ArrayList<>();
        list.add(new Recipe("Bánh quy socola", "Bột, Trứng, Socola", "Trộn đều hỗn hợp trứng bột socola sau đó đem nướng", null));
        list.add(new Recipe("Bánh bông lan trứng muối","Bột, Trứng, Socola", "Trộn đều hỗn hợp trứng bột socola sau đó đem nướng", null));
        list.add(new Recipe("Bánh tiramisu truyền thống đậm vị socola", "Bột, Trứng, Socola", "Trộn đều hỗn hợp trứng bột socola sau đó đem nướng", null));
        return list;
    }
}
