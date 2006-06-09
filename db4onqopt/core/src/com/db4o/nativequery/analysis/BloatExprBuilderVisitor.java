package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.build.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;

public class BloatExprBuilderVisitor extends TreeVisitor {

	// TODO discuss: drop or make configurable
	private final static int MAX_DEPTH=10;
	
	private final static String[] PRIMITIVE_WRAPPER_NAMES={
		Boolean.class.getName(),
		Byte.class.getName(),
		Short.class.getName(),
		Character.class.getName(),
		Integer.class.getName(),
		Long.class.getName(),
		Double.class.getName(),
		Float.class.getName(),
		String.class.getName(),
	};
	
	static {
		Arrays.sort(PRIMITIVE_WRAPPER_NAMES);
	}
	
	private final static ExpressionBuilder BUILDER=new ExpressionBuilder();

	private final static Map BUILDERS=new HashMap();
	private final static Map OP_SYMMETRY=new HashMap();
	
	private static class ComparisonBuilder {
		private ComparisonOperator op;
		
		public ComparisonBuilder(ComparisonOperator op) {
			this.op = op;
		}

		public Expression buildComparison(FieldValue fieldValue, ComparisonOperand valueExpr) {
			return new ComparisonExpression(fieldValue,valueExpr,op);
		}
	}

	private static class NegateComparisonBuilder extends ComparisonBuilder {
		public NegateComparisonBuilder(ComparisonOperator op) {
			super(op);
		}

		public Expression buildComparison(FieldValue fieldValue, ComparisonOperand valueExpr) {
			return BUILDER.not(super.buildComparison(fieldValue,valueExpr));
		}
	}

	static {
		BUILDERS.put(new Integer(IfStmt.EQ),new ComparisonBuilder(ComparisonOperator.EQUALS));
		BUILDERS.put(new Integer(IfStmt.NE),new NegateComparisonBuilder(ComparisonOperator.EQUALS));
		BUILDERS.put(new Integer(IfStmt.LT),new ComparisonBuilder(ComparisonOperator.SMALLER));
		BUILDERS.put(new Integer(IfStmt.GT),new ComparisonBuilder(ComparisonOperator.GREATER));
		BUILDERS.put(new Integer(IfStmt.LE),new NegateComparisonBuilder(ComparisonOperator.GREATER));
		BUILDERS.put(new Integer(IfStmt.GE),new NegateComparisonBuilder(ComparisonOperator.SMALLER));
		
		OP_SYMMETRY.put(new Integer(IfStmt.EQ),new Integer(IfStmt.EQ));
		OP_SYMMETRY.put(new Integer(IfStmt.NE),new Integer(IfStmt.NE));
		OP_SYMMETRY.put(new Integer(IfStmt.LT),new Integer(IfStmt.GT));
		OP_SYMMETRY.put(new Integer(IfStmt.GT),new Integer(IfStmt.LT));
		OP_SYMMETRY.put(new Integer(IfStmt.LE),new Integer(IfStmt.GE));
		OP_SYMMETRY.put(new Integer(IfStmt.GE),new Integer(IfStmt.LE));
	}
	
	private Expression expr;
	private Object retval;
	private Map seenBlocks=new HashMap();
	private BloatUtil bloatUtil;

	private LinkedList methodStack=new LinkedList();
	private LinkedList localStack=new LinkedList();
	private int retCount=0;
	private int blockCount=0;
	
	public BloatExprBuilderVisitor(BloatUtil bloatUtil) {
		this.bloatUtil=bloatUtil;
		localStack.addLast(new ComparisonOperand[]{PredicateFieldRoot.INSTANCE,CandidateFieldRoot.INSTANCE});
	}
	
	private Object purgeReturnValue() {
		Object expr=this.retval;
		retval(null);
		return expr;
	}

	private void expression(Expression expr) {
		retval(expr);
		this.expr=expr;
	}

	private void retval(Object expr) {
		this.retval=expr;
	}

	private ComparisonBuilder builder(int op) {
		return (ComparisonBuilder)BUILDERS.get(new Integer(op));
	}
	
