package com.example.formular_cookie;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.formular_cookie.model.Recipe;
import com.example.formular_cookie.repository.FirebaseRecipeRepository;
import com.example.formular_cookie.route.NavigationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.formular_cookie.fragment.RecipeSearchFragment;

public class MainActivity extends AppCompatActivity implements RecipeSearchFragment.OnRecipeSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tải danh sách tên công thức từ Firebase
        FirebaseRecipeRepository.getInstance(this).fetchAndStoreRecipeNames(new FirebaseRecipeRepository.OnRecipeNamesLoadedListener() {
            @Override
            public void onRecipeNamesLoaded(int count) {
                // Tên công thức đã được tải thành công
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
            }
        });
        navigationManager = new NavigationManager(getSupportFragmentManager());

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (savedInstanceState != null) {
            // Khôi phục trạng thái
            String currentTabTag = savedInstanceState.getString("currentTabTag", NavigationManager.TAG_HOME_FRAGMENT);
            boolean isDetailShowing = savedInstanceState.getBoolean("isDetailShowing", false);
            Recipe currentRecipe = savedInstanceState.getParcelable("currentRecipe");

            navigationManager.restoreTabState(currentTabTag, isDetailShowing, currentRecipe);
        } else {
            navigationManager.switchToTab(NavigationManager.TAG_HOME_FRAGMENT, false);
        }

        setupBottomNavigation();
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        navigationManager.showDetailFragment(recipe, true);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            String newTabTag = NavigationManager.TAG_HOME_FRAGMENT;

            if (item.getItemId() == R.id.nav_search) {
                newTabTag = NavigationManager.TAG_SEARCH_FRAGMENT;
            }else if(item.getItemId() == R.id.nav_shopping){ //TODO: Thay bằng fragment thực tế
                newTabTag = NavigationManager.TAG_POST_RESCIPE_FRAGMENT;
            }else if(item.getItemId() == R.id.nav_account){ //TODO: Thay bằng fragment thực tế
                newTabTag = NavigationManager.TAG_ACCOUNT;
            }

            if (!newTabTag.equals(navigationManager.getCurrentTabTag())) {
                navigationManager.clearBackStack();
                navigationManager.switchToTab(newTabTag, false);
            }

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}