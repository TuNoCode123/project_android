package com.example.formular_cookie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApproveRecipesFragment extends Fragment {

    private List<Recipe> recipeList;
    private RecipeAdapter adapter;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList, this::openDetail);
        recyclerView.setAdapter(adapter);
        databaseRef = FirebaseDatabase.getInstance().getReference("recipes");
        loadRecipesFromFirebase();

        return view;
    }

    private void loadRecipesFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setId(recipeSnapshot.getKey()); // nếu cần ID
                        recipeList.add(recipe);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải công thức: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
