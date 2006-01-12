/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.inline;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Used to keep track of the height of the stack.  As instructions are
 * visited, the height of the stack is adjusted accordingly.
 */
public class StackHeightCounter extends InstructionAdapter {
  public static boolean DEBUG = false;

  private int height;           // Current stack height
  private HashMap labelHeights; // Maps labels to their heights as Integers
  private MethodEditor method;  // Method whose height we're computing
  Set tryCatches;            // TryCatches active at current instruction

  private static void db(String s) {
    if(DEBUG)
      System.out.println(s);
  }

  /**
   * Constructor.
   *
   * @param height
   *        Initial height of stack
   */
  public StackHeightCounter(MethodEditor method) {
    this.method = method;
    this.height = 0;
    this.labelHeights = new HashMap();
    this.tryCatches = new HashSet();
  }

  /**
   * Returns the current height of the stack.
   */
  public int height() {
    return(this.height);
  }

  /**
   * Handles a Label.  Special provisions must be made for labels that
   * catch exceptions.
   */
  public void handle(Label label) {
    Integer labelHeight = (Integer) labelHeights.get(label);
    if(labelHeight != null) {
      height = labelHeight.intValue();
    }
    
    // If this label begins an exception handler, then start it off
    // with a new stack with one element (the exception object) on it.
    Iterator tryCatches = method.tryCatches().iterator();
    while(tryCatches.hasNext()) {
      TryCatch tc = (TryCatch) tryCatches.next();
      if(tc.handler().equals(label)) {
	label.setStartsBlock(true);
	height = 1;
	break;
      }

      if(tc.start().equals(label)) {
	// If this block starts a protected region make note of the
	// TryCatch block
	this.tryCatches.add(tc);
      }

      if(tc.end().equals(label)) {
	// If this block ends a protected region, remove it from the
	// tryCatches list
	this.tryCatches.remove(tc);
      }
    }
  }

  /**
   * Handles an instruction.  Special provisions must be made to
   * handle jumps, switches, throws, and returns.
   */
  public void handle(Instruction inst) {
    inst.visit(this);

    if(inst.isJump()) {
      Label target = (Label) inst.operand();
      target.setStartsBlock(true);
      Integer targetHeight = (Integer) labelHeights.get(target);
      if(targetHeight != null) {
	if(targetHeight.intValue() != height) {
	  // Make sure stack heights match
	  db("Stack height mismatch (" + targetHeight.intValue() + 
	     " != " + height + ") at " + inst);
	}

      } else {
	labelHeights.put(target, new Integer(height));
      }
      
    } else if(inst.isSwitch()) {
      // Propagate height to all targets
      Switch sw = (Switch) inst.operand();
      Label defaultTarget = sw.defaultTarget();
      defaultTarget.setStartsBlock(true);
      Integer dTargetHeight = (Integer) labelHeights.get(defaultTarget);
      if(dTargetHeight != null) {
	if(dTargetHeight.intValue() != height) {
	  // Make sure stack heights match
	  db("Stack height mismatch (" + dTargetHeight.intValue() + 
	     " != " + height + ") at " + inst);
	}
      } else {
	labelHeights.put(defaultTarget, new Integer(height));
      }
      
      Label[] targets = sw.targets();
      for(int t = 0; t < targets.length; t++) {
	Label target = targets[t];
	target.setStartsBlock(true);
	Integer targetHeight = (Integer) labelHeights.get(target);
	if(targetHeight != null) {
	  if(targetHeight.intValue() != height) {
	    // Make sure stack heights match
	    db("Stack height mismatch (" + targetHeight.intValue() + 
	       " != " + height + ") at " + inst);
	  }
	} else {
	  labelHeights.put(target,  new Integer(height));
	}
      }
      
    } else if(inst.isJsr()) {
      // We have to account for the return address being pushed on the
      // stack.  Let's ignore the fact that someday in the future
      // subroutines may push stuff on the stack.  M'kay?
      Label subroutine = (Label) inst.operand();
      subroutine.setStartsBlock(true);
      Integer subHeight = (Integer) labelHeights.get(subroutine);
      if(subHeight != null) {
	if(subHeight.intValue() != height + 1) {
	  db("Stack height mismatch at subroutine (" +
	     subHeight.intValue() + " != " +  
	     (height + 1) + ") at " + inst);
	}

      } else {
	labelHeights.put(subroutine, new Integer(height + 1));
      }

    } else if(inst.isThrow() || inst.isReturn()) {
      // Clear the stack
      height = 0;
    }
  }

  /**
   * Simulates the effect of "backing up" over an instruction.
   */
  public void unhandle(Instruction inst) {
    // Temporarily negate the stack height, perform the normal handle,
    // and then negate the stack height again.
    this.height = -this.height;
    this.handle(inst);
    this.height = -this.height;
  }

  /**
   * Returns the set of <tt>TryCatch</tt> objects for the protected
   * region that the current instruction may be in.
   */
  public Set tryCatches() {
    return(this.tryCatches);
  }

