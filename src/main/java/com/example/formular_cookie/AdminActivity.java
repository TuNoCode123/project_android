package com.example.formular_cookie;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        bottomNavigationView = findViewById(R.id.admin_nav_view);

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "current_fragment");
        }

        if (currentFragment == null) {
            // Nếu không có fragment nào được lưu, khởi tạo một fragment mặc định
            currentFragment = new ApproveRecipesFragment();
        }
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_approve_recipes) {
                    fragment = new ApproveRecipesFragment();
                } else if (itemId == R.id.nav_post_recipe) {
                    fragment = new PostRecipeFragment();
                } else if (itemId == R.id.nav_recipe_list) {
                    fragment = new RecipeListFragment();
                } else if (itemId == R.id.nav_account) {
                    fragment = new AccountFragment();
                } else if (itemId == R.id.nav_manage_users) {
                    fragment = new UserFragment();
                }
                if (fragment != null) {
                    currentFragment = fragment;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu lại fragment hiện tại
        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, "current_fragment", currentFragment);
        }
    }

}
