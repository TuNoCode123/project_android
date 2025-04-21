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


import java.util.ArrayList;
import java.util.List;

public class Likefrag extends Fragment {

    private List<String> likedRecipeIds;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private List<ShareItem> items;
    private Shareadapter likeadapter;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.likefrag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.likeitemlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        items = new ArrayList<>();
        likeadapter = new Shareadapter(getContext(), items);
        recyclerView.setAdapter(likeadapter);
        emptyView = view.findViewById(R.id.emptyView);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String name = document.getString("name");
                            Log.d(TAG, "Name"+name);
                            likedRecipeIds = (List<String>) document.get("likedRecipies");
                            if (likedRecipeIds != null) {
                                for (String recipeId: likedRecipeIds){
                                    Log.d(TAG, "RECPId "+recipeId);
                                    DocumentReference docRef = db.collection("recipes").document(recipeId);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                    String name = document.getString("title");
                                                    String imageUrl = document.getString("imageUrl");
                                                    items.add(new ShareItem(name, imageUrl));
                                                    Log.d(TAG, userId);
                                                    Log.d(TAG, "Added recipe: " + name + ", Image URL: " + imageUrl);
                                                    likeadapter.notifyDataSetChanged();
                                                    Log.d(TAG, "items count: " + items.size());
                                                    if (items.isEmpty()) {
                                                        recyclerView.setVisibility(View.GONE);
                                                        emptyView.setVisibility(View.VISIBLE);
                                                    }
                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }
                            }
                            else {
                                Log.d(TAG, "No liked recipes");
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

}
