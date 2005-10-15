package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.foundation.Iterator4;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.build.*;
import com.db4o.nativequery.expr.cmp.*;

public class BloatExprBuilderVisitor extends TreeVisitor {
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
	
	private ClassFileLoader loader;
	private Expression expr;
	private Object retval;
	private Map seenBlocks=new HashMap();

	public BloatExprBuilderVisitor(ClassFileLoader loader) {
		this.loader=loader;
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
		return (checkComparisons(expr) ? expr : null);
	}
	
	private boolean checkComparisons(Expression expr) {
		if(expr==null) {
			return true;
		}
		final boolean[] result={true};
		DiscriminatingExpressionVisitor visitor=new TraversingExpressionVisitor() {
			public void visit(ComparisonExpression expression) {
				if(expression.left().parentIdx()!=1) {
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
			retval=new ComparisonExpression((FieldValue)retval,new ConstValue(null),ComparisonOperator.EQUALS);
			cmpNull=true;
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
			throw new RuntimeException("Cannot handle "+retval+" in ifzero.");
		}
		ThreeWayComparison cmp=(ThreeWayComparison)retval;
		Expression expr=null;
		switch(stmt.comparison()) {
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
			if(rightField.parentIdx()==1) {
				Object swap=left;
				left=right;
				right=swap;
				op=((Integer)OP_SYMMETRY.get(new Integer(op))).intValue();
			}
		}
		if(!(left instanceof FieldValue)||!(right instanceof ComparisonOperand)) {
			expression(null);
			return;
//			throw new RuntimeException();
		}
		FieldValue fieldExpr=(FieldValue)left;
		ComparisonOperand valueExpr=(ComparisonOperand)right;
		
		Expression cmp = buildComparison(stmt, builder(op).buildComparison(fieldExpr,valueExpr));
		expression(cmp);
	}
		
	public void visitCallMethodExpr(CallMethodExpr expr) {	
		if(expr.method().name().equals("equals")) {
			processEqualsCall(expr);
			return;
		}
		MemberRef methodRef=expr.method();
		try {
			expr.receiver().visit(this);
			Object rcvRetval=purgeReturnValue();
			FlowGraph flowGraph=BloatUtil.flowGraph(loader,methodRef.declaringClass().className(),methodRef.name());
			flowGraph.visit(this);
			Object methodRetval=purgeReturnValue();
			if(methodRetval instanceof FieldValue) {
				FieldValue methField=(FieldValue)methodRetval;
				if(rcvRetval instanceof FieldValue) {
					FieldValue rcvField=(FieldValue)rcvRetval;
					for(Iterator4 nameIter=methField.fieldNames();nameIter.hasNext();) {
						rcvField.descend((String)nameIter.next());
					}
					retval(rcvField);
				}
				else {
					retval(new FieldValue(1,((FieldValue)methodRetval).fieldNames()));
				}
			}
			else {
				retval(methodRetval);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void processEqualsCall(CallMethodExpr expr) {
		Expr left=expr.receiver();
		Expr right=expr.params()[0];
		if(/*(right instanceof FieldExpr)&&*/(left instanceof ConstantExpr)) {
			Expr swap=left;
			left=right;
			right=swap;
		}
		if(!((left instanceof FieldExpr)||(left instanceof CallMethodExpr)||(left instanceof ConstantExpr))||!((right instanceof ConstantExpr)||(right instanceof FieldExpr)||(right instanceof CallMethodExpr))) {
			return;
		}
		left.visit(this);
		// FIXME check before!
		FieldValue fieldValue=(FieldValue)purgeReturnValue();
		if(fieldValue.parentIdx()==1) {
			right.visit(this);
			ComparisonOperand valueExpr=(ComparisonOperand)purgeReturnValue();
			expression(new ComparisonExpression(fieldValue,valueExpr,ComparisonOperator.EQUALS));
		}
	}

	public void visitFieldExpr(FieldExpr expr) {
		expr.object().visit(this);	
		Object fieldObj=retval;
		String fieldName=expr.field().name();
		if(fieldObj instanceof FieldValue) {
			((FieldValue)fieldObj).descend(fieldName);
			return;
		}
		int idx=((Integer)fieldObj).intValue();
		retval(new FieldValue(idx,fieldName));
	}

	public void visitConstantExpr(ConstantExpr expr) {
		super.visitConstantExpr(expr);
		retval(new ConstValue(expr.value()));
	}

	public void visitLocalExpr(LocalExpr expr) {
		super.visitLocalExpr(expr);
		retval(new Integer(expr.index()));
	}
	
	public void visitBlock(Block block) {
		if(!seenBlocks.containsKey(block)) {
			super.visitBlock(block);
			seenBlocks.put(block,retval);
		}
		else {
			retval(seenBlocks.get(block));
		}
	}

	public void visitArithExpr(ArithExpr expr) {
		expr.left().visit(this);
		ComparisonOperand left=(ComparisonOperand)purgeReturnValue();
		expr.right().visit(this);
		ComparisonOperand right=(ComparisonOperand)purgeReturnValue();
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
				// FIXME duplication?
				if((left instanceof ComparisonOperand)&&(right instanceof FieldValue)) {
					FieldValue rightField=(FieldValue)right;
					if(rightField.parentIdx()==1) {
						ComparisonOperand swap=left;
						left=right;
						right=swap;
					}
				}
				if(left instanceof FieldValue) {
					retval(new ThreeWayComparison((FieldValue)left,right));
				}
				break;
			default:
				return;
		}
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
			throw new RuntimeException();
		}
		return BUILDER.ifThenElse(cmp,trueExpr,falseExpr);
	}
	
	private Expression asExpression(Object obj) {
		if(obj instanceof Expression) {
			return (Expression)obj;
		}
		if(obj instanceof ConstValue) {
			Object val=((ConstValue)obj).value();
			if(val instanceof Boolean) {
				return BoolConstExpression.expr(((Boolean)val).booleanValue());
			}
			if(val instanceof Integer) {
				int exprval=((Integer)val).intValue();
				if(exprval==0||exprval==1) {
					return BoolConstExpression.expr(exprval==1);
				}
			}
		}
		return null;
	}	
}
