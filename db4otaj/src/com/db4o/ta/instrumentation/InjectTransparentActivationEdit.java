package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.*;
import com.db4o.instrumentation.*;
import com.db4o.ta.*;
import com.db4o.ta.internal.*;

public class InjectTransparentActivationEdit implements BloatClassEdit {

	private LocalVariable THIS_VAR = new LocalVariable(0);
	
	public void bloat(ClassEditor ce) {
		ce.addInterface(Activatable.class);
		createActivatorField(ce);
		createBindMethod(ce);
		createActivateMethod(ce);
		instrumentNonPrivateMethods(ce);
	}

	private void createActivatorField(ClassEditor ce) {
		// private transient Activator _activator;
		FieldEditor fieldEditor = new FieldEditor(ce, Modifiers.PRIVATE | Modifiers.TRANSIENT, Type.getType(Activator.class), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		fieldEditor.commit();
	}

	private void createBindMethod(ClassEditor ce) {
		// public void bind(ObjectContainer container)
		final Type activatorType = Type.getType(Activator.class);
		final Type objectContainerType = Type.getType(ObjectContainer.class);
		String methodName = TransparentActivationInstrumentationConstants.BIND_METHOD_NAME;
		Type[] paramTypes = { objectContainerType };
		MethodEditor methodEditor = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID, methodName, paramTypes, new Type[] {});
		Label startLabel = new Label(0);
		Label setActivatorLabel = new Label(1);
		LocalVariable objectContainerArgLocal = new LocalVariable(1);
		
		methodEditor.addLabel(startLabel);

		// if (null != _activator) 
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_ifnull, setActivatorLabel);
		
		// { _activator.assertCompatible(container); return; }
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_aload, objectContainerArgLocal);
		methodEditor.addInstruction(Opcode.opc_invokevirtual, createMethodReference(activatorType, TransparentActivationInstrumentationConstants.ASSERT_COMPATIBLE_METHOD_NAME, new Type[] { objectContainerType }, Type.VOID));
		methodEditor.addInstruction(Opcode.opc_return);
		
		// _activator = new Activator(container, this);
		methodEditor.addLabel(setActivatorLabel);	
		loadThisOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_new,activatorType);
		methodEditor.addInstruction(Opcode.opc_dup);
		methodEditor.addInstruction(Opcode.opc_aload, objectContainerArgLocal);
		loadThisOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_invokespecial, createMethodReference(activatorType, TransparentActivationInstrumentationConstants.INIT_METHOD_NAME, new Type[] { objectContainerType, Type.OBJECT }, Type.VOID));
		methodEditor.addInstruction(Opcode.opc_putfield, createFieldReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME, activatorType));		
		methodEditor.addInstruction(Opcode.opc_return);
		
		methodEditor.commit();
	}

	private void createActivateMethod(ClassEditor ce) {
		// private void activate()
		final Type activatorType = Type.getType(Activator.class);
		String methodName = TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME;
		MethodEditor methodEditor = new MethodEditor(ce, Modifiers.PROTECTED, Type.VOID, methodName, new Type[] { }, new Type[] {});
		Label startLabel = new Label(0);
		Label activateLabel = new Label(1);

		// if (_activator == null) { return; }
		methodEditor.addLabel(startLabel);
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_ifnonnull, activateLabel);
		methodEditor.addInstruction(Opcode.opc_return);
		
		// _activator.activate();
		methodEditor.addLabel(activateLabel);
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_invokevirtual, createMethodReference(activatorType, TransparentActivationInstrumentationConstants.ACTIVATOR_ACTIVATE_METHOD_NAME, new Type[] { }, Type.VOID));
		methodEditor.addInstruction(Opcode.opc_return);
		
		methodEditor.commit();
	}

	private void instrumentNonPrivateMethods(final ClassEditor ce) {
		final MemberRef activateMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID);
		final MemberRef bindMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Type[]{ Type.getType(ObjectContainer.class) }, Type.VOID);
		ce.visit(new EditorVisitor() {

			public void visitClassEditor(ClassEditor editor) {
			}

			public void visitFieldEditor(FieldEditor editor) {
			}

			public void visitMethodEditor(MethodEditor editor) {
				if(editor.isConstructor() || editor.isPrivate()) {
					return;
				}
				MemberRef methodRef = editor.memberRef();
				if(methodRef.equals(activateMethod) || methodRef.equals(bindMethod)) {
					return;
				}
				// activate();
				editor.insertCodeAt(new Instruction(Opcode.opc_aload, new LocalVariable(0)), 1);
				editor.insertCodeAt(new Instruction(Opcode.opc_invokevirtual, createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID)), 2);
				editor.commit();
			}
			
		});
	}

	private void loadThisOnStack(MethodEditor methodEditor) {
		methodEditor.addInstruction(Opcode.opc_aload, THIS_VAR);
	}
	
	private void loadActivatorFieldOnStack(MethodEditor methodEditor) {
		Type activatorType = Type.getType(Activator.class);
		loadThisOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_getfield, createFieldReference(methodEditor.declaringClass().type(), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME, activatorType));
	}
	
	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}

	private MemberRef createFieldReference(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}

}
