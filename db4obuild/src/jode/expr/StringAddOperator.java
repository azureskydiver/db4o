/* StringAddOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class StringAddOperator extends Operator
{
    protected Type operandType;
    
    public StringAddOperator() {
	super(Type.tString, 1);
	this.initOperands(2);
    }
    
    public int getPriority() {
	return 610;
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof StringAddOperator;
    }
    
    public void updateSubTypes() {
	/* empty */
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (!subExpressions[0].getType().isOfType(Type.tString)
	    && !subExpressions[1].getType().isOfType(Type.tString)) {
	    tabbedprintwriter.print("\"\"");
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(this.getOperatorString());
	}
	subExpressions[0].dumpExpression(tabbedprintwriter, 610);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(this.getOperatorString());
	subExpressions[1].dumpExpression(tabbedprintwriter, 611);
    }
}
