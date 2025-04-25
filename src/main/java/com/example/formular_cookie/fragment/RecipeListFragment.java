package com.example.formular_cookie.fragment;

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

import com.example.formular_cookie.model.AdminIngredient;
import com.example.formular_cookie.R;
import com.example.formular_cookie.model.AdminRecipe;
import com.example.formular_cookie.adapter.AdminRecipeAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {

    private List<AdminRecipe> adminRecipeList;
    private RecyclerView recyclerView;
    private AdminRecipeAdapter adapter;
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

        adminRecipeList = new ArrayList<>();
        adapter = new AdminRecipeAdapter(adminRecipeList, this::openDetail);
        recyclerView.setAdapter(adapter);
        loadRecipesFromFirebase();

        return view;
    }
    private void loadRecipesFromFirebase() {
        db.collection("recipes")
                .whereEqualTo("status", true) // lọc theo status = true
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    adminRecipeList.clear(); // clear danh sách cũ trước khi thêm mới
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AdminRecipe adminRecipe = doc.toObject(AdminRecipe.class);
                        adminRecipe.setId(doc.getId());
                        adminRecipeList.add(adminRecipe);

                        List<AdminIngredient> adminIngredients = adminRecipe.getIngredients();
                        if (adminIngredients != null) {
                            for (AdminIngredient adminIngredient : adminIngredients) {
                                Log.d("RECIPE", "Ingredient - Name: " + adminIngredient.getName() +
                                        ", Amount: " + adminIngredient.getAmount() +
                                        ", Unit: " + adminIngredient.getUnit());
                            }
                        }

                        Log.d("RECIPE", "Thêm: " + adminRecipe.getTitle());
                    }

                    adapter.notifyDataSetChanged();

                    // Kiểm tra danh sách rỗng
                    if (adminRecipeList.isEmpty()) {
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



    private void openDetail(AdminRecipe adminRecipe) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", adminRecipe);
        bundle.putBoolean("isEditMode", true);

        AdminRecipeDetailFragment detailFragment = new AdminRecipeDetailFragment();
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
