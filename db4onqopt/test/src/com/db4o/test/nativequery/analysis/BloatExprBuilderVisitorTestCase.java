/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.analysis;

import EDU.purdue.cs.bloat.cfg.FlowGraph;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.tree.PrintVisitor;

import com.db4o.nativequery.NQDebug;
import com.db4o.nativequery.analysis.BloatExprBuilderVisitor;
import com.db4o.nativequery.bloat.BloatUtil;
import com.db4o.nativequery.bloat.Db4oClassSource;
import com.db4o.nativequery.expr.AndExpression;
import com.db4o.nativequery.expr.BoolConstExpression;
import com.db4o.nativequery.expr.ComparisonExpression;
import com.db4o.nativequery.expr.Expression;
import com.db4o.nativequery.expr.OrExpression;
import com.db4o.nativequery.expr.cmp.ArithmeticExpression;
import com.db4o.nativequery.expr.cmp.ArithmeticOperator;
import com.db4o.nativequery.expr.cmp.ArrayAccessValue;
import com.db4o.nativequery.expr.cmp.ComparisonOperand;
import com.db4o.nativequery.expr.cmp.ComparisonOperator;
import com.db4o.nativequery.expr.cmp.ConstValue;
import com.db4o.nativequery.expr.cmp.FieldValue;
import com.db4o.nativequery.expr.cmp.MethodCallValue;
import com.db4o.nativequery.expr.cmp.field.PredicateFieldRoot;
import com.db4o.nativequery.expr.cmp.field.StaticFieldRoot;

import db4ounit.Assert;
import db4ounit.Test;
import db4ounit.TestMethod;
import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.extensions.AbstractDb4oTestCase;


public class BloatExprBuilderVisitorTestCase extends AbstractDb4oTestCase {	
	private static final String INT_WRAPPED_FIELDNAME = "idWrap";
	private static final String BOOLEAN_FIELDNAME = "bool";
	private static final String BOOLEAN_WRAPPED_FIELDNAME = "boolWrapper";
	private static final String INT_FIELDNAME = "id";
	private static final String FLOAT_FIELDNAME = "value";
	private static final String OTHER_FLOAT_FIELDNAME = "otherValue";
	private static final String DATA_FIELDNAME="next";
	private static final String STRING_FIELDNAME = "name";
	private final static boolean BOOLEAN_CMPVAL=false;
	private final static int INT_CMPVAL=42;
	private final static float FLOAT_CMPVAL=12.3f;
	private final static String STRING_CMPVAL="Test";
	private final static Integer INT_WRAPPER_CMPVAL=new Integer(INT_CMPVAL);
	private final static Boolean BOOLEAN_WRAPPER_CMPVAL=Boolean.TRUE;
	private final Integer intWrapperCmpVal=new Integer(INT_CMPVAL);
	
	private boolean boolMember=false;
	private String stringMember="foo";
	private int intMember=43;
	private float floatMember=47.11f;
	private int[] intArrayMember={};
	private Data[] objArrayMember={};

	private ClassFileLoader loader;
	private BloatUtil bloatUtil;
	
	private int intMemberPlusOne() {
		return intMember+1;
	}

	private int sum(int a,int b) {
		return a+b;
	}
	
	public void db4oSetupBeforeStore() throws Exception {

		loader=new ClassFileLoader( 
						new Db4oClassSource(db().ext().reflector())
						);
		bloatUtil=new BloatUtil(loader);
	}
	
	// unconditional

	boolean sampleTrue(Data data) {
		return true;
	}

	public void testTrue() throws Exception {
		Assert.areEqual(BoolConstExpression.TRUE,expression("sampleTrue"));
	}

	boolean sampleFalse(Data data) {
		return false;
	}

	public void testFalse() throws Exception {
		Assert.areEqual(BoolConstExpression.FALSE,expression("sampleFalse"));
	}

	// primitive identity

	// boolean
	
	boolean sampleFieldBooleanComp(Data data) {
		return data.bool;
	}

