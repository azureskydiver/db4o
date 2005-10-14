package com.db4o.nativequery.optimization.db4o;

import java.util.*;

import junit.framework.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.optimization.*;

public class SODABloatMethodBuilderTest extends TestCase {
	private final static String[] FIELDNAMES={"foo","baz"};
	private final static Object CONSTVALUE=new Integer(42);
	private final int primitive=42;
	
	private ClassEditor classEditor;
	private SODABloatMethodBuilder builder;
	
	protected void setUp() throws Exception {
		classEditor=BloatUtil.classEditor(new ClassFileLoader(),getClass().getName());
		builder=new SODABloatMethodBuilder();
	}
	
	public void testComparison() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		ConstValue right=new ConstValue(CONSTVALUE);
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,
				Opcode.opc_ldc,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	public void testBoolean() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		ConstValue right=new ConstValue(CONSTVALUE);
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		expr=new AndExpression(expr,new NotExpression(expr));
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,
				Opcode.opc_ldc,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface,
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,
				Opcode.opc_ldc,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	public void testArithmetic() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		ConstValue constVal=new ConstValue(CONSTVALUE);
		ArithmeticExpression right=new ArithmeticExpression(constVal,constVal,ArithmeticOperator.SUBTRACT);
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,
				Opcode.opc_ldc,
				Opcode.opc_ldc,
				Opcode.opc_isub,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	public void testPredicateFieldObjectAccess() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		FieldValue right=new FieldValue(0,"CONSTVALUE");
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_aload,
				Opcode.opc_getfield,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	public void testPredicateFieldPrimitiveAccess() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		FieldValue right=new FieldValue(0,"primitive");
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,				
				Opcode.opc_aload,
				Opcode.opc_getfield,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	public void testNegatedPredicateFieldPrimitiveAccess() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		FieldValue right=new FieldValue(0,"primitive");
		
		Expression expr=new NotExpression(new ComparisonExpression(left,right,ComparisonOperator.SMALLER));
		int[] expected={
				Opcode.opc_aload,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_ldc,
				Opcode.opc_invokeinterface,
				Opcode.opc_new,
				Opcode.opc_dup,				
				Opcode.opc_aload,
				Opcode.opc_getfield,
				Opcode.opc_invokespecial,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface,
				Opcode.opc_invokeinterface
		};
		assertByteCodes(expr,expected);
	}

	private void assertByteCodes(Expression expr,int[] expectedCode) {
		MethodEditor methodEditor=builder.injectOptimization(expr, classEditor, getClass().getClassLoader());
		//methodEditor.print(System.out);
		List actualCode=methodEditor.code();
		assertEquals(expectedCode.length+5,actualCode.size());
		assertLabel(actualCode,0);
		for(int idx=1;idx<=expectedCode.length;idx++) {
			assertByteCode(actualCode,idx,expectedCode[idx-1]);
		}
		assertByteCode(actualCode,actualCode.size()-4,Opcode.opc_pop);
		assertLabel(actualCode,actualCode.size()-3);
		assertByteCode(actualCode,actualCode.size()-2,Opcode.opc_return);
		assertLabel(actualCode,actualCode.size()-1);
		new FlowGraph(methodEditor); // checks consistency
	}
	
	private void assertByteCode(List byteCode,int idx,int expected) {
		Instruction instr=(Instruction)byteCode.get(idx);
		assertEquals(expected,instr.origOpcode());
	}

	private void assertLabel(List byteCode,int idx) {
		assertTrue(byteCode.get(idx) instanceof Label);
	}
}
