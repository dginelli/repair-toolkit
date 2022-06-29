package it.unimib.repair;

import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import it.unimib.compiler.CompilerUtil;
import it.unimib.validator.PatchInfoWriter;
import it.unimib.validator.PatchValidator;
import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepairUtil {

    private final Launcher launcher;
    private final FlacocoConfig flacocoConfig;
    private final String output;

    public RepairUtil(Launcher launcher, FlacocoConfig flacocoConfig, String output) {
        this.launcher = launcher;
        this.output = output;
        this.flacocoConfig = flacocoConfig;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    /**
     * It generates the source code associated with a program variant, and it compiles the variant.
     * @return true if the source code compiles, false otherwise
     */
    public boolean generateVariant() {

        JavaOutputProcessor javaOutputProcessor = new JavaOutputProcessor(launcher.createPrettyPrinter());
        javaOutputProcessor.setFactory(launcher.getFactory());
        javaOutputProcessor.getEnvironment().setSourceOutputDirectory(new File(output + File.separator + "source"));

        SpoonModelBuilder spoonModelBuilder = launcher.getModelBuilder();
        spoonModelBuilder.generateProcessedSourceFiles(OutputType.CLASSES);

        File file = new File(output + File.separator + "build");
        try {
            FileUtils.forceMkdir(file);
            CompilerUtil.compile(javaOutputProcessor.getOutputDirectory().getPath(), file.getPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * It processes a program variant.
     * @param ctStatement the faulty statement changed to fix the bug
     * @param failingTestClass the failed test class
     * @param failingTestMethod the failed test method
     * @param failingTestsNumber the number of failed test cases
     * @return true if the program variant passes the previous failed test case, false otherwise
     */
    public boolean processVariant(CtStatement ctStatement, String failingTestClass, String failingTestMethod, int failingTestsNumber) {
        if (generateVariant()) {
            setSourceDirectoryForProgramVariant();

            PatchValidator patchValidator = new PatchValidator(flacocoConfig);

            if (patchValidator.runTestCases(failingTestClass, failingTestMethod, failingTestsNumber)) {

                CtClass<?> ctClass = ctStatement.getParent(CtClass.class);

                PatchInfoWriter patchInfoWriter = new PatchInfoWriter(output);
                patchInfoWriter.writePatchedJavaFile(ctClass, launcher);

                String patchedFile = launcher.getEnvironment().getSourceOutputDirectory().getPath() + File.separator +
                        ctClass.getQualifiedName().replace(".", File.separator) + ".java";

                String originalFile = launcher.getEnvironment().getSourceOutputDirectory().getPath().
                        replace("patch", "original") + File.separator +
                        ctClass.getQualifiedName().replace(".", File.separator) + ".java";

                patchInfoWriter.writePatchDiff(new File(originalFile), new File(patchedFile));
                return true;
            }
        }
        return false;
    }

    /**
     * It sets the source and bin folders of the program variant
     * for correctly run the test cases on it.
     */
    public void setSourceDirectoryForProgramVariant() {
        List<String> srcJavaDir = new ArrayList<>();
        List<String> binJavaDir = new ArrayList<>();
        srcJavaDir.add(output + File.separator + "source");
        binJavaDir.add(output + File.separator + "build");

        flacocoConfig.setSrcJavaDir(srcJavaDir);
        flacocoConfig.setBinJavaDir(binJavaDir);
    }

    /**
     * It saves the original version of the faulty program.
     */
    public void saveOriginalVersion() {
        launcher.setSourceOutputDirectory(output + File.separator + "original");
        SpoonModelBuilder spoonModelBuilder = launcher.getModelBuilder();
        spoonModelBuilder.generateProcessedSourceFiles(OutputType.CLASSES);
    }
}
