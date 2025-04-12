package com.example.formular_cookie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {

    private List<Recipe> recipeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);


        LinearLayout recipeContainer = view.findViewById(R.id.recipe_list_container);

        Bundle args = getArguments();
        if(args != null){
            for (Recipe recipe : recipeList = (List<Recipe>) args.getSerializable("recipeList")) {
                View recipeItem = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_recipe_card, recipeContainer, false);

                TextView title = recipeItem.findViewById(R.id.tv_recipe_title);
                Button btn = recipeItem.findViewById(R.id.btn_view_detail);

                title.setText(recipe.getTitle());

                btn.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("recipe", recipe);
                    bundle.putBoolean("isEditMode", true);
                    bundle.putSerializable("recipeList", (Serializable) recipeList);
                    RecipeDetailFragment detailFragment = new RecipeDetailFragment();
                    detailFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.admin_fragment_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                });
                recipeContainer.addView(recipeItem);
            }

        }
        return view;
    }
}

