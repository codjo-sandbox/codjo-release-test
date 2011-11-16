/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test;
import net.codjo.test.release.ant.AntRunner;
import java.io.File;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 *
 */
public abstract class AbstractSampleGuiTestCase extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    protected void runScenario(String fileName) {
        runAntResourceFile(this.getClass(), fileName);
    }


    protected void runScenario(String fileName, Project project) {
        runAntResourceFile(this.getClass(), fileName, project);
    }


    /**
     * Ex�cute un script Ant packag� en tant que ressource.
     *
     * @param clazz    classe � laquelle le nom de resource est relatif
     * @param fileName nom relatif du fichier ressource
     */
    protected static void runAntResourceFile(Class clazz, String fileName) {
        AntRunner.start(getResourceFile(clazz, fileName));
    }


    /**
     * Ex�cute un script Ant packag� en tant que ressource.
     *
     * @param clazz    classe � laquelle le nom de resource est relatif
     * @param fileName nom relatif du fichier ressource
     */
    protected static void runAntResourceFile(Class clazz, String fileName, Project project) {
        AntRunner.start(project, getResourceFile(clazz, fileName));
    }


    /**
     * Permet d'obtenir l'objet {@link java.io.File} associ� � une ressource relative � une classe donn�e. La
     * ressource doit �tre dans un r�pertoire du disque local, et non situ�e dans un fichier jar.
     *
     * @param clazz    classe � laquelle le nom de resource est relatif
     * @param fileName nom relatif du fichier ressource
     *
     * @return l'objet File.
     */
    protected static File getResourceFile(Class clazz, String fileName) {
        URL url = clazz.getResource(fileName);
        return new File(url.getFile());
    }
}
