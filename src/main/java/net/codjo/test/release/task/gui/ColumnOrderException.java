package net.codjo.test.release.task.gui;
/**
 * Exception g�n�r�e par un AssertTableExcel quand l'ordre des colonnes test�es ne correspond pas � l'�talon.
 */
public class ColumnOrderException extends GuiException {
    public ColumnOrderException(String message) {
        super(message);
    }
}
