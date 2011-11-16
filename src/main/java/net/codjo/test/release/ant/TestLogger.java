/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.ant;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
/**
 * Ce {@link org.apache.tools.ant.BuildLogger} est utilis� par les tests release pour ne pas afficher les
 * messages BUILD SUCCESSFUL et BUILD FAILED avec la pile des exceptions. S'il y a une erreur, l'exception
 * sera catch�e par JUnit et il affichera les informations de l'exception.
 *
 * @version $Revision: 1.4 $
 */
public class TestLogger extends DefaultLogger {
    @Override
    public void buildFinished(BuildEvent event) {
        // Rien
    }
}
