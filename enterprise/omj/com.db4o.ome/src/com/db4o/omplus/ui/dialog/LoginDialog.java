package com.db4o.omplus.ui.dialog;

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.db4o.omplus.connection.ConnectionParams;
import com.db4o.omplus.connection.DbConnect;
import com.db4o.omplus.connection.FileConnectionParams;
import com.db4o.omplus.connection.RecentConnectionList;
import com.db4o.omplus.connection.RemoteConnectionParams;
import com.db4o.omplus.datalayer.ImageUtility;
import com.db4o.omplus.datalayer.OMPlusConstants;
import com.db4o.omplus.ui.OMPlusPerspective;
import com.db4o.omplus.ui.ViewerManager;
import com.db4o.omplus.ui.actions.BackupDBAction;
import com.db4o.omplus.ui.actions.ConnectToDBAction;
import com.db4o.omplus.ui.actions.DefragDBAction;


public class LoginDialog
{
	private static final String LOCAL_DIALOG_TITLE = "Connect to db4o database";

	private static final String REMOTE_DIALOG_TITLE = "Connect to db4o server";

	private final String GENERIC_OBJ = "com.db4o.reflect.generic.GenericObject";
	
	private final String VERSION_UPDATE_TXT = "Old database file format detected. Would you like to upgrade ?";
	
	private Shell mainCompositeShell;
	private Shell parentShell;
	
	private Button localConnection;
	private Button remoteConnection;
	
	private Composite innerComposite;	
	private Composite buttonComposite;
	
	//Local connections
	private Label recentConnectionLabel = null;	
	private Combo recentConnectionCombo = null;
	private Label newConnectionLabel = null;
	private Text newConnectionText = null;	
	private Button browseBtn =null;

	//Remote conditions
	private Label recentConnectionRemoteLabel;
	private Label passwordLabel;
	private Label usernameLabel;
	private Label portLabel;
	private Label hostLabel;
	private Combo recentConnectionRemoteCombo;
	private Text hostText;
	private Text portText;
	private Text usernameText;
	private Text passwordText;
	
	private Button cancelBtn;
	private Button connectBtn;
	private Button openBtn;

	
	private RecentConnectionList recentConnList;

