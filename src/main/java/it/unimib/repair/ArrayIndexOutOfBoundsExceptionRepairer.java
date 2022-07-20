package it.unimib.repair;

import it.unimib.repair_operator.ReplaceArrayInitializationExpressionOperator;
import it.unimib.repair_operator.ReplaceOperator;
import it.unimib.repair_operator.ReplaceVariableOperator;
import org.apache.log4j.Logger;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtArrayAccessImpl;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.List;
import java.util.Set;

public class ArrayIndexOutOfBoundsExceptionRepairer {

    private final Logger logger = Logger.getLogger(ArrayIndexOutOfBoundsExceptionRepairer.class);

    private final RepairUtil repairUtil;

    public ArrayIndexOutOfBoundsExceptionRepairer(RepairUtil repairUtil) {
        this.repairUtil = repairUtil;
    }

    public boolean repairArrayInitialization(CtStatement ctStatement, String failingTestClass, String failingTestMethod, int failingTestsNumber) {

        // 1) Get the elements associated with an array initialization (CtNewArrayImpl<?> in Spoon)
        List<CtNewArrayImpl<?>> ctNewArrayList = ctStatement.getElements(new TypeFilter<>(CtNewArrayImpl.class));

        for (CtNewArrayImpl<?> ctNewArray : ctNewArrayList) {

            // 2) Initialize a concrete Replace Operator for array initialization
            ReplaceOperator replaceOperator = new ReplaceArrayInitializationExpressionOperator(repairUtil.getLauncher());

            // 3) Get the ingredients for the operator
            Set<String> ingredients = replaceOperator.getIngredients(ctNewArray, "int", null);

            if (ingredients == null) {
                logger.info("No ingredients have been found");
                return false;
            }

            // 4) Use the ingredient to mutate the program
            for (String ingredient: ingredients) {
                // 5) Apply the change
                replaceOperator.replace(ctNewArray, ingredient);
                // 6) Check if the program variant passes the failed test case
                // without increasing the number of failed test
                if (repairUtil.processVariant(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber)) {
                    return true;
                } else {
                    // Undo the change if the program variant is not good
                    // (i.e., the failed test case still fails or the number of failed tests increase)
                    replaceOperator.undoReplace(ctNewArray);
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
            ReplaceOperator replaceOperator = new ReplaceVariableOperator(repairUtil.getLauncher());

            // 3) Get the ingredients for the operator
            Set<String> ingredients = replaceOperator.
                    getIngredients(ctArrayAccess, "int", ctArrayAccess.getIndexExpression().prettyprint());

            if (ingredients == null) {
                logger.info("No ingredients have been found");
                return false;
            }

            // 4) Use the ingredient to mutate the program
            for (String ingredient: ingredients) {
                // 5) Apply the change
                replaceOperator.replace(ctArrayAccess.getIndexExpression(), ingredient);
                // 6) Check if the program variant passes the failed test case
                // without increasing the number of failed test
                if (repairUtil.processVariant(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber)) {
                    return true;
                } else {
                    // Undo the change if the program variant is not good
                    // (i.e., the failed test case still fails or the number of failed tests increases)
                    replaceOperator.undoReplace(ctArrayAccess.getIndexExpression());
                }
            }
        }
        return false;
    }
}
