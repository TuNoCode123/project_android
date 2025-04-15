package com.example.formular_cookie.route;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.formular_cookie.MainActivity;
import com.example.formular_cookie.R;
import com.example.formular_cookie.fragment.RecipeDetailFragment;
import com.example.formular_cookie.fragment.RecipeSearchFragment;
import com.example.formular_cookie.model.Recipe;

/**
 * Lớp quản lý điều hướng giữa các màn hình trong ứng dụng
 */

public class NavigationManager {

    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    private final MainActivity activity;
    private final FragmentManager fragmentManager;

    public NavigationManager(MainActivity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    /**
     * Điều hướng đến màn hình tìm kiếm
     */
    public void navigateToSearch() {
        RecipeSearchFragment searchFragment = (RecipeSearchFragment) fragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT);

        if (searchFragment == null) {
            searchFragment = new RecipeSearchFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, searchFragment, TAG_SEARCH_FRAGMENT)
                    .commit();
        } else {
            // Hiển thị fragment đã tồn tại
            fragmentManager.beginTransaction()
                    .show(searchFragment)
                    .commit();

            // Khôi phục trạng thái tìm kiếm
            searchFragment.restoreSearchState();
        }
    }

    /**
     * Điều hướng đến màn hình chi tiết công thức
     */
    public void navigateToDetail(Recipe recipe) {
        // Tạo fragment chi tiết mới
        RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(recipe);

        // Thực hiện transaction
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment, TAG_DETAIL_FRAGMENT)
                .addToBackStack(TAG_DETAIL_FRAGMENT)
                .commit();
    }

    /**
     * Khôi phục trạng thái điều hướng
     */

    public void restoreNavigationState(String currentFragmentTag, Recipe currentRecipe) {
        if (TAG_DETAIL_FRAGMENT.equals(currentFragmentTag) && currentRecipe != null) {
            navigateToDetail(currentRecipe);
        } else {
            navigateToSearch();
        }
    }
}

