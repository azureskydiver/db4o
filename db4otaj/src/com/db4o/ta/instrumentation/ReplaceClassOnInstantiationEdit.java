package com.db4o.ta.instrumentation;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.inline.*;

import com.db4o.collections.*;
import com.db4o.instrumentation.core.*;

public class ReplaceClassOnInstantiationEdit implements BloatClassEdit {

	private final Type origType;
	private final Type replacementType;
	
	public ReplaceClassOnInstantiationEdit(Class origClazz, Class replacementClazz) {
		// TODO get type from runtime bloat environment and pass qualified names instead?
		origType = Type.getType(origClazz);
		replacementType = Type.getType(replacementClazz);
	}
	
	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		ArrayListInstantiationMethodVisitor methodVisitor = new ArrayListInstantiationMethodVisitor();
		try {
			ce.visit(methodVisitor);
		}
		catch(Exception exc) {
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
		}

		public void visitFieldEditor(FieldEditor editor) {
		}

		public void visitMethodEditor(MethodEditor editor) {
			final StackHeightCounter shc = new StackHeightCounter(editor);
			final LinkedList newStackHeights = new LinkedList();
			boolean instrumented = false;
			for(int codeIdx = 0; codeIdx < editor.codeLength(); codeIdx++) {
				final Object instructionOrLabel = editor.codeElementAt(codeIdx);
				if(instructionOrLabel instanceof Label) {
					shc.handle((Label)instructionOrLabel);
					continue;
				}
				if(!(instructionOrLabel instanceof Instruction)) {
					throw new IllegalStateException();
				}
				final Instruction instruction = (Instruction)instructionOrLabel;
				shc.handle(instruction);
				
				int lastHeight = newStackHeights.isEmpty() ? -1 : ((Integer)newStackHeights.getLast()).intValue();
				switch(instruction.origOpcode()) {
					case Instruction.opc_new:
						if(!instruction.operand().equals(origType)) {
							break;
						}
						instruction.setOperand(replacementType);
						newStackHeights.addLast(new Integer(shc.height()));
						break;
					case Instruction.opc_dup:
						if(shc.height() != lastHeight + 1) {
							break;
						}
						newStackHeights.removeLast();
						newStackHeights.addLast(new Integer(lastHeight + 1));
						break;
					case Instruction.opc_invokespecial:
						MemberRef methodRef = (MemberRef) instruction.operand();
						if(!methodRef.declaringClass().equals(origType)) {
							break;
						}
						if(shc.height() != lastHeight - 1) {
							break;
						}
						newStackHeights.removeLast();
						instruction.setOperand(new MemberRef(replacementType, methodRef.nameAndType()));
						instrumented = true;
						break;
					default:
						// do nothing
				}
			}
			if(!newStackHeights.isEmpty()) {
				throw new IllegalStateException();
			}
			if(instrumented) {
				_instrumented = true;
				editor.commit();
			}
			
		}

		// initial version, kept for reference, delete if not needed
		public void _visitMethodEditor(MethodEditor editor) {
			boolean instrumented = false;
			for(int codeIdx = 0; codeIdx < editor.codeLength(); codeIdx++) {
				Instruction newInstruction = match(editor.codeElementAt(codeIdx), Instruction.opc_new, Type.getType(ArrayList.class));
				if (newInstruction == null)
					continue;
				
				Instruction dupInstruction = match(editor.codeElementAt(codeIdx + 1), Instruction.opc_dup);
				if (dupInstruction == null)
					continue;
				Instruction invokeInstruction = match(editor.codeElementAt(codeIdx + 2), Instruction.opc_invokespecial);
				if (invokeInstruction == null)
					continue;
				
				Type replacementType = Type.getType(ActivatableArrayList.class);
				newInstruction.setOperand(replacementType);
				MemberRef methodRef = (MemberRef) invokeInstruction.operand();
				invokeInstruction.setOperand(new MemberRef(replacementType, methodRef.nameAndType()));
				instrumented = true;
				codeIdx += 2;
			}
			if(instrumented) {
				_instrumented = true;
				editor.commit();
			}
		}

		private Instruction match(Object instructionOrLabel, int expectedOpcode, Object expectedOperand) {
			Instruction instruction = match(instructionOrLabel, expectedOpcode);
			if (instruction == null) {
				return null;
			}
			return instruction.operand().equals(expectedOperand) ? instruction : null;
		}

		private Instruction match(Object instructionOrLabel, int expectedOpcode) {
			if (!(instructionOrLabel instanceof Instruction)) {
				return null;
			}
			Instruction instruction = (Instruction) instructionOrLabel;
			return instruction.origOpcode() == expectedOpcode ? instruction : null;
			
		}

		public boolean instrumented() {
			return _instrumented;
		}
	}

}
