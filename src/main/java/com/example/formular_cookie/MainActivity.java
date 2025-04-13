package com.example.formular_cookie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.formular_cookie.model.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.formular_cookie.repository.FirebaseRecipeRepository;

public class MainActivity extends AppCompatActivity implements RecipeSearchFragment.OnRecipeSelectedListener {
    private BottomNavigationView bottomNavigationView;

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

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        // Chuyển đến RecipeDetailFragment khi người dùng chọn một công thức
        Fragment detailFragment = RecipeDetailFragment.newInstance(recipe);
        loadFragment(detailFragment, true);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Handle navigation item clicks
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new RecipeSearchFragment();
            } else if (itemId == R.id.nav_smart_cooking) {
                // Create other fragments as needed
                Toast.makeText(MainActivity.this, "Nấu ăn thông minh", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_shopping) {
                Toast.makeText(MainActivity.this, "Mua sắm", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                Toast.makeText(MainActivity.this, "Tài khoản", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}