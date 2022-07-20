package it.unimib.repair;

import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import it.unimib.model.RepairTarget;
import it.unimib.model.SuspiciousLocation;

import it.unimib.validator.PatchInfoWriter;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;

import java.util.List;

public class FailureRepairer {

    private final Logger logger = Logger.getLogger(FailureRepairer.class);

    private static final String OUTPUT = "./output";

    private final Launcher launcher;
    private final RepairUtil repairUtil;

    public FailureRepairer(FlacocoConfig flacocoConfig, String programSourceCodePath) {
        this.launcher = new Launcher();
        launcher.addInputResource(programSourceCodePath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(true);
        launcher.buildModel();
        repairUtil = new RepairUtil(launcher, flacocoConfig, OUTPUT);

        repairUtil.saveOriginalVersion();
    }

    /**
     * It initializes the repair process using the Repairtarget objects
     * @param repairTargetList the list of RepairTarget
     * @param failingTestClass the class of the failed test
     * @param failingTestMethod the method of the failed test
     * @param failedTests the number of failed tests
     * @return true if the program variant fixed the failed test without increasing the number of failed tests
     */
    public boolean repair(List<RepairTarget> repairTargetList, String failingTestClass,
                          String failingTestMethod, int failedTests) {

        for (RepairTarget repairTarget : repairTargetList) {
            CtStatement ctStatement = getSuspiciousStatement(repairTarget.getSuspiciousLocation());

            if (ctStatement == null) {
                logger.info("The statement associated with the RepairTarget has not been found");
                return false;
            }

            boolean result = generateVariant(repairTarget, ctStatement, failingTestClass, failingTestMethod, failedTests);

            if (result) {
                PatchInfoWriter patchInfoWriter = new PatchInfoWriter(OUTPUT);
                patchInfoWriter.writePatchDetails(repairTarget, failingTestClass, failingTestMethod);
                return true;
            }
        }
        return false;
    }

    /**
     * It generates a program variant
     * @param repairTarget the repair target used to mutate the program
     * @param ctStatement the statement associated with the suspicious location
     * @param failingTestClass the class of the failed test
     * @param failingTestMethod the class of the failed test
     * @param failingTestsNumber the number of failed test cases
     * @return true if the program variant fixed the failed test without increasing the number of failed tests
     */
    private boolean generateVariant(RepairTarget repairTarget, CtStatement ctStatement, String failingTestClass,
                                    String failingTestMethod, int failingTestsNumber) {

        ArrayIndexOutOfBoundsExceptionRepairer arrayIndexOutOfBoundsExceptionRepairer =
                new ArrayIndexOutOfBoundsExceptionRepairer(repairUtil);
        switch (repairTarget.getGuessedFault()) {
            case ARRAY_INDEX_INITIALIZATION_IS_WRONG:
                return arrayIndexOutOfBoundsExceptionRepairer.
                        repairArrayInitialization(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber);
            case ARRAY_INDEX_IS_WRONG:
                return arrayIndexOutOfBoundsExceptionRepairer.
                        repairArrayIndex(ctStatement, failingTestClass, failingTestMethod, failingTestsNumber);
            default:
                return false;
        }
    }

    /**
     * It returns the Spoon element (CtStatement) associated with the suspicious statement
     * @param suspiciousLocation the suspicious location to analyze
     * @return the Spoon element (CtStatement) associated with the suspicious statement
     */
    public CtStatement getSuspiciousStatement(SuspiciousLocation suspiciousLocation) {

        // 1) This is the class that contains the suspicious statement


        // 2) Get the list of statements contained in the class

        // 3) Find the suspicious statement in the class (same line of suspiciousLocation)

        return null;
    }
}