	public void testFieldBooleanComp() throws Exception {
		assertComparison("sampleFieldBooleanComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldBooleanConstantEqualsComp(Data data) {
		return data.bool==true;
	}

	public void testFieldBooleanConstantEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanConstantEqualsComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleUnnecessarilyComplicatedFieldBooleanConstantEqualsComp(Data data) {
		if(data.bool) {
			return true;
		}
		return false;
	}

	public void _testUnnecessarilyComplicatedFieldBooleanConstantEqualsComp() throws Exception {
		assertComparison("sampleUnnecessarilyComplicatedFieldBooleanConstantEqualsComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldBooleanNotComp(Data data) {
		return !data.bool;
	}

	public void testFieldBooleanNotComp() throws Exception {
		assertComparison("sampleFieldBooleanNotComp",BOOLEAN_FIELDNAME,Boolean.FALSE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldBooleanEqualsComp(Data data) {
		return data.bool==boolMember;
	}

	public void testFieldBooleanEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanEqualsComp",BOOLEAN_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"boolMember","Z"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleBooleanFieldEqualsComp(Data data) {
		return boolMember==data.bool;
	}

	public void testBooleanFieldEqualsComp() throws Exception {
		assertComparison("sampleBooleanFieldEqualsComp",BOOLEAN_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"boolMember","Z"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldBooleanNotEqualsComp(Data data) {
		return data.bool!=boolMember;
	}

	public void testFieldBooleanNotEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanNotEqualsComp",BOOLEAN_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"boolMember","Z"),ComparisonOperator.EQUALS,true);
	}

	boolean sampleBooleanFieldNotEqualsComp(Data data) {
		return boolMember!=data.bool;
	}

	public void testBooleanFieldNotEqualsComp() throws Exception {
		assertComparison("sampleBooleanFieldNotEqualsComp",BOOLEAN_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"boolMember","Z"),ComparisonOperator.EQUALS,true);
	}

	// int
	
	boolean sampleFieldIntZeroEqualsComp(Data data) {
		return data.id==0;
	}

	public void testFieldIntZeroEqualsComp() throws Exception {
		assertComparison("sampleFieldIntZeroEqualsComp",INT_FIELDNAME,new Integer(0),ComparisonOperator.EQUALS,false);
	}

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
	
	boolean sampleFieldFloatZeroEqualsComp(Data data) {
		return data.value==0.0f;
	}

	public void testFieldFloatZeroEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatZeroEqualsComp",FLOAT_FIELDNAME,new Float(0.0f),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldFloatZeroIntEqualsComp(Data data) {
		return data.value==0;
	}

	public void testFieldFloatZeroIntEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatZeroIntEqualsComp",FLOAT_FIELDNAME,new Float(0.0f),ComparisonOperator.EQUALS,false);
	}

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

	// TODO: nonsense, but need single roots for method calls
	boolean sampleCandidateIdentity(Data data) {
		return data==null;
	}

	public void _testCandidateIdentity() throws Exception {
		assertComparison("sampleCandidateIdentity",new String[]{},null,ComparisonOperator.EQUALS,false);
	}

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

	boolean sampleMemberIntSmallerEqualsComp(Data data) {
		return intMember<=data.id;
	}

	public void testMemberIntSmallerEqualsComp() throws Exception {
		assertComparison("sampleMemberIntSmallerEqualsComp",INT_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),ComparisonOperator.SMALLER,true);
	}
	
	boolean sampleMemberFloatSmallerEqualsComp(Data data) {
		return floatMember<=data.value;
	}

