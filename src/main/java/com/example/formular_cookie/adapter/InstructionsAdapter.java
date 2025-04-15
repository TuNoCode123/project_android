package com.example.formular_cookie.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formular_cookie.R;

import java.util.ArrayList;
import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {

    private Context context;
    private List<String> steps;

    public InstructionsAdapter(Context context) {
        this.context = context;
        this.steps = new ArrayList<>();
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instruction_step, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        String step = steps.get(position);

        // Set step number (position + 1 vì bắt đầu từ 0)
        holder.tvStepNumber.setText(String.valueOf(position + 1));

        // Set step instruction
        holder.tvStepInstruction.setText(step);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void updateData(List<String> newSteps) {
        steps.clear();

        if (newSteps != null) {
            steps.addAll(newSteps);
        }

        notifyDataSetChanged();
    }

    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvStepInstruction;

        InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepInstruction = itemView.findViewById(R.id.tv_step_instruction);
        }
    }
}
