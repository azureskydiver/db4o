/* EmptyBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class EmptyBlock extends StructuredBlock
{
    public EmptyBlock() {
	/* empty */
    }
    
    public EmptyBlock(Jump jump) {
	this.setJump(jump);
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public StructuredBlock appendBlock(StructuredBlock structuredblock) {
	if (outer instanceof ConditionalBlock) {
	    IfThenElseBlock ifthenelseblock
		= new IfThenElseBlock(((ConditionalBlock) outer)
					  .getInstruction());
	    ifthenelseblock.moveDefinitions(outer, this);
	    ifthenelseblock.replace(outer);
	    ifthenelseblock.moveJump(outer.jump);
	    ifthenelseblock.setThenBlock(this);
	}
	structuredblock.replace(this);
	return structuredblock;
    }
    
    public StructuredBlock prependBlock(StructuredBlock structuredblock) {
	structuredblock = appendBlock(structuredblock);
	structuredblock.moveJump(jump);
	return structuredblock;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (jump == null)
	    tabbedprintwriter.println("/* empty */");
    }
}