	public void testMemberFloatSmallerEqualsComp() throws Exception {
		assertComparison("sampleMemberFloatSmallerEqualsComp",FLOAT_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember","F"),ComparisonOperator.SMALLER,true);
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
	
	// string-specific comparisons

	boolean sampleFieldStringContains(Data data) {
		return data.name.contains(STRING_CMPVAL);
	}

	public void testFieldStringContains() throws Exception {
		assertComparison("sampleFieldStringContains",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.CONTAINS,false);
	}

	boolean sampleFieldStringContainsWrongWay(Data data) {
		return STRING_CMPVAL.contains(data.name);
	}

	public void testFieldStringContainsWrongWay() throws Exception {
		assertInvalid("sampleFieldStringContainsWrongWay");
	}

	boolean sampleFieldStringStartsWith(Data data) {
		return data.name.startsWith(STRING_CMPVAL);
	}

	public void testFieldStringStartsWith() throws Exception {
		assertComparison("sampleFieldStringStartsWith",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.STARTSWITH,false);
	}

	boolean sampleFieldStringStartsWithWrongWay(Data data) {
		return STRING_CMPVAL.startsWith(data.name);
	}

	public void testFieldStringStartsWithWrongWay() throws Exception {
		assertInvalid("sampleFieldStringStartsWithWrongWay");
	}

	boolean sampleFieldStringEndsWith(Data data) {
		return data.name.endsWith(STRING_CMPVAL);
	}

	public void testFieldStringEndsWith() throws Exception {
		assertComparison("sampleFieldStringEndsWith",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.ENDSWITH,false);
	}

	boolean sampleFieldStringEndsWithWrongWay(Data data) {
		return STRING_CMPVAL.endsWith(data.name);
	}

	public void testFieldStringEndsWithWrongWay() throws Exception {
		assertInvalid("sampleFieldStringEndsWithWrongWay");
	}

	boolean sampleFieldStringToLowerCaseStartsWith(Data data) throws Exception {
		return data.getName().toLowerCase().startsWith(STRING_CMPVAL);
	}

	public void testFieldStringToLowerCaseStartsWith() throws Exception {
		assertInvalid("sampleFieldStringToLowerCaseStartsWith");
	}

	// primitive wrapper equality

	boolean sampleFieldBooleanWrapperEqualsComp(Data data) {
		return data.boolWrapper.booleanValue();
	}

	public void testFieldBooleanWrapperEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanWrapperEqualsComp",BOOLEAN_WRAPPED_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(intWrapperCmpVal);
	}

	public void testFieldIntWrapperEqualsComp() throws Exception {
		assertComparison("sampleFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal","java.lang.Integer"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntWrapperFieldEqualsComp(Data data) {
		return intWrapperCmpVal.equals(data.getIdWrapped());
	}

	public void testIntWrapperFieldEqualsComp() throws Exception {
		assertComparison("sampleIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal","java.lang.Integer"),ComparisonOperator.EQUALS,false);
	}	
	
	// descend into primitive wrapper

	boolean sampleWrapperFieldValueIntSameComp(Data data) {
		return data.getIdWrapped().intValue()==INT_CMPVAL;
	}

	public void testWrapperFieldValueIntSameComp() throws Exception {
		assertComparison("sampleWrapperFieldValueIntSameComp",INT_WRAPPED_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.EQUALS,false);
	}	

	boolean sampleNotValueBoolWrapperFieldSameComp(Data data) {
		return data.boolWrapper.booleanValue();
	}

	public void testNotValueBoolWrapperFieldSameComp() throws Exception {
		assertComparison("sampleNotValueBoolWrapperFieldSameComp",BOOLEAN_WRAPPED_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}	

	// primitive field against wrapper

	boolean sampleFieldWrapperIntSameComp(Data data) {
		return data.getId()==INT_WRAPPER_CMPVAL.intValue();
	}

	public void testFieldWrapperIntSameComp() throws Exception {
		assertComparison("sampleFieldWrapperIntSameComp",INT_FIELDNAME,new MethodCallValue(new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"INT_WRAPPER_CMPVAL",Integer.class.getName()),"intValue",new Class[0],new ComparisonOperand[0]),ComparisonOperator.EQUALS,false);
	}	

	boolean sampleBoolWrapperFieldSameComp(Data data) {
		return data.bool==BOOLEAN_WRAPPER_CMPVAL.booleanValue();
	}

	public void testBoolWrapperFieldSameComp() throws Exception {
		assertComparison("sampleBoolWrapperFieldSameComp",BOOLEAN_FIELDNAME,new MethodCallValue(new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"BOOLEAN_WRAPPER_CMPVAL",Boolean.class.getName()),"booleanValue",new Class[0],new ComparisonOperand[0]),ComparisonOperator.EQUALS,false);
	}	

	// wrapper comparison

	boolean sampleFieldWrapperIntCompToEquals(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)==0;
	}

