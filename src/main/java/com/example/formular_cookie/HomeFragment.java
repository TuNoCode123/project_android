package com.example.formular_cookie;// HomeFragment.java


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.formular_cookie.R;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private ImageButton btnTheme;
    private ImageButton btnNotification;
    private boolean isDarkMode = false;
    private Chip chipNewRecipes;
    private Chip chipPopularDishes;
    private Chip chipCookingVideos;
    private Chip chipIngredientSuggestions;

    private ImageView image1, image2, image3;

    VideoView videoView;
    ImageView playButton,videoThumbnail;



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

        image1 = view.findViewById(R.id.image1);
        image2 = view.findViewById(R.id.image2);
        image3 = view.findViewById(R.id.image3);


        Glide.with(getContext())
                .load(R.drawable.home_image_1)
                .into(image1);
        Glide.with(getContext()).load(R.drawable.home_image_2).into(image2);
        Glide.with(getContext()).load(R.drawable.home_image_3).into(image3);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<FoodItem> foods = new ArrayList<>();

        FoodItem item1 = new FoodItem();
        item1.title = "Món Bò Chiên – Hương Vị Đậm Đà, Giòn Rụm";
        item1.description = "Bò chiên là một món ăn hấp dẫn với lớp vỏ vàng giòn, thịt bò bên trong mềm, thấm đẫm gia vị, tạo nên hương vị khó cưỡng. Đây là một món ăn phổ biến trong nhiều nền ẩm thực....";
        item1.imageResId = R.drawable.home_image_1;
        item1.userName = "An clock";
        item1.userFollowers = "12.3K Followers";

        foods.add(item1);

        FoodItem item2 = new FoodItem();
        item2.title = "Món Bánh Xèo – Giòn Rụm, Thơm Ngon Khó Cưỡng";
        item2.description = "Bánh xèo là một món ăn dân dã nhưng lại có sức hút đặc biệt trong ẩm thực Việt Nam. Lớp vỏ giòn rụm, nhân thơm lừng, kết hợp với rau sống và nước chấm ...";
        item2.imageResId = R.drawable.home_image_1;
        item2.userName = "Zero";
        item2.userFollowers = "3K Followers";

        foods.add(item2);

        FoodAdapter adapter = new FoodAdapter(getContext(), foods);
        recyclerView.setAdapter(adapter);



         videoView = view.findViewById(R.id.videoView);
         playButton = view.findViewById(R.id.playButton);
         videoThumbnail = view.findViewById(R.id.videoThumbnail);

        String videoPath = "android.resource://" + getContext().getPackageName()+ "/" + R.raw.video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        playButton.setOnClickListener(v -> {
            videoThumbnail.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();
        });


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