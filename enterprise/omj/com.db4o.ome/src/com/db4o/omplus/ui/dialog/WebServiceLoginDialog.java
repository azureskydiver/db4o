package com.db4o.omplus.ui.dialog;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.db4o.omplus.Activator;
import com.db4o.omplus.datalayer.FileDataStore;
import com.db4o.omplus.datalayer.ImageUtility;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.datalayer.webservices.PermissionValidator;
import com.db4o.omplus.datalayer.webservices.WebServiceConnector;
import com.db4o.omplus.datalayer.webservices.connection.UserWebServiceCredentials;
import com.db4o.omplus.ui.actions.StatusAction;
import com.db4o.omplus.ui.actions.WebServiceLoginAction;
import com.db4o.omplus.ui.actions.WebServiceLogoutAction;



public class WebServiceLoginDialog 
{
	private final int MAIN_SHELL_WIDTH = 400;
	private final int MAIN_SHELL_HEIGHT = 290;
	
	private final int FORGOT_PASSWORD_BROWSER = 0;
	
	private final int PURCHASE_LICENSE_BROWSER = 1;
	private final int WEB_SERVICE_CANCEL_BROWSER = 2;
		
	private Shell mainCompositeShell;
	
	private Composite imageComposite;
	
	private Composite upperComposite;
	private Label usernameLabel;
	private Label passwordLabel;
	private Text usernameText;
	private Text passwordText;
	private Button rememberMeCheckBox;
	
	private Composite buttonComposite;
	private Button loginBtn;
	private Button cancelButton;	
	private Link forgotPasswordLink;
	
	private Composite labelComposite;
	private Label cancelLabel;
	private Link licenceLabelLink;
	private FontData licenceLabelFontData =	new FontData("TAHOMA", 8, SWT.BOLD);
	private Font licenceLabelFont = new Font(null,licenceLabelFontData);
	
	private int btnClciked = -1;
	
	
	
