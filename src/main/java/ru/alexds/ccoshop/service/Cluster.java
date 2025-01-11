package ru.alexds.ccoshop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    private List<double[]> vectors = new ArrayList<>();
    private Map<Long, double[]> users = new HashMap<>();

    public Cluster(Long userId, double[] vector) {
        vectors.add(vector);
        users.put(userId, vector);
    }

    public boolean match(double[] userVector, double threshold) {
        for (double[] vector : vectors) {
            double similarity = cosineSimilarity(vector, userVector);
            if (similarity >= threshold) return true;
        }
        return false;
    }

    public void addUser(Long userId, double[] userVector) {
        vectors.add(userVector);
        users.put(userId, userVector);
    }

    public Map<Long, double[]> getUsers() {
        return users;
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public List<Long> getTopRecommendedProducts(Long userId, int numberOfRecommendations) {
        return null;
    }
}