  public void visit_ldc(Instruction inst) {
    Object operand = inst.operand();

    if(operand instanceof Long || operand instanceof Double) {
      height += 2;

    } else {
      height += 1;
    }
  } 

  public void visit_iload(Instruction inst) {
    height += 1;
  }

  public void visit_lload(Instruction inst) {
    height += 2;
  }

  public void visit_fload(Instruction inst) {
    height += 1;
  }

  public void visit_dload(Instruction inst) {
    height += 2;
  }

  public void visit_aload(Instruction inst) {
    height += 1;
  }

  public void visit_iaload(Instruction inst) {
    height -= 1;
  }

  public void visit_laload(Instruction inst) {
    height -= 0;
  }

  public void visit_faload(Instruction inst) {
    height -= 1;
  }

  public void visit_daload(Instruction inst) {
    height -= 0;
  }

  public void visit_aaload(Instruction inst) {
    height -= 1;
  }

  public void visit_baload(Instruction inst) {
    height -= 1;
  }

  public void visit_caload(Instruction inst) {
    height -= 1;
  }

  public void visit_saload(Instruction inst) {
    height -= 1;
  }

  public void visit_istore(Instruction inst) {
    height -= 1;
  }

  public void visit_lstore(Instruction inst) {
    height -= 2;
  }

  public void visit_fstore(Instruction inst) {
    height -= 1;
  }

  public void visit_dstore(Instruction inst) {
    height -= 2;
  }

  public void visit_astore(Instruction inst) {
    height -= 1;
  }

  public void visit_iastore(Instruction inst) {
    height -= 3;
  }

  public void visit_lastore(Instruction inst) {
    height -= 4;
  }

  public void visit_fastore(Instruction inst) {
    height -= 3;
  }

  public void visit_dastore(Instruction inst) {
    height -= 4;
  }

  public void visit_aastore(Instruction inst) { 
    height -= 3;
  }

  public void visit_bastore(Instruction inst) {
    height -= 3;
  }

  public void visit_castore(Instruction inst) {
    height -= 3;
  }

  public void visit_sastore(Instruction inst) {
    height -= 3;
  }

  public void visit_pop(Instruction inst) {
    height -= 1;
  }

  public void visit_pop2(Instruction inst) {
    height -= 2;
  }

  public void visit_dup(Instruction inst) {
    height += 1;
  }

  public void visit_dup_x1(Instruction inst) {
    height += 1;
  }

  public void visit_dup_x2(Instruction inst) {
    height += 1;
  }

  public void visit_dup2(Instruction inst) {
    height += 2;
  }

  public void visit_dup2_x1(Instruction inst) {
    height += 2;
  }

  public void visit_dup2_x2(Instruction inst) {
    height += 2;
  }

  public void visit_iadd(Instruction inst) {
    height -= 1;
  }

  public void visit_ladd(Instruction inst) {
    height -= 2;
  }

  public void visit_fadd(Instruction inst) {
    height -= 1;
  }

  public void visit_dadd(Instruction inst) {
    height -= 2;
  }

  public void visit_isub(Instruction inst) {
    height -= 1;
  }

  public void visit_lsub(Instruction inst) {
    height -= 2;
  }

  public void visit_fsub(Instruction inst) {
    height -= 1;
  }

  public void visit_dsub(Instruction inst) {
    height -= 2;
  }

  public void visit_imul(Instruction inst) {
    height -= 1;
  }

  public void visit_lmul(Instruction inst) {
    height -= 2;
  }

  public void visit_fmul(Instruction inst) {
    height -= 1;
  }

  public void visit_dmul(Instruction inst) {
    height -= 2;
  }

  public void visit_idiv(Instruction inst) {
    height -= 1;
  }

  public void visit_ldiv(Instruction inst) {
    height -= 2;
  }

  public void visit_fdiv(Instruction inst) {
    height -= 1;
  }

  public void visit_ddiv(Instruction inst) {
    height -= 2;
  }

  public void visit_irem(Instruction inst) {
    height -= 1;
  }

  public void visit_lrem(Instruction inst) {
    height -= 2;
  }

  public void visit_frem(Instruction inst) {
    height -= 1;
  }

  public void visit_drem(Instruction inst) {
    height -= 2;
  }

  public void visit_ishl(Instruction inst) {
    height -= 1;
  }

  public void visit_lshl(Instruction inst) {
    height -= 1;
  }

  public void visit_ishr(Instruction inst) {
    height -= 1;
  }

  public void visit_lshr(Instruction inst) {
    height -= 1;
  }

  public void visit_iushr(Instruction inst) {
    height -= 1;
  }

  public void visit_lushr(Instruction inst) {
    // Yes, it's only -1.  The long and the int index are popped off
    // and the shifted value is pushed.  Net loss of 1.
    height -= 1;
  }

  public void visit_iand(Instruction inst) {
    height -= 1;
  }

  public void visit_land(Instruction inst) {
    height -= 2;
  }

  public void visit_ior(Instruction inst) {
    height -= 1;
  }

  public void visit_lor(Instruction inst) {
    height -= 2;
  }

