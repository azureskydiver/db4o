/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.field.internal;


import com.db4o.binding.CannotSaveException;
import com.db4o.binding.field.IFieldController;
import com.db4o.binding.reflect.IProperty;
import com.db4o.binding.reflect.ReflectedMethod;

/**
 * TextFieldController. An IFieldController that can bind any object with a
 * setText (and optionally a getText) method.
 * 
 * TODO:
 * 
 * I'm nearly at the point where I can implement this class. The only thing
 * remaining right now is to implement IProperty for db4object's idea of an
 * IProperty--namely a field that even can be a private field. I also need to
 * implement an IPropertyFactory to construct these IProperty objects.
 * 
 * @author djo
 */
public class TextFieldController implements IFieldController {
    private Object control;
    private Object input;
    private IProperty property;
    
	private ReflectedMethod getText;
    private ReflectedMethod setText;

    public TextFieldController(Object control, IProperty property) {
        getText = new ReflectedMethod(control, "getText", new Class[] {});
        setText = new ReflectedMethod(control, "setText", new Class[] {String.class});
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#setPropertyName(java.lang.String)
	 */
	public void setPropertyName(String name) {

	}
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#getPropertyName()
	 */
	public String getPropertyName() {
		// TODO Auto-generated method stub
		return null;
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#isDirty()
	 */
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		// TODO Auto-generated method stub

	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.binding.IWidgetBinding#undo()
     */
    public void undo() {
        // TODO Auto-generated method stub

    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.binding.field.IFieldController#save()
	 */
	public void save() throws CannotSaveException {
		// TODO Auto-generated method stub

	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		// TODO Auto-generated method stub

	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.binding.IWidgetBinding#getInput()
     */
    public Object getInput() {
        // TODO Auto-generated method stub
        return null;
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.IWidgetBinding#verify()
	 */
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}
}
