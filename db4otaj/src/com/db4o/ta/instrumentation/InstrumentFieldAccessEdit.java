/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorVisitor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Instruction;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;

import com.db4o.activation.Activator;
import com.db4o.instrumentation.BloatClassEdit;

public class InstrumentFieldAccessEdit implements BloatClassEdit {

	public void bloat(ClassEditor ce) {
		instrumentAllMethods(ce);
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

				TreeMap fieldAccessIndexes = new TreeMap(new Comparator() {
					public int compare(Object o1, Object o2) {
						return -((Comparable)o1).compareTo(o2);
					}
				});
				for(int codeIdx = 0; codeIdx < editor.codeLength(); codeIdx++) {
					Object curCode = editor.codeElementAt(codeIdx);
					MemberRef fieldRef = fieldRef(curCode);
					if(fieldRef != null) {
						fieldAccessIndexes.put(new Integer(codeIdx), fieldRef);
					}
				}
				if(fieldAccessIndexes.isEmpty()) {
					return;
				}
				for (Iterator idxIter = fieldAccessIndexes.keySet().iterator(); idxIter.hasNext();) {
					Integer idx = ((Integer) idxIter.next());
					MemberRef fieldRef = (MemberRef)fieldAccessIndexes.get(idx);
					MemberRef targetActivateMethod = createMethodReference(fieldRef.declaringClass(),  TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID);
					if(targetActivateMethod == null) {
						return;
					}
					editor.insertCodeAt(new Instruction(Opcode.opc_dup), idx.intValue());
					editor.insertCodeAt(new Instruction(Opcode.opc_invokevirtual, targetActivateMethod), idx.intValue() + 1);
					editor.commit();
				}
			}

			private MemberRef fieldRef(Object code) {
				if(!(code instanceof Instruction)) {
					return null;
				}
				Instruction curInstr = (Instruction)code;
				if(curInstr.origOpcode() == Opcode.opc_getfield) {
					return (MemberRef) curInstr.operand();
				}
				return null;
			}

		});
	}
	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}
}
