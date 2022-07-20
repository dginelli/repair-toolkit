package it.unimib.repair_operator;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

public class ReplaceArrayInitializationExpressionOperator extends ReplaceOperator {

    private final Logger logger = Logger.getLogger(ReplaceArrayInitializationExpressionOperator.class);

    private String originalArrayIntegerExpression;

    public ReplaceArrayInitializationExpressionOperator(Launcher launcher) {
        super(launcher);
    }

    @Override
    public void replace(CtElement ctNewArray, String ingredient) {

    }

    @Override
    public void undoReplace(CtElement ctElement) {

    }
}
