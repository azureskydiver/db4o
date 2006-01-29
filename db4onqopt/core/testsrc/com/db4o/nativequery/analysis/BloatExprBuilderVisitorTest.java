package com.db4o.nativequery.analysis;

import junit.framework.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.foundation.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;

class Base {
	int id;
	Integer idWrap;

	public int getId() {
		return id;
	}

	public Integer getIdWrapped() {
		return idWrap;
	}

	public int getIdPlusOne() {
		return id+1;
	}
}

class Data extends Base {
	float value;
	String name;
	Data next;
	
	public float getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public Data getNext() {
		return next;
	}
	
	public boolean hasNext() {
		return getNext()!=null;
	}
	
	public void someMethod() {
		System.out.println();
	}
}

public class BloatExprBuilderVisitorTest extends TestCase {	
	private static final String INT_WRAPPED_FIELDNAME = "idWrap";
	private static final String INT_FIELDNAME = "id";
	private static final String FLOAT_FIELDNAME = "value";
	private static final String DATA_FIELDNAME="next";
	private static final String STRING_FIELDNAME = "name";
	private final static int INT_CMPVAL=42;
	private final static float FLOAT_CMPVAL=12.3f;
	private final static String STRING_CMPVAL="Test";
	// TODO: handle StaticFieldExpr
	private final static Integer INT_WRAPPER_CMPVAL=new Integer(INT_CMPVAL);
	private final Integer intWrapperCmpVal=new Integer(INT_CMPVAL);
	
	private String stringMember="foo";
	private int intMember=43;
	private float floatMember=47.11f;

	private ClassFileLoader loader;
	private BloatUtil bloatUtil;
	
	private int intMember() {
		return intMember;
	}
	
	private int intMemberPlusOne() {
		return intMember+1;
	}

	protected void setUp() throws Exception {
		loader=new ClassFileLoader();
		bloatUtil=new BloatUtil(loader);
	}
	
	// unconditional

	boolean sampleTrue(Data data) {
		return true;
	}

	public void testTrue() throws Exception {
		assertEquals(BoolConstExpression.TRUE,expression("sampleTrue"));
	}

	boolean sampleFalse(Data data) {
		return false;
	}

	public void testFalse() throws Exception {
		assertEquals(BoolConstExpression.FALSE,expression("sampleFalse"));
	}

	// primitive identity

	// int
	
	boolean sampleFieldIntEqualsComp(Data data) {
		return data.id==INT_CMPVAL;
	}

