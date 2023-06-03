package com.example.buildbudget;

// PlannedPaymentAdapter.java

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlannedPaymentAdapter extends RecyclerView.Adapter<PlannedPaymentAdapter.PlannedPaymentViewHolder> {

    private List<PlannedPayment> plannedPaymentsList;

    public PlannedPaymentAdapter(List<PlannedPayment> plannedPaymentsList) {
        this.plannedPaymentsList = plannedPaymentsList;
    }

    @NonNull
    @Override
    public PlannedPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new PlannedPaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedPaymentViewHolder holder, int position) {
        PlannedPayment plannedPayment = plannedPaymentsList.get(position);

        holder.textViewPaymentName.setText(plannedPayment.getName());
        holder.textViewPaymentAmount.setText(String.valueOf(plannedPayment.getAmount()));
        holder.textViewPaymentDate.setText(plannedPayment.getDate());

        Log.d("Planned Adapater","Text set done");

    }

    @Override
    public int getItemCount() {
        return plannedPaymentsList.size();
    }

    public static class PlannedPaymentViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPaymentName;
        public TextView textViewPaymentAmount;
        public TextView textViewPaymentDate;

        public PlannedPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPaymentName = itemView.findViewById(R.id.textViewPaymentName);
            textViewPaymentAmount = itemView.findViewById(R.id.textViewPaymentAmount);
            textViewPaymentDate = itemView.findViewById(R.id.textViewPaymentDate);
        }
    }
}
