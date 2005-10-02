package com.db4o.nativequery.analysis;

import java.util.*;

import junit.framework.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;

class Data {
	int id;
	String name;
	Data next;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Data getNext() {
		return next;
	}
	public int getIdPlusOne() {
		return id+1;
	}
}

public class BloatExprBuilderVisitorTest extends TestCase {	
	private static final String INT_FIELDNAME = "id";
	private static final String DATA_FIELDNAME="next";
	private static final String STRING_FIELDNAME = "name";
	private final static int INT_CMPVAL=42;
	private final static String STRING_CMPVAL="Test";
	
	private String stringMember="foo";
	private int intMember=43;
	
	private int intMember() {
		return intMember;
	}
	
	private int intMemberPlusOne() {
		return intMember+1;
	}
	
	// no appropriate method
	
	boolean sampleNoParam() {
		return false;
	}

	public void testNoParam() throws Exception {
		assertNull(expression("sampleNoParam"));
	}

	// primitive identity
	
	boolean sampleFieldIntEqualsComp(Data data) {
		return data.id==INT_CMPVAL;
	}

	public void testFieldIntEqualsComp() throws Exception {
		assertComparison("sampleFieldIntEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldIntNotEqualsComp(Data data) {
		return data.id!=INT_CMPVAL;
	}

	public void testFieldIntNotEqualsComp() throws Exception {
		assertComparison("sampleIntFieldNotEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	boolean sampleIntFieldEqualsComp(Data data) {
		return INT_CMPVAL==data.id;
	}

	public void testIntFieldEqualsComp() throws Exception {
		assertComparison("sampleIntFieldEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntFieldNotEqualsComp(Data data) {
		return INT_CMPVAL!=data.id;
	}

	public void testIntFieldNotEqualsComp() throws Exception {
		assertComparison("sampleIntFieldNotEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	// object identity

	boolean sampleEqualsNullComp(Data data) {
		return data.next==null;
	}

	public void testEqualsNullComp() throws Exception {
		assertComparison("sampleEqualsNullComp",DATA_FIELDNAME,null,ComparisonOperator.EQUALS,false);
	}

	// primitive unequal comparison
	
	boolean sampleFieldIntSmallerComp(Data data) {
		return data.id<INT_CMPVAL;
	}

	public void testFieldIntSmallerComp() throws Exception {
		assertComparison("sampleFieldIntSmallerComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleFieldIntGreaterComp(Data data) {
		return data.id>INT_CMPVAL;
	}

	public void testFieldIntGreaterComp() throws Exception {
		assertComparison("sampleFieldIntGreaterComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleFieldIntSmallerEqualsComp(Data data) {
		return data.id<=INT_CMPVAL;
	}

	public void testFieldIntSmallerEqualsComp() throws Exception {
		assertComparison("sampleFieldIntSmallerEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldIntGreaterEqualsComp(Data data) {
		return data.id>=INT_CMPVAL;
	}

	public void testFieldIntGreaterEqualsComp() throws Exception {
		assertComparison("sampleFieldIntGreaterEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldSmallerComp(Data data) {
		return INT_CMPVAL<data.id;
	}

	public void testIntFieldSmallerComp() throws Exception {
		assertComparison("sampleIntFieldSmallerComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldGreaterComp(Data data) {
		return INT_CMPVAL>data.id;
	}

	public void testIntFieldGreaterComp() throws Exception {
		assertComparison("sampleIntFieldGreaterComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleIntFieldSmallerEqualsComp(Data data) {
		return INT_CMPVAL<=data.id;
	}

	public void testIntFieldSmallerEqualsComp() throws Exception {
		assertComparison("sampleIntFieldSmallerEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntFieldGreaterEqualsComp(Data data) {
		return INT_CMPVAL>=data.id;
	}

	public void testIntFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntFieldGreaterEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	// string equality
	
	boolean sampleFieldStringEqualsComp(Data data) {
		return data.name.equals(STRING_CMPVAL);
	}

	public void testFieldStringEqualsComp() throws Exception {
		assertComparison("sampleFieldStringEqualsComp",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	boolean sampleStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.name);
	}

	public void testStringFieldEqualsComp() throws Exception {
		assertComparison("sampleStringFieldEqualsComp",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	// getter
	
	boolean sampleGetterIntEqualsComp(Data data) {
		return data.getId()==INT_CMPVAL;
	}

	public void testGetterIntEqualsComp() throws Exception {
		assertComparison("sampleGetterIntEqualsComp",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleGetterStringEqualsComp(Data data) {
		return data.getName().equals(STRING_CMPVAL);
	}

	public void testGetterStringEqualsComp() throws Exception {
		assertComparison("sampleGetterStringEqualsComp",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	// field cascade

	boolean sampleCascadeFieldStringEqualsComp(Data data) {
		return data.next.name.equals(STRING_CMPVAL);
	}

	public void testCascadeFieldStringEqualsComp() throws Exception {
		assertComparison("sampleCascadeFieldStringEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	boolean sampleGetterCascadeIntFieldEqualsComp(Data data) {
		return INT_CMPVAL==data.getNext().getId();
	}

	public void testGetterCascadeIntFieldEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeIntFieldEqualsComp",new String[]{DATA_FIELDNAME,INT_FIELDNAME},Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleCascadeStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.next.name);
	}

	public void testCascadeStringFieldEqualsComp() throws Exception {
		assertComparison("sampleCascadeStringFieldEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	boolean sampleGetterCascadeStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.getNext().getName());
	}

	public void testGetterCascadeStringFieldEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeStringFieldEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	// member field comparison

	boolean sampleFieldIntMemberEqualsComp(Data data) {
		return data.getId()==intMember;
	}

	public void testFieldIntMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldIntMemberEqualsComp",new String[]{INT_FIELDNAME},new FieldValue(0,"intMember"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldStringMemberEqualsComp(Data data) {
		return data.getName().equals(stringMember);
	}

	public void testFieldStringMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldStringMemberEqualsComp",new String[]{STRING_FIELDNAME},new FieldValue(0,"stringMember"),ComparisonOperator.EQUALS,false);
	}

	// negations
	
	boolean sampleStringNot(Data data) {
		return !STRING_CMPVAL.equals(data.name);
	}
	
	public void testStringNot() throws Exception {
		assertComparison("sampleStringNot",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.EQUALS,true);
	}

	boolean sampleIntEqualsNot(Data data) {
		return !(data.id==INT_CMPVAL);
	}
	
	public void testIntEqualsNot() throws Exception {
		assertComparison("sampleIntEqualsNot",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	boolean sampleIntNotEqualsNot(Data data) {
		return !(data.id!=INT_CMPVAL);
	}
	
	public void testIntNotEqualsNot() throws Exception {
		assertComparison("sampleIntNotEqualsNot",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntGreaterNot(Data data) {
		return !(data.id>INT_CMPVAL);
	}
	
	public void testIntGreaterNot() throws Exception {
		assertComparison("sampleIntGreaterNot",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleIntSmallerEqualsNot(Data data) {
		return !(data.id<=INT_CMPVAL);
	}
	
	public void testIntSmallerEqualsNot() throws Exception {
		assertComparison("sampleIntSmallerEqualsNot",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntNotNot(Data data) {
		return !(!(data.id<INT_CMPVAL));
	}
	
	public void testIntNotNot() throws Exception {
		assertComparison("sampleIntNotNot",INT_FIELDNAME,Integer.valueOf(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	// conjunctions
	
	boolean sampleIntIntAnd(Data data) {
		return (data.id>42)&&(data.id<100);
	}
	
	public void testIntIntAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleIntIntAnd");
		assertComparison(expr.left(),new String[]{"id"},Integer.valueOf(42),ComparisonOperator.GREATER,false);
		assertComparison(expr.right(),new String[]{"id"},Integer.valueOf(100),ComparisonOperator.SMALLER,false);
	}

	boolean sampleStringIntOr(Data data) {
		return (data.name.equals("Foo"))||(data.id==42);
	}

	public void testStringIntOr() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleStringIntOr");
		assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,false);
		AndExpression right=(AndExpression)expr.right();
		assertComparison(right.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,true);
		assertComparison(right.right(),new String[]{"id"},Integer.valueOf(42),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntStringNotOr(Data data) {
		return !((data.id==42)||(data.name.equals("Foo")));
	}

	public void testIntStringNotOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleIntStringNotOr");
		assertComparison(expr.left(),new String[]{"id"},Integer.valueOf(42),ComparisonOperator.EQUALS,true);
		assertComparison(expr.right(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,true);
	}
	
	// arithmetic
	
	boolean sampleSanityIntAdd(Data data) {
		return data.id<INT_CMPVAL+INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntAdd() throws Exception {
		assertComparison("sampleSanityIntAdd",INT_FIELDNAME,Integer.valueOf(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleSanityIntMultiply(Data data) {
		return data.id<2*INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntMultiply() throws Exception {
		assertComparison("sampleSanityIntMultiply",INT_FIELDNAME,Integer.valueOf(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleMemberIntMultiply(Data data) {
		return data.id<2*intMember;
	}
	
	public void testMemberIntMultiply() throws Exception {
		assertComparison("sampleMemberIntMultiply",INT_FIELDNAME,new ArithmeticExpression(new ConstValue(new Integer(2)),new FieldValue(0,"intMember"),ArithmeticOperator.MULTIPLY),ComparisonOperator.SMALLER,false);
	}

	boolean sampleIntMemberDivide(Data data) {
		return data.id>intMember/2;
	}
	
	public void testIntMemberDivide() throws Exception {
		assertComparison("sampleIntMemberDivide",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(0,"intMember"),new ConstValue(new Integer(2)),ArithmeticOperator.DIVIDE),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntMemberMemberAdd(Data data) {
		return data.id==intMember+intMember;
	}
	
	public void testIntMemberMemberAdd() throws Exception {
		assertComparison("sampleIntMemberMemberAdd",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(0,"intMember"),new FieldValue(0,"intMember"),ArithmeticOperator.ADD),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntAddInPredicateMethod(Data data) {
		return data.getId()==intMemberPlusOne();
	}

	public void testIntAddInPredicateMethod() throws Exception {
		assertComparison("sampleIntAddInPredicateMethod",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(0,"intMember"),new ConstValue(new Integer(1)),ArithmeticOperator.ADD),ComparisonOperator.EQUALS,false);
	}

	// TODO: string append via '+'?
	
	// not applicable
	
	boolean sampleOtherMemberEqualsComp(Data data) {
		return stringMember.equals(STRING_CMPVAL);
	}

	public void testOtherMemberEqualsComp() throws Exception {
		Expression expression = expression("sampleOtherMemberEqualsComp");
		assertNull(expression);
	}

	boolean sampleOtherMemberSameComp(Data data) {
		return stringMember==STRING_CMPVAL;
	}

	public void testOtherMemberSameComp() throws Exception {
		Expression expression = expression("sampleOtherMemberSameComp");
		assertNull(expression);
	}

	boolean sampleCandidateMemberArithmetic(Data data) {
		return data.id-1==INT_CMPVAL;
	}

	public void testCandidateMemberArithmetic() throws Exception {
		Expression expression = expression("sampleCandidateMemberArithmetic");
		assertNull(expression);
	}

	// internal
	
	private void assertComparison(String methodName, String fieldName,Object value, ComparisonOperator op,boolean negated) {
		assertComparison(methodName,new String[]{fieldName},value,op,negated);
	}

	private void assertComparison(String methodName, String[] fieldNames,Object value, ComparisonOperator op,boolean negated) {
		try {
			Expression expr = expression(methodName);
			assertComparison(expr, fieldNames, value, op, negated);
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}

	private void assertComparison(Expression expr, String[] fieldNames, Object value, ComparisonOperator op, boolean negated) {
		if(negated) {
			NotExpression notExpr=(NotExpression)expr;
			expr=notExpr.expr();
		}
		ComparisonExpression cmpExpr=(ComparisonExpression)expr;
		assertEquals(op, cmpExpr.op());
		FieldValue fieldValue=(FieldValue) cmpExpr.left();
		assertEquals(1,fieldValue.parentIdx());
		Iterator foundNames=fieldValue.fieldNames();
		int foundFieldIdx=0;
		while(foundNames.hasNext()) {
			assertEquals(fieldNames[foundFieldIdx], (String)foundNames.next());
			foundFieldIdx++;
		}
		assertEquals(fieldNames.length,foundFieldIdx);
		ComparisonOperand right = cmpExpr.right();
		if(right instanceof ConstValue) {
			assertEquals(value, ((ConstValue) right).value());
			return;
		}
		assertEquals(value,(ComparisonOperand)right);
	}

	private Expression expression(String methodName) throws ClassNotFoundException {
		ClassFileLoader loader=new ClassFileLoader();
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(loader);		
		FlowGraph flowGraph=BloatUtil.flowGraph(loader,getClass().getName(),methodName);
		//flowGraph.visit(new PrintVisitor());
		flowGraph.visit(visitor);
		return visitor.expression();		
	}
}
