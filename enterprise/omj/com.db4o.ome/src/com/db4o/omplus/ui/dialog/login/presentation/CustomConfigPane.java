/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.jface.layout.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.config.*;
import com.db4o.omplus.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.CustomConfigModel.CustomConfigListener;

public class CustomConfigPane extends Composite {

	private CustomConfigModel model;
	
	public CustomConfigPane(Shell dialog, Composite parent, CustomConfigModel model) {
		super(parent, SWT.NONE);
		this.model = model;
		createContents(dialog, parent);
	}

	private void createContents(final Shell dialog, final Composite parent) {
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
				if(jarPath != null){
					model.addJarPaths(jarPath);
				}
			}
		});		
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String[] selectedItems = jarList.getSelection();
				if(selectedItems.length > 0) {
					model.removeJarPaths(selectedItems);
				}
			}
		});		
		cancelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				dialog.close();
			}
		});
		
		model.addListener(new CustomConfigListener() {
			public void customConfig(String[] jarPaths, String[] configClassNames, String[] selectedConfigNames) {
				jarList.setItems(jarPaths);
				confList.setItems(configClassNames);
				confList.setSelection(selectedConfigNames);
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
		ErrorMessageSink errSink = new ErrorMessageSink() {
			public void showError(String msg) {
				System.err.println(msg);
			}
			
			public void showExc(String msg, Throwable exc) {
				System.err.println("ERR: " + msg);
				exc.printStackTrace();
			}

			public void logWarning(String msg, Throwable exc) {
				System.err.println("WARN: " + msg);
				exc.printStackTrace();
			}
		};
		CustomConfigSink sink = new CustomConfigSink() {
			public void customConfig(String[] jarPaths, String[] configClassNames) {
				System.out.println(java.util.Arrays.toString(jarPaths));
				System.out.println(java.util.Arrays.toString(configClassNames));
			}
		};
		new CustomConfigPane(shell, shell, new CustomConfigModel(sink, new SPIConfiguratorExtractor(EmbeddedConfigurationItem.class), new ErrorMessageHandler(errSink)));
		shell.pack();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
