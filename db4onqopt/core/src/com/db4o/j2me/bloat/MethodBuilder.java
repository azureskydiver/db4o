package com.db4o.j2me.bloat;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

public class MethodBuilder {
	private Map _labels;

	private Map _localVars;

	private MethodEditor _editor;

	private Enhancer _context;

	public MethodBuilder(Enhancer context, ClassEditor classEditor,
			int modifiers, Class type, String name, Class[] params,
			Class[] exceptions) {
		_context = context;
		_editor = new MethodEditor(classEditor, modifiers, type, name, params,
				exceptions);
		_labels = new HashMap();
		_localVars = new HashMap();
		label(0);
	}

	public void label(int id) {
		Label label = forceLabel(id);
		_editor.addLabel(label);
	}

	private Label forceLabel(int id) {
		Integer key = new Integer(id);
		Label label = (Label) _labels.get(key);
		if (label == null) {
			label = new Label(id);
			_labels.put(key, label);
		}
		return label;
	}

	private LocalVariable forceLocalVar(int id) {
		Integer key = new Integer(id);
		LocalVariable localVar = (LocalVariable) _localVars.get(key);
		if (localVar == null) {
			localVar = new LocalVariable(id);
			_localVars.put(key, localVar);
		}
		return localVar;
	}

	public void aload(int id) {
		_editor.addInstruction(Opcode.opc_aload, forceLocalVar(id));
	}

	public void iload(int id) {
		_editor.addInstruction(Opcode.opc_iload, forceLocalVar(id));
	}

	public void newarray(Class clazz) {
		_editor.addInstruction(Opcode.opc_newarray, _context.getType(clazz));
	}

	public void astore(int id) {
		_editor.addInstruction(Opcode.opc_astore, forceLocalVar(id));
	}

	public void areturn() {
		_editor.addInstruction(Opcode.opc_areturn);
	}

	public void invoke(int mode, Class parent, String name, Class[] params,
			Class ret) {
		invoke(mode, _context.getType(parent), name, params, ret);
	}

	public void invoke(int mode, Type parent, String name, Class[] params,
			Class ret) {
		_editor.addInstruction(mode, _context.methodRef(parent, name, params,
				ret));
	}

	public void invoke(int mode, Type parent, String name, Type[] params,
			Type ret) {
		_editor.addInstruction(mode, _context.methodRef(parent, name, params,
				ret));
	}

	public void newRef(Class clazz) {
		_editor.addInstruction(Opcode.opc_new, _context.getType(clazz));
	}

	public void dup() {
		_editor.addInstruction(Opcode.opc_dup);
	}

	public void athrow() {
		_editor.addInstruction(Opcode.opc_athrow);
	}

	public void ldc(int constant) {
		ldc(new Integer(constant));
	}

	public void ldc(Object constant) {
		_editor.addInstruction(Opcode.opc_ldc, constant);
	}

	public void ifeq(int labelId) {
		_editor.addInstruction(Opcode.opc_ifeq, forceLabel(labelId));
	}

	public void addTryCatch(int from, int to, int handler, Class thrown) {
		_editor.addTryCatch(new TryCatch(forceLabel(from), forceLabel(to),
				forceLabel(handler), _context.getType(thrown)));
	}

	public void getstatic(Type parent, Class type, String name) {
		getstatic(parent,_context.getType(type), name);
	}

	public void getstatic(Type parent, Type type, String name) {
		_editor.addInstruction(Opcode.opc_getstatic, _context.fieldRef(parent,
				type, name));
	}

	public void putstatic(Type parent, Class type, String name) {
		_editor.addInstruction(Opcode.opc_putstatic, _context.fieldRef(parent,
				_context.getType(type), name));
	}

	public void checkcast(Class type) {
		_editor.addInstruction(Opcode.opc_checkcast, _context.getType(type));
	}

	public void checkcast(Type type) {
		_editor.addInstruction(Opcode.opc_checkcast, type);
	}

	public void commit() {
		_editor.commit();
	}

	public MemberRef memberRef() {
		return _editor.memberRef();
	}

	public Type parentType() {
		return _editor.declaringClass().type();
	}

	/** @deprecated */
	public MethodEditor editor() {
		return _editor;
	}

	// TODO: Why is an empty 'throws' generated according to javap?
	public static void createLoadClassConstMethod(Enhancer context,
			ClassEditor ce) {
		MethodBuilder bld = new MethodBuilder(context, ce, Modifiers.PROTECTED
				| Modifiers.STATIC, Class.class, "db4o$class$",
				new Class[] { String.class }, null);
		// invoke Class#forName() and return result
		bld.aload(0);
		bld.invoke(Opcode.opc_invokestatic, Class.class, "forName",
				new Class[] { String.class }, Class.class);
		bld.label(1);
		bld.areturn();
		// wrap ClassNotFoundException in NoClassDefFoundError
		bld.label(2);
		bld.astore(1);
		bld.newRef(NoClassDefFoundError.class);
		bld.dup();
		bld.aload(1);
		bld.invoke(Opcode.opc_invokevirtual, ClassNotFoundException.class,
				"getMessage", new Class[] {}, String.class);
		bld.invoke(Opcode.opc_invokespecial, NoClassDefFoundError.class,
				"<init>", new Class[] { String.class }, Void.TYPE);
		bld.athrow();
		bld.addTryCatch(0, 1, 2, ClassNotFoundException.class);
		bld.commit();
	}

	public void getfield(MemberRef field) {
		_editor.addInstruction(Opcode.opc_getfield,field);
	}

}
