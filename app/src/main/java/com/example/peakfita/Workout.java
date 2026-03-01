package com.example.peakfita;

import java.util.HashMap;
import java.util.Map;

public class Workout {
    private String workoutId;
    private String title;
    private String description;
    private String date;
    private int dayOfWeek;
    private String scheduledTime;
    private Map<String, String> exercises;

    public Workout() {
        this.exercises = new HashMap<>();
    }

    public Workout(String workoutId, String title, String description, int dayOfWeek,String date) {
        this.workoutId = workoutId;
        this.title = title;
        this.description = description;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.exercises = new HashMap<>();
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getWorkoutId() { return workoutId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public String getScheduledTime() { return scheduledTime; }
    public Map<String, String> getExercises() { return exercises; }


    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setDate(String date) { this.date = date; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public void setExercises(Map<String, String> exercises) { this.exercises = exercises; }
}