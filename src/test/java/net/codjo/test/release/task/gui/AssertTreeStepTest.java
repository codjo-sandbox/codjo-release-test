/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import net.codjo.test.common.PathUtil;
import org.apache.tools.ant.Project;

import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;
/**
 * Classe de test de {@link AssertTreeStep}.
 */
public class AssertTreeStepTest extends JFCTestCase {
    private AssertTreeStep step;
    private Project project;


    @Override
    protected void setUp() throws Exception {
        project = new Project();
        project.setProperty(
              TEST_DIRECTORY,
              PathUtil.findResourcesFileDirectory(getClass()).getPath());
        step = new AssertTreeStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(null, step.getPath());
    }


    public void test_nok_treeNotFound() throws Exception {
        JTree tree = new JTree();
        showFrame(tree);

        TestContext context = new TestContext(this);
        step.setName("UnArbreInconnu");
        step.setPath("root");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'arbre n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_ok() throws Exception {
        // Cr�ation d'un arbre avec un noeud racine sans feuille
        JTree tree = new JTree();
        DefaultTreeModel treeModel = new DefaultTreeModel(new TestableTreeNode("rootName"));
        tree.setModel(treeModel);
        showFrame(tree);

        step.setName("treeName");
        step.setPath("rootName");
        step.proceed(new TestContext(this));
    }


    public void test_treeWithRootOnly_ok() throws Exception {
        createTreeWithRootOnly();

        step.setName("treeName");
        step.setPath("rootName");
        TestContext context = new TestContext(this);
        step.proceed(context);
    }


    private void createTreeWithRootOnly() {
        JTree tree = new JTree();

        // Noeud racine
        DefaultMutableTreeNode root = new TestableTreeNode("rootName");

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        showFrame(tree);
    }


    /**
     * Test sur le path non renseign�
     */
    public void test_tree_nok_noPath() throws Exception {
        TestContext context = new TestContext(this);
        createTreeWithRootOnly();

        step.setName("treeName");
        step.setPath(null);
        step.setExists(false);

        try {
            step.proceed(context);
            fail("Proceed n'a pas echou� alors que le path n'est pas renseign�.");
        }
        catch (GuiConfigurationException ex) {
            ; // cas normal
        }
    }


    public void test_treeWithChilds() throws Exception {
        showFrame(createTreeWithChilds());

        step.setName("treeName");
        step.setPath("rootName:child2");
        step.setExists(true);
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("rootName:child11");
        step.setExists(false);
        step.proceed(context);

        step.setPath("rootName:child8");
        step.setExists(false);
        step.proceed(context);

        try {
            step.setPath("rootName:child8");
            step.setExists(true);
            step.proceed(context);
            fail("Le test doit echouer car le noeud n'existe pas");
        }
        catch (GuiAssertException guiException) {
            assertEquals("Le noeud 'rootName:child8' n'existe pas.\n"
                         + "Liste des noeuds pr�sents :\n" + "-rootName\n" + "-rootName:child1\n"
                         + "-rootName:child1:child11\n" + "-rootName:child2\n", guiException.getMessage());
        }

        step.setPath("rootName");
        step.setExists(true);
        step.proceed(context);
    }


    public void test_treeWithProperties() throws Exception {
        showFrame(createTreeWithChilds());

        step.setName("treeName");
        step.setPath("rootName:child${two}");
        step.setExists(true);
        TestContext context = new TestContext(this);
        context.setProperty("two", "2");
        step.proceed(context);
    }


