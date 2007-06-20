/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.ClassSource;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.build.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;
import com.db4o.reflect.Reflector;

public class BloatExprBuilderVisitor extends TreeVisitor {

	// TODO discuss: drop or make configurable
	private final static int MAX_DEPTH = 10;

	private final static String[] PRIMITIVE_WRAPPER_NAMES = {
			Boolean.class.getName(), Byte.class.getName(),
			Short.class.getName(), Character.class.getName(),
			Integer.class.getName(), Long.class.getName(),
			Double.class.getName(), Float.class.getName(),
			String.class.getName(), };

	static {
		Arrays.sort(PRIMITIVE_WRAPPER_NAMES);
	}

	private final static ExpressionBuilder BUILDER = new ExpressionBuilder();

	private final static Map BUILDERS = new HashMap();

	private final static Map OP_SYMMETRY = new HashMap();

	private static class ComparisonBuilder {
		private ComparisonOperator op;

		public ComparisonBuilder(ComparisonOperator op) {
			this.op = op;
		}

		public Expression buildComparison(FieldValue fieldValue,
				ComparisonOperand valueExpr) {
			if(isBooleanField(fieldValue)) {
				if(valueExpr instanceof ConstValue) {
					ConstValue constValue = (ConstValue) valueExpr;
					if(constValue.value() instanceof Integer) {
						Integer intValue = (Integer) constValue.value();
						Boolean boolValue = (intValue.intValue()==0 ? Boolean.FALSE : Boolean.TRUE);
						valueExpr = new ConstValue(boolValue);
					}
				}
			}
			return new ComparisonExpression(fieldValue, valueExpr, op);
		}
	}

	private static class NegateComparisonBuilder extends ComparisonBuilder {
		public NegateComparisonBuilder(ComparisonOperator op) {
			super(op);
		}

		public Expression buildComparison(FieldValue fieldValue,
				ComparisonOperand valueExpr) {
			return BUILDER.not(super.buildComparison(fieldValue, valueExpr));
		}
	}

	private static class BuilderSpec {
		private int _op;
		private boolean _primitive;

		public BuilderSpec(int op, boolean primitive) {
			this._op = op;
			this._primitive = primitive;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + _op;
			result = prime * result + (_primitive ? 1231 : 1237);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final BuilderSpec other = (BuilderSpec) obj;
			if (_op != other._op)
				return false;
			if (_primitive != other._primitive)
				return false;
			return true;
		}		
	}
	
	static {
		BUILDERS.put(new BuilderSpec(IfStmt.EQ,false), new ComparisonBuilder(
				ComparisonOperator.IDENTITY));
		BUILDERS.put(new BuilderSpec(IfStmt.EQ,true), new ComparisonBuilder(
				ComparisonOperator.EQUALS));
		BUILDERS.put(new BuilderSpec(IfStmt.NE,false), new NegateComparisonBuilder(
				ComparisonOperator.IDENTITY));
		BUILDERS.put(new BuilderSpec(IfStmt.NE,true), new NegateComparisonBuilder(
				ComparisonOperator.EQUALS));
		BUILDERS.put(new BuilderSpec(IfStmt.LT,false), new ComparisonBuilder(
				ComparisonOperator.SMALLER));
		BUILDERS.put(new BuilderSpec(IfStmt.LT,true),builder(IfStmt.LT,false));
		BUILDERS.put(new BuilderSpec(IfStmt.GT,false), new ComparisonBuilder(
				ComparisonOperator.GREATER));
		BUILDERS.put(new BuilderSpec(IfStmt.GT,true),builder(IfStmt.GT,false));
		BUILDERS.put(new BuilderSpec(IfStmt.LE,false), new NegateComparisonBuilder(
				ComparisonOperator.GREATER));
		BUILDERS.put(new BuilderSpec(IfStmt.LE,true),builder(IfStmt.LE,false));
		BUILDERS.put(new BuilderSpec(IfStmt.GE,false), new NegateComparisonBuilder(
				ComparisonOperator.SMALLER));
		BUILDERS.put(new BuilderSpec(IfStmt.GE,true),builder(IfStmt.GE,false));

		OP_SYMMETRY.put(new Integer(IfStmt.EQ), new Integer(IfStmt.EQ));
		OP_SYMMETRY.put(new Integer(IfStmt.NE), new Integer(IfStmt.NE));
		OP_SYMMETRY.put(new Integer(IfStmt.LT), new Integer(IfStmt.GT));
		OP_SYMMETRY.put(new Integer(IfStmt.GT), new Integer(IfStmt.LT));
		OP_SYMMETRY.put(new Integer(IfStmt.LE), new Integer(IfStmt.GE));
		OP_SYMMETRY.put(new Integer(IfStmt.GE), new Integer(IfStmt.LE));
	}

