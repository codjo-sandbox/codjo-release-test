/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
/**
 * Exception lev�e lorsqu'une assertion �choue dans un test IHM.
 */
public class GuiAssertException extends GuiException {
    public GuiAssertException(String message) {
        super(message);
    }


    public GuiAssertException(String message, Throwable cause) {
        super(message, cause);
    }
}
