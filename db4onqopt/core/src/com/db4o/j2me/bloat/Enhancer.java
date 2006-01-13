package com.db4o.j2me.bloat;

import java.io.File;

import EDU.purdue.cs.bloat.context.PersistentBloatContext;
import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorContext;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Label;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassFileLoader;

public class Enhancer {
	protected  ClassEditor createClass(ClassFileLoader loader, String outputDir,
			int modifiers, String className, Type superType, Type[] Interfaces) {
		loader.setOutputDir(new File(outputDir));
		EditorContext context = new PersistentBloatContext(loader);
		return context.newClass(modifiers, className, superType, Interfaces);
	}

	protected MethodEditor createMethod(ClassEditor ce, int modiefiers,
			Class type, String methodName, Class[] params, Class[] exeptions) {
		return new MethodEditor(ce, modiefiers, type, methodName, params,
				exeptions);
	}

	protected FieldEditor createField(ClassEditor ce, int modifiers, Type type,
			String fieldName) {
		FieldEditor fe = new FieldEditor(ce, modifiers, type, fieldName);
		fe.commit();
		return fe;
	}
	

	protected MemberRef fieldRef(Class parent, Class fieldClass, String name) {
		return fieldRef(getType(parent),fieldClass,name);
	}

	protected MemberRef fieldRef(Type parent, Class fieldClass, String name) {
		return new MemberRef(parent, new NameAndType(name,
				getType(fieldClass)));
	}
//	protected MemberRef fieldRef(String parent, Class fieldClass, String name) {
//		return new MemberRef(Type.getType("L"+ parent + ";"), new NameAndType(name,
//				getType(fieldClass)));
//	}

	protected MemberRef fieldRef(String parent, Class fieldClass, String name) {
		Type type=Type.getType("L"+parent+";");
		return fieldRef(type,fieldClass,name);
	}

	protected MemberRef methodRef(Class parent, String name, Class[] param,
			Class ret) {
		Type[] paramTypes = new Type[param.length];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = getType(param[i]);
		}
		NameAndType nat = new NameAndType(name, Type.getType(paramTypes,
				getType(ret)));
		return new MemberRef(getType(parent), nat);
	}

	protected Type getType(Class clazz) {
		return Type.getType(clazz);
	}

	protected Label[] createLabels(int num) {
		Label[] labels=new Label[num+1];
		for(int i=0;i<=num;i++) {
			labels[i]=new Label(i);
		}
		return labels;
	}

	protected LocalVariable[] createLocalVariables(int num) {
		LocalVariable[] localVars=new LocalVariable[num+1];
		for(int i=0;i<=num;i++) {
			localVars[i]=new LocalVariable(i);
		}
		return localVars;
	}
}
