/* VariableStack - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.AssertError;
import jode.decompiler.LocalInfo;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;

public class VariableStack
{
    public static final VariableStack EMPTY = new VariableStack();
    final LocalInfo[] stackMap;
    
    private VariableStack() {
	stackMap = new LocalInfo[0];
    }
    
    private VariableStack(LocalInfo[] localinfos) {
	stackMap = localinfos;
    }
    
    public boolean isEmpty() {
	return stackMap.length == 0;
    }
    
    public VariableStack pop(int i) {
	LocalInfo[] localinfos = new LocalInfo[stackMap.length - i];
	System.arraycopy(stackMap, 0, localinfos, 0, stackMap.length - i);
	return new VariableStack(localinfos);
    }
    
    public VariableStack push(LocalInfo localinfo) {
	return poppush(0, localinfo);
    }
    
    public VariableStack poppush(int i, LocalInfo localinfo) {
	LocalInfo[] localinfos = new LocalInfo[stackMap.length - i + 1];
	System.arraycopy(stackMap, 0, localinfos, 0, stackMap.length - i);
	localinfos[stackMap.length - i] = localinfo;
	return new VariableStack(localinfos);
    }
    
    public VariableStack peek(int i) {
	LocalInfo[] localinfos = new LocalInfo[i];
	System.arraycopy(stackMap, stackMap.length - i, localinfos, 0, i);
	return new VariableStack(localinfos);
    }
    
    public void merge(VariableStack variablestack_0_) {
	if (stackMap.length != variablestack_0_.stackMap.length)
	    throw new IllegalArgumentException("stack length differs");
	for (int i = 0; i < stackMap.length; i++) {
	    if (stackMap[i].getType().stackSize()
		!= variablestack_0_.stackMap[i].getType().stackSize())
		throw new IllegalArgumentException
			  ("stack element length differs at " + i);
	    stackMap[i].combineWith(variablestack_0_.stackMap[i]);
	}
    }
    
    public static VariableStack merge(VariableStack variablestack,
				      VariableStack variablestack_1_) {
	if (variablestack == null)
	    return variablestack_1_;
	if (variablestack_1_ == null)
	    return variablestack;
	variablestack.merge(variablestack_1_);
	return variablestack;
    }
    
    public Expression mergeIntoExpression(Expression expression) {
	for (int i = stackMap.length - 1; i >= 0; i--)
	    expression
		= expression.addOperand(new LocalLoadOperator(stackMap[i]
								  .getType(),
							      null,
							      stackMap[i]));
	return expression;
    }
    
    public VariableStack executeSpecial(SpecialBlock specialblock) {
	if (specialblock.type == SpecialBlock.POP) {
	    int i = 0;
	    int i_2_ = stackMap.length;
	    for (/**/; i < specialblock.count;
		 i += stackMap[i_2_].getType().stackSize())
		i_2_--;
	    if (i != specialblock.count)
		throw new IllegalArgumentException("wrong POP");
	    LocalInfo[] localinfos = new LocalInfo[i_2_];
	    System.arraycopy(stackMap, 0, localinfos, 0, i_2_);
	    return new VariableStack(localinfos);
	}
	if (specialblock.type == SpecialBlock.DUP) {
	    int i = 0;
	    int i_3_ = 0;
	    int i_4_ = stackMap.length;
	    for (/**/; i < specialblock.count;
		 i += stackMap[i_4_].getType().stackSize()) {
		i_4_--;
		i_3_++;
	    }
	    if (i != specialblock.count)
		throw new IllegalArgumentException("wrong DUP");
	    int i_5_ = i_4_;
	    int i_6_;
	    for (i_6_ = 0; i_6_ < specialblock.depth;
		 i_6_ += stackMap[i_5_].getType().stackSize())
		i_5_--;
	    if (i_6_ != specialblock.depth)
		throw new IllegalArgumentException("wrong DUP");
	    LocalInfo[] localinfos = new LocalInfo[stackMap.length + i_3_];
	    System.arraycopy(stackMap, 0, localinfos, 0, i_5_);
	    System.arraycopy(stackMap, i_4_, localinfos, i_5_, i_3_);
	    System.arraycopy(stackMap, i_5_, localinfos, i_5_ + i_3_,
			     i_4_ - i_5_);
	    System.arraycopy(stackMap, i_4_, localinfos, i_4_ + i_3_, i_3_);
	    return new VariableStack(localinfos);
	}
	if (specialblock.type == SpecialBlock.SWAP) {
	    LocalInfo[] localinfos = new LocalInfo[stackMap.length];
	    System.arraycopy(stackMap, 0, localinfos, 0, stackMap.length - 2);
	    if (stackMap[stackMap.length - 2].getType().stackSize() != 1
		|| stackMap[stackMap.length - 1].getType().stackSize() != 1)
		throw new IllegalArgumentException("wrong SWAP");
	    localinfos[stackMap.length - 2] = stackMap[stackMap.length - 1];
	    localinfos[stackMap.length - 1] = stackMap[stackMap.length - 2];
	    return new VariableStack(localinfos);
	}
	throw new AssertError("Unknown SpecialBlock");
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer("[");
	for (int i = 0; i < stackMap.length; i++) {
	    if (i > 0)
		stringbuffer.append(", ");
	    stringbuffer.append(stackMap[i].getName());
	}
	return stringbuffer.append("]").toString();
    }
}
