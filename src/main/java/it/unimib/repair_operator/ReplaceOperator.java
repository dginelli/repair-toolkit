package it.unimib.repair_operator;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

public abstract class ReplaceOperator {

    private final Logger logger = Logger.getLogger(ReplaceOperator.class);

    protected final Launcher launcher;

    public ReplaceOperator(Launcher launcher) {
        this.launcher = launcher;
    }

    public abstract void replace(CtElement ctElement, String ingredient);

    public abstract void undoReplace(CtElement ctElement);

    // We need to find integer expressions to fix faults related to array initialization
    // or indexes used to access an array
    public Set<String> getIngredients(CtElement ctElement, String type, String original) {
        return null;
    }
}
