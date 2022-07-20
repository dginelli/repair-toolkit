package it.unimib.repair_operator;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

public class ReplaceVariableOperator extends ReplaceOperator {

    private final Logger logger = Logger.getLogger(ReplaceVariableOperator.class);

    private String originalVariable;

    public ReplaceVariableOperator(Launcher launcher) {
        super(launcher);
    }

    @Override
    public void replace(CtElement ctElement, String ingredient) {

    }
    @Override
    public void undoReplace(CtElement ctElement) {

    }
}