	public void testFieldWrapperIntCompToEquals() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToEquals",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"INT_WRAPPER_CMPVAL",Integer.class.getName()),ComparisonOperator.EQUALS,false);
	}	

	boolean sampleFieldWrapperIntCompToNotEquals(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)!=0;
	}

	public void testFieldWrapperIntCompToNotEquals() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToNotEquals",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"INT_WRAPPER_CMPVAL",Integer.class.getName()),ComparisonOperator.EQUALS,true);
	}	

	boolean sampleFieldWrapperIntCompToGreater(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)>0;
	}

	public void testFieldWrapperIntCompToGreater() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToGreater",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"INT_WRAPPER_CMPVAL",Integer.class.getName()),ComparisonOperator.GREATER,false);
	}	

	boolean sampleFieldWrapperIntCompToLE(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)<=0;
	}

	public void testFieldWrapperIntCompToLE() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToLE",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(BloatExprBuilderVisitorTestCase.class.getName()),"INT_WRAPPER_CMPVAL",Integer.class.getName()),ComparisonOperator.GREATER,true);
	}	

	//static member comparison

	boolean sampleStaticFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(INT_WRAPPER_CMPVAL);
	}

	public void testStaticFieldIntWrapperEqualsComp() throws Exception {
		//assertInvalid("sampleStaticFieldIntWrapperEqualsComp");
		assertComparison("sampleStaticFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(getClass().getName()),"INT_WRAPPER_CMPVAL","java.lang.Integer"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleStaticIntWrapperFieldEqualsComp(Data data) {
		return INT_WRAPPER_CMPVAL.equals(data.getIdWrapped());
	}

	public void testStaticIntWrapperFieldEqualsComp() throws Exception {
		//assertInvalid("sampleStaticIntWrapperFieldEqualsComp");
		assertComparison("sampleStaticIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,new FieldValue(new StaticFieldRoot(getClass().getName()),"INT_WRAPPER_CMPVAL","java.lang.Integer"),ComparisonOperator.EQUALS,false);
	}	
	
	// getter

	boolean sampleGetterBoolComp(Data data) {
		return data.getBool();
	}

	public void testGetterBoolComp() throws Exception {
		assertComparison("sampleGetterBoolComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	// TODO fails when run via Ant?!?
	boolean sampleBoolGetterNotEqualsComp(Data data) {
		return BOOLEAN_CMPVAL!=data.getBool();
	}

	public void _testBoolGetterNotEqualsComp() throws Exception {
		assertComparison("sampleBoolGetterNotEqualsComp",BOOLEAN_FIELDNAME,Boolean.valueOf(!BOOLEAN_CMPVAL),ComparisonOperator.EQUALS,false);
	}

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
		assertComparison("sampleGetterCascadeFloatFieldGreaterEqualsComp",new String[]{DATA_FIELDNAME,FLOAT_FIELDNAME},new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	// member field comparison

	boolean sampleFieldIntMemberEqualsComp(Data data) {
		return data.getId()==intMember;
	}

	public void testFieldIntMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldIntMemberEqualsComp",new String[]{INT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntMemberFieldGreaterEqualsComp(Data data) {
		return intMember>=data.getId();
	}

	public void testIntMemberFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntMemberFieldGreaterEqualsComp",new String[]{INT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldStringMemberEqualsComp(Data data) {
		return data.getName().equals(stringMember);
	}

	public void testFieldStringMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldStringMemberEqualsComp",new String[]{STRING_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"stringMember","java.lang.String"),ComparisonOperator.EQUALS,false);
	}

	boolean sampleFieldFloatMemberNotEqualsComp(Data data) {
		return data.getValue()!=floatMember;
	}

	public void testFieldFloatMemberNotEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatMemberNotEqualsComp",new String[]{FLOAT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember","F"),ComparisonOperator.EQUALS,true);
	}

	boolean sampleFloatMemberFieldNotEqualsComp(Data data) {
		return floatMember!=data.getValue();
	}

	public void testFloatMemberFieldNotEqualsComp() throws Exception {
		assertComparison("sampleFloatMemberFieldNotEqualsComp",new String[]{FLOAT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember","F"),ComparisonOperator.EQUALS,true);
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

	boolean sampleBoolBoolAnd(Data data) {
		return !data.getBool()&&data.getBool();
	}
	
	public void testBoolBoolAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleBoolBoolAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{BOOLEAN_FIELDNAME},Boolean.FALSE,ComparisonOperator.EQUALS,false);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{BOOLEAN_FIELDNAME},Boolean.TRUE,ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntIntAnd(Data data) {
		return (data.id>42)&&(data.id<100);
	}
	
	public void testIntIntAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleIntIntAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.GREATER,false);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{"id"},new Integer(100),ComparisonOperator.SMALLER,false);
	}

	boolean sampleStringIntOr(Data data) {
		return (data.name.equals("Foo"))||(data.id==42);
	}

	public void testStringIntOr() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleStringIntOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,false);
		ComparisonExpression right=(ComparisonExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(right,new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,false);
	}

	boolean sampleIntStringNotOr(Data data) {
		return !((data.id==42)||(data.name.equals("Foo")));
	}

	public void testIntStringNotOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleIntStringNotOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,true);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,true);
	}

	boolean sampleOuterOrInnerAnd(Data data) {
		return (data.id==42)&&(data.getName().equals("Bar"))||(data.name.equals("Foo"));
	}

	public void testOuterOrInnerAnd() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleOuterOrInnerAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.EQUALS,false);
		AndExpression andExpr=(AndExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(andExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.EQUALS,false);
		NQOptimizationAssertUtil.assertComparison(andExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.EQUALS,false);
	}

	boolean sampleOuterAndInnerOr(Data data) {
		return ((data.id<42)||(data.getName().equals("Bar")))&&(data.getId()>10);
	}

	public void testOuterAndInnerOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleOuterAndInnerOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(10),ComparisonOperator.GREATER,false);
		OrExpression orExpr=(OrExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(orExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.SMALLER,false);
		NQOptimizationAssertUtil.assertComparison(orExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.EQUALS,false);
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
		assertComparison("sampleMemberIntMultiply",INT_FIELDNAME,new ArithmeticExpression(new ConstValue(new Integer(2)),new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),ArithmeticOperator.MULTIPLY),ComparisonOperator.SMALLER,false);
	}

	boolean sampleIntMemberDivide(Data data) {
		return data.id>intMember/2;
	}
	
	public void testIntMemberDivide() throws Exception {
		assertComparison("sampleIntMemberDivide",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),new ConstValue(new Integer(2)),ArithmeticOperator.DIVIDE),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntMemberMemberAdd(Data data) {
		return data.id==intMember+intMember;
	}
	
	public void testIntMemberMemberAdd() throws Exception {
		assertComparison("sampleIntMemberMemberAdd",INT_FIELDNAME,new ArithmeticExpression(new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),ArithmeticOperator.ADD),ComparisonOperator.EQUALS,false);
	}

	// array access
	
	boolean sampleIntArrayAccess(Data data) {
		return data.id==intArrayMember[0];
	}

	public void testIntArrayAccess() throws Exception {
		assertComparison("sampleIntArrayAccess","id",new ArrayAccessValue(new FieldValue(PredicateFieldRoot.INSTANCE,"intArrayMember","[I"),new ConstValue(new Integer(0))),ComparisonOperator.EQUALS,false);
	}

	boolean sampleObjectArrayAccess(Data data) {
		return data.next.id==objArrayMember[0].id;
	}

	public void testObjectArrayAccess() throws Exception {
		assertComparison("sampleObjectArrayAccess",new String[]{"next","id"},new FieldValue(new ArrayAccessValue(new FieldValue(PredicateFieldRoot.INSTANCE,"objArrayMember","[L"+Data.class.getName()+";"),new ConstValue(new Integer(0))),"id","I"),ComparisonOperator.EQUALS,false);
	}

	
	// non-candidate method calls

	boolean sampleIntAddInPredicateMethod(Data data) {
		return data.getId()==intMemberPlusOne();
	}

	public void testIntAddInPredicateMethod() throws Exception {
		assertComparison("sampleIntAddInPredicateMethod",INT_FIELDNAME,new MethodCallValue(PredicateFieldRoot.INSTANCE,"intMemberPlusOne",new Class[]{},new ComparisonOperand[]{}),ComparisonOperator.EQUALS,false);
	}

	boolean sampleStaticMethodCall(Data data) {
		return data.id==Integer.parseInt(stringMember);
	}

	public void testStaticMethodCall() throws Exception {
		assertComparison("sampleStaticMethodCall",INT_FIELDNAME,new MethodCallValue(new StaticFieldRoot(Integer.class.getName()),"parseInt",new Class[]{String.class},new ComparisonOperand[]{new FieldValue(PredicateFieldRoot.INSTANCE,"stringMember","java.lang.String")}),ComparisonOperator.EQUALS,false);
	}

	boolean sampleTwoParamMethodCall(Data data) {
		return data.id==sum(intMember,0);
	}

	public void testTwoParamMethodCall() throws Exception {
		assertComparison("sampleTwoParamMethodCall",INT_FIELDNAME,new MethodCallValue(PredicateFieldRoot.INSTANCE,"sum",new Class[]{Integer.TYPE,Integer.TYPE},new ComparisonOperand[]{new FieldValue(PredicateFieldRoot.INSTANCE,"intMember","I"),new ConstValue(new Integer(0))}),ComparisonOperator.EQUALS,false);
	}

	// multiple methods with the same name
	
	boolean sampleTimesValueMethodEqualsComp(Data data) {
		return data.getValue(INT_CMPVAL)==floatMember;
	}

	public void testTimesValueMethodEqualsComp() throws Exception {
		assertComparison("sampleTimesValueMethodEqualsComp",new String[]{OTHER_FLOAT_FIELDNAME},new FieldValue(PredicateFieldRoot.INSTANCE,"floatMember","F"),ComparisonOperator.EQUALS,false);
	}
	
	// not applicable
	
	// TODO: definitely applicable - fix!
	boolean sampleInvalidOtherMemberEqualsComp(Data data) {
		return stringMember.equals(STRING_CMPVAL);
	}

	public void testInvalidOtherMemberEqualsComp() throws Exception {
		assertInvalid("sampleInvalidOtherMemberEqualsComp");
	}

	boolean sampleInvalidLocalVarComp(Data data) {
		Data next=data.next;
		return next.bool;
	}
	
	public void testInvalidLocalVarComp() throws Exception {
		assertInvalid("sampleInvalidLocalVarComp");
	}

	boolean sampleInvalidLocalVarCombinedComp(Data data) {
		Data next=data.next;
		return next.bool && data.bool;
	}
		
	public void testInvalidLocalVarCombinedComp() throws Exception {
		assertInvalid("sampleInvalidLocalVarCombinedComp");
	}
	
	boolean sampleInvalidNotOptimizableMethodCallCombined(Data data) {
		return data.getName().indexOf(STRING_CMPVAL)>=0&&data.bool;
	}
	
	public void testInvalidNotOptimizableMethodCallCombined() throws Exception {
		assertInvalid("sampleInvalidNotOptimizableMethodCallCombined");
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

	boolean sampleInvalidMethodCall(Data data) {
		data.someMethod();
		return true;
	}

	public void testInvalidMethodCall() throws Exception {
		assertInvalid("sampleInvalidMethodCall");
	}

	boolean sampleInvalidConstructorCall(Data data) {
		return data.next==new Data().getNext();
	}

	public void testInvalidConstructorCall() throws Exception {
		assertInvalid("sampleInvalidConstructorCall");
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

	boolean sampleCandidateIntArrayAccess(Data data) {
		return data.intArray[0]==0;
	}

	public void testCandidateIntArrayAccess() throws Exception {
		assertInvalid("sampleCandidateIntArrayAccess");
	}

	boolean sampleCandidateObjectArrayAccess(Data data) {
		return data.objArray[0].id==0;
	}

	public void testCandidateObjectArrayAccess() throws Exception {
		assertInvalid("sampleCandidateObjectArrayAccess");
	}

	boolean sampleCandidateParamMethodCall(Data data) {
		return data.id==sum(data.id,0);
	}

	public void testCandidateParamMethodCall() throws Exception {
		assertInvalid("sampleCandidateParamMethodCall");
	}

	boolean sampleCandidateParamStaticMethodCall(Data data) {
		return data.id==Integer.parseInt(data.name);
	}

	public void testCandidateParamStaticMethodCall() throws Exception {
		assertInvalid("sampleCandidateParamStaticMethodCall");
	}

	boolean sampleSwitch(Data data) {
		switch(data.id) {
			case 0:
			case 1:
			case 2:
			case 4:
				return true;
			default:
				return false;
		}
	}

	public void testSwitch() throws Exception {
		assertInvalid("sampleSwitch");
	}

	boolean sampleStringAppend(Data data) {
		return data.name.equals(stringMember+"X");
	}

	public void testStringAppend() throws Exception {
		assertInvalid("sampleStringAppend");
	}

	boolean sampleExternalWrapperComp(Data data) {
		return INT_WRAPPER_CMPVAL.compareTo(INT_WRAPPER_CMPVAL)==0;
	}

	public void testExternalWrapperComp() throws Exception {
		assertInvalid("sampleExternalWrapperComp");
	}

	boolean sampleNotApplicableIfCondition(Data data) {
		if(stringMember.equals("XXX")) {
			return data.getName().equals(STRING_CMPVAL);
		}
		else {
			return false;
		}
	}

	public void testNotApplicableIfCondition() throws Exception {
		assertInvalid("sampleNotApplicableIfCondition");
	}

	boolean sampleNotApplicableIfStringAppendCondition(Data data) {
		if(stringMember.equals(stringMember+"X")) {
			return data.getName().equals(STRING_CMPVAL);
		}
		else {
			return false;
		}
	}

	public void testNotApplicableIfStringAppendCondition() throws Exception {
		assertInvalid("sampleNotApplicableIfStringAppendCondition");
	}

	boolean sampleNotApplicableSideEffectIfThenBranch(Data data) {
		if(data.getName().equals("foo")) {
			intMember+=data.getId();
			return true;
		}
		else {
			return false;
		}
	}

	public void testNotApplicableSideEffectIfThenBranch() throws Exception {
		assertInvalid("sampleNotApplicableSideEffectIfThenBranch");
	}

	// internal
	
	private void assertComparison(String methodName, String fieldName,Object value, ComparisonOperator op,boolean negated) {
		assertComparison(methodName,new String[]{fieldName},value,op,negated);
	}

	private void assertComparison(String methodName, String[] fieldNames,Object value, ComparisonOperator op,boolean negated) {
		try {
			Expression expr = expression(methodName);
			NQOptimizationAssertUtil.assertComparison(expr, fieldNames, value, op, negated);
		} catch (ClassNotFoundException e) {
			Assert.fail(e.getMessage());
		}
	}

	private void assertInvalid(String methodName) throws ClassNotFoundException {
		Expression expression = expression(methodName);
		if(expression!=null) {
			System.err.println(expression);
		}
		Assert.isNull(expression);
	}
	
	private Expression expression(String methodName) throws ClassNotFoundException {
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(bloatUtil);	
		FlowGraph flowGraph=bloatUtil.flowGraph(getClass().getName(),methodName);
		if(NQDebug.LOG) {
			flowGraph.visit(new PrintVisitor());
//		flowGraph.visit(new TreeStructureVisitor());
		}
		flowGraph.visit(visitor);
		Expression expr = visitor.expression();
		if(NQDebug.LOG) {
			System.out.println(expr);
		}
		return expr;		
	}
	
	public static void main(String[] args) throws Exception {
		java.lang.reflect.Method method=BloatExprBuilderVisitorTestCase.class.getMethod("testFieldWrapperIntCompToEquals",new Class[]{});
		Test[] tests={
				new TestMethod(new BloatExprBuilderVisitorTestCase(),method)
		};
		TestSuite suite=new TestSuite(tests);
		new TestRunner(suite).run();
	}
}
