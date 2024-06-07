package org.example.communication;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Subscription {
    private String name;
    private int age;
    private double score;


    public Subscription() {
    }

    public Subscription(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", score=" + score +
                '}';
    }

    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Subscription object to JSON string", e);
        }
    }

    public static Subscription fromString(String data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(data, Subscription.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse string to Subscription object", e);
        }
    }
}