  public void visit_ixor(Instruction inst) {
    height -= 1;
  }

  public void visit_lxor(Instruction inst) {
    height -= 2;
  }

  public void visit_i2l(Instruction inst) {
    height += 1;
  }

  public void visit_i2d(Instruction inst) {
    height += 1;
  }

  public void visit_l2i(Instruction inst) {
    height -= 1;
  }

  public void visit_l2f(Instruction inst) {
    height -= 1;
  }

  public void visit_f2l(Instruction inst) {
    height += 1;
  }

  public void visit_f2d(Instruction inst) {
    height += 1;
  }

  public void visit_d2i(Instruction inst) {
    height -= 1;
  }

  public void visit_d2f(Instruction inst) {
    height -= 1;
  }

  public void visit_lcmp(Instruction inst) {
    height -= 3;
  }

  public void visit_fcmpl(Instruction inst) {
    height -= 1;
  }

  public void visit_fcmpg(Instruction inst) {
    height -= 1;
  }

  public void visit_dcmpl(Instruction inst) {
    height -= 3;
  }

  public void visit_dcmpg(Instruction inst) {
    height -= 3;
  }

  public void visit_ifeq(Instruction inst) {
    height -= 1;
  }

  public void visit_ifne(Instruction inst) {
    height -= 1;
  }

  public void visit_iflt(Instruction inst) {
    height -= 1;
  }

  public void visit_ifge(Instruction inst) {
    height -= 1;
  }

  public void visit_ifgt(Instruction inst) {
    height -= 1;
  }

  public void visit_ifle(Instruction inst) {
    height -= 1;
  }

  public void visit_if_icmpeq(Instruction inst) {
    height -= 2;
  }

  public void visit_if_icmpne(Instruction inst) {
    height -= 2;
  }

  public void visit_if_icmplt(Instruction inst) {
    height -= 2;
  }

  public void visit_if_icmpge(Instruction inst) {
    height -= 2;
  }

  public void visit_if_icmpgt(Instruction inst) {
    height -= 2;
  }

  public void visit_if_icmple(Instruction inst) {
    height -= 2;
  }

  public void visit_if_acmpeq(Instruction inst) {
    height -= 2;
  }

  public void visit_if_acmpne(Instruction inst) {
    height -= 2;
  }

  public void visit_jsr(Instruction inst) {
    // Even though the jsr instruction itself pushes the return
    // address onto the stack, we don't want to account for that
    // here.  It is already taken care of in the handle method.  This
    // way the label following the jsr (the return site) will have the
    // stack height it had before the call.  Once again, we do not
    // account for the possibility of the jsr modifying the height of
    // the stack. 
    height += 0;
  }

  public void visit_switch(Instruction inst) {
    height -= 1;
  }

  public void visit_ireturn(Instruction inst) {
    height = 0;
  }

  public void visit_lreturn(Instruction inst) {
    height = 0;
  }

  public void visit_freturn(Instruction inst) {
    height = 0;
  }

  public void visit_dreturn(Instruction inst) {
    height = 0;
  }

  public void visit_areturn(Instruction inst) {
    height = 0;
  }

  public void visit_return(Instruction inst) {
    height = 0;
  }

  public void visit_getstatic(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height += type.stackHeight();
  }

  public void visit_putstatic(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height -= type.stackHeight();
  }

  public void visit_putstatic_nowb(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height -= type.stackHeight();
  }

  public void visit_getfield(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height += type.stackHeight() - 1;
  }

  public void visit_putfield(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height -= type.stackHeight() + 1;
  }

  public void visit_putfield_nowb(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    height -= type.stackHeight() + 1;
  }

  public void visit_invokevirtual(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    height += type.returnType().stackHeight() - type.stackHeight() - 1;
  }

  public void visit_invokespecial(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    height += type.returnType().stackHeight() - type.stackHeight() - 1;
  }

  public void visit_invokestatic(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    height += type.returnType().stackHeight() - type.stackHeight();
  }

  public void visit_invokeinterface(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    height += type.returnType().stackHeight() - type.stackHeight() - 1;

  }

  public void visit_new(Instruction inst) {
    height += 1;
  }

  public void visit_monitorenter(Instruction inst) {
    height -= 1;
  }

  public void visit_monitorexit(Instruction inst) {
    height -= 1;
  }

  public void visit_multianewarray(Instruction inst) {
    MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
    int dim = operand.dimensions();

    height += 1 - dim;
  }

  public void visit_ifnull(Instruction inst) {
    height -= 1;
  }

  public void visit_ifnonnull(Instruction inst) {
    height -= 1;
  }

  public void visit_aswizzle(Instruction inst) {
    height -= 2;
  }

  public void visit_aswrange(Instruction inst) {
    height -= 3;
  }

  /**
   * Returns a clone of this <tt>StackHeightCounter</tt>
   */
  public Object clone() {
    StackHeightCounter clone = new StackHeightCounter(this.method);
    clone.height = this.height;
    clone.labelHeights = (HashMap) this.labelHeights.clone();
    return(clone);
  }
}
