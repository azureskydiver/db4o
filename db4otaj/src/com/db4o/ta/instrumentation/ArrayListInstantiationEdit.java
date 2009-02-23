package com.db4o.ta.instrumentation;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.collections.*;
import com.db4o.instrumentation.core.*;

public class ArrayListInstantiationEdit implements BloatClassEdit {
	
	private final class ArrayListInstantiationMethodVisitor implements EditorVisitor {
		private boolean _instrumented;

		public void visitClassEditor(ClassEditor editor) {
		}

		public void visitFieldEditor(FieldEditor editor) {
		}

		public void visitMethodEditor(MethodEditor editor) {
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

		private Instruction match(Object instructionOrLabel, int expectedOpcode,
				Object expectedOperand) {
			
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

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		
		ArrayListInstantiationMethodVisitor methodVisitor = new ArrayListInstantiationMethodVisitor();
		try {
			ce.visit(methodVisitor);
		}
		catch(Exception exc) {
			return InstrumentationStatus.FAILED;
		}
		if (methodVisitor.instrumented()) {
			ce.commit();
			return InstrumentationStatus.INSTRUMENTED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

}
