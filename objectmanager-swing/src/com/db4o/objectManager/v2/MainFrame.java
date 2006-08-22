package com.db4o.objectManager.v2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.db4o.objectmanager.model.Db4oConnectionSpec;

/**
 * Main frame for a particular connection.
 *
 * todo: save resize dimensions in prefs (addComponentListener to this frame)
 *
 * User: treeder
 * Date: Aug 8, 2006
 * Time: 11:21:49 AM
 */
public class MainFrame extends JFrame {

    protected static final Dimension PREFERRED_SIZE =
            LookUtils.IS_LOW_RESOLUTION
                    ? new Dimension(650, 510)
                    : new Dimension(730, 560);


    private static final String COPYRIGHT =
            "\u00a9 2006 db4objects Inc. All Rights Reserved.";


    /**
     * Describes optional settings of the JGoodies Looks.
     */
    private final Settings settings;
    private MainPanel mainPanel;

    /**
     * Constructs a <code>DemoFrame</code>, configures the UI,
     * and builds the content.
     */
    protected MainFrame(Settings settings) {
        this.settings = settings;
        configureUI();
        build();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        Settings settings = createDefaultSettings();
        if (args.length > 0) {
            String lafShortName = args[0];
            String lafClassName;
            if ("Windows".equalsIgnoreCase(lafShortName)) {
                lafClassName = Options.JGOODIES_WINDOWS_NAME;
            } else if ("Plastic".equalsIgnoreCase(lafShortName)) {
                lafClassName = Options.PLASTIC_NAME;
            } else if ("Plastic3D".equalsIgnoreCase(lafShortName)) {
                lafClassName = Options.PLASTIC3D_NAME;
            } else if ("PlasticXP".equalsIgnoreCase(lafShortName)) {
                lafClassName = Options.PLASTICXP_NAME;
            } else {
                lafClassName = lafShortName;
            }
            System.out.println("L&f chosen: " + lafClassName);
            settings.setSelectedLookAndFeel(lafClassName);
        }
        MainFrame.createDefaultFrame(settings);
    }

    private static MainFrame createDefaultFrame(Settings settings) {
        MainFrame instance = new MainFrame(settings);
        instance.setSize(PREFERRED_SIZE);
        instance.locateOnScreen(instance);
        instance.setVisible(true);
        return instance;
    }

    public static MainFrame createDefaultFrame() {
        return createDefaultFrame(MainFrame.createDefaultSettings());
    }

    static Settings createDefaultSettings() {
        Settings settings = Settings.createDefault();

        // Configure the settings here.

        return settings;
    }


    /**
     * Configures the user interface; requests Swing settings and
     * JGoodies Looks options from the launcher.
     */
    private void configureUI() {
        // UIManager.put("ToolTip.hideAccelerator", Boolean.FALSE);

        Options.setDefaultIconSize(new Dimension(18, 18));

        Options.setUseNarrowButtons(settings.isUseNarrowButtons());

        // Global options
        Options.setTabIconsEnabled(settings.isTabIconsEnabled());
        UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY,
                settings.isPopupDropShadowEnabled());

        // Swing Settings
        LookAndFeel selectedLaf = settings.getSelectedLookAndFeel();
        if (selectedLaf instanceof PlasticLookAndFeel) {
            PlasticLookAndFeel.setPlasticTheme(settings.getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(settings.getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(
                    settings.isPlasticHighContrastFocusEnabled());
        } else if (selectedLaf.getClass() == MetalLookAndFeel.class) {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        }

        // Work around caching in MetalRadioButtonUI
        JRadioButton radio = new JRadioButton();
        radio.getUI().uninstallUI(radio);
        JCheckBox checkBox = new JCheckBox();
        checkBox.getUI().uninstallUI(checkBox);

        try {
            UIManager.setLookAndFeel(selectedLaf);
        } catch (Exception e) {
            System.out.println("Can't change L&F: " + e);
        }

    }

    /**
     * Builds the <code>DemoFrame</code> using Options from the Launcher.
     */
    private void build() {
        setContentPane(buildContentPane());
        setTitle(getWindowTitle());
        setJMenuBar(new ConnectedMenuBar(settings,
                        createHelpActionListener(),
                        createAboutActionListener()));
        setIconImage(ResourceManager.createImageIcon("database2.gif", "database").getImage());

    }




    /**
     * Builds and answers the content.
     */
    private JComponent buildContentPane() {
        mainPanel = new MainPanel(this, settings);
        return mainPanel;
    }


    protected String getWindowTitle() {
        return "ObjectManager 2.0";
    }

    /**
     * Locates the given component on the screen's center.
     */
    protected void locateOnScreen(Component component) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation(
                (screenSize.width - paneSize.width) / 2,
                (screenSize.height - paneSize.height) / 2);
    }


    /**
     * Creates and answers an ActionListener that opens the help viewer.
     */
    protected ActionListener createHelpActionListener() {
        return null;
    }

    protected ActionListener createAboutActionListener() {
        return new AboutActionListener();
    }

    public void setConnectionInfo(Db4oConnectionSpec connectionInfo) {
        mainPanel.setConnectionInfo(connectionInfo);
    }


    private final class AboutActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                    MainFrame.this,
                    "db4o Object Manager\n\n"
                            + COPYRIGHT + "\n\n");
        }
    }

    private final class OpenFileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new JFileChooser().showOpenDialog(MainFrame.this);
        }
    }


}