package it.unimib;

import it.unimib.fault_localization.FailureLocator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;
import it.unimib.repair.FailureRepairer;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * RepairToolkit
 */
public class App {

    private static final Logger logger = Logger.getLogger(App.class);

    private static final String PROGRAM_SOURCE_CODE_PATH = "/Users/davide/Development/APR/library/src/main/java";
    private static final String PROGRAM_CLASSES_PATH = "/Users/davide/Development/APR/library/target/classes";
    private static final String TEST_SOURCE_CODE_PATH = "/Users/davide/Development/APR/library/src/test/java";
    private static final String TEST_CLASSES_PATH = "/Users/davide/Development/APR/library/target/test-classes";

    public static void main(String[] args) {

        FailureLocator failureLocator =
                new FailureLocator(PROGRAM_SOURCE_CODE_PATH, PROGRAM_CLASSES_PATH,
                        TEST_SOURCE_CODE_PATH, TEST_CLASSES_PATH);

        // 1) Get the map with <FailureInfo, List<RepairTarget>> using Except
        Map<FailureInfo, List<RepairTarget>> failureInfoListMap = failureLocator.getRepairTargets();

        if (failureInfoListMap == null) {
            logger.info("No suspicious locations have been found: it was not possible to create a patch");
            System.exit(0);
        }

        // 2) Use FailureRepairer to initialize the repair process
        FailureRepairer failureRepairer = new FailureRepairer(failureLocator.getFlacocoConfig(), PROGRAM_SOURCE_CODE_PATH);

        // 3) Get the number of failed test cases
        int failedTests = failureLocator.getFailedTestsNumber();

        // 4) For every FailureInfo, the process tries to fix the associated failed test case,
        // and to not make other tests fail
        for (Map.Entry<FailureInfo, List<RepairTarget>> set : failureInfoListMap.entrySet()) {
            // 5) Get the list of RepairTarget associated with the FailureInfo
            List<RepairTarget> repairTargetList = failureInfoListMap.get(set.getKey());
            for (int i = 0; i < repairTargetList.size(); i++) {
                // 6) After a program variant has been generated, check if the failed test case now passes
                boolean result = failureRepairer.repair(repairTargetList, set.getKey().getFailingTestClass(),
                        set.getKey().getFailingTestMethod(), failedTests);
                if (result) {
                    failedTests--;
                    break;
                }
            }
        }

        // 7) If the number of test cases that fail is 0, it means that a possible patch has been generated
        if (failedTests == 0) {
            logger.info("A solution has been found");
        } else {
            logger.info("No solutions have been found");
        }
    }
}
