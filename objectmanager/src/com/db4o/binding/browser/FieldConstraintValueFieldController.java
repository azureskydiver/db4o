/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.browser;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ve.sweet.CannotSaveException;
import org.eclipse.ve.sweet.converter.IConverter;
import org.eclipse.ve.sweet.hinthandler.DelegatingHintHandler;
import org.eclipse.ve.sweet.hinthandler.IHintHandler;
import org.eclipse.ve.sweet.objectviewer.IPropertyEditor;
import org.eclipse.ve.sweet.validator.IValidator;

import com.db4o.browser.gui.standalone.StandaloneBrowser;
import com.db4o.browser.gui.standalone.StatusBar;
import com.db4o.browser.model.IDatabase;
import com.db4o.browser.query.model.FieldConstraint;

public class FieldConstraintValueFieldController extends FieldController {
    
    private FieldConstraint constraint;
    private IDatabase database;

    private Text ui;
    
    private boolean dirty = false;
    
    private Object input;

    private IConverter converter2String;
    private IConverter converter2Value;

    private IValidator validator;
    private DelegatingHintHandler hintHandler = new DelegatingHintHandler();

    public FieldConstraintValueFieldController(Text ui, FieldConstraint constraint, IDatabase database) {
        super(database);
        this.ui = ui;
        this.constraint = constraint;
        this.database = database;
        converter2String = get(constraint.field.getType(), c(String.class));
        converter2Value = get(c(String.class), constraint.field.getType());
        validator = get(constraint.field.getType());
        input = constraint.value;
        initControl();
        ui.addVerifyListener(verifyListener);
        ui.addFocusListener(focusListener);
    }

    private void initControl() {
        final String converted = input == null ? "" : (String)converter2String.convert(input);
        ui.setText(converted);
    }
    
    // Not used in this implementation

    public void setInput(IPropertyEditor input) throws CannotSaveException {
        
    }

    public IPropertyEditor getInput() {
        return null;
    }

    public String getPropertyName() {
        return "Value";
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void undo() {
        if (dirty) {
            initControl();
            dirty = false;
        }
    }

    public void save() throws CannotSaveException {
        if ("".equals(ui.getText())) {
            input = null;
            constraint.value = input;
            dirty = false;
            return;
        }
        if (validate() != null) {
            throw new CannotSaveException("Data value does not pass validation tests");
        }
        input = converter2Value.convert(ui.getText());
        constraint.value = input;
        dirty = false;
    }

    public String validate() {
        return validator.isValid(ui.getText());
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ve.sweet.fieldviewer.IFieldViewer#setHintHandler(org.eclipse.ve.sweet.hinthandler.IHintHandler)
     */
    public void setHintHandler(IHintHandler hintHandler) {
        this.hintHandler .delegate = hintHandler;
    }

    protected void comeBackHerePlease() {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                ui.setFocus();
            }
        });
    }

    private StatusBar getStatusBar() {
        StatusBar statusBar = (StatusBar) ui.getShell().getData(StandaloneBrowser.STATUS_BAR);
        return statusBar;
    }

    private VerifyListener verifyListener = new VerifyListener() {
        public void verifyText(VerifyEvent e) {
            String currentText = ui.getText();
            String newValue = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
            String error = validator.isValidPartialInput(newValue);
            if (error != null) {
                e.doit = false;
                getStatusBar().setMessage(error);
            } else {
                dirty = true;
                getStatusBar().clearMessage();
            }
        }
    };

    private FocusListener focusListener = new FocusAdapter() {
        public void focusLost(FocusEvent e) {
            if (dirty) {
                try {
                    save();
                    getStatusBar().clearMessage();
                } catch (CannotSaveException e1) {
                    comeBackHerePlease();
                    getStatusBar().setMessage(validator.getHint());
                }
            }
        }
    };

}
