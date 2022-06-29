package it.unimib.fault_localization;

import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import it.unimib.failure_locator.TestCasesExecutor;
import it.unimib.generator.RepairTargetGenerator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;

import java.util.List;
import java.util.Map;

public class FailureLocator {

    private final String programSourceCodePath;
    private final String programClassesPath;
    private final String testsSourceCodePath;
    private final String testsClassesPath;
    private FlacocoConfig flacocoConfig;
    private int failedTests;

    public FailureLocator(String programSourceCodePath, String programClassesPath, String testsSourceCodePath, String testsClassesPath) {
        this.programSourceCodePath = programSourceCodePath;
        this.programClassesPath = programClassesPath;
        this.testsSourceCodePath = testsSourceCodePath;
        this.testsClassesPath = testsClassesPath;
    }

    public Map<FailureInfo, List<RepairTarget>> getRepairTargets() {
        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, null);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();
        this.flacocoConfig = testCasesExecutor.getFlacocoConfig();
        this.failedTests = testCasesExecutor.getFlacocoResult().getFailingTests().size();

        return RepairTargetGenerator.getRepairTargets(failuresInformationList, programSourceCodePath);
    }

    public FlacocoConfig getFlacocoConfig() {
        return this.flacocoConfig;
    }

    public int getFailedTestsNumber() {
        return failedTests;
    }
}
