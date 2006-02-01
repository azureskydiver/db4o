package com.db4o.j2me.bloat;

import java.io.File;

import EDU.purdue.cs.bloat.context.PersistentBloatContext;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.reflect.*;

public class Enhancer {
	private ClassFileLoader _loader;
	
	public Enhancer(ClassFileLoader loader, String outputDirPath) {
		_loader = loader;
		_loader.setOutputDir(new File(outputDirPath));
	}
	public ClassEditor loadClass(String classPath,
			String className) {
		_loader.appendClassPath(classPath);
		try {
			ClassInfo info = _loader.loadClass(className);
			EditorContext context = new PersistentBloatContext(info.loader());
			return context.editClass(info);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ClassEditor createClass(int modifiers, String className, Type superType, Type[] Interfaces) {
		EditorContext context = new PersistentBloatContext(_loader);
		return context.newClass(modifiers, className, superType, Interfaces);
	}

	public MethodEditor createMethod(ClassEditor ce, int modiefiers,
			Class type, String methodName, Class[] params, Class[] exeptions) {
		return new MethodEditor(ce, modiefiers, type, methodName, params,
				exeptions);
	}

	public FieldEditor createField(ClassEditor ce, int modifiers, Type type,
			String fieldName) {
		FieldEditor fe = new FieldEditor(ce, modifiers, type, fieldName);
		fe.commit();
		return fe;
	}
	

	public MemberRef fieldRef(Class parent, Class fieldClass, String name) {
		return fieldRef(getType(parent),fieldClass,name);
	}

	public MemberRef fieldRef(Type parent, Class fieldClass, String name) {
		return fieldRef(parent,getType(fieldClass),name);
	}

	public MemberRef fieldRef(Type parent, Type type, String name) {
		return new MemberRef(parent, new NameAndType(name,type));
	}

//	protected MemberRef fieldRef(String parent, Class fieldClass, String name) {
//		return new MemberRef(Type.getType("L"+ parent + ";"), new NameAndType(name,
//				getType(fieldClass)));
//	}

	public MemberRef fieldRef(String parent, Class fieldClass, String name) {
		Type type=Type.getType(Type.classDescriptor(parent));
		return fieldRef(type,fieldClass,name);
	}

	public MemberRef methodRef(Type parent, String name, Type[] param,
			Type ret) {
		NameAndType nat = new NameAndType(name, Type.getType(param,ret));
		return new MemberRef(parent, nat);
	}

	public MemberRef methodRef(Type parent, String name, Class[] param,
			Class ret) {
		Type[] paramTypes = new Type[param.length];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = getType(param[i]);
		}
		return methodRef(parent,name,paramTypes,getType(ret));
	}

	public MemberRef methodRef(Class parent, String name, Class[] param,
			Class ret) {
		return methodRef(getType(parent),name,param,ret);
	}

	public Type getType(Class clazz) {
		return Type.getType(clazz);
	}

	public Type getType(String desc) {
		return Type.getType(desc);
	}

	public Label[] createLabels(int num) {
		Label[] labels=new Label[num+1];
		for(int i=0;i<=num;i++) {
			labels[i]=new Label(i);
		}
		return labels;
	}

	public LocalVariable[] createLocalVariables(int num) {
		LocalVariable[] localVars=new LocalVariable[num+1];
		for(int i=0;i<=num;i++) {
			localVars[i]=new LocalVariable(i);
		}
		return localVars;
	}
	
	// TODO: Why is an empty 'throws' generated according to javap?
	public void createLoadClassConstMethod(ClassEditor ce) {
		MethodEditor me=createMethod(ce, Modifiers.PROTECTED|Modifiers.STATIC, Class.class, "db4o$class$", new Class[]{String.class}, null);
		LocalVariable[] localVars=createLocalVariables(1);
		Label[] labels=createLabels(2);
		me.addLabel(labels[0]);
//		   0:   aload_0
		me.addInstruction(Opcode.opc_aload,localVars[0]);
//		   1:   invokestatic    #1; //Method java/lang/Class.forName:(Ljava/lang/String;)Ljava/lang/Class;
		me.addInstruction(Opcode.opc_invokestatic, methodRef(Class.class, "forName", new Class[]{String.class}, Class.class));
		me.addLabel(labels[1]);
//		   4:   areturn
		me.addInstruction(Opcode.opc_areturn);
//		   5:   astore_1		
		me.addLabel(labels[2]);
		me.addInstruction(Opcode.opc_astore,localVars[1]);
//		   6:   new     #3; //class NoClassDefFoundError
		me.addInstruction(Opcode.opc_new,getType(NoClassDefFoundError.class));
//		   9:   dup
		me.addInstruction(Opcode.opc_dup);
//		   10:  aload_1
		me.addInstruction(Opcode.opc_aload,localVars[1]);
//		   11:  invokevirtual   #4; //Method java/lang/ClassNotFoundException.getMessage:()Ljava/lang/String;
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(ClassNotFoundException.class, "getMessage", new Class[]{}, String.class));
//		   14:  invokespecial   #5; //Method java/lang/NoClassDefFoundError."<init>":(Ljava/lang/String;)V
		me.addInstruction(Opcode.opc_invokespecial, methodRef(NoClassDefFoundError.class, "<init>", new Class[]{String.class}, Void.TYPE));
//		   17:  athrow
		me.addInstruction(Opcode.opc_athrow);
//	    0     4     5   Class java/lang/ClassNotFoundException
		me.addTryCatch(new TryCatch(labels[0],labels[1],labels[2],getType(ClassNotFoundException.class)));
		me.commit();
	}
	
	public void invokeLoadClassConstMethod(MethodBuilder builder,String clazzName) {
		builder.ldc(normalizeClassName(clazzName));
		builder.invoke(Opcode.opc_invokestatic, builder.parentType(),
				"db4o$class$", new Class[] { String.class }, Class.class);
	}
	
	public String normalizeClassName(String name) {
		return name.replace('/', '.');
	}
	public MemberRef[] collectDeclaredFields(ClassEditor ce) {
		FieldInfo[] fields = ce.fields();
		MemberRef[] refs = new MemberRef[fields.length];
		for (int i = 0; i < fields.length; i++) {
			refs[i] = new FieldEditor(ce, fields[i]).memberRef();
		}
		return refs;
	}
}
