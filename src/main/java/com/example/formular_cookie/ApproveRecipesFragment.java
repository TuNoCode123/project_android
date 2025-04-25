package com.example.formular_cookie;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ApproveRecipesFragment extends Fragment {

    private List<Recipe> recipeList;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private TextView tvEmptyList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recyclerView = view.findViewById(R.id.rv_recipe_list);
        tvEmptyList = view.findViewById(R.id.tv_empty_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        loadRecipesFromFirebase();
        adapter = new RecipeAdapter(recipeList, this::openDetail);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadRecipesFromFirebase() {
        db.collection("recipes")
                .whereEqualTo("status", false) // lọc theo status = false
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear(); // clear danh sách cũ trước khi thêm mới
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);

                        List<Ingredient> ingredients = recipe.getIngredients();
                        if (ingredients != null) {
                            for (Ingredient ingredient : ingredients) {
                                Log.d("RECIPE", "Ingredient - Name: " + ingredient.getName() +
                                        ", Amount: " + ingredient.getAmount() +
                                        ", Unit: " + ingredient.getUnit());
                            }
                        }

                        Log.d("RECIPE", "Thêm: " + recipe.getTitle());
                    }

                    adapter.notifyDataSetChanged();

                    // Kiểm tra danh sách rỗng
                    if (recipeList.isEmpty()) {
                        tvEmptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyList.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("RECIPE", "Lỗi khi đọc dữ liệu", e);
                });
    }

    private void openDetail(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        bundle.putBoolean("isApproveMode", true);

        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}