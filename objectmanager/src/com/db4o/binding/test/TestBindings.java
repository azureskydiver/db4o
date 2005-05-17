/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.test;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.binding.dataeditors.ObjectEditorFactory;
import com.db4o.binding.dataeditors.db4o.Db4oObjectEditorFactory;
import com.db4o.browser.gui.standalone.IControlFactory;
import com.db4o.browser.gui.standalone.SWTProgram;
import com.swtworkbench.community.xswt.XSWT;

public class TestBindings implements IControlFactory {

    private ObjectContainer database;

    public void createContents(Composite parent) {
        database = Db4o.openFile("TestBindings.yap");
        Person person = (Person) database.get(Person.class).next();
        if (person == null) {
            person = new Person();
        }
        
        parent.setLayout(new GridLayout());
        ITestBindings ui = (ITestBindings) XSWT.createl(parent, "TestBindings.xswt", getClass(), ITestBindings.class);
        
        ObjectEditorFactory.factory = new Db4oObjectEditorFactory(database);
        IObjectEditor personObjectEditor = ObjectEditorFactory.construct(person);
        personObjectEditor.bind(ui.getName(), "Name");
        personObjectEditor.bind(ui.getAge(), "Age");
    }
    
    private void close() {
        database.close();
    }

    public static void main(String[] args) {
        TestBindings program = new TestBindings();
        SWTProgram.runWithLog(program);
        program.close();
    }

}