	public WebServiceLoginDialog(Shell parentShell) 
	{
		mainCompositeShell = new Shell(parentShell,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		mainCompositeShell.setText("ObjectManager Enterprise Login");		
		
		//To make the dilaog appear at center
		Rectangle rectangle = parentShell.getBounds(); 
		mainCompositeShell.setBounds(rectangle.width/3, rectangle.height/3, MAIN_SHELL_WIDTH,MAIN_SHELL_HEIGHT);
		mainCompositeShell.setImage(ImageUtility.getImage(OMPlusConstants.DB4O_WIND_ICON));
		createComponents();
	}

	public void open()
	{
		mainCompositeShell.open();
	}
	
	/*public Shell open()
	{
		mainCompositeShell.open();
		return mainCompositeShell;
	}*/
	public Shell getShell()
	{
		return mainCompositeShell;
	}
	
	public int getButtonClicked()
	{
		return btnClciked;
	}
	

	/**
	 * Create child components
	 */
	private void createComponents()
	{
		imageComposite = new Composite(mainCompositeShell, SWT.NONE);
		upperComposite = new Composite(mainCompositeShell, SWT.NONE);
		buttonComposite = new Composite(mainCompositeShell, SWT.NONE);
		labelComposite = new Composite(mainCompositeShell, SWT.BORDER);
		
		setImageCompositeComponents();
		setUpperCompositeComponents();
		setButtonCompositeComponents();
		setLabelCompositeComponents();
		
		setMainShellLayout();
		setUpperCompositeLayout();
		setButtonCompositeLayout();
		setLabelCompositeLayout();
		
		mainCompositeShell.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e)
			{
				disposeFontsAndColors();
				
			}
			
		});
		
		
		mainCompositeShell.layout(true, true);
		//addListenersForButtons();
		
	}	
	/*private void addListenersForButtons() {
		cancelButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				
			}

			public void widgetSelected(SelectionEvent e) {
				disposeFontsAndColors();
				mainCompositeShell.dispose();			
			}
		});
	}*/

	private void setImageCompositeComponents() {
		imageComposite.setLayout(new FormLayout());
		Label img_Label = new Label(imageComposite, SWT.NONE);
		img_Label.setLayoutData(new FormData(450, 75));
		Image img = ImageUtility.getImage(OMPlusConstants.DB4O_LOGO_IMAGE);
		img_Label.setImage(img);	
	}

	/**
	 * Create components for UpperComposite....username, passowrd labels/text
	 */
	private void setUpperCompositeComponents()
	{
		usernameLabel = new Label(upperComposite, SWT.NONE);
		usernameLabel.setText("Username:");
	
		
		usernameText = new Text(upperComposite, SWT.BORDER);

		passwordLabel = new Label(upperComposite, SWT.NONE);
		passwordLabel.setText("Password:");

		
		passwordText = new Text(upperComposite, SWT.BORDER|SWT.PASSWORD);
		//Enter on password should fire a Login request
		passwordText.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent e) 
			{
				if(e.character == SWT.CR)
				{
					loginToWebService();
				}
			}

			public void keyReleased(KeyEvent e) 
			{// Auto-generated method stub			
			}
		});
			
		rememberMeCheckBox = new Button(upperComposite, SWT.CHECK);
		rememberMeCheckBox.setText("Remember Me");
		
	}
	
	/**
	 * Create components for ButtonComposite....login, cancel button
	 */
	private void setButtonCompositeComponents()
	{
		loginBtn = new Button(buttonComposite, SWT.PUSH);
		loginBtn.setText("Login");
		loginBtn.addSelectionListener(new WebServiceLoginListener());
		
		cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e) {
				//  Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) 
			{
				showBrowser(WEB_SERVICE_CANCEL_BROWSER);
				disposeFontsAndColors();
				btnClciked = OMPlusConstants.DIALOG_CANCEL_CLICKED;
				mainCompositeShell.dispose();			
				//mainCompositeShell.close();
				
			}

			
			
		});
	
		forgotPasswordLink = new Link(buttonComposite, SWT.NONE);
		String linkText = "<a>Forgot Password?</a>";
		forgotPasswordLink.setText(linkText);
		forgotPasswordLink.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e)
			{
				showBrowser(FORGOT_PASSWORD_BROWSER);
				
			}
			
		});
		forgotPasswordLink.setToolTipText("URL");
	}
	
	/**
	 * Create lowermost labels
	 */
	private void setLabelCompositeComponents()
	{
		cancelLabel = new Label(labelComposite, SWT.NONE);
		cancelLabel.setText("Press Cancel to continue to Reduced Mode");
		
		licenceLabelLink = new Link(labelComposite, SWT.NONE);
		licenceLabelLink.setText("This product requires a dDN Enterprise License. <a>Purchase</a>");
		licenceLabelLink.setFont(licenceLabelFont);
		licenceLabelLink.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e)
			{
				showBrowser(PURCHASE_LICENSE_BROWSER);
				
			}
			
		});
	}
	
	/**
	 * Set layout for Main shell
	 */
	private void setMainShellLayout()
	{
		mainCompositeShell.setSize(MAIN_SHELL_WIDTH, MAIN_SHELL_HEIGHT);
		mainCompositeShell.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.top = new FormAttachment(0,0);
		data.left = new FormAttachment(0,0);		
		imageComposite.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(imageComposite, 4);
		data.left = new FormAttachment(2,2);
		data.right = new FormAttachment(100, -5);
//		data.width = MAIN_SHELL_WIDTH;
		upperComposite.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(upperComposite, 4);
		data.left = new FormAttachment(2,2);
		data.right = new FormAttachment(100, -5);
		buttonComposite .setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(buttonComposite, 15);
		data.left = new FormAttachment(0,0);
		data.width = MAIN_SHELL_WIDTH;
		data.height = 120;
		labelComposite .setLayoutData(data);		
	}
	
	/**
	 * Set layout for UpperComposite
	 */
	private void setUpperCompositeLayout() 
	{
		upperComposite.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.top = new FormAttachment(2,4);
		data.left = new FormAttachment(20,2);		
		usernameLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(2,2);
		data.left = new FormAttachment(usernameLabel,10);
		data.right = new FormAttachment(80, -10 );
		usernameText.setLayoutData(data);
		
		
		data = new FormData();
		data.top = new FormAttachment(usernameLabel,13);
		data.left = new FormAttachment(20, 2);
		passwordLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(usernameText, 7);
		data.left = new FormAttachment(passwordLabel, 12);// TODO
		data.right = new FormAttachment(80, -10 );
		passwordText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment( passwordText ,7);
		data.left = new FormAttachment(34, 11);
		rememberMeCheckBox.setLayoutData(data);		
	}
	
	/**
	 * Set layout for ButtonComposite
	 */
	private void setButtonCompositeLayout() 
	{
		buttonComposite.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.top = new FormAttachment(2,4);
		data.left = new FormAttachment(37, 0);		
		data.width = 55;
		loginBtn.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(2,4);
		data.left = new FormAttachment(loginBtn,10);
		//data.right =  new FormAttachment(75, -10);
		data.width = 55;
		cancelButton .setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(loginBtn,5);
		data.left = new FormAttachment(37, 4);
		forgotPasswordLink.setLayoutData(data);		
	}
	
	/**
	 * Set layout for LabelComposite
	 */
	private void setLabelCompositeLayout() 
	{
		labelComposite.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.top = new FormAttachment(6,0);
		data.left = new FormAttachment(20, 0);		
		cancelLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(cancelLabel,6);
		data.left = new FormAttachment(8,0);		
		licenceLabelLink.setLayoutData(data);
		
		
	}
	
	/**
	 * Show various browsers
	 * @param browserId
	 */
	private void showBrowser(int browserId)
	{
		if(checkIfBrowserExists(browserId))
		{
			maximizebrowser(browserId);
			return;
		}
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchBrowserSupport support =	workbench.getBrowserSupport();
		int browserStyle = IWorkbenchBrowserSupport.AS_EDITOR|IWorkbenchBrowserSupport.LOCATION_BAR|
						   IWorkbenchBrowserSupport.NAVIGATION_BAR;
		
		switch(browserId)
		{
			case FORGOT_PASSWORD_BROWSER:
			{	
				try 
				{
					IWebBrowser browser = support.createBrowser(browserStyle,
												OMPlusConstants.FORGOT_PASSWORD_BROWSER_ID,
												OMPlusConstants.FORGOT_PASSWORD_BROWSER_NAME,
												OMPlusConstants.FORGOT_PASSWORD_BROWSER_TOOLTIP);
				
					URL url = new URL(OMPlusConstants.FORGOT_PASSWORD_URL);
					browser.openURL(url);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				break;
			}
			//NOTE: PURCHASE_LICENSE_BROWSER & WEB_SERVICE_CANCEL_BROWSER currently show same browser
			case WEB_SERVICE_CANCEL_BROWSER:
			case PURCHASE_LICENSE_BROWSER:
			{
				try 
				{
					IWebBrowser browser = support.createBrowser(browserStyle,
												OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_ID,
												OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_NAME,
												OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_TOOLTIP);
				
					/*URL url = new URL(OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_STRING);
					browser.openURL(url);*/
					URL url = Platform.getBundle(OMPlusConstants.PLUGIN_ID).getEntry(OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_LOCATION);
//					URL url = getURL();
					if(url != null)
						System.out.println(FileLocator.toFileURL(url));
						browser.openURL(FileLocator.toFileURL(url));		
					
				}
				catch (Exception e) 
				{
					// Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		maximizebrowser(browserId);
		disposeFontsAndColors();
		mainCompositeShell.dispose();
		
	}
	
	@SuppressWarnings("unused")
	private URL getURL() {
		URL url = null; 
		String path =  System.getProperty(OMPlusConstants.CLASSPATH);
		String []entries = path.split(";");
		for( String cPath : entries){
			cPath = cPath.replace('\\', OMPlusConstants.BACKSLASH);
			if(!cPath.contains("eclipse/plugins"))
				continue;
			StringBuilder sb = new StringBuilder("file:///");
			sb.append(cPath.split(OMPlusConstants.PLUGIN_FLD)[0]);
			sb.append(OMPlusConstants.PLUGIN_FLD);
			sb.append(OMPlusConstants.BACKSLASH);
			sb.append("com.db4o.ome_1.0.0");sb.append(OMPlusConstants.BACKSLASH);
			if(cPath != null){
				try {
					sb.append(OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_LOCATION);
					url = new URL(sb.toString());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return url;
		}
		return null;
	}

	/**
	 * Maximize the browser
	 * @param browserId
	 */
	private void maximizebrowser(int browserId)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorReference ref[]  = workbench.getActiveWorkbenchWindow().		
									getActivePage().getEditorReferences();
		
		String browserName = null;
		
		switch(browserId)
		{
			case FORGOT_PASSWORD_BROWSER:
				browserName = OMPlusConstants.FORGOT_PASSWORD_BROWSER_NAME;
				break;
			
			//NOTE: PURCHASE_LICENSE_BROWSER & WEB_SERVICE_CANCEL_BROWSER currently show same browser	
			case PURCHASE_LICENSE_BROWSER:	
			case WEB_SERVICE_CANCEL_BROWSER:
				browserName = OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_NAME;
				break;				
		}	
		
		for (int i = 0; i < ref.length; i++) 
		{
			if(ref[i].getTitle().equals(browserName))
			{
				//activate the correct browser
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().activate(ref[i].getPart(true));
				
				//maximize browser
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
											getActivePage().setPartState(ref[i],
													IWorkbenchPage.STATE_MAXIMIZED);
				
				return;
			}
		}
	}
	
	/**
	 * Check if browser exists
	 * @param browserId
	 * @return
	 */
	private boolean checkIfBrowserExists(int browserId)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorReference ref[]  = workbench.getActiveWorkbenchWindow().		
									getActivePage().getEditorReferences();
		
		String browserName = null;
		switch(browserId)
		{
			case FORGOT_PASSWORD_BROWSER:
				browserName = OMPlusConstants.FORGOT_PASSWORD_BROWSER_NAME;
				break;
			
			//NOTE: PURCHASE_LICENSE_BROWSER & WEB_SERVICE_CANCEL_BROWSER currently show same browser	
			case PURCHASE_LICENSE_BROWSER:	
			case WEB_SERVICE_CANCEL_BROWSER:
				browserName = OMPlusConstants.WEB_SERVICE_CANCEL_BROWSER_NAME;
				break;				
		}	

		for (int i = 0; i < ref.length; i++) 
		{
			if(ref[i].getTitle().equals(browserName))
			{
				return true;				
			}
		}
		return false;
	}
	
	/**
	 * dispose fonts explicitly
	 */
	private void disposeFontsAndColors()
	{	
		licenceLabelFont.dispose();
	}
	
	/**
	 * LOGIC: This code is same as in WebServiceLoginListener.widgetSelected()
	 *  Added so that password text box's enter tries to perform web servcie login.
	 *  To prevent side effects repition of code
	 *  
	 *  TODO: Remove the  WebServiceLoginListener class and on Login's click call this function only
	 */
	private void loginToWebService()
	{
		String username = usernameText.getText();
		String password = passwordText.getText();
		if(username == null || username.trim().length()==0 ||
			password == null || password.trim().length()==0	)
		{
			MessageDialog.openInformation(null, OMPlusConstants.DIALOG_BOX_TITLE, 
										 "Credentials cannot be empty.Please enter again.");
			usernameText.setText("");
			passwordText.setText("");
			return;
		}
		
		//Ideally call the UserWebServiceCredential reset first because this is new login
		UserWebServiceCredentials.resetInstance();
		UserWebServiceCredentials.getInstance().setUsername(username);
		UserWebServiceCredentials.getInstance().setPassword(password);
		
		//Connect to the webservice 
		String address = Activator.getProxyAddress();
		int port = Activator.getProxyPort();
		boolean value = false;
		try {
			value = WebServiceConnector.connectToWebService(null, username, password,
								address, port);
		}
		/*catch (AxisFault ex) {
			String str = ex.toString();
			if(str.contains(UnknownHostException.class.getName())){
				showErrorDialog("No internet connection available.");
				return;
			}
			
		}*/catch (Exception ex) {
			String str = ex.getMessage();
			if(str.contains(UnknownHostException.class.getName())){
				showErrorDialog(str);
//				return;
			}
			value = false;
		}
		if(value)
		{
			if(rememberMeCheckBox.getSelection()){
				UserWebServiceCredentials userCredentials = UserWebServiceCredentials.getInstance();
				FileDataStore fStore = new FileDataStore();
				try {
					
					fStore.cacheUserCredentials(userCredentials.getUsername(), userCredentials.getPassword());
					 
				} catch (Exception ex) {
					showErrorDialog(ex.getMessage());
				}
			}
			if(PermissionValidator.checkIfUserHasPermissionForService(OMPlusConstants.WEB_SERVICE_QUERY_BUILDER))
				StatusAction.setStatus(OMPlusConstants.FULL_MODE);
			else
				StatusAction.setStatus(OMPlusConstants.REDUCED_MODE);
			WebServiceLoginAction.enableAction(false);
			WebServiceLogoutAction.enableAction(true);
			WebServiceLoginDialog.this.disposeFontsAndColors();
			btnClciked = OMPlusConstants.DIALOG_OK_CLICKED;
			WebServiceLoginDialog.this.mainCompositeShell.dispose();				
			//WebServiceLoginDialog.this.mainCompositeShell.close();
		}				
		else
		{
			MessageDialog.openInformation(WebServiceLoginDialog.this.mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, 
											"Credentials do not match.Please enter again.");
			WebServiceLoginDialog.this.usernameText.setText("");
			WebServiceLoginDialog.this.passwordText.setText("");
			WebServiceLoginDialog.this.usernameText.setFocus();				
		}
	}

	private void showErrorDialog(String message) 
	{
		MessageDialog.openError(mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, message);
		
	}
	
	

/////////////////////////////////// INNER class for Login button
	
	class WebServiceLoginListener implements SelectionListener
	{
		public WebServiceLoginListener()
		{
			
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// Auto-generated method stub
			System.out.println("TO IMPLEMENT: widgetSelected() in WEbServiceLoginListener."); 
			
		}

		public void widgetSelected(SelectionEvent e) 
		{
			String username = usernameText.getText();
			String password = passwordText.getText();
			if(username == null || username.trim().length()==0||
				password == null || password.trim().length()==0	)
			{
				MessageDialog.openInformation(null, OMPlusConstants.DIALOG_BOX_TITLE, 
											 "Credentials cannot be empty.Please enter again.");
				usernameText.setText("");
				passwordText.setText("");
				return;
			}
			
			//Ideally call the UserWebServiceCredential reset first because this is new login
			UserWebServiceCredentials.resetInstance();
			UserWebServiceCredentials.getInstance().setUsername(username);
			UserWebServiceCredentials.getInstance().setPassword(password);
			
			String address = Activator.getProxyAddress();
			int port = Activator.getProxyPort();
			//Connect to the webservice 
			boolean value = false;
			try {
				value = WebServiceConnector.connectToWebService(null, username, password,
										address, port);
			}
			/*catch (AxisFault ex) {
				String str = ex.toString();
				if(str.contains(UnknownHostException.class.getName())){
					showErrorDialog("No internet connection available.");
					return;
				}
				else if(str.equals("java.net.ConnectException: Connection timed out: connect")){
					showErrorDialog("Connection timed out: connect");
				}
				
				
			}*/catch (Exception ex) {
				String str = ex.getMessage();
//				if(str.contains(UnknownHostException.class.getName())){
					showErrorDialog(str);
					return;
//				}
//				value = false;
			}
			if(value)
			{
				if(rememberMeCheckBox.getSelection()){
					UserWebServiceCredentials userCredentials = UserWebServiceCredentials.getInstance();
					FileDataStore fStore = new FileDataStore();
					try {
						
						fStore.cacheUserCredentials(userCredentials.getUsername(), userCredentials.getPassword());
						
					} catch (Exception ex) {
						showErrorDialog(ex.getMessage());
					}
				}
				if(PermissionValidator.checkIfUserHasPermissionForService(OMPlusConstants.WEB_SERVICE_QUERY_BUILDER))
					StatusAction.setStatus(OMPlusConstants.FULL_MODE);
				else
					StatusAction.setStatus(OMPlusConstants.REDUCED_MODE);
				WebServiceLoginAction.enableAction(false);
				WebServiceLogoutAction.enableAction(true);
				WebServiceLoginDialog.this.disposeFontsAndColors();
				btnClciked = OMPlusConstants.DIALOG_OK_CLICKED;
				WebServiceLoginDialog.this.mainCompositeShell.dispose();				
				//WebServiceLoginDialog.this.mainCompositeShell.close();
			}				
			else
			{
				MessageDialog.openInformation(WebServiceLoginDialog.this.mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, 
												"Credentials do not match.Please enter again.");
				WebServiceLoginDialog.this.usernameText.setText("");
				WebServiceLoginDialog.this.passwordText.setText("");
				WebServiceLoginDialog.this.usernameText.setFocus();				
			}
		}

		private void showErrorDialog(String message) {
			MessageDialog.openError(mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, message);
			
		}
	}

	
}
