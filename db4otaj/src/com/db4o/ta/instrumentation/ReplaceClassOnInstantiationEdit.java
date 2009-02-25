package com.db4o.ta.instrumentation;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.instrumentation.core.*;

public class ReplaceClassOnInstantiationEdit implements BloatClassEdit {

	private final Type _origType;
	private final Type _replacementType;
	
	public ReplaceClassOnInstantiationEdit(Class origClazz, Class replacementClazz) {
		// TODO get type from runtime bloat environment and pass qualified names instead?
		_origType = Type.getType(origClazz);
		_replacementType = Type.getType(replacementClazz);
	}
	
	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		ArrayListInstantiationMethodVisitor methodVisitor = new ArrayListInstantiationMethodVisitor();
		try {
			ce.visit(methodVisitor);
		} catch(Exception exc) {
			exc.printStackTrace();
			return InstrumentationStatus.FAILED;
		}
		if (methodVisitor.instrumented()) {
			ce.commit();
			return InstrumentationStatus.INSTRUMENTED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

	private final class ArrayListInstantiationMethodVisitor implements EditorVisitor {
		private boolean _instrumented;

		public void visitClassEditor(ClassEditor editor) {
			if (!_origType.equals(editor.superclass())) {
				return;
			}
			editor.setSuperclass(_replacementType);
			_instrumented = true;
		}

		public void visitFieldEditor(FieldEditor editor) {
		}

		public void visitMethodEditor(MethodEditor editor) {
			boolean instrumented = false;
			final Iterator codeIterator = editor.code().iterator();
			while (codeIterator.hasNext()) {
				final Object instructionOrLabel = codeIterator.next();
				if(instructionOrLabel instanceof Label) {
					continue;
				}
				if(!(instructionOrLabel instanceof Instruction)) {
					throw new IllegalStateException();
				}
				final Instruction instruction = (Instruction)instructionOrLabel;
				switch(instruction.origOpcode()) {
					case Instruction.opc_new:
						if(!instruction.operand().equals(_origType)) {
							break;
						}
						instruction.setOperand(_replacementType);
						break;
					// invokespecial covers instance initializer, super class method and private method invocations
					case Instruction.opc_invokespecial:
						MemberRef methodRef = (MemberRef) instruction.operand();
						if(!methodRef.declaringClass().equals(_origType)) {
							break;
						}
						instruction.setOperand(new MemberRef(_replacementType, methodRef.nameAndType()));
						instrumented = true;
						break;
					default:
						// do nothing
				}
			}
			if(instrumented) {
				_instrumented = true;
				editor.commit();
			}
			
		}
		
		public boolean instrumented() {
			return _instrumented;
		}
	}

}