	public LoginDialog(Shell parentShell) 
	{
		this.parentShell = parentShell;
		mainCompositeShell = new Shell(parentShell,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		mainCompositeShell.setText("Connection Info");
		recentConnList = new RecentConnectionList();
		//To make the dilaog appear at center
		Rectangle rectangle = parentShell.getBounds(); 
		mainCompositeShell.setBounds(rectangle.width/3, rectangle.height/3, 430, 230);
		mainCompositeShell.setImage(ImageUtility.getImage(OMPlusConstants.DB4O_WIND_ICON));
		createContents();
	}
	
	public void open()
	{
		mainCompositeShell.open();
	}
	
	protected Control createContents() 
	{
		mainCompositeShell.setLayout(new FormLayout());
		 
		localConnection = new Button(mainCompositeShell, SWT.RADIO);
		localConnection.setText("Local");
		
		remoteConnection = new Button(mainCompositeShell, SWT.RADIO);
		remoteConnection.setText("Remote");
		
		innerComposite = new Composite(mainCompositeShell, SWT.BORDER);
		buttonComposite = new Composite(mainCompositeShell, SWT.NONE); 
		setLayoutForMainComposite();
	
		if(!recentConnList.showRemoteConn()){
			localConnection.setSelection(true);
			resetInnerComposite(0);	
		}
		else {
			remoteConnection.setSelection(true);
			resetInnerComposite(1);	
		}
				
		 // Listener for local button.
		localConnection.addSelectionListener(new SelectionListener() {
	
			public void widgetDefaultSelected(SelectionEvent e) {
				// Auto-generated method stub
				
			}
	
			public void widgetSelected(SelectionEvent e) 
			{
				resetInnerComposite(0);				
			}
			 
		 });		 
		 
		
		remoteConnection.addSelectionListener(new SelectionListener() {
	
				public void widgetDefaultSelected(SelectionEvent e) {
					// Auto-generated method stub
					
				}
	
				public void widgetSelected(SelectionEvent e) 
				{
					resetInnerComposite(1);				
				}				 
			 });
		
		
		
		 
		
		mainCompositeShell.addShellListener(new ShellListener() {
	
			public void shellActivated(ShellEvent e) {
				// Auto-generated method stub
				
			}
	
			public void shellClosed(ShellEvent e) {
				// Auto-generated method stub
//				System.out.println("Handle this.....");
				
			}
	
			public void shellDeactivated(ShellEvent e) {
				// Auto-generated method stub
				
			}
	
			public void shellDeiconified(ShellEvent e) {
				// Auto-generated method stub
				
			}
	
			public void shellIconified(ShellEvent e) {
				// Auto-generated method stub
				
			}
			
		});
		
		return mainCompositeShell;
	 }
	 
	
	/**
	 * Set the layout for Main composite
	 */
	 private void setLayoutForMainComposite()
	 {
		 int main_top = 10;
		FormData data = new FormData();
		data.top = new FormAttachment(2,main_top);
		data.left = new FormAttachment(2,2);
		data.right = new FormAttachment(20);
		localConnection.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(2,main_top);
		data.left = new FormAttachment(localConnection,2);
		data.right = new FormAttachment(40);
		remoteConnection.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(localConnection, main_top);
		data.left = new FormAttachment(2,2);
	/*	data.width = 300;
		data.height = 100; */
		data.right = new FormAttachment(100, -10);
		data.bottom = new FormAttachment(80, -5);
		innerComposite.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(innerComposite, main_top);
		data.left = new FormAttachment(2,2);
	/*	data.width = 300;
		data.height = 50;*/
		data.right = new FormAttachment(100, -main_top);
		buttonComposite.setLayoutData(data);
		
		innerComposite.setLayout(new FormLayout());
		buttonComposite.setLayout(new FormLayout());
//		mainCompositeShell.setSize(430, 300);
		
		
	}

	 /**
	  * Reset the inner composite's child components depending upon "Local"/"Remote" is selected 
	  * 
	  * @param index
	  */
	private void resetInnerComposite(int index)
	 {
		 for (Control control : innerComposite.getChildren()) 
		 {
			control.dispose();
		}
		 
		 if(localConnection.getSelection())
		 {
			 setInnerCompositeForLocalConnections();
			 setlayoutForInnerCompositeLocal();
			 addButtonsForLocal();
			 mainCompositeShell.setText(LOCAL_DIALOG_TITLE);
			 if(recentConnectionCombo.getSelectionIndex()>=0)
				 recentConnectionCombo.setToolTipText(recentConnectionCombo.getItem
						 					(recentConnectionCombo.getSelectionIndex()));
			 if(newConnectionText.getText()!=null && newConnectionText.getText().trim().length()>0)
				 newConnectionText.setToolTipText(newConnectionText.getText());
		 }
		 else
		 {
			 setInnerCompositeForRemoteConnections();
			 setLayoutForInnerCompositeRemote();
			 addButtonsForRemote();
			 mainCompositeShell.setText(REMOTE_DIALOG_TITLE);
			 if(recentConnectionRemoteCombo.getSelectionIndex()>=0)
				 recentConnectionRemoteCombo.setToolTipText(recentConnectionRemoteCombo.getItem
						 					(recentConnectionRemoteCombo.getSelectionIndex()));
		 }
		 innerComposite.layout(true);
		 buttonComposite.layout(true);
	 }
	
	/**
	 * Set components for Local connections
	 */
	private void setInnerCompositeForLocalConnections()
	{
		String str0 = "Recent Connections: ";
		String str1 = "New Connections:    ";	
		
		recentConnectionLabel = new Label(innerComposite, SWT.NONE);
		recentConnectionLabel.setText(str0);
		
		recentConnectionCombo = new Combo(innerComposite, SWT.NONE);
		recentConnectionCombo.setItems(getRecentConnections(0));
		recentConnectionCombo.select(0);
		
		addListenerForLocalCombo();
	
		newConnectionLabel = new Label(innerComposite, SWT.NONE);
		newConnectionLabel.setText(str1);
		
		newConnectionText = new Text(innerComposite, SWT.BORDER);
		// Check if this is rightway. when the local mode shows up first
		if(recentConnectionCombo.getText() != null)
			newConnectionText.setText(recentConnectionCombo.getText());
		
		browseBtn = new Button(innerComposite, SWT.PUSH);
		//browseBtn.setText("Browse");
		browseBtn.setImage(ImageUtility.getImage(OMPlusConstants.BROWSE_ICON));
		browseBtn.setToolTipText("Browse");
		
		//btn.addSelectionListener
		browseBtn.addListener(SWT.MouseUp, new Listener() {

			public void handleEvent(Event event) {
				FileDialog fileChooser = new FileDialog(mainCompositeShell.getShell(), SWT.OPEN);
				String dbfile = fileChooser.open();
				if(dbfile != null){
					newConnectionText.setText(dbfile);
					newConnectionText.setToolTipText(dbfile);
				}
					
			}
			
		});		
	}
	
	private void addListenerForLocalCombo() {
		recentConnectionCombo.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			
			}

			public void widgetSelected(SelectionEvent e) 
			{
				String str = recentConnectionCombo.getText();
				if(str != null && str.trim().length() > 0)
				{
					newConnectionText.setText(str);
					
					//Change the tool tips
					if(recentConnectionCombo.getSelectionIndex()>=0)
						 recentConnectionCombo.setToolTipText(recentConnectionCombo.getItem
								 					(recentConnectionCombo.getSelectionIndex()));
					 if(newConnectionText.getText()!=null && newConnectionText.getText().trim().length()>0)
						 newConnectionText.setToolTipText(newConnectionText.getText());
				}
			}
			
		});
		
	}
	
	private String[] getRecentConnections(int type) {
		ArrayList<ConnectionParams> connections = null;
		if(type == 0)
			connections = recentConnList.getRecentFileConnections();
		else
			connections = recentConnList.getRecentRemoteConnections();
		int size = 0;
		if(connections != null)
			size = connections.size();
		String [] connPaths = new String[size];
		if(size != 0){
			int count = size - 1;
			ListIterator<ConnectionParams> iterator = connections.listIterator();
			while(iterator.hasNext()){
				connPaths[count--] = ((ConnectionParams)iterator.next()).getPath(); 
			}
		}
		return connPaths;
	}

	/**
	 * Set the layout for inner composite for local connections
	 */
	private void setlayoutForInnerCompositeLocal()
	{
		FormData data = new FormData();
		data.top = new FormAttachment(2,21);
		data.left = new FormAttachment(2,2);
		data.width = 110;
		recentConnectionLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(2,19);
		data.left = new FormAttachment(recentConnectionLabel,1);
//		data.width = 145;
		data.right = new FormAttachment(100, -10);
		recentConnectionCombo.setLayoutData(data);
				
		data = new FormData();
		data.top = new FormAttachment(recentConnectionLabel,27);
		data.left = new FormAttachment(2,2);
		data.width = 110;
		newConnectionLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(recentConnectionLabel,25);
		data.left = new FormAttachment(newConnectionLabel,1);
		data.right = new FormAttachment(browseBtn, -1);
//		data.width = 140;
		newConnectionText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(recentConnectionLabel,24);
//		data.left = new FormAttachment(newConnectionText,2);
		data.right = new FormAttachment(100, -9);
		data.left = new FormAttachment(92);
		data.height = 21;
//		data.width = 23;
		browseBtn.setLayoutData(data);
		
		innerComposite.layout(true);
		
	}
	
	
	
	/**
	 * Set the components for inner composite for remote connection
	 */
	private void setInnerCompositeForRemoteConnections()
	{
		recentConnectionRemoteLabel = new Label(innerComposite, SWT.NONE);
		recentConnectionRemoteLabel.setText("Recent Connections: ");
		
		recentConnectionRemoteCombo = new Combo(innerComposite, SWT.DROP_DOWN|SWT.READ_ONLY);
		recentConnectionRemoteCombo.setItems(getRecentConnections(1));
		recentConnectionRemoteCombo.select(0);
			
		addListenerForRemoteCombo();
		
		hostLabel = new Label(innerComposite, SWT.NONE);
		hostLabel.setText("Hostname: ");
		
		hostText  = new Text(innerComposite, SWT.BORDER);
		
		
		portLabel = new Label(innerComposite, SWT.NONE);
		portLabel.setText("Port: ");
		
		portText  = new Text(innerComposite, SWT.BORDER);
		
		
		usernameLabel = new Label(innerComposite, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameText  = new Text(innerComposite, SWT.BORDER);
		
		passwordLabel = new Label(innerComposite, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordText  = new Text(innerComposite, SWT.BORDER|SWT.PASSWORD);
	
		if(recentConnectionRemoteCombo.getText() != null)
			setComponentText(recentConnectionRemoteCombo.getText());
	}

	private boolean validationForPort() {
		boolean msgShown = false;
		String portStr = portText.getText();
		if(portStr != null && portStr.trim().length() > 0) {
			try{
				int port = new Integer(portStr).intValue();
				if(port <= 65536)
					return true;
			}catch(NumberFormatException ex){
				msgShown = true;
				showErrorMsg("Enter a valid port number ( < 65536).");
			}
		}
		if(!msgShown)
			showErrorMsg("Enter a valid port number ( < 65536).");
		portText.setText("");
		return false;
	}
	
	private void addListenerForRemoteCombo() {
		recentConnectionRemoteCombo.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			
			}

			public void widgetSelected(SelectionEvent e) 
			{
				String str = recentConnectionRemoteCombo.getText();
				if(str != null && str.trim().length() > 0)
				{	
					setComponentText(str);
					
					//Change the tool tip
					 if(recentConnectionRemoteCombo.getSelectionIndex()>=0)
						 recentConnectionRemoteCombo.setToolTipText(recentConnectionRemoteCombo.getItem
								 					(recentConnectionRemoteCombo.getSelectionIndex()));
				}
			}
			
		});
	}

	private void setComponentText(String text) {
		// TODO hosttext, porttext, usernameText
		if(text != null && text.trim().length() > 0){
			String []strArray = text.split(OMPlusConstants.REGEX);
			int length = strArray.length;
			hostText.setText(strArray[0]);
			portText.setText(strArray[1]);
			if(length > 2)
				usernameText.setText(strArray[2]);
			passwordText.setText("");
		}
	}

	/**
	 * Set the layout for inner composite for remote connection
	 */
	private void setLayoutForInnerCompositeRemote()
	{
		int second_row = 15;
		int third_row = 15;
		
		FormData data = new FormData();
		data.top = new FormAttachment(3,10);
		data.left = new FormAttachment(2,2);
		data.right = new FormAttachment(30);
		recentConnectionRemoteLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(3,8);
		data.left = new FormAttachment(recentConnectionRemoteLabel,7);
		data.right = new FormAttachment(100, -10);
		recentConnectionRemoteCombo.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(recentConnectionRemoteCombo,second_row+2);
		data.left = new FormAttachment(2,2);
		data.width = 50;
		hostLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(recentConnectionRemoteCombo,second_row);
		data.left = new FormAttachment(hostLabel,5);
		data.right = new FormAttachment(portLabel, -10);
		hostText.setLayoutData(data);
				
		data = new FormData();
		data.top = new FormAttachment(recentConnectionRemoteCombo,second_row+2);
		data.left = new FormAttachment(68, 3);
		data.width = 35;
		portLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(recentConnectionRemoteCombo,second_row);
		data.left = new FormAttachment(portLabel);
		data.right = new FormAttachment(100, -10);
		portText.setLayoutData(data);
		
		
		data = new FormData();
		data.top = new FormAttachment(hostLabel,third_row+2);
		data.left = new FormAttachment(2,2);
		data.width = 50;
		usernameLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(hostLabel,third_row);
		data.left = new FormAttachment(usernameLabel,5);
		data.right = new FormAttachment(passwordLabel, -10);
		usernameText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(hostLabel,third_row+2);
		data.left = new FormAttachment(52);
		data.width = 50;
		passwordLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(hostLabel,third_row);
		data.left = new FormAttachment(passwordLabel,5);
		data.right = new FormAttachment(100, -10);
		passwordText.setLayoutData(data);		
		
	}
	
	/**
	 * add buttons for remote connection
	 */
	private void addButtonsForRemote()
	{
		for (Control control : buttonComposite.getChildren()) 
		{
			control.dispose();
		}
		
		connectBtn = new Button(buttonComposite, SWT.PUSH);
		connectBtn.setText("Connect");
		addListenerForConnect();
		
		cancelBtn = new Button(buttonComposite, SWT.PUSH);
		cancelBtn.setText("Cancel");
		cancelBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				mainCompositeShell.dispose();
			}
		});
			
		buttonComposite.setLayout(new FormLayout());
		
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(60);
		data.right = new FormAttachment(cancelBtn , -5);
		connectBtn.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(81);
		data.right = new FormAttachment(100);
		cancelBtn.setLayoutData(data);						
	}
	
	private void addListenerForConnect() {
		connectBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				String host = hostText.getText();
				String port = portText.getText();
				String user = usernameText.getText();
				String pwd = passwordText.getText();
				if( host == null || port == null || user == null || pwd == null ||
						host.trim().length() == 0 || port.trim().length() == 0 ||
						user.trim().length()== 0 || pwd.trim().length()== 0)
					showErrorMsg("Fields cannot be empty.");
				else{
					boolean connected = false;
					if(validationForPort()){
						RemoteConnectionParams remote = new RemoteConnectionParams(
															host, port, user, pwd);
						connected = openDBConnection(remote);
						if(connected) {
							recentConnList.addNewConnection(remote);
							showPerspective();
							mainCompositeShell.dispose();					
						}
					}
				}
				
			}
		});
	}

	/**
	 * add buttons for local connection
	 */
	private void addButtonsForLocal()
	{
//		int btn_width = 50;
		for (Control control : buttonComposite.getChildren()) 
		{
			control.dispose();
		}
		
		openBtn = new Button(buttonComposite, SWT.PUSH);
		openBtn.setText("Open");
		
		cancelBtn = new Button(buttonComposite, SWT.PUSH);
		cancelBtn.setText("Cancel");
		
		openBtn.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e)
			{
				String connStr = newConnectionText.getText();
					boolean connected = false;
					if(connStr != null && connStr.trim().length() > 0){
						FileConnectionParams file = new FileConnectionParams(connStr);
						connected = openDBConnection(file);
						if(connected){
							recentConnList.addNewConnection(file);
							showPerspective();
						}
					}
					if(connected)
						mainCompositeShell.dispose();
			}
		});
		
		cancelBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				mainCompositeShell.dispose();
			}
		});
		
		//Set Layout
		buttonComposite.setLayout(new FormLayout());

		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(60);
		data.right = new FormAttachment(cancelBtn , -5);
		openBtn.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(81);
		data.right = new FormAttachment(100);
		cancelBtn.setLayoutData(data);						
	}
	
	private boolean openDBConnection(ConnectionParams params) {
		// TODO: display error msg for filelocked, old verion db etc
		try{
			DbConnect connect = new DbConnect();
			if(params instanceof FileConnectionParams)
				connect.connectToFile((FileConnectionParams)params);
			else
				connect.connectToServer((RemoteConnectionParams)params);
		}catch(ClassCastException ex){
			String msg = ex.getMessage();
			if(msg.equals(GENERIC_OBJ))
				msg = "Couldn't open .NET database in OME eclipse plugin";
			showErrorMsg(msg);
			return false;
		}catch(ArrayIndexOutOfBoundsException ex){
			// TODO: Use helper to create Msg Dialog for msgs
			String msg = "ArrayIndexOutOfBoundsException thrown while opening the database";
			showErrorMsg(msg);
			return false;
		}catch(Exception ex){
			String message = ex.getMessage();
			if(message.equals(DbConnect.OLD_FORMAT)){
				return checkForUpdate(params);
			}
			showErrorMsg("Could not open the database. " + message);
			return false;
		}
		enableDBMaintenanceActions(true);
		parentShell.setText(params.getPath());
		return true;
	}
	
	private boolean checkForUpdate(ConnectionParams params) {
		boolean allowUpdate = MessageDialog.openQuestion(mainCompositeShell
				, OMPlusConstants.DIALOG_BOX_TITLE, VERSION_UPDATE_TXT);
		if(allowUpdate) {
			params.configureUpdates();
			return openDBConnection(params);
		}
		return false;
	}

	private void showErrorMsg(String msg){
		MessageDialog.openError(mainCompositeShell, OMPlusConstants.DIALOG_BOX_TITLE, msg);
	}
	
	
	private void enableDBMaintenanceActions(boolean enabled) {
		BackupDBAction.enableAction(enabled);
		DefragDBAction.enableAction(enabled);
		if(enabled)
			ConnectToDBAction.setStatus("Disconnect");
	}

	private void showPerspective() 
	{
		
		try {
			//Show the perspective always else views not arranged as needed
			PlatformUI.getWorkbench().showPerspective(OMPlusPerspective.ID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			ViewerManager.resetAllViewsToInitialState();
			
		} catch (WorkbenchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	
}
