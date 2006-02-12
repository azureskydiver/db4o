/**
 * 
 */
package com.db4o.nativequery.optimization;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;

final class ComparisonQueryGeneratingVisitor implements ComparisonOperandVisitor {
	private Object predicate;
	
	
	private Object value=null;

	public Object value() {
		return value;
	}
	
	public void visit(ConstValue operand) {
		value = operand.value();
	}

	public void visit(FieldValue operand) {
		operand.parent().accept(this);
		Class clazz=((operand.parent() instanceof StaticFieldRoot) ? (Class)value : value.getClass());
		try {
			Field field=ReflectUtil.fieldFor(clazz,operand.fieldName());
			value=field.get(value); // arg is ignored for static
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	Object add(Object a,Object b) {
		if(a instanceof Double||b instanceof Double) {
			return new Double(((Double)a).doubleValue()+ ((Double)b).doubleValue());
		}
		if(a instanceof Float||b instanceof Float) {
			return new Float(((Float)a).floatValue()+ ((Float)b).floatValue());
		}
		if(a instanceof Long||b instanceof Long) {
			return new Long(((Long)a).longValue()+ ((Long)b).longValue());
		}
		return new Integer(((Integer)a).intValue()+ ((Integer)b).intValue());
	}

	Object subtract(Object a,Object b) {
		if(a instanceof Double||b instanceof Double) {
	        return new Double(((Double)a).doubleValue()- ((Double)b).doubleValue());
		}
		if(a instanceof Float||b instanceof Float) {
	        return new Float(((Float)a).floatValue() - ((Float)b).floatValue());
		}
		if(a instanceof Long||b instanceof Long) {
	        return new Long(((Long)a).longValue() - ((Long)b).longValue());
		}
	    return new Integer(((Integer)a).intValue() - ((Integer)b).intValue());
	}

	Object multiply(Object a,Object b) {
	    if(a instanceof Double||b instanceof Double) {
	        return new Double(((Double)a).doubleValue() * ((Double)b).doubleValue());
	    }
	    if(a instanceof Float||b instanceof Float) {
	        return new Float(((Float)a).floatValue() * ((Float)b).floatValue());
	    }
	    if(a instanceof Long||b instanceof Long) {
	        return new Long(((Long)a).longValue() * ((Long)b).longValue());
	    }
	    return new Integer(((Integer)a).intValue() * ((Integer)b).intValue());
	}

	Object divide(Object a,Object b) {
	    if(a instanceof Double||b instanceof Double) {
	        return new Double(((Double)a).doubleValue()/ ((Double)b).doubleValue());
	    }
	    if(a instanceof Float||b instanceof Float) {
	        return new Float(((Float)a).floatValue() / ((Float)b).floatValue());
	    }
	    if(a instanceof Long||b instanceof Long) {
	        return new Long(((Long)a).longValue() / ((Long)b).longValue());
	    }
	    return new Integer(((Integer)a).intValue() / ((Integer)b).intValue());
	}

	public void visit(ArithmeticExpression operand) {
		operand.left().accept(this);
		Object left=value;
		operand.right().accept(this);
		Object right=value;
		switch(operand.op().id()) {
			case ArithmeticOperator.ADD_ID: 
				value=add(left,right);
				break;
			case ArithmeticOperator.SUBTRACT_ID: 
				value=subtract(left,right);
				break;
			case ArithmeticOperator.MULTIPLY_ID: 
				value=multiply(left,right);
				break;
			case ArithmeticOperator.DIVIDE_ID: 
				value=divide(left,right);
				break;
		}
	}

	public void visit(CandidateFieldRoot root) {
	}

	public void visit(PredicateFieldRoot root) {
		value=predicate;
	}

	public void visit(StaticFieldRoot root) {
		try {
			value=Class.forName(root.className());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void visit(ArrayAccessValue operand) {
		operand.parent().accept(this);
		Object parent=value;
		operand.index().accept(this);
		Integer index=(Integer)value;
		value=Array.get(parent, index.intValue());
	}

	public void visit(MethodCallValue operand) {
		operand.parent().accept(this);
		Object receiver=value;
		Object[] params=new Object[operand.params().length];
		for (int paramIdx = 0; paramIdx < operand.params().length; paramIdx++) {
			operand.params()[paramIdx].accept(this);
			params[paramIdx]=value;
		}
		Class clazz=receiver.getClass();
		if(operand.parent().root() instanceof StaticFieldRoot) {
			clazz=(Class)receiver;
		}
		Method method=ReflectUtil.methodFor(clazz,operand.methodName(),operand.paramTypes());
		try {
			value=method.invoke(receiver, params);
		} catch (Exception exc) {
			exc.printStackTrace();
			value=null;
		}
	}

	public ComparisonQueryGeneratingVisitor(Object predicate) {
		super();
		this.predicate = predicate;
	}
	
}