	public void testFieldIntEqualsComp() throws Exception {
		assertComparison("sampleFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldIntNotEqualsComp(Data data) {
		return data.id!=INT_CMPVAL;
	}

	public void testFieldIntNotEqualsComp() throws Exception {
		assertComparison("sampleFieldIntNotEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	boolean sampleIntFieldEqualsComp(Data data) {
		return INT_CMPVAL==data.id;
	}

	public void testIntFieldEqualsComp() throws Exception {
		assertComparison("sampleIntFieldEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntFieldNotEqualsComp(Data data) {
		return INT_CMPVAL!=data.id;
	}

	public void testIntFieldNotEqualsComp() throws Exception {
		assertComparison("sampleIntFieldNotEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	// float
	
	boolean sampleFieldFloatEqualsComp(Data data) {
		return data.value==FLOAT_CMPVAL;
	}

	public void testFieldFloatEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldFloatNotEqualsComp(Data data) {
		return data.value!=FLOAT_CMPVAL;
	}

	public void testFieldFloatNotEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatNotEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	boolean sampleFloatFieldEqualsComp(Data data) {
		return FLOAT_CMPVAL==data.value;
	}

	public void testFloatFieldEqualsComp() throws Exception {
		assertComparison("sampleFloatFieldEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFloatFieldNotEqualsComp(Data data) {
		return FLOAT_CMPVAL!=data.value;
	}

	public void testFloatFieldNotEqualsComp() throws Exception {
		assertComparison("sampleFloatFieldNotEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	// object identity

	boolean sampleIdentityNullComp(Data data) {
		return data.next==null;
	}

	public void testIdentityNullComp() throws Exception {
		assertComparison("sampleIdentityNullComp",DATA_FIELDNAME,null,ComparisonOperator.EQUALS,false);
	}

	boolean sampleNotIdentityNullComp(Data data) {
		return data.next!=null;
	}

	public void testNotIdentityNullComp() throws Exception {
		assertComparison("sampleNotIdentityNullComp",DATA_FIELDNAME,null,ComparisonOperator.EQUALS,true);
	}

	// primitive unequal comparison
	
	// int
	
	boolean sampleFieldIntSmallerComp(Data data) {
		return data.id<INT_CMPVAL;
	}

	public void testFieldIntSmallerComp() throws Exception {
		assertComparison("sampleFieldIntSmallerComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleFieldIntGreaterComp(Data data) {
		return data.id>INT_CMPVAL;
	}

	public void testFieldIntGreaterComp() throws Exception {
		assertComparison("sampleFieldIntGreaterComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleFieldIntSmallerEqualsComp(Data data) {
		return data.id<=INT_CMPVAL;
	}

	public void testFieldIntSmallerEqualsComp() throws Exception {
		assertComparison("sampleFieldIntSmallerEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldIntGreaterEqualsComp(Data data) {
		return data.id>=INT_CMPVAL;
	}

	public void testFieldIntGreaterEqualsComp() throws Exception {
		assertComparison("sampleFieldIntGreaterEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldSmallerComp(Data data) {
		return INT_CMPVAL<data.id;
	}

	public void testIntFieldSmallerComp() throws Exception {
		assertComparison("sampleIntFieldSmallerComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntFieldGreaterComp(Data data) {
		return INT_CMPVAL>data.id;
	}

	public void testIntFieldGreaterComp() throws Exception {
		assertComparison("sampleIntFieldGreaterComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleIntFieldSmallerEqualsComp(Data data) {
		return INT_CMPVAL<=data.id;
	}

	public void testIntFieldSmallerEqualsComp() throws Exception {
		assertComparison("sampleIntFieldSmallerEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldGreaterEqualsComp(Data data) {
		return INT_CMPVAL>=data.id;
	}

	public void testIntFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntFieldGreaterEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldFloatSmallerComp(Data data) {
		return data.value<FLOAT_CMPVAL;
	}

	public void testFieldFloatSmallerComp() throws Exception {
		assertComparison("sampleFieldFloatSmallerComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleFieldFloatGreaterComp(Data data) {
		return data.value>FLOAT_CMPVAL;
	}

	public void testFieldFloatGreaterComp() throws Exception {
		assertComparison("sampleFieldFloatGreaterComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleFieldFloatSmallerEqualsComp(Data data) {
		return data.value<=FLOAT_CMPVAL;
	}

	public void testFieldFloatSmallerEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatSmallerEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,true);
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
	
	// primitive wrapper equality

	boolean sampleFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(intWrapperCmpVal);
	}

	public void testFieldIntWrapperEqualsComp() throws Exception {
		assertComparison("sampleFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntWrapperFieldEqualsComp(Data data) {
		return intWrapperCmpVal.equals(data.getIdWrapped());
	}

	public void testIntWrapperFieldEqualsComp() throws Exception {
		assertComparison("sampleIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal"),ComparisonOperator.EQUALS,false);
	}	

	//static member comparison

	boolean sampleStaticFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(INT_WRAPPER_CMPVAL);
	}

	public void testStaticFieldIntWrapperEqualsComp() throws Exception {
		//assertInvalid("sampleStaticFieldIntWrapperEqualsComp");
		assertComparison("sampleStaticFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(getClass().getName()),"INT_WRAPPER_CMPVAL"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleStaticIntWrapperFieldEqualsComp(Data data) {
		return INT_WRAPPER_CMPVAL.equals(data.getIdWrapped());
	}

	public void testStaticIntWrapperFieldEqualsComp() throws Exception {
		//assertInvalid("sampleStaticIntWrapperFieldEqualsComp");
		assertComparison("sampleStaticIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(getClass().getName()),"INT_WRAPPER_CMPVAL"),ComparisonOperator.EQUALS,false);
	}	
	
	// getter
	
	boolean sampleGetterIntEqualsComp(Data data) {
		return data.getId()==INT_CMPVAL;
	}

	public void testGetterIntEqualsComp() throws Exception {
		assertComparison("sampleGetterIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleGetterStringEqualsComp(Data data) {
		return data.getName().equals(STRING_CMPVAL);
	}

	public void testGetterStringEqualsComp() throws Exception {
		assertComparison("sampleGetterStringEqualsComp",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.EQUALS,false);
	}

	boolean sampleGetterFloatSmallerComp(Data data) {
		return data.getValue()<FLOAT_CMPVAL;
	}

	public void testGetterFloatSmallerComp() throws Exception {
		assertComparison("sampleGetterFloatSmallerComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.SMALLER,false);
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
		assertComparison("sampleGetterCascadeIntFieldEqualsComp",new String[]{DATA_FIELDNAME,INT_FIELDNAME},new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
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

	boolean sampleGetterCascadeFloatFieldGreaterEqualsComp(Data data) {
		return FLOAT_CMPVAL>=data.getNext().getValue();
	}

	public void testGetterCascadeFloatFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeFloatFieldGreaterEqualsComp",new String[]{DATA_FIELDNAME,FLOAT_FIELDNAME},new Float(FLOAT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	// member field comparison

	boolean sampleFieldIntMemberEqualsComp(Data data) {
		return data.getId()==intMember;
	}

	public void testFieldIntMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldIntMemberEqualsComp",new String[]{INT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntMemberFieldGreaterEqualsComp(Data data) {
		return intMember>=data.getId();
	}

	public void testIntMemberFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntMemberFieldGreaterEqualsComp",new String[]{INT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldStringMemberEqualsComp(Data data) {
		return data.getName().equals(stringMember);
	}

	public void testFieldStringMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldStringMemberEqualsComp",new String[]{STRING_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"stringMember"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldFloatMemberNotEqualsComp(Data data) {
		return data.getValue()!=floatMember;
	}

	public void testFieldFloatMemberNotEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatMemberNotEqualsComp",new String[]{FLOAT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember"),ComparisonOperator.EQUALS,true);
	}

	boolean sampleFloatMemberFieldNotEqualsComp(Data data) {
		return floatMember!=data.getValue();
	}

	public void testFloatMemberFieldNotEqualsComp() throws Exception {
		assertComparison("sampleFloatMemberFieldNotEqualsComp",new String[]{FLOAT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember"),ComparisonOperator.EQUALS,true);
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
		assertComparison("sampleIntEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,true);
	}

	boolean sampleIntNotEqualsNot(Data data) {
		return !(data.id!=INT_CMPVAL);
	}
	
	public void testIntNotEqualsNot() throws Exception {
		assertComparison("sampleIntNotEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntGreaterNot(Data data) {
		return !(data.id>INT_CMPVAL);
	}
	
	public void testIntGreaterNot() throws Exception {
		assertComparison("sampleIntGreaterNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleIntSmallerEqualsNot(Data data) {
		return !(data.id<=INT_CMPVAL);
	}
	
	public void testIntSmallerEqualsNot() throws Exception {
		assertComparison("sampleIntSmallerEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntNotNot(Data data) {
		return !(!(data.id<INT_CMPVAL));
	}
	
	public void testIntNotNot() throws Exception {
		assertComparison("sampleIntNotNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	// conjunctions
	
	boolean sampleIntIntAnd(Data data) {
		return (data.id>42)&&(data.id<100);
	}
	
	public void testIntIntAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleIntIntAnd");
		assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.GREATER,false);
		assertComparison(expr.right(),new String[]{"id"},new Integer(100),ComparisonOperator.SMALLER,false);
	}

	boolean sampleStringIntOr(Data data) {
		return (data.name.equals("Foo"))||(data.id==42);
	}

	public void testStringIntOr() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleStringIntOr");
		assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,false);
		ComparisonExpression right=(ComparisonExpression)expr.right();
		assertComparison(right,new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntStringNotOr(Data data) {
		return !((data.id==42)||(data.name.equals("Foo")));
	}

	public void testIntStringNotOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleIntStringNotOr");
		assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,true);
		assertComparison(expr.right(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,true);
	}

	boolean sampleOuterOrInnerAnd(Data data) {
		return (data.id==42)&&(data.getName().equals("Bar"))||(data.name.equals("Foo"));
	}

	public void testOuterOrInnerAnd() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleOuterOrInnerAnd");
		assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,false);
		AndExpression andExpr=(AndExpression)expr.right();
		assertComparison(andExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,false);
		assertComparison(andExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.EQUALS,false);
	}

	boolean sampleOuterAndInnerOr(Data data) {
		return ((data.id<42)||(data.getName().equals("Bar")))&&(data.getId()>10);
	}

	public void testOuterAndInnerOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleOuterAndInnerOr");
		assertComparison(expr.left(),new String[]{"id"},new Integer(10),ComparisonOperator.GREATER,false);
		OrExpression orExpr=(OrExpression)expr.right();
		assertComparison(orExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.SMALLER,false);
		assertComparison(orExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.EQUALS,false);
	}

	// arithmetic
	
	boolean sampleSanityIntAdd(Data data) {
		return data.id<INT_CMPVAL+INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntAdd() throws Exception {
		assertComparison("sampleSanityIntAdd",INT_FIELDNAME,new Integer(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleSanityIntMultiply(Data data) {
		return data.id<2*INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntMultiply() throws Exception {
		assertComparison("sampleSanityIntMultiply",INT_FIELDNAME,new Integer(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleMemberIntMultiply(Data data) {
		return data.id<2*intMember;
	}
	
	public void testMemberIntMultiply() throws Exception {
		assertComparison("sampleMemberIntMultiply",INT_FIELDNAME,new ArithmeticExpression(new ConstValue(new Integer(2)),new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),ArithmeticOperator.MULTIPLY),ComparisonOperator.SMALLER,false);
	}

	boolean sampleIntMemberDivide(Data data) {
		return data.id>intMember/2;
	}
	
	public void testIntMemberDivide() throws Exception {
		assertComparison("sampleIntMemberDivide",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),new ConstValue(new Integer(2)),ArithmeticOperator.DIVIDE),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntMemberMemberAdd(Data data) {
		return data.id==intMember+intMember;
	}
	
	public void testIntMemberMemberAdd() throws Exception {
		assertComparison("sampleIntMemberMemberAdd",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),ArithmeticOperator.ADD),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntAddInPredicateMethod(Data data) {
		return data.getId()==intMemberPlusOne();
	}

	public void testIntAddInPredicateMethod() throws Exception {
		assertComparison("sampleIntAddInPredicateMethod",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(PredicateFieldRoot.INSTANCE,"intMember"),new ConstValue(new Integer(1)),ArithmeticOperator.ADD),ComparisonOperator.EQUALS,false);
	}

	// TODO: string append via '+'?
	
	// not applicable
	
	boolean sampleInvalidOtherMemberEqualsComp(Data data) {
		return stringMember.equals(STRING_CMPVAL);
	}

	public void testInvalidOtherMemberEqualsComp() throws Exception {
		assertInvalid("sampleInvalidOtherMemberEqualsComp");
	}

	boolean sampleInvalidOtherMemberSameComp(Data data) {
		return stringMember==STRING_CMPVAL;
	}

	public void testInvalidOtherMemberSameComp() throws Exception {
		assertInvalid("sampleInvalidOtherMemberSameComp");
	}

	boolean sampleInvalidCandidateMemberArithmetic(Data data) {
		return data.id-1==INT_CMPVAL;
	}

	public void testInvalidCandidateMemberArithmetic() throws Exception {
		assertInvalid("sampleInvalidCandidateMemberArithmetic");
	}

	boolean sampleInvalidTemporaryStorage(Data data) {
		int val=INT_CMPVAL-1;
		return data.id==val;
	}

	public void testInvalidTemporaryStorage() throws Exception {
		assertInvalid("sampleInvalidTemporaryStorage");
	}

	boolean sampleInvalidStaticMethodCall(Data data) {
		return data.id==Integer.parseInt(data.name);
	}

	public void testInvalidStaticMethodCall() throws Exception {
		assertInvalid("sampleInvalidStaticMethodCall");
	}

	boolean sampleInvalidMethodCall(Data data) {
		data.someMethod();
		return true;
	}

	public void testInvalidMethodCall() throws Exception {
		assertInvalid("sampleInvalidMethodCall");
	}

	boolean sampleExternalMethodCall(Data data) {
		return data.next==new Data().getNext();
	}

	public void testExternalMethodCall() throws Exception {
		assertInvalid("sampleExternalMethodCall");
	}

	boolean sampleSimpleObjectComparison(Data data) {
		return data.equals(new Data());
	}

	public void testSimpleObjectComparison() throws Exception {
		assertInvalid("sampleSimpleObjectComparison");
	}

	boolean sampleSimpleFieldObjectComparison(Data data) {
		return data.next.equals(new Data());
	}

	public void testSimpleFieldObjectComparison() throws Exception {
		assertInvalid("sampleSimpleFieldObjectComparison");
	}

	boolean sampleSimpleFieldObjectIdentityComparison(Data data) {
		return data.next.equals(data.next);
	}

	public void testSimpleFieldObjectIdentityComparison() throws Exception {
		assertInvalid("sampleSimpleFieldObjectIdentityComparison");
	}

	boolean sampleCandEqualsNullComparison(Data data) {
		return data.equals(null);
	}

	public void testCandEqualsNullComparison() throws Exception {
		assertInvalid("sampleCandEqualsNullComparison");
	}

	boolean sampleCandIdentityObjectComparison(Data data) {
		return data.equals(data);
	}

	public void testCandIdentityObjectComparison() throws Exception {
		assertInvalid("sampleCandIdentityObjectComparison");
	}

	boolean sampleRecursiveCall(Data data) {
		return sampleRecursiveCall(data);
	}

	public void testRecursiveCall() throws Exception {
		assertInvalid("sampleRecursiveCall");
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
		assertEquals(CandidateFieldRoot.INSTANCE,fieldValue.root());
		Iterator4 foundNames=fieldValue.fieldNames();
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

	private void assertInvalid(String methodName) throws ClassNotFoundException {
		Expression expression = expression(methodName);
		assertNull(expression);
	}
	
	private Expression expression(String methodName) throws ClassNotFoundException {
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(bloatUtil);	
		FlowGraph flowGraph=bloatUtil.flowGraph(getClass().getName(),methodName);
//		flowGraph.visit(new PrintVisitor());
//		flowGraph.visit(new TreeStructureVisitor());
		flowGraph.visit(visitor);
		return visitor.expression();		
	}
}
