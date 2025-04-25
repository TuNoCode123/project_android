package com.example.formular_cookie.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formular_cookie.R;
import com.example.formular_cookie.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserDetailFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private LinearLayout layoutFavorites;
    private LinearLayout layoutPostedRecipes;

    private ImageView btnBack;
    private Button btnDelete;
    private FirebaseFirestore db;


    private User user; // Assume this is passed in via arguments or loaded

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        layoutFavorites = view.findViewById(R.id.layout_favorites);
        layoutPostedRecipes = view.findViewById(R.id.layout_posted_recipes);
        btnDelete = view.findViewById(R.id.btn_delete_user);
        btnBack = view.findViewById(R.id.btn_back);
        db = FirebaseFirestore.getInstance();


        if (getArguments() != null && getArguments().containsKey("user")) {
            user = (User) getArguments().getSerializable("user");
            assert user != null;
            tvUsername.setText(user.getName());
            tvEmail.setText(user.getEmail());
        }

        if (user != null ) {
            loadFavoriteRecipeTitles(user.getLikedRecipies());
            loadPostedRecipeTitles(user.getId());
        }


        btnDelete.setOnClickListener(v -> {
            db.collection("users").document(user.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void loadFavoriteRecipeTitles(List<String> recipeIds) {
        layoutFavorites.removeAllViews(); // Clear nếu có dữ liệu cũ

        if (recipeIds == null || recipeIds.isEmpty()) {
            TextView textView = new TextView(getContext());
            textView.setText(getString(R.string.no_favorites));
            textView.setTextSize(16);
            textView.setPadding(8, 8, 8, 8);
            layoutFavorites.addView(textView);
            return;
        };

        for (String recipeId : recipeIds) {
            db.collection("recipes").document(recipeId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String title = documentSnapshot.getString("title");
                            TextView textView = new TextView(requireContext());
                            textView.setText("- " + title);
                            textView.setTextSize(16);
                            textView.setPadding(8, 8, 8, 8);
                            layoutFavorites.addView(textView);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FAVORITE", "Lỗi tải công thức " + recipeId, e);
                    });
        }
    }

    private void loadPostedRecipeTitles(String userId) {
        layoutPostedRecipes.removeAllViews(); // Xoá nếu có dữ liệu cũ

        db.collection("recipes")
                .whereEqualTo("authorID", userId) // Lọc theo UID người đăng
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        TextView textView = new TextView(getContext());
                        textView.setText(getString(R.string.no_posted_recipes));
                        textView.setTextSize(16);
                        textView.setPadding(8, 8, 8, 8);
                        layoutPostedRecipes.addView(textView);
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot) {
                        String title = doc.getString("title");
                        TextView textView = new TextView(getContext());
                        textView.setText("- " + title);
                        textView.setTextSize(16);
                        textView.setPadding(8, 8, 8, 8);
                        layoutPostedRecipes.addView(textView);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("POSTED_RECIPES", "Lỗi tải công thức đã đăng", e);
                });
    }

}