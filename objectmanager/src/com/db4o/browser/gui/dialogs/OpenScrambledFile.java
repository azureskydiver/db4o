/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.db4o.binding.verifier.IVerifier;
import com.swtworkbench.community.xswt.XSWT;

public class OpenScrambledFile extends Dialog {

    public OpenScrambledFile(Shell shell) {
        super(shell);
        setBlockOnOpen(true);
    }
    
    private IOpenScrambledFile pane;

    protected Control createDialogArea(Composite composite) {
        Composite holder = (Composite) super.createDialogArea(composite);
        
        pane = (IOpenScrambledFile) XSWT.createl(holder,
                "openScrambledFile.xswt", getClass(), IOpenScrambledFile.class);
        
        pane.getFileName().addVerifyListener(verifyFileName);
        pane.getBrowseButton().addSelectionListener(browseForFile);

        return holder;
    }
    
    protected void createButtonsForButtonBar(Composite arg0) {
        super.createButtonsForButtonBar(arg0);
        getOKButton().setEnabled(false);
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Open scrambled file");
    }
    
    private IVerifier fileNameVerifier = new IVerifier() {
        public boolean verifyFragment(String fragment) {
            return true;
        }

        public boolean verifyFullValue(String value) {
            File file = new File(value);
            if (file.isFile()) {
                return true;
            }
            return false;
        }

        public String getHint() {
            return "Please enter a legal path and file name";
        }
    };
    
    private VerifyListener verifyFileName = new VerifyListener() {
        public void verifyText(VerifyEvent e) {
            String currentText = pane.getFileName().getText();
            String newValue = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
            if (!fileNameVerifier.verifyFragment(newValue)) {
                e.doit = false;
                verifyEverything(currentText);
            } else {
                verifyEverything(newValue);
            }
        }
    };

    protected void verifyEverything(String fileName) {
        if (fileNameVerifier.verifyFullValue(fileName)) {
            getOKButton().setEnabled(true);
            pane.getHelpArea().setText("");
        } else {
            getOKButton().setEnabled(false);
            pane.getHelpArea().setText(fileNameVerifier.getHint());
        }
    }
    
    
    protected SelectionListener browseForFile = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(pane.getBrowseButton().getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[]{"*.yap", "*"});
            String file = dialog.open();
            if (file != null) {
                pane.getFileName().setText(file);
                verifyEverything(file);
            }
        }
    };
    
    private String fileName = "";
    private String password = "";
    
    protected void okPressed() {
        fileName = pane.getFileName().getText();
        password = pane.getPassword().getText();
        super.okPressed();
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }
    
}
