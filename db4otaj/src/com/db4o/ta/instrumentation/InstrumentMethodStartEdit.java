/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorVisitor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Instruction;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;

import com.db4o.activation.Activator;
import com.db4o.instrumentation.*;

public class InstrumentMethodStartEdit implements BloatClassEdit {

	public boolean bloat(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		instrumentAllMethods(ce);
		return true;
	}

	private void instrumentAllMethods(final ClassEditor ce) {
		final MemberRef activateMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID);
		final MemberRef bindMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Type[]{ Type.getType(Activator.class) }, Type.VOID);
		ce.visit(new EditorVisitor() {

			public void visitClassEditor(ClassEditor editor) {
			}

			public void visitFieldEditor(FieldEditor editor) {
			}

			public void visitMethodEditor(MethodEditor editor) {
				if(editor.isConstructor() || editor.isAbstract() || editor.isStatic()) {
					return;
				}
				MemberRef methodRef = editor.memberRef();
				if(methodRef.equals(activateMethod) || methodRef.equals(bindMethod)) {
					return;
				}

				// activate();
				insertActivateCall(ce, editor, 1);
				editor.commit();
			}

			private void insertActivateCall(final ClassEditor ce, MethodEditor editor, int idx) {
				editor.insertCodeAt(new Instruction(Opcode.opc_aload, new LocalVariable(0)), idx);
				editor.insertCodeAt(new Instruction(Opcode.opc_invokevirtual, createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID)), idx + 1);
			}
		});
	}

	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}
}
