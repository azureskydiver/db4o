/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.db4o.binding.converter.Converter;
import com.db4o.binding.converter.IConverter;
import com.db4o.binding.verifiers.IntVerifier;
import com.swtworkbench.community.xswt.XSWT;

public class SelectServer extends Dialog {

    public SelectServer(Shell shell) {
        super(shell);
        setBlockOnOpen(true);
    }
    
    private ISelectServerPane pane;

    protected Control createDialogArea(Composite composite) {
        Composite holder = (Composite) super.createDialogArea(composite);
        
        pane = (ISelectServerPane) XSWT.createl(holder,
                "selectServerPane.xswt", getClass(), ISelectServerPane.class);

        pane.getHostName().addVerifyListener(verifyHostName);
        pane.getHostPort().addVerifyListener(verifyPort);
        
        return holder;
    }
    
    protected void createButtonsForButtonBar(Composite arg0) {
        super.createButtonsForButtonBar(arg0);
        getOKButton().setEnabled(false);
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Connect to db4o Server");
    }
    
    private IntVerifier portVerifier = new IntVerifier();
    
    private VerifyListener verifyPort = new VerifyListener() {
        public void verifyText(VerifyEvent e) {
            String currentText = pane.getHostPort().getText();
            String newValue = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
            if (!portVerifier.verifyFragment(newValue)) {
                e.doit = false;
                verifyEverything(currentText);
            } else {
                verifyEverything(newValue);
            }
        }
    };

    private boolean hostNameIsValid = false;
    
    private VerifyListener verifyHostName = new VerifyListener() {
        public void verifyText(VerifyEvent e) {
            String currentText = pane.getHostName().getText();
            String newValue = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
            if (newValue.length() > 0) {
                hostNameIsValid = true;
            } else {
                hostNameIsValid = false;
            }
            verifyEverything(pane.getHostPort().getText());
        }
    };
    
    protected void verifyEverything(String hostPort) {
        if (hostNameIsValid && portVerifier.verifyFullValue(hostPort)) {
            getOKButton().setEnabled(true);
        } else {
            getOKButton().setEnabled(false);
        }
    }
    
    private String hostName = "";
    private int port;
    private String username = "";
    private String password = "";
    
    protected void okPressed() {
        hostName = pane.getHostName().getText();
        IConverter converter = Converter.get(String.class, Integer.TYPE);
        port = ((Integer)converter.convert(pane.getHostPort().getText())).intValue();
        username = pane.getUsername().getText();
        password = pane.getPassword().getText();
        super.okPressed();
    }

    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }
    
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }

    public String getUser() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
}
