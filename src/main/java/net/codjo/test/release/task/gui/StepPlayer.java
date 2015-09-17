/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.WindowMonitor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;

import static java.awt.event.KeyEvent.VK_PRINTSCREEN;
/**
 * Classe permettant d'exécuter une {@link GuiStep}.
 *
 * @noinspection JUnitTestCaseInProductSource, UnconstructableJUnitTestCase, JUnitTestClassNamingConvention
 */
public class StepPlayer extends JFCTestCase {
    static final String USE_PRINT_SCREEN_KEY = "usePrintScreenKey";
    private static final Logger LOG = Logger.getLogger(StepPlayer.class);
    public static final String TEST_DIRECTORY = "test.dir";

    private GuiStep step;
    private Project project;
    private final String testDirectory;
    private TestContext currentTestContext;


    public StepPlayer(Project project, String testDirectory) {
        this.project = project;
        this.testDirectory = testDirectory;
        setName("playImpl"); // must be initialized here because public methods need it
    }


    public void play(GuiStep guiStep) {
        this.step = guiStep;
        this.currentTestContext = new TestContext(this, project);
        if (testDirectory != null) {
            currentTestContext.setProperty(TEST_DIRECTORY, testDirectory);
        }
        try {
            runBare();
        }
        catch (Throwable throwable) {
            saveScreenshot();
            throw new GuiException(currentTestContext.getTestLocation().getLocationMessage() + " \n--> "
                                   + throwable.getMessage(), throwable);
        }
    }


    public void playImpl() {
        step.proceed(currentTestContext);
    }


    public void cleanUp() {
        try {
            runCode(new Runnable() {
                public void run() {
                    try {
                        resumeAWT();

                        TestHelper.cleanUp(StepPlayer.this);
                        for (Window window : WindowMonitor.getWindows()) {
                            if (window.isVisible()) {
                                window.setVisible(false);
                                window.dispose();
                            }
                        }

                        flushAWT();
                    }
                    catch (Throwable e) {
                        setError(e);
                    }
                }
            });
        }
        catch (Throwable throwable) {
            LOG.warn("cleanup has failed", throwable);
        }
    }


    private void saveScreenshot() {
        try {
            saveScreenshot(new Robot());
        }
        catch (Throwable throwable) {
            LOG.warn("Echec du Screenshot", throwable);
        }
    }


    void saveScreenshot(Robot robot) {
        try {
            File screenShotFile = determineScreenShotFile();

            boolean usePrintScreenKey = "true".equals(System.getProperty(USE_PRINT_SCREEN_KEY));
            LOG.info(
                  "Taking screenshot with " + (usePrintScreenKey ? "PrintScreen key" : "Robot#createScreenCapture()"));
            BufferedImage image;
            if (usePrintScreenKey) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                robot.keyPress(VK_PRINTSCREEN);
                robot.delay(40);
                robot.keyRelease(VK_PRINTSCREEN);
                robot.delay(40);

                DataFlavor wantedFlavor = DataFlavor.imageFlavor;
                if (!cb.isDataFlavorAvailable(wantedFlavor)) {
                    LOG.error("Can't take screenshort because data flavor " + wantedFlavor
                              + " is unavailable. Available data flavors : "
                              + StringUtils.join(cb.getAvailableDataFlavors()));
                    return;
                }
                image = (BufferedImage)cb.getData(wantedFlavor);
                ImageIO.write(image, "jpg", screenShotFile);
            }
            else {
                image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            }
            ImageIO.write(image, "jpg", screenShotFile);
        }
        catch (Throwable throwable) {
            LOG.warn("Echec du Screenshot", throwable);
        }
    }


    File determineScreenShotFile() {
        String imageName;
        if (project.getUserProperty("ant.file") != null) {
            imageName = new File(project.getUserProperty("ant.file")).getName();
        }
        else {
            imageName = "Error-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        }
        return new File("./target/", imageName + ".jpeg");
    }
}
