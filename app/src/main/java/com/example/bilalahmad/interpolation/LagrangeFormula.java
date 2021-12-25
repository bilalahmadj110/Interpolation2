package com.example.bilalahmad.interpolation;

public class LagrangeFormula {

    public static double calculateLagrangeValue(double[] xValues, double[] yValues, double toFind) {
        double answer = 0;
        for (int i = 0; i < xValues.length; i++) {
            double numerator = 1;
            double denominator = 1;
            for (int j = 0; j < xValues.length; j++) {
                if (j != i) {
                    numerator *= toFind - xValues[j];
                    denominator *= xValues[i] - xValues[j];
                }
            }
            answer += (numerator / denominator) * yValues[i];
        }
        return answer;
    }
}
