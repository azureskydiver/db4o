/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors.db4o;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Control;

import com.db4o.ObjectContainer;
import com.db4o.binding.CannotSaveException;
import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.binding.dataeditors.IPropertyEditor;
import com.db4o.binding.field.FieldController;
import com.db4o.binding.field.IFieldController;
import com.db4o.reflect.ext.RelaxedDuckType;

public class Db4oObject implements IObjectEditor {
    private ObjectContainer database;

    private Object input = null;
    private IDb4oBean inputBean = null;

    private static final int DEFAULT_REFRESH_DEPTH = 5;
    private int refreshDepth = DEFAULT_REFRESH_DEPTH;

    /**
     * @param database
     */
    public Db4oObject(ObjectContainer database) {
        this.database = database;
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#setInput(java.lang.Object)
     */
    public boolean setInput(Object input) {
        if (this.input != null && (!verifyAndSaveEditedFields() || !verifyAndSaveObject() || input == null)) {
            return false;
        }
        try {
            commit();
        } catch (CannotSaveException e) {
            throw new RuntimeException("Should be able to save if fields and object verify", e);
        }
        this.input = input;
        inputBean = (IDb4oBean) RelaxedDuckType.implement(IDb4oBean.class, input);
        refreshFieldsFromInput();
        return true;
    }

    private void refreshFieldsFromInput() {
        for (Iterator bindingIter = bindings.iterator(); bindingIter.hasNext();) {
            IFieldController controller = (IFieldController) bindingIter.next();
            try {
                controller.setInput(getProperty(controller.getPropertyName()));
            } catch (CannotSaveException e) {
                throw new RuntimeException("Should be able to save if fields and object verify", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Should be able to save if fields and object verify", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#getInput()
     */
    public Object getInput() {
        return input;
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#getProperty(java.lang.String)
     */
    public IPropertyEditor getProperty(String name) throws NoSuchMethodException {
        return Db4oBeanProperty.construct(getInput(), name, database);
    }
    
    private LinkedList bindings = new LinkedList();

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#getWidgetBinding(org.eclipse.swt.widgets.Control, java.lang.String)
     */
    public IFieldController bind(Control control, String propertyName) {
        ensureShellListener(control);
        
        IPropertyEditor propertyEditor;
        try {
            propertyEditor = getProperty(propertyName);
        } catch (NoSuchMethodException e) {
            return null;
        }
        
        IFieldController result = FieldController.construct(control, this, propertyEditor);
        
        if (result != null) {
            bindings.addLast(result);
        }
        return result;
    }

    private void ensureShellListener(Control control) {
        if (shellListener == null) {
            shellListener = new ShellAdapter() {
                public void shellClosed(ShellEvent e) {
                    if (!verifyAndSaveEditedFields()) {
                        e.doit = false;
                    }
                    if (!verifyAndSaveObject()) {
                        e.doit = false;
                    }
                }
            };
        }
        control.getShell().addShellListener(shellListener);
    }
    
    private ShellListener shellListener;

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#verifyEditedFields()
     */
    public boolean verifyAndSaveEditedFields() {
        for (Iterator bindingsIter = bindings.iterator(); bindingsIter.hasNext();) {
            IFieldController field = (IFieldController) bindingsIter.next();
            if (field.isDirty()) {
                if (!field.verify()) {
                    return false;
                }
                try {
                    field.save();
                } catch (CannotSaveException e) {
                    return false;
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#verifyObject()
     */
    public boolean verifyAndSaveObject() {
        /*
         * The return type for RelaxedDuckType is false for boolean types if
         * the method does not exist.  So we have to test explicitly here...
         */
        if (RelaxedDuckType.includes(input, "verifyObject", new Class[] {}) && !inputBean.verifyObject()) {
            return false;
        }
        database.set(input);
        return true;
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#commit()
     */
    public void commit() throws CannotSaveException {
        if (input == null) {
            return;
        }
        if (!verifyAndSaveEditedFields())
            throw new CannotSaveException("Unable to save edited fields");
        if (!verifyAndSaveObject())
            throw new CannotSaveException("Unable to save object");
        inputBean.commit();
        database.commit();
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#refresh()
     */
    public void refresh() {
        database.ext().refresh(input, refreshDepth);
        inputBean.refresh();
        refreshFieldsFromInput();
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#rollback()
     */
    public void rollback() {
        database.rollback();
        inputBean.rollback();
        refresh();
    }

    /* (non-Javadoc)
     * @see com.db4o.binding.dataeditors.IObjectEditor#delete()
     */
    public void delete() {
        inputBean.delete();
        database.delete(input);
    }

    /**
     * @return Returns the refreshDepth.
     */
    public int getRefreshDepth() {
        return refreshDepth;
    }
    

    /**
     * @param refreshDepth The refreshDepth to set.
     */
    public void setRefreshDepth(int refreshDepth) {
        this.refreshDepth = refreshDepth;
    }
    

}
