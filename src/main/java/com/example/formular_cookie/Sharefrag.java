package com.example.formular_cookie;

import static android.content.ContentValues.TAG;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sharefrag extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private List<ShareItem> items;
    private Shareadapter adapter;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.itemlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        items = new ArrayList<>();
        adapter = new Shareadapter(getContext(), items);
        emptyView = view.findViewById(R.id.emptyView);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            db.collection("recipes")
                    .whereEqualTo("authorID", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            items.clear(); // Clear existing items before adding new ones
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get all the necessary fields from the document
                                String name = document.getString("title");
                                String imageUrl = document.getString("imageUrl");

                                // Create new ShareItem with the actual data from Firestore
                                items.add(new ShareItem(name, imageUrl));
                                Log.d(TAG, userId);
                                Log.d(TAG, "Added recipe: " + name + ", Image URL: " + imageUrl);
                            }
                            adapter.notifyDataSetChanged();
                            if (items.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share, container, false);
    }
}