    public void test_mode() throws Exception {
        JTree tree = createTreeWithChilds();
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row,
                                                          boolean focus) {
                return super.getTreeCellRendererComponent(tree,
                                                          value.toString() + " (" + value.toString() + ")",
                                                          sel, expanded, leaf, row, focus);
            }
        });

        showFrame(tree);
        step.setName("treeName");
        TestContext context = new TestContext(this);

        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName (rootName):child11 (child11)");
        step.setExists(false);
        step.proceed(context);

        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.setPath("rootName:child2");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName:child11");
        step.setExists(false);
        step.proceed(context);

        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName (rootName):child11 (child11)");
        step.setExists(false);
        step.proceed(context);

        step.setMode("plusALaMode");
        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"display\", \"model\"}.",
                         e.getMessage());
        }
    }


    public void test_assertRow() throws Exception {
        JTree tree = createTreeWithChilds();
        showFrame(tree);

        TreeUtils.expandSubtree(tree, new TreePath(tree.getModel().getRoot()));

        step.setName("treeName");
        step.setPath("rootName");
        step.setExists(true);
        step.setRow(0);
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("rootName:child1");
        step.setRow(1);
        step.proceed(context);

        step.setPath("rootName:child1:child11");
        step.setRow(2);
        step.proceed(context);

        step.setPath("rootName:child2");
        step.setRow(3);
        step.proceed(context);

        try {
            step.setPath("rootName:child1");
            step.setRow(3);
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException guiException) {
            assertEquals("Le noeud 'rootName:child1' ne se situe pas � la position '3' "
                         + "mais � la position '1'", guiException.getMessage());
        }
    }


    public void test_assertColors() throws Exception {
        JTree tree = createTreeWithColorsAndIcons();
        showFrame(tree);
        TreeUtils.expandSubtree(tree, new TreePath(tree.getModel().getRoot()));

        step.setName("treeName");
        step.setPath("coloredRootName");
        step.setExists(true);
        step.setForeground("255,0,0");
        step.setBackground("0,0,255");
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("coloredRootName:coloredChild1");
        step.setForeground("0,255,0");
        step.setBackground("255,0,0");
        step.proceed(context);

        step.setPath("coloredRootName:coloredChild1:coloredChild11");
        step.setForeground("255,0,0");
        step.setBackground("255,255,255");
        step.proceed(context);

        try {
            step.setPath("coloredRootName:coloredChild2");
            step.setForeground("0,0,255");
            step.setBackground("0,255,0");
            step.proceed(context);
            fail("Proceed method should have raised an exception");
        }
        catch (GuiAssertException guiException) {
            assertEquals(
                  "Couleur de police du composant 'treeName' au niveau de 'coloredRootName:coloredChild2' : attendu='java.awt.Color[r=0,g=0,b=255]' obtenu='java.awt.Color[r=255,g=0,b=0]'",
                  guiException.getMessage());
        }

        try {
            step.setPath("coloredRootName:coloredChild1:coloredChild11");
            step.setForeground("255,0,0");
            step.setBackground("0,0,0");
            step.proceed(context);
            fail("Proceed method should have raised an exception");
        }
        catch (GuiAssertException guiException) {
            assertEquals(
                  "Couleur de fond du composant 'treeName' au niveau de 'coloredRootName:coloredChild1:coloredChild11' : attendu='java.awt.Color[r=0,g=0,b=0]' obtenu='java.awt.Color[r=255,g=255,b=255]'",
                  guiException.getMessage());
        }
    }


    public void test_assertOpaque() throws Exception {
        JTree tree = createTreeWithColorsAndIcons();
        showFrame(tree);
        TreeUtils.expandSubtree(tree, new TreePath(tree.getModel().getRoot()));

        step.setName("treeName");
        step.setPath("coloredRootName");
        step.setExists(true);
        step.setOpaque(false);
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("coloredRootName:coloredChild1");
        step.setOpaque(true);
        step.proceed(context);

        try {
            step.setPath("coloredRootName:coloredChild2");
            step.setOpaque(true);
            step.proceed(context);
            fail("Proceed method should have raised an exception");
        }
        catch (GuiAssertException guiException) {
            assertEquals(
                  "Opacit� du composant 'treeName' au niveau de 'coloredRootName:coloredChild2' : attendu='true' obtenu='false'",
                  guiException.getMessage());
        }

        try {
            step.setPath("coloredRootName:coloredChild1:coloredChild11");
            step.setOpaque(false);
            step.proceed(context);
            fail("Proceed method should have raised an exception");
        }
        catch (GuiAssertException guiException) {
            assertEquals(
                  "Opacit� du composant 'treeName' au niveau de 'coloredRootName:coloredChild1:coloredChild11' : attendu='false' obtenu='true'",
                  guiException.getMessage());
        }
    }


    public void test_assertIcons() throws Exception {
        TestContext context = new TestContext(this, project);
        JTree tree = createTreeWithColorsAndIcons();
        showFrame(tree);

        step.setName("treeName");
        step.setPath("coloredRootName");
        step.setExists(true);
        step.setIcon("folder_expanded.png");
        step.proceed(context);

        step.setPath("coloredRootName:coloredChild1");
        step.setIcon("folder_collapsed.png");
        step.setIcon("folder_collapsed.png");
        step.proceed(context);

        step.setPath("coloredRootName:coloredChild1:coloredChild11");
        step.setIcon("red.png");
        step.proceed(context);

        try {
            step.setPath("coloredRootName:coloredChild2");
            step.setIcon("green.png");
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException guiException) {
            assertEquals(
                  "Erreur de l'icone sur 'treeName' au niveau de 'coloredRootName:coloredChild2' : attendu='green.png' obtenu='red.png'",
                  guiException.getMessage());
        }
        Thread.sleep(5000);
    }


    private JTree createTreeWithChilds() {
        JTree tree = new JTree();

        // Noeud racine
        DefaultMutableTreeNode root = new TestableTreeNode("rootName");

        // Ajout d'une feuille � la racine
        DefaultMutableTreeNode child1 = new TestableTreeNode("child1");
        root.add(child1);
        child1.add(new TestableTreeNode("child11"));
        root.add(new TestableTreeNode("child2"));

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);

        return tree;
    }


    private JTree createTreeWithColorsAndIcons() {
        // Noeud racine
        TestableTreeNode coloredRoot = new TestableTreeNode("coloredRootName", Color.RED, Color.BLUE, "red.png", false);

        // Ajout d'une feuille � la racine
        TestableTreeNode coloredChild1 = new TestableTreeNode("coloredChild1",
                                                              Color.GREEN,
                                                              Color.RED,
                                                              "green.png",
                                                              true);

        TestableTreeNode coloredChild2 = new TestableTreeNode("coloredChild2",
                                                              Color.RED,
                                                              Color.GREEN,
                                                              "red.png",
                                                              false);
        coloredRoot.add(coloredChild1);
        coloredRoot.add(coloredChild2);
        TestableTreeNode coloredChild11 = new TestableTreeNode("coloredChild11",
                                                               Color.RED,
                                                               Color.WHITE,
                                                               "red.png",
                                                               true);
        coloredChild1.add(coloredChild11);

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(coloredRoot);
        JTree tree = new JTree(treeModel);
        tree.setCellRenderer(new ColorsAndIconsTreeRenderer());

        return tree;
    }


    private class TestableTreeNode extends DefaultMutableTreeNode {

        private final Color foregroundColor;
        private final Color backgroundColor;
        private final boolean opaque;
        private final Icon icon;


        private TestableTreeNode(String userObject) {
            this(userObject, Color.RED, Color.WHITE, "red.png", false);
        }


        private TestableTreeNode(String userObject,
                                 Color foregroundColor,
                                 Color backgroundColor,
                                 String iconResource,
                                 boolean opaque) {
            super(userObject);
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.opaque = opaque;
            this.icon = new ImageIcon(getClass().getResource(iconResource));
        }


        private Color getForegroundColor() {
            return foregroundColor;
        }


        public Color getBackgroundColor() {
            return backgroundColor;
        }


        public Icon getIcon() {
            return icon;
        }


        public boolean isOpaque() {
            return opaque;
        }
    }

    private class ColorsAndIconsTreeRenderer implements TreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            TestableTreeNode node = (TestableTreeNode)value;
            String colorText = (String)node.getUserObject();
            Icon icon = getIcon(node, expanded);
            return buildLabel(node, colorText, icon);
        }


        private Icon getIcon(TestableTreeNode node, boolean expanded) {
            if (!node.isLeaf()) {
                String iconName = expanded ? "folder_expanded.png" : "folder_collapsed.png";
                return new ImageIcon(getClass().getResource(iconName));
            }
            return node.getIcon();
        }


        private JLabel buildLabel(TestableTreeNode node, String color, Icon icon) {
            JLabel component = new JLabel(color, icon, JLabel.HORIZONTAL);
            component.setBackground(node.getBackgroundColor());
            component.setForeground(node.getForegroundColor());
            component.setOpaque(node.isOpaque());
            return component;
        }
    }


    private void showFrame(JTree tree) {
        JFrame frame = new JFrame("Test JTree");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        tree.setName("treeName");
        panel.add(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
