/* DeadCodeAnalysis - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.util.Iterator;

import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;

public class DeadCodeAnalysis
{
    private static final String REACHABLE = "R";
    private static final String REACHCHANGED = "C";
    
    private static void propagateReachability(BytecodeInfo bytecodeinfo) {
	boolean bool;
	do {
	    bool = false;
	    Iterator iterator = bytecodeinfo.getInstructions().iterator();
	    while (iterator.hasNext()) {
		Instruction instruction = (Instruction) iterator.next();
		if (instruction.getTmpInfo() == "C") {
		    bool = true;
		    instruction.setTmpInfo("R");
		    Instruction[] instructions = instruction.getSuccs();
		    if (instructions != null) {
			for (int i = 0; i < instructions.length; i++) {
			    if (instructions[i].getTmpInfo() == null)
				instructions[i].setTmpInfo("C");
			}
		    }
		    if (!instruction.doesAlwaysJump()
			&& instruction.getNextByAddr() != null
			&& instruction.getNextByAddr().getTmpInfo() == null)
			instruction.getNextByAddr().setTmpInfo("C");
		    if (instruction.getOpcode() == 168
			&& instruction.getNextByAddr().getTmpInfo() == null)
			instruction.getNextByAddr().setTmpInfo("C");
		}
	    }
	} while (bool);
    }
    
    public static void removeDeadCode(BytecodeInfo bytecodeinfo) {
	((Instruction) bytecodeinfo.getInstructions().get(0)).setTmpInfo("C");
	propagateReachability(bytecodeinfo);
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	boolean bool;
	do {
	    bool = false;
	    for (int i = 0; i < handlers.length; i++) {
		if (handlers[i].catcher.getTmpInfo() == null) {
		    for (Instruction instruction = handlers[i].start;
			 instruction != null;
			 instruction = instruction.getNextByAddr()) {
			if (instruction.getTmpInfo() != null) {
			    handlers[i].catcher.setTmpInfo("C");
			    propagateReachability(bytecodeinfo);
			    bool = true;
			    break;
			}
			if (instruction == handlers[i].end)
			    break;
		    }
		}
	    }
	} while (bool);
	for (int i = 0; i < handlers.length; i++) {
	    if (handlers[i].catcher.getTmpInfo() == null) {
		Handler[] handlers_0_ = new Handler[handlers.length - 1];
		System.arraycopy(handlers, 0, handlers_0_, 0, i);
		System.arraycopy(handlers, i + 1, handlers_0_, i,
				 handlers.length - (i + 1));
		handlers = handlers_0_;
		bytecodeinfo.setExceptionHandlers(handlers_0_);
		i--;
	    } else {
		while (handlers[i].start.getTmpInfo() == null)
		    handlers[i].start = handlers[i].start.getNextByAddr();
		while (handlers[i].end.getTmpInfo() == null)
		    handlers[i].end = handlers[i].end.getPrevByAddr();
	    }
	}
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    if (instruction.getTmpInfo() != null)
		instruction.setTmpInfo(null);
	    else
		iterator.remove();
	}
    }
}
