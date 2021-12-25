package com.example.bilalahmad.interpolation;

public class NewtonForwardFormula {

    DifferenceTable differenceTable;

    public double calculateForward(double[] xValues, double[] yValues, double toFindAt) {
        double Answer = yValues[0];
        boolean pass = true;
        for (int i = 0; i < xValues.length; i++) {
            if (xValues[i] == toFindAt) {
                Answer = yValues[i];
                pass = false;
                break;
            }
        }
        if (pass) {
            differenceTable = new DifferenceTable(xValues, yValues);
            double h = 0;
            try {
                h = xValues[1] - xValues[0];
            } catch (Exception e) {
                h = xValues[0]   ;
            }
            double p = 0;
            int indexAt = 0;

            for (int index = 0; index < xValues.length; index++) {
                p = (toFindAt - xValues[index]) / h;
                if (p > 0 && p < 1) {
                    indexAt = index;
                    break;
                }
            }
            System.out.println("I selected y = " + yValues[indexAt] + " : forward");
            double[] calculatedArray = differenceTable.getForward(0);
            for (int pos = 1; pos < calculatedArray.length + 1; pos++) {
                Answer += (differenceTable.calculateP_And_Factorial_Forward(p, pos)[0] /
                        differenceTable.calculateP_And_Factorial_Forward(p, pos)[1]) * calculatedArray[pos - 1];
            }
        }
        return Answer;
    }


}