	public Expression expression() {
		if(expr==null&&isSingleReturn()&&retval instanceof ConstValue) {
			expression(asExpression(retval));
		}
		return (checkComparisons(expr) ? expr : null);
	}

	private boolean isSingleReturn() {
		return retCount==1
				&&blockCount==4; // one plus source,init,sink
	}
	
	private boolean checkComparisons(Expression expr) {
		if(expr==null) {
			return true;
		}
		final boolean[] result={true};
		ExpressionVisitor visitor=new TraversingExpressionVisitor() {
			public void visit(ComparisonExpression expression) {
				if(expression.left().root()!=CandidateFieldRoot.INSTANCE) {
					result[0]=false;
				}
			}
		};
		expr.accept(visitor);
		return result[0];
	}

	public void visitIfZeroStmt(IfZeroStmt stmt) {
		stmt.expr().visit(this);
		Object retval=purgeReturnValue();
		boolean cmpNull=false;
		if(retval instanceof FieldValue) {
			Expression forced=identityOrBoolComparisonOrNull(retval,true);
			if(forced!=null) {
				retval=forced;
			}
			else {
				retval=new ComparisonExpression((FieldValue)retval,new ConstValue(null),ComparisonOperator.EQUALS);
				cmpNull=true;
			}
		}
		if(retval instanceof Expression) {
			Expression expr=(Expression)retval;
			if(stmt.comparison()==IfStmt.EQ&&!cmpNull||stmt.comparison()==IfStmt.NE&&cmpNull) {
				expr=BUILDER.not(expr);
			}
			expression(buildComparison(stmt,expr));
			return;
		}
		if(!(retval instanceof ThreeWayComparison)) {
			expression(null);
			return;
		}
		ThreeWayComparison cmp=(ThreeWayComparison)retval;
		Expression expr=null;
		int comparison=stmt.comparison();
		if(cmp.swapped()) {
			comparison=((Integer)OP_SYMMETRY.get(new Integer(comparison))).intValue();
		}
		switch(comparison) {
			case IfStmt.EQ:
				expr=new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.EQUALS);
				break;
			case IfStmt.NE:
				expr=BUILDER.not(new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.EQUALS));
				break;
			case IfStmt.LT:
				expr=new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.SMALLER);
				break;
			case IfStmt.GT:
				expr=new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.GREATER);
				break;
			case IfStmt.LE:
				expr=BUILDER.not(new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.GREATER));
				break;
			case IfStmt.GE:
				expr=BUILDER.not(new ComparisonExpression(cmp.left(),cmp.right(),ComparisonOperator.SMALLER));
				break;
			default:
				break;
		}
		expression(buildComparison(stmt,expr));
	}
	
	public void visitIfCmpStmt(IfCmpStmt stmt) {
		stmt.left().visit(this);
		Object left=purgeReturnValue();
		stmt.right().visit(this);
		Object right=purgeReturnValue();
		int op=stmt.comparison();
		if((left instanceof ComparisonOperand)&&(right instanceof FieldValue)) {
			FieldValue rightField=(FieldValue)right;
			if(rightField.root()==CandidateFieldRoot.INSTANCE) {
				Object swap=left;
				left=right;
				right=swap;
				op=((Integer)OP_SYMMETRY.get(new Integer(op))).intValue();
			}
		}
		if(!(left instanceof FieldValue)||!(right instanceof ComparisonOperand)) {
			expression(null);
			return;
		}
		FieldValue fieldExpr=(FieldValue)left;
		ComparisonOperand valueExpr=(ComparisonOperand)right;
		
		Expression cmp = buildComparison(stmt, builder(op).buildComparison(fieldExpr,valueExpr));
		expression(cmp);
	}
		
	public void visitExprStmt(ExprStmt stmt) {
		super.visitExprStmt(stmt);
	}
	
	public void visitCallExpr(CallExpr expr) {	
		boolean isStatic=(expr instanceof CallStaticExpr);
		if(!isStatic&&expr.method().name().equals("<init>")) {
			retval(null);
			return;
		}
		if(!isStatic&&expr.method().name().equals("equals")) {
			CallMethodExpr call=(CallMethodExpr)expr;
			if(isPrimitive(call.receiver().type())) {
				processEqualsCall(call,ComparisonOperator.EQUALS);
			}
			return;
		}
		if(expr.method().name().equals("contains")&&expr.method().declaringClass().equals(Type.STRING)) {
			processEqualsCall((CallMethodExpr)expr,ComparisonOperator.CONTAINS);
			return;
		}
		if(expr.method().name().equals("startsWith")&&expr.method().declaringClass().equals(Type.STRING)) {
			processEqualsCall((CallMethodExpr)expr,ComparisonOperator.STARTSWITH);
			return;
		}
		if(expr.method().name().equals("endsWith")&&expr.method().declaringClass().equals(Type.STRING)) {
			processEqualsCall((CallMethodExpr)expr,ComparisonOperator.ENDSWITH);
			return;
		}
		MemberRef methodRef=expr.method();
		if(methodStack.contains(methodRef)||methodStack.size()>MAX_DEPTH) {
			return;
		}
		methodStack.addLast(methodRef);
		try {
			ComparisonOperandAnchor rcvRetval=null;
			if(!isStatic) {
				((CallMethodExpr)expr).receiver().visit(this);
				rcvRetval=(ComparisonOperandAnchor)purgeReturnValue();
			}
			List params=new ArrayList(expr.params().length+1);
			params.add(rcvRetval);
			for(int idx=0;idx<expr.params().length;idx++) {
				expr.params()[idx].visit(this);
				ComparisonOperand curparam=(ComparisonOperand)purgeReturnValue();
				if((curparam instanceof ComparisonOperandAnchor)&&(((ComparisonOperandAnchor)curparam).root()==CandidateFieldRoot.INSTANCE)) {
					retval(null);
					return;
				}
				params.add(curparam);
			}
			localStack.addLast(params.toArray(new ComparisonOperand[params.size()]));

			if(rcvRetval==null||rcvRetval.root()!=CandidateFieldRoot.INSTANCE) {
				if(rcvRetval==null) {
					rcvRetval=new StaticFieldRoot(normalizedClassName(expr.method().declaringClass()));
				}
				params.remove(0);
				Type[] paramTypes=expr.method().nameAndType().type().paramTypes();
				Class[] javaParamTypes=new Class[paramTypes.length];
				for (int paramIdx = 0; paramIdx < paramTypes.length; paramIdx++) {
					String className = normalizedClassName(paramTypes[paramIdx]);
					javaParamTypes[paramIdx]=(PRIMITIVE_CLASSES.containsKey(className) ? (Class)PRIMITIVE_CLASSES.get(className) : Class.forName(className));
				}
				retval(new MethodCallValue(rcvRetval,expr.method().name(),javaParamTypes,(ComparisonOperand[])params.toArray(new ComparisonOperand[params.size()])));
				return;
			}

			FlowGraph flowGraph=bloatUtil.flowGraph(methodRef.declaringClass().className(),methodRef.name());
			if(flowGraph==null) {
				return;
			}
			if(NQDebug.LOG) {
				System.out.println("METHOD:"+flowGraph.method().nameAndType());
				flowGraph.visit(new PrintVisitor());
			}
			flowGraph.visit(this);
			Object methodRetval=purgeReturnValue();
			retval(methodRetval);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			localStack.removeLast();
			Object last=methodStack.removeLast();
			if(!last.equals(methodRef)) {
				throw new RuntimeException("method stack inconsistent: push="+methodRef+" , pop="+last);
			}
		}
	}

	private final static Map PRIMITIVE_CLASSES;
	
	static {
		PRIMITIVE_CLASSES=new HashMap();
		PRIMITIVE_CLASSES.put("Z",Boolean.TYPE);
		PRIMITIVE_CLASSES.put("B",Byte.TYPE);
		PRIMITIVE_CLASSES.put("S",Short.TYPE);
		PRIMITIVE_CLASSES.put("C",Character.TYPE);
		PRIMITIVE_CLASSES.put("I",Integer.TYPE);
		PRIMITIVE_CLASSES.put("J",Long.TYPE);
		PRIMITIVE_CLASSES.put("F",Float.TYPE);
		PRIMITIVE_CLASSES.put("D",Double.TYPE);
	}
	
	private boolean isPrimitive(Type type) {
		return Arrays.binarySearch(PRIMITIVE_WRAPPER_NAMES,normalizedClassName(type))>=0;
	}

	private void processEqualsCall(CallMethodExpr expr,ComparisonOperator op) {
		Expr left=expr.receiver();
		Expr right=expr.params()[0];
		if(!isComparableExprOperand(left)||!isComparableExprOperand(right)) {
			return;
		}
		left.visit(this);
		Object leftObj=purgeReturnValue();
		if(!(leftObj instanceof ComparisonOperand)) {
			return;
		}
		ComparisonOperand leftOp=(ComparisonOperand)leftObj;
		right.visit(this);
		ComparisonOperand rightOp=(ComparisonOperand) purgeReturnValue();
		if(op.isSymmetric()&&isCandidateFieldValue(rightOp)&&!isCandidateFieldValue(leftOp)) {
			ComparisonOperand swap=leftOp;
			leftOp=rightOp;
			rightOp=swap;
		}
		if(!isCandidateFieldValue(leftOp)||rightOp==null) {
			return;
		}
		expression(new ComparisonExpression((FieldValue)leftOp,rightOp,op));
	}

	private boolean isCandidateFieldValue(ComparisonOperand op) {
		return((op instanceof FieldValue)&&((FieldValue)op).root()==CandidateFieldRoot.INSTANCE);
	}
	
	private boolean isComparableExprOperand(Expr expr) {
		return (expr instanceof FieldExpr)||(expr instanceof StaticFieldExpr)||(expr instanceof CallMethodExpr)||(expr instanceof CallStaticExpr)||(expr instanceof ConstantExpr)||(expr instanceof LocalExpr);
	}

	public void visitFieldExpr(FieldExpr expr) {
		expr.object().visit(this);	
		Object fieldObj=purgeReturnValue();
		String fieldName=expr.field().name();
		if(fieldObj instanceof ComparisonOperandAnchor) {
			retval(new FieldValue((ComparisonOperandAnchor)fieldObj,fieldName,normalizedClassName(expr.field().type())));
			return;
		}
	}

	public void visitStaticFieldExpr(StaticFieldExpr expr) {
		MemberRef field = expr.field();
		retval(new FieldValue(new StaticFieldRoot(normalizedClassName(field.declaringClass())),field.name(),normalizedClassName(field.type())));
	}
	
	public void visitConstantExpr(ConstantExpr expr) {
		super.visitConstantExpr(expr);
		retval(new ConstValue(expr.value()));
	}

	public void visitLocalExpr(LocalExpr expr) {
		super.visitLocalExpr(expr);
		ComparisonOperand[] locals=(ComparisonOperand[])localStack.getLast();
		if(expr.index()>=locals.length) {
			retval(null);
			return;
		}
		retval(locals[expr.index()]);
	}
	
	public void visitBlock(Block block) {
		if(!seenBlocks.containsKey(block)) {
			super.visitBlock(block);
			seenBlocks.put(block,retval);
			blockCount++;
		}
		else {
			retval(seenBlocks.get(block));
		}
	}

	public void visitFlowGraph(FlowGraph graph) {
		super.visitFlowGraph(graph);
		if(expr==null) {
			Expression forced=identityOrBoolComparisonOrNull(retval,true);
			if(forced!=null) {
				expression(forced);
			}
		}
	}

	private Expression identityOrBoolComparisonOrNull(Object val,boolean cmp) {
		if(val instanceof Expression) {
			return (Expression)val;
		}
		if(!(val instanceof FieldValue)) {
			return null;
		}
		FieldValue fieldVal=(FieldValue)val;
		if(fieldVal.root()!=CandidateFieldRoot.INSTANCE||isBooleanField(fieldVal)) {
			return null;
		}
		return new ComparisonExpression(fieldVal,new ConstValue(Boolean.valueOf(cmp)),ComparisonOperator.EQUALS);
	}

	private boolean isBooleanField(FieldValue fieldVal) {
		return !"Z".equals(fieldVal.tag());
	}
	
	public void visitArithExpr(ArithExpr expr) {
		expr.left().visit(this);
		Object leftObj=purgeReturnValue();
		if(!(leftObj instanceof ComparisonOperand)) {
			return;
		}
		ComparisonOperand left=(ComparisonOperand)leftObj;
		expr.right().visit(this);
		Object rightObj=purgeReturnValue();
		if(!(rightObj instanceof ComparisonOperand)) {
			return;
		}
		ComparisonOperand right=(ComparisonOperand)rightObj;
		boolean swapped=false;
		if((left instanceof ComparisonOperand)&&(right instanceof FieldValue)) {
			FieldValue rightField=(FieldValue)right;
			if(rightField.root()==CandidateFieldRoot.INSTANCE) {
				ComparisonOperand swap=left;
				left=right;
				right=swap;
				swapped=true;
			}
		}
		switch(expr.operation()) {
			case ArithExpr.ADD:
			case ArithExpr.SUB:
			case ArithExpr.MUL:
			case ArithExpr.DIV:
				retval(new ArithmeticExpression(left,right,arithmeticOperator(expr.operation())));
				break;
			case ArithExpr.CMP:
			case ArithExpr.CMPG:
			case ArithExpr.CMPL:
				if(left instanceof FieldValue) {
					retval(new ThreeWayComparison((FieldValue)left,right,swapped));
				}
				break;
			case ArithExpr.XOR:
				if(left instanceof FieldValue) {
					retval(BUILDER.not(new ComparisonExpression((FieldValue)left,right,ComparisonOperator.EQUALS)));
				}
				break;
			default:
				break;
		}
	}

	public void visitArrayRefExpr(ArrayRefExpr expr) {
		expr.array().visit(this);
		ComparisonOperandAnchor arrayOp=(ComparisonOperandAnchor)purgeReturnValue();
		expr.index().visit(this);
		ComparisonOperand idxOp=(ComparisonOperand)purgeReturnValue();
		if(arrayOp==null||idxOp==null||arrayOp.root()==CandidateFieldRoot.INSTANCE) {
			retval(null);
			return;
		}
		retval(new ArrayAccessValue(arrayOp,idxOp));
	}
	
	public void visitReturnExprStmt(ReturnExprStmt stat) {
		stat.expr().visit(this);
		retCount++;
	}
	
	private ArithmeticOperator arithmeticOperator(int bloatOp) {
		switch(bloatOp) {
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
	
	private Expression buildComparison(IfStmt stmt,Expression cmp) {
		stmt.trueTarget().visit(this);
		Object trueVal=purgeReturnValue();
		stmt.falseTarget().visit(this);
		Object falseVal=purgeReturnValue();		
		Expression trueExpr=asExpression(trueVal);
		Expression falseExpr=asExpression(falseVal);
		if(trueExpr==null||falseExpr==null) {
			return null;
		}
		return BUILDER.ifThenElse(cmp,trueExpr,falseExpr);
	}
	
	private Expression asExpression(Object obj) {
		if(obj instanceof Expression) {
			return (Expression)obj;
		}
		if(obj instanceof ConstValue) {
			Object val=((ConstValue)obj).value();
			return asExpression(val);
		}
		if(obj instanceof Boolean) {
			return BoolConstExpression.expr(((Boolean)obj).booleanValue());
		}
		if(obj instanceof Integer) {
			int exprval=((Integer)obj).intValue();
			if(exprval==0||exprval==1) {
				return BoolConstExpression.expr(exprval==1);
			}
		}
		return null;
	}
	
	private String normalizedClassName(Type type) {
		return type.className().replace('/','.');
	}
}
