package it.unimib.repair;

import it.unimib.repair_operator.ReplaceOperator;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtArrayAccessImpl;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.List;
import java.util.Set;

public class ArrayIndexOutOfBoundsExceptionRepairer {

    private final RepairUtil repairUtil;

    public ArrayIndexOutOfBoundsExceptionRepairer(RepairUtil repairUtil) {
        this.repairUtil = repairUtil;
    }

    public boolean repairArrayInitialization(CtStatement ctStatement, String failingTestClass, String failingTestMethod, int failingTestsNumber) {

        // 1) Get the elements associated with an array initialization
        List<CtNewArrayImpl<?>> ctNewArrayList = ctStatement.getElements(new TypeFilter<>(CtNewArrayImpl.class));

        for (CtNewArrayImpl<?> ctNewArray : ctNewArrayList) {

            // 2) Initialize a concrete Replace Operator
            ReplaceOperator replaceOperator = null;

            // 3) Get the ingredients for the operator
            Set<String> ingredients = replaceOperator.getIngredients(ctNewArray, "int", null);

            // 4) Use the ingredient to mutate the program
            for (String ingredient: ingredients) {
                // 5) Apply the change

                // 6) Check if the program variant passes the failed test case
                // without increasing the number of failed test
                if (repairUtil.processVariant(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber)) {
                    return true;
                } else {
                    // Undo the change if the program variant is not good
                    // (i.e., the failed test case still fails or the number of failed tests increase)
                }
            }
        }
        return false;
    }

    public boolean repairArrayIndex(CtStatement ctStatement, String failingTestClass, String failingTestMethod, int failingTestsNumber) {
        // 1) Get the elements associated with an array index
        List<CtArrayAccessImpl<?,?>> ctArrayAccessList = ctStatement.getElements(new TypeFilter<>(CtArrayAccessImpl.class));
        System.out.println(ctStatement);
        for (CtArrayAccessImpl<?,?> ctArrayAccess : ctArrayAccessList) {

            // 2) Initialize a concrete Replace Operator
            ReplaceOperator replaceOperator = null; //

            // 3) Get the ingredients for the operator
            Set<String> ingredients = replaceOperator.
                    getIngredients(ctArrayAccess, "int", ctArrayAccess.getIndexExpression().prettyprint());

            // 4) Use the ingredient to mutate the program
            for (String ingredient: ingredients) {
                // 5) Apply the change

                // 6) Check if the program variant passes the failed test case
                // without increasing the number of failed test
                if (repairUtil.processVariant(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber)) {
                    return true;
                } else {
                    // Undo the change if the program variant is not good
                    // (i.e., the failed test case still fails or the number of failed tests increase)
                }
            }
        }
        return false;
    }
}
