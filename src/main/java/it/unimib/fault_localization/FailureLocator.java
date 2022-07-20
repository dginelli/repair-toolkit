package it.unimib.fault_localization;

import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
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

        // 1) Create a TestCaseExecutor

        // 2) Get the list of FailureInfo objects

        // 3) Get Flacoco configuration

        // 4) Get the number of failed tests

        // 5) Generate the list of RepairTarget objects and return the Map<FailureInfo, List<RepairTarget>>

        return null;
    }

    public FlacocoConfig getFlacocoConfig() {
        return this.flacocoConfig;
    }

    public int getFailedTestsNumber() {
        return failedTests;
    }
}
