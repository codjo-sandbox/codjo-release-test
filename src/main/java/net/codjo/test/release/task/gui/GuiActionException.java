/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
/**
 * Exception lev�e lorsqu'une action sur un composant a �chou�.
 */
public class GuiActionException extends GuiException {
    public GuiActionException(String message) {
        super(message);
    }


    public GuiActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
