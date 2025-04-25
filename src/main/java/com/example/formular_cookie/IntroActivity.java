package com.example.formular_cookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.formular_cookie.adapter.OnBoardingAdapter;
import com.example.formular_cookie.model.OnBoardingItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private Button buttonStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_intro);
        viewPager = findViewById(R.id.onboardingViewPager);
        buttonStart = findViewById(R.id.buttonStart);
        WormDotsIndicator tabLayout = findViewById(R.id.dots_indicator);

        List<OnBoardingItem> items = new ArrayList<>();
        items.add(new OnBoardingItem(R.drawable.intro1, "Giao lưu cộng đồng", "Ở CookMate, bạn có thể kết nối với hàng ngàn tín đồ ẩm thực, chia sẻ công thức yêu thích và khám phá món ăn mới mỗi ngày. Cùng nhau tạo nên những bữa ăn ngon!"));
        items.add(new OnBoardingItem(R.drawable.intro2, "Vào bếp thật đơn giản", "Bạn có thể tìm kiếm hàng ngàn công thức với hướng dẫn chi tiết, từ bữa ăn hàng ngày đến món ăn đặc biệt. Dễ dàng nấu ăn, thỏa sức sáng tạo!"));
        items.add(new OnBoardingItem(R.drawable.intro3, "Lưu giữ & chia sẻ", " bạn có thể ghi lại công thức yêu thích, chia sẻ bí quyết nấu ăn và cùng nhau tạo nên những khoảnh khắc tuyệt vời bên mâm cơm gia đình."));
        viewPager.setAdapter(new OnBoardingAdapter(this,items));
        tabLayout.setViewPager2(viewPager);

        buttonStart.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
            editor.putBoolean("first_time", false);
            editor.apply();
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });
    }
}