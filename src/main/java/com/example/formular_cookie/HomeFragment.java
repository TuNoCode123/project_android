package com.example.formular_cookie;// HomeFragment.java


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.formular_cookie.R;
import com.google.android.material.chip.Chip;

public class HomeFragment extends Fragment {


    private ImageButton btnTheme;
    private ImageButton btnNotification;
    private boolean isDarkMode = false;
    private Chip chipNewRecipes;
    private Chip chipPopularDishes;
    private Chip chipCookingVideos;
    private Chip chipIngredientSuggestions;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnTheme = view.findViewById(R.id.btnTheme);
        btnNotification = view.findViewById(R.id.btnNotification);

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show();
            }
        });





        chipNewRecipes = view.findViewById(R.id.chipNewRecipes);
        chipPopularDishes = view.findViewById(R.id.chipPopularDishes);
        chipCookingVideos = view.findViewById(R.id.chipCookingVideos);
        chipIngredientSuggestions = view.findViewById(R.id.chipIngredientSuggestions);
        setupChipListeners();
        return view;
    }
    private void setupChipListeners() {
        View.OnClickListener chipClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset all chips
                chipNewRecipes.setChecked(false);
                chipPopularDishes.setChecked(false);
                chipCookingVideos.setChecked(false);
                chipIngredientSuggestions.setChecked(false);

                // Set the clicked chip as checked
                ((Chip) v).setChecked(true);

                // Show content based on selected chip
                String category = ((Chip) v).getText().toString();
                Toast.makeText(getContext(), "Selected: " + category, Toast.LENGTH_SHORT).show();

                // Here you would load the appropriate content based on the selection
                loadCategoryContent(v.getId());
            }
        };

        chipNewRecipes.setOnClickListener(chipClickListener);
        chipPopularDishes.setOnClickListener(chipClickListener);
        chipCookingVideos.setOnClickListener(chipClickListener);
        chipIngredientSuggestions.setOnClickListener(chipClickListener);
    }

    private void loadCategoryContent(int chipId) {
        String message = "";

        if (chipId == R.id.chipNewRecipes) {
            message = "Loading new recipes...";
        } else if (chipId == R.id.chipPopularDishes) {
            message = "Loading popular dishes...";
        } else if (chipId == R.id.chipCookingVideos) {
            message = "Loading cooking videos...";
        } else if (chipId == R.id.chipIngredientSuggestions) {
            message = "Loading ingredient suggestions...";
        } else {
            message = "Unknown category";
        }

//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}