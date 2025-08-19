package com.gaa.player.dto;

import com.gaa.player.model.Player;

public class PlayerDTO {
    private int id;
    private String name;
    private int age;
    private float height;
    private float weight;
    private String position;
    private String county;
    private String club;
    private int goalsScored;
    private int pointsScored;
    private boolean isActive;

    public PlayerDTO() {}

    public PlayerDTO(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.age = player.getAge();
        this.height = player.getHeight();
        this.weight = player.getWeight();
        this.position = player.getPosition();
        this.county = player.getCounty();
        this.club = player.getClub();
        this.goalsScored = player.getGoalsScored();
        this.pointsScored = player.getPointsScored();
        this.isActive = player.isActive();
    }

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }
    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }
    public int getGoalsScored() { return goalsScored; }
    public void setGoalsScored(int goalsScored) { this.goalsScored = goalsScored; }
    public int getPointsScored() { return pointsScored; }
    public void setPointsScored(int pointsScored) { this.pointsScored = pointsScored; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", position='" + position + '\'' +
                ", county='" + county + '\'' +
                ", club='" + club + '\'' +
                ", goalsScored=" + goalsScored +
                ", pointsScored=" + pointsScored +
                ", isActive=" + isActive +
                '}';
    }
}