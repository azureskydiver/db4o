package com.db4o.objectManager.v2;

import com.jgoodies.looks.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.db4o.objectManager.v2.custom.BackgroundPanel;
import com.db4o.objectmanager.model.Db4oFileConnectionSpec;
import com.db4o.objectmanager.model.Db4oConnectionSpec;
import com.db4o.objectmanager.model.Db4oSocketConnectionSpec;
import com.db4o.objectmanager.api.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.w3c.dom.css.CSS2Properties;

/**
 * User: treeder
 * Date: Aug 8, 2006
 * Time: 11:21:49 AM
 */
public class Dashboard {

	private static final String COPYRIGHT =
			"\u00a9 2006 db4objects Inc. All Rights Reserved.";
	public static final String VERSION = "6.0.003";


	private JFrame frame;
	private JTextField fileTextField;
	private RecentConnectionList recentConnectionList;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private Settings settings;
	private JTextField hostTextField;
	private JTextField portTextField;
	private static final String TITLE = "ObjectManager " + VERSION;


	/**
	 * Configures the UI, then builds and opens the UI.
	 */
	public static void main(String[] args) {
		Dashboard instance = new Dashboard();
		instance.configureUI();
		instance.buildInterface();
	}

	/**
	 * Configures the UI; tries to set the system look on Mac,
	 * <code>WindowsLookAndFeel</code> on general Windows, and
	 * <code>Plastic3DLookAndFeel</code> on Windows XP and all other OS.<p>
	 * <p/>
	 * The JGoodies Swing Suite's <code>ApplicationStarter</code>,
	 * <code>ExtUIManager</code>, and <code>LookChoiceStrategies</code>
	 * classes provide a much more fine grained algorithm to choose and
	 * restore a look and theme.
	 */
	private void configureUI() {
		settings = MainFrame.createDefaultSettings();

		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setDefaultIconSize(new Dimension(18, 18));

		String lafName =
				//LookUtils.IS_OS_WINDOWS_XP
				//  ? Options.getCrossPlatformLookAndFeelClassName() :
				Options.getSystemLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(lafName);
		} catch (Exception e) {
			System.err.println("Can't set look & feel:" + e);
		}
		/* Font controlFont = Fonts.WINDOWS_XP_96DPI_NORMAL;
				FontSet fontSet = FontSets.createDefaultFontSet(controlFont);
				FontPolicy fontPolicy = FontPolicies.createFixedPolicy(fontSet);
				WindowsLookAndFeel.setFontPolicy(fontPolicy);*/
	}

	/**
	 * Creates and configures a frame, builds the menu bar, builds the
	 * content, locates the frame on the screen, and finally shows the frame.
	 */
	private void buildInterface() {
		frame = new JFrame();
		frame.setIconImage(ResourceManager.createImageIcon(ResourceManager.ICONS_PLAIN_16X16 + "data.png", "database").getImage());
		frame.setJMenuBar(buildMenuBar());
		frame.setContentPane(buildContentPane());
		frame.setSize(600, 462);
		frame.setResizable(false);
		locateOnScreen(frame);
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Locates the frame on the screen center.
	 */
	private void locateOnScreen(Frame frame) {
		Dimension paneSize = frame.getSize();
		Dimension screenSize = frame.getToolkit().getScreenSize();
		frame.setLocation(
				(screenSize.width - paneSize.width) / 2,
				(screenSize.height - paneSize.height) / 2);
	}

	/**
	 * Builds and returns the menu bar.
	 */
	private JMenuBar buildMenuBar() {
		DashboardMenuBar dashboardMenuBar = new DashboardMenuBar(this,
				settings,
				createHelpActionListener(frame),
				createAboutActionListener(frame));
		return dashboardMenuBar;
	}

	/**
	 * Builds and returns the content pane.
	 * @return the main content pane component
	 */
	private JComponent buildContentPane() {
		JPanel panel = new JPanel(new BorderLayout());
		//panel.add(buildToolBar(), BorderLayout.NORTH);
		panel.add(buildConnectionsPanel(), BorderLayout.CENTER);
		return panel;
	}

	private Component buildConnectionsPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// create db4o icon
		String logoPath = "db4ologo.png";
		ImageIcon icon = ResourceManager.createImageIcon(logoPath, "db4o");
		JLabel label1 = new JLabel(icon, JLabel.LEFT);
		panel.add(label1, BorderLayout.NORTH);

		// gradient background
		BackgroundPanel backgroundPanel = new BackgroundPanel();

		// add welcome message
		JLabel welcome = new JLabel("Recent Connections");
		welcome.setFont(Fonts.WINDOWS_VISTA_96DPI_LARGE);//new Font("Verdana", Font.PLAIN, 30));
		welcome.setForeground(Color.white);
		welcome.setVerticalAlignment(JLabel.TOP);

		recentConnectionList = new RecentConnectionList();
		recentConnectionList.getList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Db4oConnectionSpec connectionSpec = recentConnectionList.getSelected();
				if (connectionSpec != null) {
					showInForm(connectionSpec);
					if (e.getClickCount() == 2) {
						connectAndOpenFrame();
					}
				}
			}
		});
		recentConnectionList.getList().addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					Db4oConnectionSpec connectionSpec = recentConnectionList.getSelected();
					if (connectionSpec != null) {
						showInForm(connectionSpec);
						connectAndOpenFrame();
					}
				}
			}

		});

		JLabel welcome2 = new JLabel("New Connection");
		welcome2.setFont(Fonts.WINDOWS_VISTA_96DPI_LARGE);//new Font("Verdana", Font.PLAIN, 30));
		welcome2.setForeground(Color.white);

		FormLayout layout = new FormLayout(
				"right:max(40dlu;pref), 3dlu, 120dlu, 7dlu," // 1st major colum
						+ "right:max(40dlu;pref), 3dlu, 80dlu",		// 2nd major column
				"");										 // add rows dynamically
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		// builder.appendSeparator("Flange");

		builder.append(welcome);

		builder.nextLine();
		builder.append("", recentConnectionList, 3);

		builder.nextLine();
		builder.append(welcome2);
		builder.nextLine();
		//builder.append("");
		builder.appendSeparator("Local");
		fileTextField = new JTextField();
		builder.append("File:", fileTextField);
		final JFileChooser fc = new JFileChooser();
		final Button browse = new Button("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Handle open button action.
				if (e.getSource() == browse) {
					int returnVal = fc.showOpenDialog(frame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						fileTextField.setText(file.getAbsolutePath());
					} else {

					}
				}
			}
		});
		builder.append(browse);
		builder.nextLine();
		JButton openButton = new JButton("Open");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectAndOpenFrame();
			}
		});
		builder.append("", openButton);


		builder.nextLine();
		builder.appendSeparator("Remote");

		//builder.append("");
		hostTextField = new JTextField();
		builder.append("Host:", hostTextField);
		portTextField = new JTextField();
		portTextField.setColumns(5);
		// todo: validate port to ensure this is an integer - portTextField.addActionListener();
		builder.append("Port:", portTextField);
		builder.nextLine();
		usernameTextField = new JTextField();
		builder.append("Username:", usernameTextField);
		builder.nextLine();
		//builder.append("");
		passwordTextField = new JTextField();
		builder.append("Password:", passwordTextField);
		builder.nextLine();
		//builder.append("");
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectAndOpenFrame();
			}
		});
		builder.append("", connectButton);

		JPanel panel2 = builder.getPanel();
		panel2.setOpaque(false);
		backgroundPanel.add(panel2);

		// add to main panel
		panel.add(backgroundPanel, BorderLayout.CENTER);

		return panel;
	}


	public void connectTo(String dataFile) {
		fileTextField.setText(dataFile);
		connectAndOpenFrame();
	}

	private void connectAndOpenFrame() {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Db4oConnectionSpec connectionSpec = null;
		if (fileTextField.getText().length() > 0) {
			// then open file connection
			connectionSpec = new Db4oFileConnectionSpec(fileTextField.getText(), false);
		} else if (hostTextField.getText().length() > 0) {
			connectionSpec = new Db4oSocketConnectionSpec(hostTextField.getText(), Integer.parseInt(portTextField.getText()), usernameTextField.getText(), passwordTextField.getText(), false);
		}
		recentConnectionList.addNewConnectionSpec(connectionSpec);
		clearForm();
		Dimension frameSize = (Dimension) Preferences.getDefault().getPreference(Preferences.FRAME_SIZE);
		Point frameLocation = (Point) Preferences.getDefault().getPreference(Preferences.FRAME_LOCATION);
		// todo: connect here first, then open frame if successful
		MainFrame instance = MainFrame.createDefaultFrame(frameSize, frameLocation, connectionSpec);

		instance.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				// save size in prefs
				Preferences.getDefault().setPreference(Preferences.FRAME_SIZE, e.getComponent().getSize());
			}

			public void componentMoved(ComponentEvent e) {
				// save location in prefs
				Preferences.getDefault().setPreference(Preferences.FRAME_LOCATION, e.getComponent().getLocation());
			}

			public void componentShown(ComponentEvent e) {

			}

			public void componentHidden(ComponentEvent e) {

			}
		});
		frame.setCursor(null);
	}

	private void clearForm() {
		fileTextField.setText("");
		usernameTextField.setText("");
		passwordTextField.setText("");
		portTextField.setText("");
		hostTextField.setText("");
	}

	private void showInForm(Db4oConnectionSpec connectionSpec) {
		clearForm();
		if (connectionSpec instanceof Db4oFileConnectionSpec) {
			fileTextField.setText(connectionSpec.getPath());
		} else if (connectionSpec instanceof Db4oSocketConnectionSpec) {
			Db4oSocketConnectionSpec spec = (Db4oSocketConnectionSpec) connectionSpec;
			hostTextField.setText(spec.getHost());
			portTextField.setText(spec.getPort() + "");
			usernameTextField.setText(spec.getUser());
			passwordTextField.setText(spec.getPassword());
		}
	}

	// Helper Code ********************************************************

	/**
	 * Creates and answers a <code>JScrollpane</code> that has no border.
	 */
	private JScrollPane createStrippedScrollPane(Component c) {
		JScrollPane scrollPane = new JScrollPane(c);
		scrollPane.setBorder(null);
		return scrollPane;
	}

	/**
	 * Creates and answers a <code>JLabel</code> that has the text
	 * centered and that is wrapped with an empty border.
	 */
	private Component createCenteredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(new EmptyBorder(3, 3, 3, 3));
		return label;
	}

	public void showError(String msg) {
		JOptionPane.showMessageDialog(frame, "msg", "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Creates and answers an ActionListener that opens the help viewer.
	 * @param frame
	 */
	public static ActionListener createHelpActionListener(Component frame) {
		return new HelpActionListener(frame);
	}

	public static ActionListener createAboutActionListener(Component frame) {
		return new AboutActionListener(frame);
	}

	public Frame getFrame() {
		return frame;
	}

	private static final class AboutActionListener implements ActionListener {
		private Component frame;

		public AboutActionListener(Component frame) {

			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			String buildNumber = getBuildNumber();
			JOptionPane.showMessageDialog(
					frame,
					"db4o Object Manager\n" +
							"Version: " + VERSION + "\n" +
							"Build: " + buildNumber + "\n" +
							COPYRIGHT + "\n\n");
		}

		private String getBuildNumber() {
			URL url = Dashboard.class.getResource("build.number");
			if (url != null) {
				try {
					Properties props = new Properties();
					props.load(url.openStream());
					return (String) props.get("build.number");
				} catch (IOException e) {
					System.out.println("Could not get build number.");
				}
			}
			return null;
		}
	}

	private static final class HelpActionListener implements ActionListener {
		private Component frame;

		public HelpActionListener(Component frame) {

			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
					frame,
					// todo: make this linkable, need to figure out how to launch a browser
					"Please visit our online help at: \n" +
							"http://developer.db4o.com/DocsWiki/view.aspx/Reference/Object_Manager_For_db4o/Installation");
		}
	}



}
