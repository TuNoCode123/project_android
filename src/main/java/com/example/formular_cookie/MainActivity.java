package com.example.formular_cookie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.formular_cookie.model.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.formular_cookie.repository.FirebaseRecipeRepository;

import com.example.formular_cookie.fragment.RecipeDetailFragment;
import com.example.formular_cookie.fragment.RecipeSearchFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecipeSearchFragment.OnRecipeSelectedListener {
    private BottomNavigationView bottomNavigationView;

    // Các tag để quản lý fragment
    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_SMART_COOKING_FRAGMENT = "smart_cooking_fragment";
    private static final String TAG_SHOPPING_FRAGMENT = "shopping_fragment";
    private static final String TAG_ACCOUNT_FRAGMENT = "account_fragment";
    private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    // Map để lưu trữ các fragment
    private Map<String, Fragment> fragmentMap = new HashMap<>();

    // Lưu trạng thái hiện tại
    private String currentTabTag = TAG_HOME_FRAGMENT;

    // Lưu trạng thái chi tiết cho mỗi tab
    private Map<String, TabState> tabStates = new HashMap<>();

    // Lớp để lưu trạng thái của mỗi tab
    private static class TabState {
        boolean isDetailShowing = false;
        Recipe currentRecipe = null;

        TabState() {}

        TabState(boolean isDetailShowing, Recipe currentRecipe) {
            this.isDetailShowing = isDetailShowing;
            this.currentRecipe = currentRecipe;
        }
    }

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

        // Khởi tạo trạng thái cho mỗi tab
        initTabStates();

        if (savedInstanceState != null) {
            // Khôi phục trạng thái
            currentTabTag = savedInstanceState.getString("currentTabTag", TAG_HOME_FRAGMENT);

            // Khôi phục trạng thái chi tiết cho mỗi tab
            restoreTabStates(savedInstanceState);

            // Khôi phục các fragment từ FragmentManager
            restoreFragments();
        } else {
            // Khởi tạo các fragment lần đầu
            initFragments();
        }

        setupBottomNavigation();

        // Hiển thị fragment ban đầu hoặc khôi phục trạng thái
        TabState currentState = tabStates.get(currentTabTag);
        if (currentState != null && currentState.isDetailShowing && currentState.currentRecipe != null) {
            // Nếu đang hiển thị fragment chi tiết, hiển thị lại nó
            showDetailFragment(currentState.currentRecipe, false);
        } else {
            // Nếu không, hiển thị tab hiện tại
            switchToTab(currentTabTag, false);

            // Cập nhật lại item được chọn trong bottom navigation
            updateSelectedNavigationItem(currentTabTag);
        }
    }

    private void initTabStates() {
        tabStates.put(TAG_HOME_FRAGMENT, new TabState());
        tabStates.put(TAG_SEARCH_FRAGMENT, new TabState());
        tabStates.put(TAG_SMART_COOKING_FRAGMENT, new TabState());
        tabStates.put(TAG_SHOPPING_FRAGMENT, new TabState());
        tabStates.put(TAG_ACCOUNT_FRAGMENT, new TabState());
    }

    private void restoreTabStates(Bundle savedInstanceState) {
        for (String tabTag : tabStates.keySet()) {
            boolean isDetailShowing = savedInstanceState.getBoolean("isDetailShowing_" + tabTag, false);
            Recipe recipe = savedInstanceState.getParcelable("currentRecipe_" + tabTag);
            tabStates.put(tabTag, new TabState(isDetailShowing, recipe));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái hiện tại
        outState.putString("currentTabTag", currentTabTag);

        // Lưu trạng thái chi tiết cho mỗi tab
        for (Map.Entry<String, TabState> entry : tabStates.entrySet()) {
            String tabTag = entry.getKey();
            TabState state = entry.getValue();
            outState.putBoolean("isDetailShowing_" + tabTag, state.isDetailShowing);
            if (state.currentRecipe != null) {
                outState.putParcelable("currentRecipe_" + tabTag, state.currentRecipe);
            }
        }
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        // Lưu công thức hiện tại cho tab hiện tại
        TabState state = tabStates.get(currentTabTag);
        if (state != null) {
            state.isDetailShowing = true;
            state.currentRecipe = recipe;
        }

        // Chuyển đến RecipeDetailFragment
        showDetailFragment(recipe, true);
    }

    private void showDetailFragment(Recipe recipe, boolean addToBackStack) {
        // Tạo fragment chi tiết mới
        Fragment detailFragment = RecipeDetailFragment.newInstance(recipe);

        // Hiển thị fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, detailFragment, TAG_DETAIL_FRAGMENT);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
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

            // Cập nhật trạng thái chi tiết cho tab hiện tại
            TabState state = tabStates.get(currentTabTag);
            if (state != null) {
                state.isDetailShowing = false;
                state.currentRecipe = null;
            }
        } else {
            super.onBackPressed();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String newTabTag = currentTabTag;

            // Handle navigation item clicks
            if (itemId == R.id.nav_home) {
                newTabTag = TAG_HOME_FRAGMENT;
            } else if (itemId == R.id.nav_search) {
                newTabTag = TAG_SEARCH_FRAGMENT;
            } else if (itemId == R.id.nav_smart_cooking) {
                newTabTag = TAG_SMART_COOKING_FRAGMENT;
                Toast.makeText(MainActivity.this, "Nấu ăn thông minh", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_shopping) {
                newTabTag = TAG_SHOPPING_FRAGMENT;
                Toast.makeText(MainActivity.this, "Mua sắm", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_account) {
                newTabTag = TAG_ACCOUNT_FRAGMENT;
                Toast.makeText(MainActivity.this, "Tài khoản", Toast.LENGTH_SHORT).show();
            }

            // Nếu tab mới khác tab hiện tại, chuyển tab
            if (!newTabTag.equals(currentTabTag)) {
                // Lưu tab hiện tại
                String oldTabTag = currentTabTag;
                currentTabTag = newTabTag;

                // Xóa back stack để tránh các vấn đề về điều hướng
                clearBackStack();

                // Kiểm tra trạng thái của tab mới
                TabState newState = tabStates.get(newTabTag);
                if (newState != null && newState.isDetailShowing && newState.currentRecipe != null) {
                    // Nếu tab mới đang hiển thị chi tiết, hiển thị lại nó
                    showDetailFragment(newState.currentRecipe, false);
                } else {
                    // Nếu không, hiển thị tab mới
                    switchToTab(newTabTag, false);
                }
            }

            return true;
        });
    }

    private void initFragments() {
        // Khởi tạo các fragment
        fragmentMap.put(TAG_HOME_FRAGMENT, new HomeFragment());
        fragmentMap.put(TAG_SEARCH_FRAGMENT, new RecipeSearchFragment());
        fragmentMap.put(TAG_SMART_COOKING_FRAGMENT, new Fragment()); // Thay thế bằng fragment thực tế
        fragmentMap.put(TAG_SHOPPING_FRAGMENT, new Fragment()); // Thay thế bằng fragment thực tế
        fragmentMap.put(TAG_ACCOUNT_FRAGMENT, new Fragment()); // Thay thế bằng fragment thực tế
    }

    private void restoreFragments() {
        // Khôi phục các fragment từ FragmentManager
        FragmentManager fm = getSupportFragmentManager();

        Fragment homeFragment = fm.findFragmentByTag(TAG_HOME_FRAGMENT);
        if (homeFragment != null) {
            fragmentMap.put(TAG_HOME_FRAGMENT, homeFragment);
        } else {
            fragmentMap.put(TAG_HOME_FRAGMENT, new HomeFragment());
        }

        Fragment searchFragment = fm.findFragmentByTag(TAG_SEARCH_FRAGMENT);
        if (searchFragment != null) {
            fragmentMap.put(TAG_SEARCH_FRAGMENT, searchFragment);
        } else {
            fragmentMap.put(TAG_SEARCH_FRAGMENT, new RecipeSearchFragment());
        }

        // Khôi phục các fragment khác tương tự
        // Đối với các fragment chưa được triển khai, sử dụng fragment trống
        fragmentMap.put(TAG_SMART_COOKING_FRAGMENT, fm.findFragmentByTag(TAG_SMART_COOKING_FRAGMENT) != null ?
                fm.findFragmentByTag(TAG_SMART_COOKING_FRAGMENT) : new Fragment());
        fragmentMap.put(TAG_SHOPPING_FRAGMENT, fm.findFragmentByTag(TAG_SHOPPING_FRAGMENT) != null ?
                fm.findFragmentByTag(TAG_SHOPPING_FRAGMENT) : new Fragment());
        fragmentMap.put(TAG_ACCOUNT_FRAGMENT, fm.findFragmentByTag(TAG_ACCOUNT_FRAGMENT) != null ?
                fm.findFragmentByTag(TAG_ACCOUNT_FRAGMENT) : new Fragment());
    }

    private void switchToTab(String tabTag, boolean addToBackStack) {
        // Lấy fragment tương ứng với tab
        Fragment fragment = fragmentMap.get(tabTag);
        if (fragment == null) {
            // Nếu fragment chưa được tạo, tạo mới
            if (tabTag.equals(TAG_HOME_FRAGMENT)) {
                fragment = new HomeFragment();
            } else if (tabTag.equals(TAG_SEARCH_FRAGMENT)) {
                fragment = new RecipeSearchFragment();
            } else {
                fragment = new Fragment(); // Fragment trống cho các tab chưa triển khai
            }
            fragmentMap.put(tabTag, fragment);
        }

        // Hiển thị fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, tabTag);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
    }

    private void updateSelectedNavigationItem(String tabTag) {
        // Cập nhật item được chọn trong bottom navigation
        if (tabTag.equals(TAG_HOME_FRAGMENT)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (tabTag.equals(TAG_SEARCH_FRAGMENT)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);
        } else if (tabTag.equals(TAG_SMART_COOKING_FRAGMENT)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_smart_cooking);
        } else if (tabTag.equals(TAG_SHOPPING_FRAGMENT)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_shopping);
        } else if (tabTag.equals(TAG_ACCOUNT_FRAGMENT)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_account);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi activity được resume, khôi phục trạng thái nếu cần
        // Điều này đảm bảo khi quay lại từ một activity khác, trạng thái sẽ được giữ nguyên
    }
}
