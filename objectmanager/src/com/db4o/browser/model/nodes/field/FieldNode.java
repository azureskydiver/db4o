/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.model.nodes.field;

import com.db4o.binding.converter.Converter;
import com.db4o.binding.converter.IConverter;
import com.db4o.browser.model.IDatabase;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.browser.model.nodes.NullNode;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.swtworkbench.community.xswt.metalogger.Logger;


/**
 * Class FieldNode.  The patriarch of the FieldNode hierarchy.  Implements
 * common functionality.
 * 
 * @author djo
 */
public class FieldNode implements IModelNode {

    protected String _fieldName;
	protected Object value;
	protected IDatabase _database;
    protected IModelNode delegate = null;
    protected ReflectClass _fieldType;
    private boolean showType;

	public FieldNode(String fieldName, ReflectClass fieldType,Object instance, IDatabase database) {
        _fieldName = fieldName;
        value = instance;
		_database = database;
        _fieldType=fieldType;

		if(value==null) {
			delegate=NullNode.INSTANCE;
			return;
		}
        ReflectClass clazz = database.reflector().forObject(value);
		delegate = new InstanceNode(value, database);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
		return delegate.hasChildren();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		return delegate.children();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
        String fieldDataType = "";
        if (showType) {
            fieldDataType = "(" + _fieldType.getName() + ") ";
        }
		return  fieldDataType // Show the data type
                + (_fieldName.equals("") ? delegate.getText()             // Show the field name
                        : _fieldName + ": " + delegate.getText());        // Show the actual value
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getName()
	 */
	public String getName() {
        String fieldDataType = "";
        if (showType) {
            fieldDataType = "(" + _fieldType.getName() + ") ";
        }
		return fieldDataType +_fieldName;
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getValueString()
	 */
	public String getValueString() {
		return (value==null ? "null" : value.toString());
	}

	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		FieldNode node=(FieldNode)obj;
		return value.equals(node.value);
	}
	
	public int hashCode() {
		return value.hashCode();
	}

    public static Object field(ReflectField field, Object instance) {
        try {
            field.setAccessible();
            return field.get(instance);
        } catch (Exception e) {
            Logger.log().error(e, "Unable to get the field contents");
            throw new IllegalStateException();
        }
    }

    public void setShowType(boolean showType) {
        this.showType = showType;
    }

    public boolean isEditable() {
        try {
            IConverter converter = Converter.get(_database.reflector().forObject(value).getName(),
                    _database.reflector().forClass(String.class).getName());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Object getEditValue() {
        return value;
    }

}
