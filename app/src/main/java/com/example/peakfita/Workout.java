package com.example.peakfita;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private String workoutId;
    private String title;
    private String description;
    private int dayOfWeek;
    private String date;
    private String scheduledTime;

    
    private List<Exercise> exercises;

    public Workout() {
        this.exercises = new ArrayList<>();
    }

    public Workout(String workoutId, String title, String description, int dayOfWeek, String date) {
        this.workoutId = workoutId;
        this.title = title;
        this.description = description;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.exercises = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getWorkoutId() { return workoutId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public String getScheduledTime() { return scheduledTime; }

    
    public List<Exercise> getExercises() { return exercises; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setDate(String date) { this.date = date; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }
}