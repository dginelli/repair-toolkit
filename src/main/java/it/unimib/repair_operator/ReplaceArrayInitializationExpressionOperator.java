package it.unimib.repair_operator;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.ArrayList;
import java.util.List;

public class ReplaceArrayInitializationExpressionOperator extends ReplaceOperator {

    private final Logger logger = Logger.getLogger(ReplaceArrayInitializationExpressionOperator.class);

    private String originalArrayIntegerExpression;

    public ReplaceArrayInitializationExpressionOperator(Launcher launcher) {
        super(launcher);
    }

    @Override
    public void replace(CtElement ctNewArray, String ingredient) {
        originalArrayIntegerExpression = ((CtNewArrayImpl<?>)ctNewArray).getDimensionExpressions().get(0).toString();

        if (!ingredient.equals(originalArrayIntegerExpression)) {
            List<CtExpression<Integer>> dimensionExpressions = new ArrayList<>();
            dimensionExpressions.add(launcher.getFactory().createCodeSnippetExpression(ingredient));
            logger.info("Old array: " + ctNewArray);
            ((CtNewArrayImpl<?>)ctNewArray).setDimensionExpressions(dimensionExpressions);
            logger.info("New array: " + ctNewArray);
        }
    }

    @Override
    public void undoReplace(CtElement ctElement) {
        CtNewArrayImpl<?> ctNewArray = (CtNewArrayImpl<?> ) ctElement;
        logger.info("Old array from undo: " + ctNewArray);
        List<CtExpression<Integer>> dimensionExpressions = new ArrayList<>();
        dimensionExpressions.add(launcher.getFactory().createCodeSnippetExpression(originalArrayIntegerExpression));
        ctNewArray.setDimensionExpressions(dimensionExpressions);
        logger.info("New array from undo: " + ctNewArray);
    }
}
