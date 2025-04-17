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

// Adapter để hiển thị danh sách các bước hướng dẫn trong RecyclerView.
public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {

    private Context context;
    private List<String> steps;

    // Constructor để khởi tạo adapter với context.
    public InstructionsAdapter(Context context) {
        this.context = context;
        this.steps = new ArrayList<>();
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item bước hướng dẫn.
        View view = LayoutInflater.from(context).inflate(R.layout.item_instruction_step, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        // Gắn dữ liệu vào view holder cho vị trí được chỉ định.
        String step = steps.get(position);

        // Đặt số bước (position + 1 vì bắt đầu từ 0).
        holder.tvStepNumber.setText(String.valueOf(position + 1));

        // Đặt nội dung hướng dẫn.
        holder.tvStepInstruction.setText(step);
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số bước.
        return steps.size();
    }

    // Cập nhật dữ liệu trong adapter và làm mới giao diện.
    public void updateData(List<String> newSteps) {
        steps.clear();

        if (newSteps != null) {
            steps.addAll(newSteps);
        }

        notifyDataSetChanged();
    }

    // ViewHolder để giữ các view hiển thị bước hướng dẫn.
    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvStepInstruction;

        InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo các TextView cho số bước và nội dung hướng dẫn.
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepInstruction = itemView.findViewById(R.id.tv_step_instruction);
        }
    }
}
