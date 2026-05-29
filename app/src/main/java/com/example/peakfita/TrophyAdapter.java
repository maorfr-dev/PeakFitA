package com.example.peakfita;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrophyAdapter extends RecyclerView.Adapter<TrophyAdapter.ViewHolder> {

    private List<TrophyRecord> trophyList;

    public TrophyAdapter(List<TrophyRecord> trophyList) {

        this.trophyList = trophyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trophy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrophyRecord trophy = trophyList.get(position);

        
        String name = trophy.getExerciseName().substring(0, 1).toUpperCase() + trophy.getExerciseName().substring(1);
        holder.tvName.setText(name);

        
        holder.tvWeight.setText(trophy.getMaxWeight() + " kg");
    }

    @Override
    public int getItemCount() {
        return trophyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTrophyExerciseName);
            tvWeight = itemView.findViewById(R.id.tvTrophyWeight);
        }
    }

    
    public static class TrophyRecord {
        private String exerciseName;
        private double maxWeight;

        public TrophyRecord(String exerciseName, double maxWeight) {
            this.exerciseName = exerciseName;
            this.maxWeight = maxWeight;
        }

        public String getExerciseName() { return exerciseName; }
        public double getMaxWeight() { return maxWeight; }
    }
}