/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.presentation;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.db4o.foundation.*;
import com.db4o.omplus.ui.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class LoginDialogUtil {

	public static final String RECENT_CONNECTION_COMBO_ID = LoginDialogUtil.class.getName() + "$recentConnectionCombo";

	// TODO: non-editable
	public static Combo recentConnectionCombo(Composite parent, final LoginPresentationModel model, final LoginPresentationModel.LoginMode mode) {
		final Combo combo = new Combo(parent, SWT.NONE);
		// FIXME not a unique id, obviously
		OMESWTUtil.assignWidgetId(combo, RECENT_CONNECTION_COMBO_ID);
		String[] items = model.recentConnections(mode);
		combo.setItems(items);
		combo.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				model.select(mode, combo.getSelectionIndex());
				combo.setToolTipText(combo.getItem(combo.getSelectionIndex()));
			}
		});
//		if(items.length > 0) {
//			model.select(mode, 0);
//			combo.setToolTipText(items[0]);
//		}
		return combo;
	}
	
	public static LoginButtonsPane createButtonsPane(Composite parent, Composite dialog, Composite relative, String openText, int offset, Closure4<Boolean> openAction) {
		LoginButtonsPane buttonComposite = new LoginButtonsPane(parent, dialog, openText, openAction);
		FormData data = new FormData();
		data.top = new FormAttachment(relative, offset);
		data.left = new FormAttachment(2,2);
		data.right = new FormAttachment(100, -offset);
		buttonComposite.setLayoutData(data);
		buttonComposite.setLayout(new FormLayout());
		return buttonComposite;
	}

}
