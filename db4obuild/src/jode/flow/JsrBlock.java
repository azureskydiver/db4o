/* JsrBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class JsrBlock extends StructuredBlock
{
    StructuredBlock innerBlock;
    boolean good = false;
    
    public JsrBlock(Jump jump, Jump jump_0_) {
	innerBlock = new EmptyBlock(jump);
	innerBlock.outer = this;
	this.setJump(jump_0_);
    }
    
    public void setGood(boolean bool) {
	good = bool;
    }
    
    public boolean isGood() {
	return good;
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_1_) {
	if (innerBlock == structuredblock)
	    innerBlock = structuredblock_1_;
	else
	    return false;
	return true;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	LocalInfo localinfo = new LocalInfo();
	localinfo.setType(Type.tUObject);
	innerBlock.mapStackToLocal(variablestack.push(localinfo));
	if (jump != null) {
	    jump.stackMap = variablestack;
	    return null;
	}
	return variablestack;
    }
    
    public StructuredBlock[] getSubBlocks() {
	return new StructuredBlock[] { innerBlock };
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println("JSR");
	tabbedprintwriter.tab();
	innerBlock.dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
    }
}
