package com.example.formular_cookie.route;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.formular_cookie.Account;
import com.example.formular_cookie.HomeFragment;
import com.example.formular_cookie.PostRecipeFragment;
import com.example.formular_cookie.R;
import com.example.formular_cookie.fragment.RecipeDetailFragment;
import com.example.formular_cookie.fragment.RecipeSearchFragment;
import com.example.formular_cookie.model.Recipe;

import java.util.HashMap;
import java.util.Map;

// Lớp quản lý điều hướng và trạng thái giữa các màn hình trong ứng dụng.
public class NavigationManager {

    private final FragmentManager fragmentManager;
    private final Map<String, Fragment> fragmentMap = new HashMap<>();
    private final Map<String, TabState> tabStates = new HashMap<>();
    private String currentTabTag;

    // Các tag để quản lý fragment
    public static final String TAG_HOME_FRAGMENT = "home_fragment";
    public static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";
    public static final String TAG_POST_RESCIPE_FRAGMENT = "postRescipe_fragment";


    public static final String TAG_ACCOUNT = "account_fragment";
    public NavigationManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        initTabStates();
    }

    // Lớp để lưu trạng thái của mỗi tab
    private static class TabState {
        boolean isDetailShowing = false;
        Recipe currentRecipe = null;

        TabState() {
        }

        TabState(boolean isDetailShowing, Recipe currentRecipe) {
            this.isDetailShowing = isDetailShowing;
            this.currentRecipe = currentRecipe;
        }
    }

    // Khởi tạo trạng thái cho mỗi tab
    private void initTabStates() {
        tabStates.put(TAG_HOME_FRAGMENT, new TabState());
        tabStates.put(TAG_SEARCH_FRAGMENT, new TabState());
        tabStates.put(TAG_POST_RESCIPE_FRAGMENT, new TabState());
        tabStates.put(TAG_ACCOUNT, new TabState());

    }

    // Chuyển đến tab mới
    public void switchToTab(String tabTag, boolean addToBackStack) {
        Fragment fragment = fragmentMap.get(tabTag);
        if (fragment == null) {
            if (tabTag.equals(TAG_HOME_FRAGMENT)) {
                fragment = new HomeFragment();
            } else if (tabTag.equals(TAG_SEARCH_FRAGMENT)) {
                fragment = new RecipeSearchFragment();
            } else if (tabTag.equals(TAG_POST_RESCIPE_FRAGMENT)) {
                fragment = new PostRecipeFragment();
            }else if (tabTag.equals(TAG_ACCOUNT)) {
                fragment = new Account();
            }
            fragmentMap.put(tabTag, fragment);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, tabTag);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
        currentTabTag = tabTag;
    }

    // Hiển thị fragment chi tiết
    public void showDetailFragment(Recipe recipe, boolean addToBackStack) {
        Fragment detailFragment = RecipeDetailFragment.newInstance(recipe);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, detailFragment, TAG_DETAIL_FRAGMENT);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        TabState state = tabStates.get(currentTabTag);
        if (state != null) {
            state.isDetailShowing = true;
            state.currentRecipe = recipe;
        }
    }

    // Xóa back stack
    public void clearBackStack() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }

    // Khôi phục trạng thái tab
    public void restoreTabState(String tabTag, boolean isDetailShowing, Recipe currentRecipe) {
        TabState state = tabStates.get(tabTag);
        if (state != null) {
            state.isDetailShowing = isDetailShowing;
            state.currentRecipe = currentRecipe;
        }
    }

    // Lấy trạng thái tab hiện tại
    public TabState getCurrentTabState() {
        return tabStates.get(currentTabTag);
    }

    public String getCurrentTabTag() {
        return currentTabTag;
    }
}