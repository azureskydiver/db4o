/* ConvertOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ConvertOperator extends Operator
{
    Type from;
    
    public ConvertOperator(Type type, Type type_0_) {
	super(type_0_, 0);
	from = type;
	this.initOperands(1);
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof ConvertOperator && type == operator.type;
    }
    
    public int getPriority() {
	return 700;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(from));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("(");
	tabbedprintwriter.printType(type.getCanonic());
	tabbedprintwriter.print(") ");
	subExpressions[0].dumpExpression(tabbedprintwriter, 700);
    }
}
