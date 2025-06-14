package com.example.electricitybillestimator; // Replace with your package name

public class BillEntry {
    private String id; // Unique ID for Firebase
    private String month;
    private double unitsUsed;
    private double totalCharges;
    private double rebatePercentage;
    private double finalCost;

    public BillEntry() {
        // Default constructor required for calls to DataSnapshot.getValue(BillEntry.class)
    }

    public BillEntry(String id, String month, double unitsUsed, double totalCharges, double rebatePercentage, double finalCost) {
        this.id = id;
        this.month = month;
        this.unitsUsed = unitsUsed;
        this.totalCharges = totalCharges;
        this.rebatePercentage = rebatePercentage;
        this.finalCost = finalCost;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getUnitsUsed() {
        return unitsUsed;
    }

    public void setUnitsUsed(double unitsUsed) {
        this.unitsUsed = unitsUsed;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(double totalCharges) {
        this.totalCharges = totalCharges;
    }

    public double getRebatePercentage() {
        return rebatePercentage;
    }

    public void setRebatePercentage(double rebatePercentage) {
        this.rebatePercentage = rebatePercentage;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }
}