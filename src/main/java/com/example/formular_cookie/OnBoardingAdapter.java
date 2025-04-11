package com.example.formular_cookie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {
    private final Context context;
    private final List<OnBoardingItem> items;

    public OnBoardingAdapter(Context context1,List<OnBoardingItem> items) {
        this.context = context1;
        this.items = items;
    }


    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnBoardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_intro, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OnBoardingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView titleView, descriptionView;

        OnBoardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            titleView = itemView.findViewById(R.id.textTitle);
            descriptionView = itemView.findViewById(R.id.textDescription);
        }

        void bind(OnBoardingItem item) {
            imageView.setImageResource(item.getImageResId());
            titleView.setText(item.getTitle());
            descriptionView.setText(item.getDescription());
        }
    }
}