	private Expression expr;

	private Object retval;

	private Map seenBlocks = new HashMap();

	private BloatUtil bloatUtil;

	private LinkedList methodStack = new LinkedList();

	private LinkedList localStack = new LinkedList();

	private int retCount = 0;

	private int blockCount = 0;
	
	private ClassSource classSource;

	public BloatExprBuilderVisitor(BloatUtil bloatUtil, ClassSource classSource) {
		this.bloatUtil = bloatUtil;
		this.classSource = classSource;
		localStack.addLast(new ComparisonOperand[] {
				PredicateFieldRoot.INSTANCE, CandidateFieldRoot.INSTANCE });
	}

	private Object purgeReturnValue() {
		Object expr = this.retval;
		retval(null);
		return expr;
	}

	private void expression(Expression expr) {
		retval(expr);
		this.expr = expr;
	}

	private void retval(Object expr) {
		this.retval = expr;
	}

    private static ComparisonBuilder builder(int op, boolean primitive) {
		return (ComparisonBuilder) BUILDERS.get(new BuilderSpec(op,primitive));
	}

	public Expression expression() {
		if (expr == null && isSingleReturn() && retval instanceof ConstValue) {
			expression(asExpression(retval));
		}
		return (checkComparisons(expr) ? expr : null);
	}

	private boolean isSingleReturn() {
		return retCount == 1 && blockCount == 4; // one plus source,init,sink
	}

	private boolean checkComparisons(Expression expr) {
		if (expr == null) {
			return true;
		}
		final boolean[] result = { true };
		ExpressionVisitor visitor = new TraversingExpressionVisitor() {
			public void visit(ComparisonExpression expression) {
				if (expression.left().root() != CandidateFieldRoot.INSTANCE) {
					result[0] = false;
				}
			}
		};
		expr.accept(visitor);
		return result[0];
	}

