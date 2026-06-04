package com.example.peakfita;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryWorkoutAdapter extends RecyclerView.Adapter<HistoryWorkoutAdapter.ViewHolder> {

    private List<Workout> historyList;

    public HistoryWorkoutAdapter(List<Workout> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_workout, parent, false);
        return new ViewHolder(view);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvExercisesCount, tvExercisesDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvExercisesCount = itemView.findViewById(R.id.tvHistoryExercisesCount);

            tvExercisesDetails = itemView.findViewById(R.id.tvHistoryExercisesDetails);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = historyList.get(position);
        holder.tvTitle.setText(workout.getTitle());
        holder.tvDate.setText("Completed on: " + workout.getDate());
        int count = (workout.getExercises() != null) ? workout.getExercises().size() : 0;
        holder.tvExercisesCount.setText(count + " Exercises Completed ✓");

        
        if (workout.getExercises() != null && count > 0) {
            StringBuilder detailsBuilder = new StringBuilder();
            for (Exercise ex : workout.getExercises()) {
                detailsBuilder.append("• ").append(ex.getName())
                        .append(" - ").append(ex.getSets()).append(" sets x ")
                        .append(ex.getReps()).append(" reps (")
                        .append(ex.getWeight()).append(" kg)\n");
            }
            holder.tvExercisesDetails.setText(detailsBuilder.toString().trim());
        } else {
            holder.tvExercisesDetails.setText("No exercise details available.");
        }

        
        holder.itemView.setOnClickListener(v -> { // opening list of exercises
            if (holder.tvExercisesDetails.getVisibility() == View.GONE) {
                holder.tvExercisesDetails.setVisibility(View.VISIBLE); 
            } else {
                holder.tvExercisesDetails.setVisibility(View.GONE); 
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }


}