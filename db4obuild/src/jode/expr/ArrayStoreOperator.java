/* ArrayStoreOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ArrayStoreOperator extends ArrayLoadOperator
    implements LValueExpression
{
    public ArrayStoreOperator(Type type) {
	super(type);
    }
    
    public boolean matches(Operator operator) {
	return operator instanceof ArrayLoadOperator;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	Type type = subExpressions[0].getType().getHint();
	if (type instanceof ArrayType) {
	    Type type_0_ = ((ArrayType) type).getElementType();
	    if (!type_0_.isOfType(this.getType())) {
		tabbedprintwriter.print("(");
		tabbedprintwriter.startOp(0, 1);
		tabbedprintwriter.print("(");
		tabbedprintwriter
		    .printType(Type.tArray(this.getType().getHint()));
		tabbedprintwriter.print(") ");
		tabbedprintwriter.breakOp();
		subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		tabbedprintwriter.print(")");
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print("[");
		subExpressions[1].dumpExpression(tabbedprintwriter, 0);
		tabbedprintwriter.print("]");
		return;
	    }
	}
	super.dumpExpression(tabbedprintwriter);
    }
}