	public void visitIfZeroStmt(IfZeroStmt stmt) {
		stmt.expr().visit(this);
		Object retval = purgeReturnValue();
		boolean cmpNull = false;
		if (retval instanceof FieldValue) {
			// TODO: merge boolean and number primitive handling
			Expression forced = identityOrBoolComparisonOrNull(retval);
			if (forced != null) {
				retval = forced;
			} else {
				FieldValue fieldVal = (FieldValue) retval;
				String fieldType = (String)fieldVal.tag();
				Object constVal=null;
				if(fieldType.length()==1) {
					constVal=new Integer(0);
				}
				retval = new ComparisonExpression(fieldVal,
						new ConstValue(constVal), ComparisonOperator.EQUALS);
				cmpNull = true;
			}
		}
		if (retval instanceof Expression) {
			Expression expr = (Expression) retval;
			if (stmt.comparison() == IfStmt.EQ && !cmpNull
					|| stmt.comparison() == IfStmt.NE && cmpNull) {
				expr = BUILDER.not(expr);
			}
			expression(buildComparison(stmt, expr));
			return;
		}
		if (!(retval instanceof ThreeWayComparison)) {
			throw new EarlyExitException();
		}
		ThreeWayComparison cmp = (ThreeWayComparison) retval;
		Expression expr = null;
		int comparison = stmt.comparison();
		if (cmp.swapped()) {
			comparison = ((Integer) OP_SYMMETRY.get(new Integer(comparison)))
					.intValue();
		}
		switch (comparison) {
		case IfStmt.EQ:
			expr = new ComparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.EQUALS);
			break;
		case IfStmt.NE:
			expr = BUILDER.not(new ComparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.EQUALS));
			break;
		case IfStmt.LT:
			expr = new ComparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.SMALLER);
			break;
		case IfStmt.GT:
			expr = new ComparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.GREATER);
			break;
		case IfStmt.LE:
			expr = BUILDER.not(new ComparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.GREATER));
			break;
		case IfStmt.GE:
			expr = BUILDER.not(new ComparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.SMALLER));
			break;
		default:
			break;
		}
		expression(buildComparison(stmt, expr));
	}

	public void visitIfCmpStmt(IfCmpStmt stmt) {
		stmt.left().visit(this);
		Object left = purgeReturnValue();
		stmt.right().visit(this);
		Object right = purgeReturnValue();
		int op = stmt.comparison();
		if ((left instanceof ComparisonOperand)
				&& (right instanceof FieldValue)) {
			FieldValue rightField = (FieldValue) right;
			if (rightField.root() == CandidateFieldRoot.INSTANCE) {
				Object swap = left;
				left = right;
				right = swap;
				op = ((Integer) OP_SYMMETRY.get(new Integer(op))).intValue();
			}
		}
		if (!(left instanceof FieldValue)
				|| !(right instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		FieldValue fieldExpr = (FieldValue) left;
		ComparisonOperand valueExpr = (ComparisonOperand) right;

        boolean isPrimitive = isPrimitiveExpr(stmt.left());
        Expression cmp = buildComparison(stmt, builder(op,isPrimitive).buildComparison(fieldExpr, valueExpr));
		expression(cmp);
	}

	public void visitExprStmt(ExprStmt stmt) {
		super.visitExprStmt(stmt);
	}

	public void visitCallExpr(CallExpr expr) {
		boolean isStatic = (expr instanceof CallStaticExpr);
		if (!isStatic && expr.method().name().equals("<init>")) {
			throw new EarlyExitException();
		}
		if (!isStatic && expr.method().name().equals("equals")) {
			CallMethodExpr call = (CallMethodExpr) expr;
			if (isPrimitiveWrapper(call.receiver().type())) {
				processEqualsCall(call, ComparisonOperator.EQUALS);
			}
			return;
		}
		if(expr.method().declaringClass().equals(Type.STRING)) {
			if(applyStringHandling(expr)) {
				return;
			}
		}
		ComparisonOperandAnchor rcvRetval = null;
		if (!isStatic) {
			((CallMethodExpr) expr).receiver().visit(this);
			rcvRetval = (ComparisonOperandAnchor) purgeReturnValue();
		}
		if(isPrimitiveWrapper(expr.method().declaringClass())) {
			if(applyPrimitiveWrapperHandling(expr,rcvRetval)) {
				return;
			}
		}
		MemberRef methodRef = expr.method();
		if (methodStack.contains(methodRef) || methodStack.size() > MAX_DEPTH) {
			throw new EarlyExitException();
		}
		methodStack.addLast(methodRef);
		boolean addedLocals=false;
		try {
			List params = new ArrayList(expr.params().length + 1);
			params.add(rcvRetval);
			for (int idx = 0; idx < expr.params().length; idx++) {
				expr.params()[idx].visit(this);
				ComparisonOperand curparam = (ComparisonOperand) purgeReturnValue();
				if ((curparam instanceof ComparisonOperandAnchor)
						&& (((ComparisonOperandAnchor) curparam).root() == CandidateFieldRoot.INSTANCE)) {
					throw new EarlyExitException();
				}
				params.add(curparam);
			}
			addedLocals=true;
			localStack.addLast(params.toArray(new ComparisonOperand[params
					.size()]));

			if (rcvRetval == null
					|| rcvRetval.root() != CandidateFieldRoot.INSTANCE) {
				if (rcvRetval == null) {
					rcvRetval = new StaticFieldRoot(bloatUtil.normalizedClassName(expr
							.method().declaringClass()));
				}
				params.remove(0);
				Type[] paramTypes = expr.method().nameAndType().type()
						.paramTypes();
				Class[] javaParamTypes = new Class[paramTypes.length];
				for (int paramIdx = 0; paramIdx < paramTypes.length; paramIdx++) {
					String className = bloatUtil.normalizedClassName(paramTypes[paramIdx]);
					javaParamTypes[paramIdx] = (PRIMITIVE_CLASSES
							.containsKey(className) ? (Class) PRIMITIVE_CLASSES
							.get(className) : Class.forName(className));
				}
				retval(new MethodCallValue(rcvRetval, expr.method().name(),
						javaParamTypes, (ComparisonOperand[]) params
								.toArray(new ComparisonOperand[params.size()])));
				return;
			}

			Type declaringClass = methodRef.declaringClass();
			// Nice try, but doesn't help, since the receiver's type always seems to be reported as java.lang.Object.
			if(expr instanceof CallMethodExpr) {
				Expr receiverExpr=((CallMethodExpr)expr).receiver();
				Type receiverType=receiverExpr.type();
				if(isSuperType(declaringClass,receiverType)) {
					declaringClass=receiverType;
				}
			}
			FlowGraph flowGraph = bloatUtil.flowGraph(declaringClass.className(), methodRef.name(),methodRef.type().paramTypes());
			if (flowGraph == null) {
				throw new EarlyExitException();
			}
			if (NQDebug.LOG) {
				System.out
						.println("METHOD:" + flowGraph.method().nameAndType());
				flowGraph.visit(new PrintVisitor());
			}
			flowGraph.visit(this);
			Object methodRetval = purgeReturnValue();
			if(methodRetval==null) {
				throw new EarlyExitException();
			}
			retval(methodRetval);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(addedLocals) {
				localStack.removeLast();
			}
			Object last = methodStack.removeLast();
			if (!last.equals(methodRef)) {
				throw new RuntimeException("method stack inconsistent: push="
						+ methodRef + " , pop=" + last);
			}
		}
	}

	private boolean isSuperType(Type declaringClass, Type receiverType) throws ClassNotFoundException {
		if(declaringClass.className().equals(receiverType.className())) {
			return false;
		}
		ClassEditor receiverEditor=bloatUtil.classEditor(receiverType.className());
		Type superClass = receiverEditor.superclass();
		if(superClass!=null) {
			if(superClass.className().equals(declaringClass.className())) {
				return true;
			}
			if(isSuperType(declaringClass,superClass)) {
				return true;
			}
		}
		Type[] interfaces=receiverEditor.interfaces();
		for (int interfaceIdx = 0; interfaceIdx < interfaces.length; interfaceIdx++) {
			if(interfaces[interfaceIdx].className().equals(declaringClass.className())) {
				return true;
			}
			if(isSuperType(declaringClass, interfaces[interfaceIdx])) {
				return true;
			}
		}
		return false;
	}

	private boolean applyPrimitiveWrapperHandling(CallExpr expr,ComparisonOperandAnchor rcvRetval) {
		String methodName = expr.method().name();
		if(methodName.endsWith("Value")) {
			return handlePrimitiveWrapperValueCall(rcvRetval);
		}
		if(methodName.equals("compareTo")) {
			return handlePrimitiveWrapperCompareToCall(expr, rcvRetval);
		}
		return false;
	}

	private boolean handlePrimitiveWrapperCompareToCall(CallExpr expr, ComparisonOperandAnchor rcvRetval) {
		ComparisonOperand left=rcvRetval;
		expr.params()[0].visit(this);
		ComparisonOperand right=(ComparisonOperand) purgeReturnValue();
		retval(new ThreeWayComparison((FieldValue)left,right,false));
		return true;
	}

	private boolean handlePrimitiveWrapperValueCall(ComparisonOperandAnchor rcvRetval) {
		retval(rcvRetval);
		if(rcvRetval instanceof FieldValue) {
			FieldValue fieldval=(FieldValue)rcvRetval;
			if(isBooleanField(fieldval)) {
				retval(new ComparisonExpression(fieldval,new ConstValue(Boolean.TRUE),ComparisonOperator.EQUALS));
			}
			if(fieldval.root().equals(CandidateFieldRoot.INSTANCE)) {
				return true;
			}
		}
		return false;
	}

	private boolean applyStringHandling(CallExpr expr) {
		if (expr.method().name().equals("contains")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.CONTAINS);
			return true;
		}
		if (expr.method().name().equals("startsWith")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.STARTSWITH);
			return true;
		}
		if (expr.method().name().equals("endsWith")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.ENDSWITH);
			return true;
		}
		return false;
	}

	private final static Map PRIMITIVE_CLASSES;

	static {
		PRIMITIVE_CLASSES = new HashMap();
		PRIMITIVE_CLASSES.put("Z", Boolean.TYPE);
		PRIMITIVE_CLASSES.put("B", Byte.TYPE);
		PRIMITIVE_CLASSES.put("S", Short.TYPE);
		PRIMITIVE_CLASSES.put("C", Character.TYPE);
		PRIMITIVE_CLASSES.put("I", Integer.TYPE);
		PRIMITIVE_CLASSES.put("J", Long.TYPE);
		PRIMITIVE_CLASSES.put("F", Float.TYPE);
		PRIMITIVE_CLASSES.put("D", Double.TYPE);
	}

	private boolean isPrimitiveWrapper(Type type) {
		return Arrays.binarySearch(PRIMITIVE_WRAPPER_NAMES,
				bloatUtil.normalizedClassName(type)) >= 0;
	}
	
	private boolean isPrimitiveExpr(Expr expr) {
		return expr.type().isPrimitive();
	}

	private void processEqualsCall(CallMethodExpr expr, ComparisonOperator op) {
		Expr left = expr.receiver();
		Expr right = expr.params()[0];
		if (!isComparableExprOperand(left) || !isComparableExprOperand(right)) {
			throw new EarlyExitException();
		}
		left.visit(this);
		Object leftObj = purgeReturnValue();
		if (!(leftObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand leftOp = (ComparisonOperand) leftObj;
		right.visit(this);
		ComparisonOperand rightOp = (ComparisonOperand) purgeReturnValue();
		if (op.isSymmetric() && isCandidateFieldValue(rightOp)
				&& !isCandidateFieldValue(leftOp)) {
			ComparisonOperand swap = leftOp;
			leftOp = rightOp;
			rightOp = swap;
		}
		if (!isCandidateFieldValue(leftOp) || rightOp == null) {
			throw new EarlyExitException();
		}
		expression(new ComparisonExpression((FieldValue) leftOp, rightOp, op));
	}

	private boolean isCandidateFieldValue(ComparisonOperand op) {
		return ((op instanceof FieldValue) && ((FieldValue) op).root() == CandidateFieldRoot.INSTANCE);
	}

	private boolean isComparableExprOperand(Expr expr) {
		return (expr instanceof FieldExpr) || (expr instanceof StaticFieldExpr)
				|| (expr instanceof CallMethodExpr)
				|| (expr instanceof CallStaticExpr)
				|| (expr instanceof ConstantExpr)
				|| (expr instanceof LocalExpr);
	}

	public void visitFieldExpr(FieldExpr expr) {
		expr.object().visit(this);
		Object fieldObj = purgeReturnValue();
		String fieldName = expr.field().name();
		if (fieldObj instanceof ComparisonOperandAnchor) {
			retval(new FieldValue((ComparisonOperandAnchor) fieldObj,
					fieldName, bloatUtil.normalizedClassName(expr.field().type())));
		}
	}

	public void visitStaticFieldExpr(StaticFieldExpr expr) {
		MemberRef field = expr.field();
		retval(new FieldValue(new StaticFieldRoot(bloatUtil.normalizedClassName(field
				.declaringClass())), field.name(), bloatUtil.normalizedClassName(field
				.type())));
	}

	public void visitConstantExpr(ConstantExpr expr) {
		super.visitConstantExpr(expr);
		retval(new ConstValue(expr.value()));
	}

	public void visitLocalExpr(LocalExpr expr) {
		super.visitLocalExpr(expr);
		ComparisonOperand[] locals = (ComparisonOperand[]) localStack.getLast();
		if (expr.index() >= locals.length) {
			throw new EarlyExitException();
		}
		retval(locals[expr.index()]);
	}

	public void visitBlock(Block block) {
		if (!seenBlocks.containsKey(block)) {
			super.visitBlock(block);
			seenBlocks.put(block, retval);
			blockCount++;
		} else {
			retval(seenBlocks.get(block));
		}
	}

	public void visitFlowGraph(FlowGraph graph) {
		try {
			super.visitFlowGraph(graph);
			if (expr == null) {
				Expression forced = identityOrBoolComparisonOrNull(retval);
				if (forced != null) {
					expression(forced);
				}
			}
		} catch (EarlyExitException exc) {
			expression(null);
		}
	}

	private Expression identityOrBoolComparisonOrNull(Object val) {
		if (val instanceof Expression) {
			return (Expression) val;
		}
		if (!(val instanceof FieldValue)) {
			return null;
		}
		FieldValue fieldVal = (FieldValue) val;
		if (fieldVal.root() != CandidateFieldRoot.INSTANCE) {
			return null;
		}
		String fieldType = ((String) fieldVal.tag());
		if (fieldType.length() != 1) {
			return null;
		}
		Object constVal = null;
		switch (fieldType.charAt(0)) {
			case 'Z':
				constVal = Boolean.TRUE;
				break;
//			case 'I':
//				constVal = new Integer(0);
//				break;
			default:
				return null;
		}
		return new ComparisonExpression(fieldVal, new ConstValue(constVal),
				ComparisonOperator.EQUALS);
	}

	private static boolean isBooleanField(FieldValue fieldVal) {
		return isFieldType(fieldVal, "Z")||isFieldType(fieldVal, Boolean.class.getName());
	}

	private boolean isIntField(FieldValue fieldVal) {
		return isFieldType(fieldVal, "I");
	}

	private static boolean isFieldType(FieldValue fieldVal, String expType) {
		return expType.equals(fieldVal.tag());
	}

	public void visitArithExpr(ArithExpr expr) {
		expr.left().visit(this);
		Object leftObj = purgeReturnValue();
		if (!(leftObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand left = (ComparisonOperand) leftObj;
		expr.right().visit(this);
		Object rightObj = purgeReturnValue();
		if (!(rightObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand right = (ComparisonOperand) rightObj;
		boolean swapped = false;
		if (right instanceof FieldValue) {
			FieldValue rightField = (FieldValue) right;
			if (rightField.root() == CandidateFieldRoot.INSTANCE) {
				ComparisonOperand swap = left;
				left = right;
				right = swap;
				swapped = true;
			}
		}
		switch (expr.operation()) {
		case ArithExpr.ADD:
		case ArithExpr.SUB:
		case ArithExpr.MUL:
		case ArithExpr.DIV:
			retval(new ArithmeticExpression(left, right,
					arithmeticOperator(expr.operation())));
			break;
		case ArithExpr.CMP:
		case ArithExpr.CMPG:
		case ArithExpr.CMPL:
			if (left instanceof FieldValue) {
				retval(new ThreeWayComparison((FieldValue) left, right, swapped));
			}
			break;
		case ArithExpr.XOR:
			if (left instanceof FieldValue) {
				retval(BUILDER.not(new ComparisonExpression((FieldValue) left,
						right, ComparisonOperator.EQUALS)));
			}
			break;
		default:
			break;
		}
	}

	public void visitArrayRefExpr(ArrayRefExpr expr) {
		expr.array().visit(this);
		ComparisonOperandAnchor arrayOp = (ComparisonOperandAnchor) purgeReturnValue();
		expr.index().visit(this);
		ComparisonOperand idxOp = (ComparisonOperand) purgeReturnValue();
		if (arrayOp == null || idxOp == null
				|| arrayOp.root() == CandidateFieldRoot.INSTANCE) {
			throw new EarlyExitException();
		}
		retval(new ArrayAccessValue(arrayOp, idxOp));
	}

	public void visitReturnExprStmt(ReturnExprStmt stat) {
		stat.expr().visit(this);
		retCount++;
	}

	private ArithmeticOperator arithmeticOperator(int bloatOp) {
		switch (bloatOp) {
		case ArithExpr.ADD:
			return ArithmeticOperator.ADD;
		case ArithExpr.SUB:
			return ArithmeticOperator.SUBTRACT;
		case ArithExpr.MUL:
			return ArithmeticOperator.MULTIPLY;
		case ArithExpr.DIV:
			return ArithmeticOperator.DIVIDE;
		default:
			return null;
		}
	}

	private Expression buildComparison(IfStmt stmt, Expression cmp) {
		stmt.trueTarget().visit(this);
		Object trueVal = purgeReturnValue();
		stmt.falseTarget().visit(this);
		Object falseVal = purgeReturnValue();
		Expression trueExpr = asExpression(trueVal);
		Expression falseExpr = asExpression(falseVal);
		if (trueExpr == null || falseExpr == null) {
			return null;
		}
		return BUILDER.ifThenElse(cmp, trueExpr, falseExpr);
	}

	private Expression asExpression(Object obj) {
		if (obj instanceof Expression) {
			return (Expression) obj;
		}
		if (obj instanceof ConstValue) {
			Object val = ((ConstValue) obj).value();
			return asExpression(val);
		}
		if (obj instanceof Boolean) {
			return BoolConstExpression.expr(((Boolean) obj).booleanValue());
		}
		if (obj instanceof Integer) {
			int exprval = ((Integer) obj).intValue();
			if (exprval == 0 || exprval == 1) {
				return BoolConstExpression.expr(exprval == 1);
			}
		}
		return null;
	}
	
	public void visitStoreExpr(StoreExpr expr) {
		if(!(expr.target() instanceof StackExpr)) {
			throw new EarlyExitException();
		}
		super.visitStoreExpr(expr);
	}
	
	private static class EarlyExitException extends RuntimeException {
	}
}
