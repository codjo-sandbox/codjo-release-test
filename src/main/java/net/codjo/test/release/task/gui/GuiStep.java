/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.ReleaseTestStep;
/**
 * Interface repr�sentant une �tape de test IHM.
 */
public interface GuiStep extends ReleaseTestStep {
    public void proceed(TestContext context);
}
