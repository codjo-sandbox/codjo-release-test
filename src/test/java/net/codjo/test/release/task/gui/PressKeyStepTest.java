package net.codjo.test.release.task.gui;
import com.google.code.tempusfugit.temporal.Condition;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import junit.extensions.jfcunit.JFCTestCase;
import net.codjo.test.common.LogString;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

public class PressKeyStepTest extends JFCTestCase {
    private final LogString log = new LogString();
    private PressKeyStep step;
    private JTextField textField;


    public void test_pressKeyUsingRobot() throws Exception {
        showFrame();

        step.setValue("D");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("D");
    }


    public void test_pressKeyUsingRobot_ko() throws Exception {
        showFrame();

        step.setValue("a");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (IllegalStateException e) {
            assertTrue(e.getLocalizedMessage().startsWith("La value 'a' est incorrecte."));
        }
    }


    public void test_pressKeyUsingRobot_shift() throws Exception {
        showFrame();

        step.setValue("shift D");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Maj D");
    }


    public void test_pressKeyUsingRobot_enter() throws Exception {
        showFrame();

        step.setValue("ENTER");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Entrée");
    }


    public void test_pressKeyUsingRobot_ctrlA() throws Exception {
        showFrame();

        step.setValue("ctrl A");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Ctrl A");
    }


    public void test_textFieldOkWithName() throws Exception {
        String textfieldName = "textfield1";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("D");
        step.proceed(new TestContext(this));
        waitOrTimeout(equals("d", text(textField)), seconds(1));
    }


    public void test_textFieldOkWithName_ctrlA() throws Exception {
        String textfieldName = "textfield2";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        textField.setText("Db");
        step.setValue("ctrl A");
        step.proceed(new TestContext(this));
        waitOrTimeout(equals("Db", text(textField)), seconds(1));
        waitOrTimeout(equals("Db", selectedText(textField)), seconds(1));
    }


    // bug jfc?
    public void textFieldOkWithName_shift() throws Exception {
        String textfieldName = "textfield3";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("shift D");
        step.proceed(new TestContext(this));
        waitOrTimeout(equals("D", text(textField)), seconds(1));
    }


    public void test_textFieldOkWithName_enter() throws Exception {
        String textfieldName = "textfield4";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("ENTER");
        step.proceed(new TestContext(this));
        waitOrTimeout(equals(Color.RED, background(textField)), seconds(1));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTextField();

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                String[] params = event.paramString().split(",");
                log.info(String.format("%s, %s", params[0], params[2]));
            }
        }, KeyEvent.KEY_EVENT_MASK);

        step = new PressKeyStep();
    }


    private void createTextField() {
        textField = new JTextField(10);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                textField.setBackground(Color.RED);
            }
        });
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        frame.add(textField);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();

        flushAWT();
    }


    private void assertPressKeyUsingRobot(String expectedText) throws TimeoutException, InterruptedException {
        List<String> expectedList = Arrays.asList(expectedText.split(" "));

        StringBuilder expectedBuffer = new StringBuilder();
        for (String expected : expectedList) {
            if (expectedBuffer.length() > 0) {
                expectedBuffer.append(", ");
            }
            expectedBuffer.append("KEY_PRESSED, keyText=").append(expected);
        }

        expectedBuffer.append(", KEY_TYPED, keyText=Unknown keyCode: 0x0");

        Collections.reverse(expectedList);
        for (String expected : expectedList) {
            expectedBuffer.append(", KEY_RELEASED, keyText=").append(expected);
        }

        waitOrTimeout(equals(expectedBuffer.toString(), log), seconds(10));
    }


    public static PropertyGetter<Color> background(final JTextComponent component) {
        return new PropertyGetter<Color>() {
            public Color getProperty() {
                return component.getBackground();
            }
        };
    }


    public static PropertyGetter<String> text(final JTextComponent component) {
        return new PropertyGetter<String>() {
            public String getProperty() {
                return component.getText();
            }
        };
    }


    public static PropertyGetter<String> selectedText(final JTextComponent component) {
        return new PropertyGetter<String>() {
            public String getProperty() {
                return component.getSelectedText();
            }
        };
    }


    private <T> Condition equals(final T expected, final PropertyGetter<T> getter) {
        return new Condition() {
            public boolean isSatisfied() {
                try {
                    assertEquals(expected, getter.getProperty());
                    return true;
                }
                catch (AssertionError e) {
                    flushAWT();
                }
                return false;
            }
        };
    }


    public static interface PropertyGetter<T> {
        public T getProperty();
    }


    private Condition equals(final String expected, final LogString logString) {
        return new Condition() {
            public boolean isSatisfied() {
                try {
                    logString.assertContent(expected);
                    return true;
                }
                catch (AssertionError e) {
                    flushAWT();
                }
                return false;
            }
        };
    }
}
