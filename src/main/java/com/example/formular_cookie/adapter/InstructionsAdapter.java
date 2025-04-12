package com.example.formular_cookie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formular_cookie.R;
import com.example.formular_cookie.model.Instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {

    private final Context context;
    private final List<Instruction> instructions;
    private final List<InstructionStep> flattenedSteps;

    public InstructionsAdapter(Context context) {
        this.context = context;
        this.instructions = new ArrayList<>();
        this.flattenedSteps = new ArrayList<>();
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instruction_step, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        InstructionStep step = flattenedSteps.get(position);

        // Đặt số thứ tự bước
        holder.tvStepNumber.setText(String.valueOf(step.number));

        // Đặt hướng dẫn chi tiết
        holder.tvStepInstruction.setText(step.instruction);
    }

    @Override
    public int getItemCount() {
        return flattenedSteps.size();
    }

    public void updateData(List<Instruction> newInstructions) {
        instructions.clear();
        flattenedSteps.clear();

        if (newInstructions != null) {
            instructions.addAll(newInstructions);

            // Chuyển đổi cấu trúc instructions thành danh sách các bước
            for (Instruction instruction : instructions) {
                if (instruction.getSteps() != null) {
                    for (Instruction.Step step : instruction.getSteps()) {
                        flattenedSteps.add(new InstructionStep(step.getNumber(), step.getStep()));
                    }
                }
            }
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

    // Lớp hỗ trợ chuyển đổi cấu trúc instruction
    private static class InstructionStep {
        int number;
        String instruction;

        InstructionStep(int number, String instruction) {
            this.number = number;
            this.instruction = instruction;
        }
    }
}
