/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.presentation;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;

import com.db4o.config.*;
import com.db4o.omplus.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class CustomConfigPane extends Composite {

	private LocalPresentationModel model;
	
	public CustomConfigPane(Shell dialog, Composite parent, LocalPresentationModel model) {
		super(parent, SWT.NONE);
		this.model = model;
		createContents(dialog, parent);
	}

	private void createContents(Shell dialog, final Composite parent) {
		Label jarLabel = label("Jars:");
		Label confLabel = label("Configurators:");
		final List jarList = new List (this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		final List confList = new List (this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		Button addButton = button("Add");
		Button removeButton = button("Remove");
		Button okButton = button("OK");
		Button cancelButton = button("Cancel");
		
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				FileDialog fileChooser = new FileDialog(parent.getShell(), SWT.OPEN);
				fileChooser.setFilterExtensions(new String[] { "*.jar" });
				fileChooser.setFilterNames(new String[] { "Jar Files (*.jar)" });
				String jarPath = fileChooser.open();
				// TODO duplicates
				if(jarPath != null){
					jarList.add(jarPath);
					updateConfiguratorList(jarList, confList);
				}
			}
		});		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String[] selectedItems = jarList.getSelection();
				if(selectedItems.length == 0) {
					return;
				}
				for (String item : selectedItems) {
					jarList.remove(item);
				}
				updateConfiguratorList(jarList, confList);
			}
		});		

		
		GridLayoutFactory.swtDefaults().numColumns(4).equalWidth(true).applyTo(this);
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(jarLabel);
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(confLabel);
		GridDataFactory.swtDefaults().minSize(400, 100).span(2, 1).grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(jarList);
		GridDataFactory.swtDefaults().minSize(400, 100).span(2, 1).grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(confList);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(addButton);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(removeButton);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(okButton);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(cancelButton);
	}

	public void updateConfiguratorList(List jarList, List configList) {
		configList.removeAll();
		java.util.List<String> configNames = retrieveConfiguratorList(jarList.getItems());
		System.out.println(configNames);
		for (String configName : configNames) {
			configList.add(configName);
		}
	}
	
	private java.util.List<String> retrieveConfiguratorList(String[] jarPaths) {
		java.util.List<String> configNames = new ArrayList<String>();
		try {
			URL[] urls = new URL[jarPaths.length];
			for (int jarIdx = 0; jarIdx < jarPaths.length; jarIdx++) {
				urls[jarIdx] = new File(jarPaths[jarIdx]).toURI().toURL();
			}
			URLClassLoader cl = new URLClassLoader(urls, Activator.class.getClassLoader());
			Iterator<EmbeddedConfigurationItem> ps = sun.misc.Service.providers(EmbeddedConfigurationItem.class, cl);
			while(ps.hasNext()) {
				EmbeddedConfigurationItem configurator = ps.next();
				configNames.add(configurator.getClass().getName());
			}
			Collections.sort(configNames);
		} 
		catch (Exception exc) {
			exc.printStackTrace();
		}
		return configNames;
	}

	protected void addJarPath(String jarPath) {
		System.out.println(jarPath);
	}

	private Label label(String text) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);
		return label;
	}
	
	private Button button(String text) {
		Button button = new Button(this, SWT.PUSH);
		button.setText(text);
		return button;
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout (new GridLayout());
		new CustomConfigPane(shell, shell, null);
		shell.pack();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
