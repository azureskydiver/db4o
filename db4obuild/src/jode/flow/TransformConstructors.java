/* TransformConstructors - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.lang.reflect.Modifier;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.OuterValueListener;
import jode.decompiler.OuterValues;
import jode.expr.Expression;
import jode.expr.FieldOperator;
import jode.expr.IIncOperator;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.LocalVarOperator;
import jode.expr.Operator;
import jode.expr.PutFieldOperator;
import jode.expr.StoreInstruction;
import jode.expr.ThisOperator;
import jode.type.MethodType;
import jode.type.Type;

public class TransformConstructors
{
    ClassAnalyzer clazzAnalyzer;
    boolean isStatic;
    MethodAnalyzer[] cons;
    int type0Count;
    int type01Count;
    OuterValues outerValues;
    
    public TransformConstructors(ClassAnalyzer classanalyzer, boolean bool,
				 MethodAnalyzer[] methodanalyzers) {
	clazzAnalyzer = classanalyzer;
	isStatic = bool;
	cons = methodanalyzers;
	if (!bool)
	    outerValues = classanalyzer.getOuterValues();
	lookForConstructorCall();
    }
    
    private int getConstructorType(StructuredBlock structuredblock) {
	InstructionBlock instructionblock;
	if (structuredblock instanceof InstructionBlock)
	    instructionblock = (InstructionBlock) structuredblock;
	else if (structuredblock instanceof SequentialBlock
		 && (structuredblock.getSubBlocks()[0]
		     instanceof InstructionBlock))
	    instructionblock
		= (InstructionBlock) structuredblock.getSubBlocks()[0];
	else
	    return 0;
	Expression expression = instructionblock.getInstruction().simplify();
	if (!(expression instanceof InvokeOperator)
	    || expression.getFreeOperandCount() != 0)
	    return 0;
	InvokeOperator invokeoperator = (InvokeOperator) expression;
	if (!invokeoperator.isConstructor() || !invokeoperator.isSuperOrThis())
	    return 0;
	Expression expression_0_ = invokeoperator.getSubExpressions()[0];
	if (!isThis(expression_0_, clazzAnalyzer.getClazz()))
	    return 0;
	if (invokeoperator.isThis())
	    return 2;
	return 1;
    }
    
    public void lookForConstructorCall() {
	type01Count = cons.length;
	int i = 0;
	while (i < type01Count) {
	    MethodAnalyzer methodanalyzer = cons[i];
	    FlowBlock flowblock = cons[i].getMethodHeader();
	    if (flowblock == null || !flowblock.hasNoJumps())
		break;
	    StructuredBlock structuredblock = cons[i].getMethodHeader().block;
	    int i_1_ = isStatic ? 0 : getConstructorType(structuredblock);
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("constr " + i + ": type" + i_1_ + " "
					  + structuredblock);
	    switch (i_1_) {
	    case 0:
		cons[i] = cons[type0Count];
		cons[type0Count++] = methodanalyzer;
		/* fall through */
	    case 1:
		i++;
		break;
	    case 2:
		cons[i] = cons[--type01Count];
		cons[type01Count] = methodanalyzer;
		break;
	    }
	}
    }
    
    public static boolean isThis(Expression expression, ClassInfo classinfo) {
	return (expression instanceof ThisOperator
		&& ((ThisOperator) expression).getClassInfo() == classinfo);
    }
    
    private void checkAnonymousConstructor() {
	if (!isStatic && cons.length == 1 && type01Count - type0Count == 1
	    && clazzAnalyzer.getName() == null) {
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("checkAnonymousConstructor of "
					  + clazzAnalyzer.getClazz());
	    StructuredBlock structuredblock = cons[0].getMethodHeader().block;
	    if (structuredblock instanceof SequentialBlock)
		structuredblock = structuredblock.getSubBlocks()[0];
	    InstructionBlock instructionblock
		= (InstructionBlock) structuredblock;
	    Expression expression
		= instructionblock.getInstruction().simplify();
	    InvokeOperator invokeoperator = (InvokeOperator) expression;
	    Expression[] expressions = invokeoperator.getSubExpressions();
	    for (int i = 1; i < expressions.length; i++) {
		if (!(expressions[i] instanceof LocalLoadOperator))
		    return;
	    }
	    Type[] types = cons[0].getType().getParameterTypes();
	    boolean bool = false;
	    int i = types.length;
	    int i_2_ = 1;
	    for (int i_3_ = 0; i_3_ < types.length - 1; i_3_++)
		i_2_ += types[i_3_].stackSize();
	    int i_4_ = 1;
	    if (expressions.length > 2) {
		LocalLoadOperator localloadoperator
		    = (LocalLoadOperator) expressions[1];
		if (localloadoperator.getLocalInfo().getSlot() == i_2_) {
		    bool = true;
		    i_4_++;
		    i--;
		    i_2_ -= types[i - 1].stackSize();
		}
	    }
	    int i_5_ = expressions.length - 1;
	    while (i_5_ >= i_4_) {
		LocalLoadOperator localloadoperator
		    = (LocalLoadOperator) expressions[i_5_];
		if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		    GlobalOptions.err.println("  pos " + i_5_ + ": " + i_2_
					      + ","
					      + localloadoperator.getLocalInfo
						    ().getSlot()
					      + "; " + i);
		if (localloadoperator.getLocalInfo().getSlot() != i_2_) {
		    i_2_ += types[i - 1].stackSize();
		    break;
		}
		i_5_--;
		if (--i == 0)
		    break;
		i_2_ -= types[i - 1].stackSize();
	    }
	    ClassAnalyzer classanalyzer = invokeoperator.getClassAnalyzer();
	    OuterValues outervalues = null;
	    if (classanalyzer != null
		&& classanalyzer.getParent() instanceof MethodAnalyzer)
		outervalues = classanalyzer.getOuterValues();
	    int i_6_ = i_5_ - i_4_ + 1;
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("  super outer: " + outervalues);
	    for (/**/; i_5_ >= i_4_; i_5_--) {
		LocalLoadOperator localloadoperator
		    = (LocalLoadOperator) expressions[i_5_];
		if (localloadoperator.getLocalInfo().getSlot() >= i_2_) {
		    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
			GlobalOptions.err.println("  Illegal slot at " + i_5_
						  + ":"
						  + localloadoperator
							.getLocalInfo
							().getSlot());
		    return;
		}
	    }
	    if (i_6_ == 1
		&& classanalyzer.getParent() instanceof ClassAnalyzer) {
		LocalLoadOperator localloadoperator
		    = (LocalLoadOperator) expressions[i_4_];
		if (outerValues.getValueBySlot(localloadoperator.getLocalInfo
						   ().getSlot())
		    instanceof ThisOperator) {
		    i_6_ = 0;
		    outerValues.setImplicitOuterClass(true);
		}
	    }
	    if (i_6_ > 0) {
		if (outervalues == null || outervalues.getCount() < i_6_) {
		    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
			GlobalOptions.err
			    .println("  super outer doesn't match: " + i_6_);
		    return;
		}
		outervalues.setMinCount(i_6_);
	    }
	    outerValues.setMinCount(i);
	    if (outervalues != null) {
		final int ovdiff = i - i_6_;
		outerValues.setCount(outervalues.getCount() + ovdiff);
		outervalues.addOuterValueListener(new OuterValueListener() {
		    public void shrinkingOuterValues
			(OuterValues outervalues_9_, int i_10_) {
			outerValues.setCount(i_10_ + ovdiff);
		    }
		});
	    } else
		outerValues.setCount(i);
	    if (bool)
		outerValues.setJikesAnonymousInner(true);
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("  succeeded: " + outerValues);
	    cons[0].setAnonymousConstructor(true);
	    instructionblock.removeBlock();
	    type0Count++;
	}
    }
    
    private boolean checkJikesSuper(Expression expression) {
	if (expression instanceof LocalStoreOperator
	    || expression instanceof IIncOperator)
	    return false;
	if (expression instanceof Operator) {
	    Expression[] expressions
		= ((Operator) expression).getSubExpressions();
	    for (int i = 0; i < expressions.length; i++) {
		if (!checkJikesSuper(expressions[i]))
		    return false;
	    }
	}
	return true;
    }
    
    private Expression renameJikesSuper(Expression expression,
					MethodAnalyzer methodanalyzer, int i,
					int i_11_) {
	if (expression instanceof LocalLoadOperator) {
	    LocalLoadOperator localloadoperator
		= (LocalLoadOperator) expression;
	    int i_12_ = localloadoperator.getLocalInfo().getSlot();
	    if (i_12_ >= i && i_12_ < i_11_)
		return outerValues.getValueBySlot(i_12_);
	    Type[] types = methodanalyzer.getType().getParameterTypes();
	    if (i_12_ >= i_11_)
		i_12_ -= i_11_ - i;
	    int i_13_;
	    for (i_13_ = 0; i_12_ > 1 && i_13_ < types.length; i_13_++)
		i_12_ -= types[i_13_].stackSize();
	    localloadoperator
		.setLocalInfo(methodanalyzer.getParamInfo(1 + i_13_));
	    localloadoperator.setMethodAnalyzer(methodanalyzer);
	    return localloadoperator;
	}
	if (expression instanceof Operator) {
	    Expression[] expressions
		= ((Operator) expression).getSubExpressions();
	    for (int i_14_ = 0; i_14_ < expressions.length; i_14_++) {
		Expression expression_15_
		    = renameJikesSuper(expressions[i_14_], methodanalyzer, i,
				       i_11_);
		if (expression_15_ != expressions[i_14_])
		    ((Operator) expression).setSubExpressions(i_14_,
							      expression_15_);
	    }
	}
	return expression;
    }
    
    public void checkJikesContinuation() {
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    System.err.println("checkJikesContinuation: " + outerValues);
    while_20_:
	for (int i = 0; i < cons.length; i++) {
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("constr " + i + " type"
					  + (i < type0Count ? 0
					     : i < type01Count ? 1 : 2)
					  + " : " + cons[i].getMethodHeader());
	    MethodAnalyzer methodanalyzer = cons[i];
	    MethodType methodtype = methodanalyzer.getType();
	    StructuredBlock structuredblock
		= methodanalyzer.getMethodHeader().block;
	    Object object = null;
	    InstructionBlock instructionblock = null;
	    if (i >= type0Count) {
		if (!(structuredblock instanceof SequentialBlock)
		    || !(structuredblock.getSubBlocks()[1]
			 instanceof InstructionBlock))
		    continue;
		instructionblock
		    = (InstructionBlock) structuredblock.getSubBlocks()[0];
		structuredblock = structuredblock.getSubBlocks()[1];
		Expression expression
		    = instructionblock.getInstruction().simplify();
		InvokeOperator invokeoperator = (InvokeOperator) expression;
		instructionblock.setInstruction(invokeoperator);
		Expression[] expressions = invokeoperator.getSubExpressions();
		for (int i_16_ = 1; i_16_ < expressions.length; i_16_++) {
		    if (!checkJikesSuper(expressions[i_16_]))
			continue while_20_;
		}
	    }
	    if (structuredblock instanceof InstructionBlock) {
		Expression expression
		    = ((InstructionBlock) structuredblock).getInstruction()
			  .simplify();
		if (expression instanceof InvokeOperator) {
		    InvokeOperator invokeoperator
			= (InvokeOperator) expression;
		    if (invokeoperator.isThis()
			&& invokeoperator.getFreeOperandCount() == 0) {
			MethodAnalyzer methodanalyzer_17_
			    = invokeoperator.getMethodAnalyzer();
			if (methodanalyzer_17_ != null) {
			    MethodType methodtype_18_
				= methodanalyzer_17_.getType();
			    Expression[] expressions
				= invokeoperator.getSubExpressions();
			    if (methodanalyzer_17_.getName()
				    .startsWith("constructor$")
				&& methodtype_18_.getReturnType() == Type.tVoid
				&& isThis(expressions[0],
					  clazzAnalyzer.getClazz())) {
				for (int i_19_ = 1; i_19_ < expressions.length;
				     i_19_++) {
				    if (!(expressions[i_19_]
					  instanceof LocalLoadOperator))
					continue while_20_;
				}
				Type[] types = methodanalyzer.getType()
						   .getParameterTypes();
				int i_20_ = types.length;
				if (outerValues.isJikesAnonymousInner())
				    i_20_--;
				int i_21_ = i_20_ - expressions.length + 2;
				int i_22_ = i_21_ - 1;
				int i_23_ = 1;
				int i_24_ = 1;
				Expression expression_25_ = null;
				if (i_21_ > 0 && expressions.length > 1
				    && outerValues.getCount() > 0) {
				    if (((LocalLoadOperator)
					 expressions[i_24_])
					    .getLocalInfo
					    ().getSlot()
					== 1) {
					i_22_ = i_21_;
					expression_25_
					    = outerValues.getValue(0);
					i_24_++;
				    } else
					i_21_--;
				    for (int i_26_ = 0; i_26_ < i_21_; i_26_++)
					i_23_ += types[i_26_].stackSize();
				}
				if (i_22_ <= outerValues.getCount()) {
				    int i_27_ = i_23_;
				    int i_28_ = i_24_;
				    int i_29_ = i_27_ - i_28_;
				    for (int i_30_ = i_24_;
					 i_30_ < expressions.length; i_30_++) {
					if (((LocalLoadOperator)
					     expressions[i_30_])
						.getLocalInfo
						().getSlot()
					    != i_23_)
					    continue while_20_;
					i_23_ += expressions[i_30_].getType
						     ().stackSize();
				    }
				    outerValues.setMinCount(i_22_);
				    outerValues.setCount(i_21_);
				    if (instructionblock != null) {
					Expression expression_31_
					    = (renameJikesSuper
					       (instructionblock
						    .getInstruction(),
						methodanalyzer_17_, i_28_,
						i_27_));
					instructionblock.removeBlock();
					methodanalyzer_17_
					    .insertStructuredBlock
					    (instructionblock);
				    }
				    if (expression_25_ != null) {
					methodanalyzer_17_.getParamInfo(1)
					    .setExpression(expression_25_);
					methodanalyzer_17_.getMethodHeader
					    ().simplify();
				    }
				    if ((GlobalOptions.debuggingFlags & 0x200)
					!= 0)
					GlobalOptions.err
					    .println("  succeeded");
				    methodanalyzer
					.setJikesConstructor(methodanalyzer);
				    methodanalyzer_17_
					.setJikesConstructor(methodanalyzer);
				    methodanalyzer_17_
					.setHasOuterValue(i_28_ == 2);
				    if (methodanalyzer
					    .isAnonymousConstructor())
					methodanalyzer_17_
					    .setAnonymousConstructor(true);
				}
			    }
			}
		    }
		}
	    }
	}
    }
    
    private Expression transformFieldInitializer(int i,
						 Expression expression) {
	if (expression instanceof LocalVarOperator) {
	    if (!(expression instanceof LocalLoadOperator)) {
		if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		    GlobalOptions.err
			.println("illegal local op: " + expression);
		return null;
	    }
	    if (outerValues != null && (Options.options & 0x200) != 0) {
		int i_32_ = ((LocalLoadOperator) expression).getLocalInfo()
				.getSlot();
		Expression expression_33_ = outerValues.getValueBySlot(i_32_);
		if (expression_33_ != null)
		    return expression_33_;
	    }
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("not outerValue: " + expression + " "
					  + outerValues);
	    return null;
	}
	if (expression instanceof FieldOperator) {
	    if (expression instanceof PutFieldOperator)
		return null;
	    FieldOperator fieldoperator = (FieldOperator) expression;
	    if (fieldoperator.getClassInfo() == clazzAnalyzer.getClazz()
		&& (clazzAnalyzer.getFieldIndex(fieldoperator.getFieldName(),
						fieldoperator.getFieldType())
		    >= i))
		return null;
	}
	if (expression instanceof InvokeOperator) {
	    MethodInfo methodinfo
		= ((InvokeOperator) expression).getMethodInfo();
	    String[] strings
		= methodinfo == null ? null : methodinfo.getExceptions();
	    if (strings != null) {
		ClassInfo classinfo
		    = ClassInfo.forName("java.lang.RuntimeException");
		ClassInfo classinfo_34_ = ClassInfo.forName("java.lang.Error");
		for (int i_35_ = 0; i_35_ < strings.length; i_35_++) {
		    ClassInfo classinfo_36_
			= ClassInfo.forName(strings[i_35_]);
		    if (!classinfo.superClassOf(classinfo_36_)
			&& !classinfo_34_.superClassOf(classinfo_36_))
			return null;
		}
	    }
	}
	if (expression instanceof Operator) {
	    Operator operator = (Operator) expression;
	    Expression[] expressions = operator.getSubExpressions();
	    for (int i_37_ = 0; i_37_ < expressions.length; i_37_++) {
		Expression expression_38_
		    = transformFieldInitializer(i, expressions[i_37_]);
		if (expression_38_ == null)
		    return null;
		if (expression_38_ != expressions[i_37_])
		    operator.setSubExpressions(i_37_, expression_38_);
	    }
	}
	return expression;
    }
    
    public void removeSynthInitializers() {
	if ((Options.options & 0x200) != 0 && !isStatic && type01Count != 0) {
	    checkAnonymousConstructor();
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("removeSynthInitializers of "
					  + clazzAnalyzer.getClazz());
	    StructuredBlock[] structuredblocks
		= new StructuredBlock[type01Count];
	    for (int i = 0; i < type01Count; i++) {
		structuredblocks[i] = cons[i].getMethodHeader().block;
		if (i >= type0Count) {
		    if (structuredblocks[i] instanceof SequentialBlock)
			structuredblocks[i]
			    = structuredblocks[i].getSubBlocks()[1];
		    else
			return;
		}
	    }
	while_21_:
	    do {
		boolean bool;
		do {
		    StructuredBlock structuredblock
			= (structuredblocks[0] instanceof SequentialBlock
			   ? structuredblocks[0].getSubBlocks()[0]
			   : structuredblocks[0]);
		    if (!(structuredblock instanceof InstructionBlock))
			break while_21_;
		    Expression expression
			= ((InstructionBlock) structuredblock).getInstruction
			      ().simplify();
		    if (!(expression instanceof StoreInstruction)
			|| expression.getFreeOperandCount() != 0)
			break while_21_;
		    StoreInstruction storeinstruction
			= (StoreInstruction) expression;
		    if (!(storeinstruction.getLValue()
			  instanceof PutFieldOperator))
			break while_21_;
		    PutFieldOperator putfieldoperator
			= (PutFieldOperator) storeinstruction.getLValue();
		    if (putfieldoperator.isStatic() != isStatic
			|| (putfieldoperator.getClassInfo()
			    != clazzAnalyzer.getClazz())
			|| !isThis(putfieldoperator.getSubExpressions()[0],
				   clazzAnalyzer.getClazz()))
			break while_21_;
		    int i = clazzAnalyzer.getFieldIndex(putfieldoperator
							    .getFieldName(),
							putfieldoperator
							    .getFieldType());
		    if (i < 0)
			break while_21_;
		    FieldAnalyzer fieldanalyzer = clazzAnalyzer.getField(i);
		    if (!fieldanalyzer.isSynthetic())
			break while_21_;
		    Expression expression_39_
			= storeinstruction.getSubExpressions()[1];
		    expression_39_
			= transformFieldInitializer(i, expression_39_);
		    if (expression_39_ == null)
			break while_21_;
		    for (int i_40_ = 1; i_40_ < type01Count; i_40_++) {
			structuredblock
			    = ((structuredblocks[i_40_]
				instanceof SequentialBlock)
			       ? structuredblocks[i_40_].getSubBlocks()[0]
			       : structuredblocks[i_40_]);
			if (!(structuredblock instanceof InstructionBlock)
			    || !((InstructionBlock) structuredblock)
				    .getInstruction
				    ().simplify
				    ().equals(expression)) {
			    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
				GlobalOptions.err.println("  constr 0 and "
							  + i_40_ + " differ: "
							  + expression
							  + "<-/->"
							  + structuredblock);
			    break while_21_;
			}
		    }
		    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
			GlobalOptions.err.println("  field "
						  + putfieldoperator
							.getFieldName()
						  + " = " + expression_39_);
		    if (!fieldanalyzer.setInitializer(expression_39_)) {
			if ((GlobalOptions.debuggingFlags & 0x200) != 0)
			    GlobalOptions.err.println("    setField failed");
			break while_21_;
		    }
		    bool = false;
		    for (int i_41_ = 0; i_41_ < type01Count; i_41_++) {
			if (structuredblocks[i_41_]
			    instanceof SequentialBlock) {
			    StructuredBlock structuredblock_42_
				= structuredblocks[i_41_].getSubBlocks()[1];
			    structuredblock_42_
				.replace(structuredblocks[i_41_]);
			    structuredblocks[i_41_] = structuredblock_42_;
			} else {
			    structuredblocks[i_41_].removeBlock();
			    structuredblocks[i_41_] = null;
			    bool = true;
			}
		    }
		} while (!bool);
		if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		    GlobalOptions.err.println("one constr is over");
	    } while (false);
	}
    }
    
    public int transformOneField(int i, StructuredBlock structuredblock) {
	if (!(structuredblock instanceof InstructionBlock))
	    return -1;
	Expression expression
	    = ((InstructionBlock) structuredblock).getInstruction().simplify();
	if (!(expression instanceof StoreInstruction)
	    || expression.getFreeOperandCount() != 0)
	    return -1;
	StoreInstruction storeinstruction = (StoreInstruction) expression;
	if (!(storeinstruction.getLValue() instanceof PutFieldOperator))
	    return -1;
	PutFieldOperator putfieldoperator
	    = (PutFieldOperator) storeinstruction.getLValue();
	if (putfieldoperator.isStatic() != isStatic
	    || putfieldoperator.getClassInfo() != clazzAnalyzer.getClazz())
	    return -1;
	if (!isStatic && !isThis(putfieldoperator.getSubExpressions()[0],
				 clazzAnalyzer.getClazz())) {
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("  not this: " + expression);
	    return -1;
	}
	int i_43_
	    = clazzAnalyzer.getFieldIndex(putfieldoperator.getFieldName(),
					  putfieldoperator.getFieldType());
	if (i_43_ <= i)
	    return -1;
	Expression expression_44_ = storeinstruction.getSubExpressions()[1];
	expression_44_ = transformFieldInitializer(i_43_, expression_44_);
	if (expression_44_ == null)
	    return -1;
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err.println("  field "
				      + putfieldoperator.getFieldName() + " = "
				      + expression_44_);
	if (i_43_ <= i
	    || !clazzAnalyzer.getField(i_43_).setInitializer(expression_44_)) {
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("set field failed");
	    return -1;
	}
	return i_43_;
    }
    
    public void transformBlockInitializer(StructuredBlock structuredblock) {
	Object object = null;
	Object object_45_ = null;
	int i = -1;
	for (/**/; structuredblock instanceof SequentialBlock;
	     structuredblock = structuredblock.getSubBlocks()[1]) {
	    StructuredBlock structuredblock_46_
		= structuredblock.getSubBlocks()[0];
	    int i_47_ = transformOneField(i, structuredblock_46_);
	    if (i_47_ < 0)
		clazzAnalyzer.addBlockInitializer(i + 1, structuredblock_46_);
	    else
		i = i_47_;
	}
	if (transformOneField(i, structuredblock) < 0)
	    clazzAnalyzer.addBlockInitializer(i + 1, structuredblock);
    }
    
    public boolean checkBlockInitializer(InvokeOperator invokeoperator) {
	if (!invokeoperator.isThis()
	    || invokeoperator.getFreeOperandCount() != 0)
	    return false;
	MethodAnalyzer methodanalyzer = invokeoperator.getMethodAnalyzer();
	if (methodanalyzer == null)
	    return false;
	FlowBlock flowblock = methodanalyzer.getMethodHeader();
	MethodType methodtype = methodanalyzer.getType();
	if (!methodanalyzer.getName().startsWith("block$")
	    || methodtype.getParameterTypes().length != 0
	    || methodtype.getReturnType() != Type.tVoid)
	    return false;
	if (flowblock == null || !flowblock.hasNoJumps())
	    return false;
	if (!isThis(invokeoperator.getSubExpressions()[0],
		    clazzAnalyzer.getClazz()))
	    return false;
	methodanalyzer.setJikesBlockInitializer(true);
	transformBlockInitializer(flowblock.block);
	return true;
    }
    
    private void removeDefaultSuper() {
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err
		.println("removeDefaultSuper of " + clazzAnalyzer.getClazz());
	for (int i = type0Count; i < type01Count; i++) {
	    MethodAnalyzer methodanalyzer = cons[i];
	    FlowBlock flowblock = cons[i].getMethodHeader();
	    StructuredBlock structuredblock = flowblock.block;
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err
		    .println("constr " + i + ": " + structuredblock);
	    InstructionBlock instructionblock;
	    if (structuredblock instanceof InstructionBlock)
		instructionblock = (InstructionBlock) structuredblock;
	    else
		instructionblock
		    = (InstructionBlock) structuredblock.getSubBlocks()[0];
	    InvokeOperator invokeoperator
		= ((InvokeOperator)
		   instructionblock.getInstruction().simplify());
	    ClassInfo classinfo = invokeoperator.getClassInfo();
	    InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	    int i_48_ = invokeoperator.getSubExpressions().length - 1;
	    if ((Options.options & 0x2) != 0 && innerclassinfos != null
		&& innerclassinfos[0].outer != null
		&& innerclassinfos[0].name != null
		&& !Modifier.isStatic(innerclassinfos[0].modifiers)) {
		if (i_48_ != 1 || !(invokeoperator.getSubExpressions()[1]
				    instanceof ThisOperator))
		    continue;
	    } else {
		ClassAnalyzer classanalyzer
		    = invokeoperator.getClassAnalyzer();
		OuterValues outervalues = null;
		if (classanalyzer != null)
		    outervalues = classanalyzer.getOuterValues();
		if (i_48_ > 0
		    && (outervalues == null || i_48_ > outervalues.getCount()))
		    continue;
	    }
	    instructionblock.removeBlock();
	    if (i > type0Count) {
		cons[i] = cons[type0Count];
		cons[type0Count] = methodanalyzer;
	    }
	    type0Count++;
	}
    }
    
    private void removeInitializers() {
	if (type01Count != 0) {
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("removeInitializers");
	    StructuredBlock[] structuredblocks
		= new StructuredBlock[type01Count];
	    for (int i = 0; i < type01Count; i++) {
		FlowBlock flowblock = cons[i].getMethodHeader();
		structuredblocks[i] = flowblock.block;
		if (i >= type0Count) {
		    if (structuredblocks[i] instanceof SequentialBlock)
			structuredblocks[i]
			    = structuredblocks[i].getSubBlocks()[1];
		    else {
			structuredblocks[i] = null;
			return;
		    }
		}
	    }
	    int i = -1;
	while_22_:
	    do {
		boolean bool;
		do {
		    StructuredBlock structuredblock
			= (structuredblocks[0] instanceof SequentialBlock
			   ? structuredblocks[0].getSubBlocks()[0]
			   : structuredblocks[0]);
		    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
			GlobalOptions.err
			    .println("Instruction: " + structuredblock);
		    if (!(structuredblock instanceof InstructionBlock))
			break while_22_;
		    Expression expression
			= ((InstructionBlock) structuredblock).getInstruction
			      ().simplify();
		    for (int i_49_ = 1; i_49_ < type01Count; i_49_++) {
			structuredblock
			    = ((structuredblocks[i_49_]
				instanceof SequentialBlock)
			       ? structuredblocks[i_49_].getSubBlocks()[0]
			       : structuredblocks[i_49_]);
			if (!(structuredblock instanceof InstructionBlock)
			    || !((InstructionBlock) structuredblock)
				    .getInstruction
				    ().simplify
				    ().equals(expression)) {
			    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
				GlobalOptions.err.println("constr " + i_49_
							  + " differs: "
							  + structuredblock);
			    break while_22_;
			}
		    }
		    if (expression instanceof InvokeOperator
			&& checkBlockInitializer((InvokeOperator)
						 expression)) {
			for (int i_50_ = 0; i_50_ < type01Count; i_50_++) {
			    if (structuredblocks[i_50_]
				instanceof SequentialBlock) {
				StructuredBlock structuredblock_51_
				    = (structuredblocks[i_50_].getSubBlocks()
				       [1]);
				structuredblock_51_
				    .replace(structuredblocks[i_50_]);
				structuredblocks[i_50_] = structuredblock_51_;
			    } else {
				structuredblocks[i_50_].removeBlock();
				structuredblocks[i_50_] = null;
			    }
			}
			break while_22_;
		    }
		    int i_52_ = transformOneField(i, structuredblock);
		    if (i_52_ < 0)
			break while_22_;
		    i = i_52_;
		    bool = false;
		    for (int i_53_ = 0; i_53_ < type01Count; i_53_++) {
			if (structuredblocks[i_53_]
			    instanceof SequentialBlock) {
			    StructuredBlock structuredblock_54_
				= structuredblocks[i_53_].getSubBlocks()[1];
			    structuredblock_54_
				.replace(structuredblocks[i_53_]);
			    structuredblocks[i_53_] = structuredblock_54_;
			} else {
			    structuredblocks[i_53_].removeBlock();
			    structuredblocks[i_53_] = null;
			    bool = true;
			}
		    }
		} while (!bool);
		if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		    GlobalOptions.err.println("one constr is over");
	    } while (false);
	}
    }
    
    public void transform() {
	if ((Options.options & 0x200) != 0 && cons.length != 0) {
	    removeDefaultSuper();
	    removeInitializers();
	    checkJikesContinuation();
	    if (outerValues != null) {
		for (int i = 0; i < cons.length; i++) {
		    for (int i_55_ = 0; i_55_ < outerValues.getCount();
			 i_55_++)
			cons[i].getParamInfo(i_55_ + 1)
			    .setExpression(outerValues.getValue(i_55_));
		    cons[i].getMethodHeader().simplify();
		}
	    }
	}
    }
}
