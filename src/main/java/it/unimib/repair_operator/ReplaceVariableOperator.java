package it.unimib.repair_operator;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl;
import spoon.support.reflect.code.CtLiteralImpl;

public class ReplaceVariableOperator extends ReplaceOperator {

    private final Logger logger = Logger.getLogger(ReplaceVariableOperator.class);

    private String originalVariable;

    public ReplaceVariableOperator(Launcher launcher) {
        super(launcher);
    }

    @Override
    public void replace(CtElement ctElement, String ingredient) {
        if (ctElement instanceof CtBinaryOperatorImpl || ctElement instanceof CtCodeSnippetExpressionImpl
                || ctElement instanceof CtLiteralImpl) {
            originalVariable = ctElement.prettyprint();
            if (!ingredient.equals(originalVariable)) {
                logger.info("Old variable: " + ctElement.getParent());
                // Look at this for replace method explanation:
                // https://github.com/INRIA/spoon/issues/3827#issuecomment-796600565
                ctElement.replace(launcher.getFactory().createCodeSnippetExpression(ingredient));
                logger.info("New variable: " + ctElement.getParent());
            }
        } else {
            originalVariable = ((CtVariableRead<?>)ctElement).getVariable().getSimpleName();
            if (!ingredient.equals(originalVariable)) {
                logger.info("Old variable: " + ctElement);
                ((CtVariableRead<?>)ctElement).getVariable().setSimpleName(ingredient);
                logger.info("New variable: " + ctElement);
            }
        }
    }

    @Override
    public void undoReplace(CtElement ctElement) {
        if (ctElement instanceof CtBinaryOperatorImpl || ctElement instanceof CtCodeSnippetExpressionImpl) {
            logger.info("Old variable from undo: " + ctElement.getParent());
            ctElement.replace(launcher.getFactory().createCodeSnippetExpression(originalVariable));
            logger.info("New variable from undo: " + ctElement.getParent());
        } else {
            logger.info("Old variable from undo: " + ctElement);
            ((CtVariableRead<?>)ctElement).getVariable().setSimpleName(originalVariable);
            logger.info("New variable from undo: " + ctElement);
        }
    }
}
