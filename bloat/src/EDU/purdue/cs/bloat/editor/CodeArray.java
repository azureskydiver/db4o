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

package EDU.purdue.cs.bloat.editor;

import java.util.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * CodeArray converts an array of Instructions into an array of bytes
 * suitable for saving to a <tt>MethodInfo</tt> with <tt>setCode</tt>.
 *
 * <p>
 *
 * The byte array is returned by calling the <tt>array</tt> method.
 *
 * <p>
 *
 * This code assumes no branch will be longer than 65536 bytes.
 *
 * @see Instruction
 * @see MethodInfo
 * @see MethodInfo#setCode
 *
 * @author Nate Nystrom
 *         (<a href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class CodeArray implements InstructionVisitor, Opcode {
  public static boolean DEBUG = Boolean.getBoolean("CodeArray.DEBUG");

  private ByteCell codeTail;       // Linked list of ByteCells representing code
  private int codeLength;          // Number of bytes in method
  private Map branches;        
  private Map longBranches;
  private Map branchInsts;
  private Map labels;              // Labels mapped to their offsets
  private int lastInst;            // Offset of last (most recent) instrucion
  private int maxStack;            // Max stack height
  private int stackHeight;         // Current stack height
  private int maxLocals;           // Max number of local variables
  private ConstantPool constants;
  private MethodEditor method;
  private boolean longBranch;      // Do we use long (wide) jumps?
  private List insts;

  /**
   * Create the byte array for a method.
   *
   * @param method
   *        The method being edited.
   * @param constants
   *        The constant pool of the class.
   * @param insts
   *        A List of Instructions and Labels to convert to a byte array.
   * @see MethodEditor
   * @see ConstantPool
   * @see Instruction
   * @see Label
   */
  public CodeArray(MethodEditor method, ConstantPool constants, List insts) {
    this.constants = constants;
    this.method = method;
    this.insts = insts;
    this.maxStack = 0;
    this.maxLocals = 0;

    longBranch = false;
    buildCode();
  }

  /**
   * Examine the method's Labels and Instructions.  Keep track of such
   * things as the height of the stack at each instruction and to
   * where subroutines return.  The ultimate goal is to compute the
   * max stack height of this method.  This computation is complicated
   * by subroutines that may be invoked at a variety of stack heights.
   */
  private void buildCode() {
    codeTail = null;
    codeLength = 0;
    branches = new HashMap();
    longBranches = new HashMap();
    branchInsts = new HashMap();
    labels = new HashMap();

    // We need at least enought locals to store the parameters
    maxLocals = method.type().stackHeight();

    if (! method.isStatic()) {
      // One more for the this pointer
      maxLocals++;
    }

    stackHeight = 0;

    Map labelPos = new HashMap();    // Maps Labels to their code offsets
    int[] heights = new int[insts.size()];   // Stack height at each inst

    // Maps Labels that begin jsrs to their return targets.  Maps ret
    // instructions to the subroutine from which they return.
    Map retTargets = new HashMap();
    Map retInsts = new HashMap();

    // Print the code we're dealing with
     if(CodeArray.DEBUG) {
       System.out.println("Building code for " +
                          method.declaringClass().name() + "." +
                          method.name()); 
       Iterator iter = insts.iterator();
       while(iter.hasNext()) {
         Object o = iter.next();
         System.out.println("  " + o);
       }
     }

    // Build the bytecode array, assuming each basic block begins with
    // stack height 0.  We'll fix up the heights later.
    Iterator iter = insts.iterator();
    int i = 0;                          // Which instruction are we at?
    Label currSub = null;               // Subroutine we're in

    while (iter.hasNext()) {
      Object ce = iter.next();

      if (ce instanceof Label) {
	Label label = (Label) ce;

	// A Label starts a new basic block.  Reset the stack height.

	stackHeight = 0;
	labelPos.put(label, new Integer(i));

	addLabel(label);
	heights[i++] = stackHeight;

	// If this label starts a subroutine (i.e. is the target of
	// jsr instruction), then make not of it.
	if(retTargets.containsKey(label)) {
	  currSub = label;
	}

      } else if (ce instanceof Instruction) {
	Instruction inst = (Instruction) ce;

	// Visit this instruction to compute the current stack height
	inst.visit(this);

	if (inst.isJsr()) {
	  // Make sure that the jsr is not the last instruction in the
	  // method.  If it was, where would we return to?  Make note
	  // of the return target (the Label following the jsr).

	  heights[i++] = stackHeight;

	  Assert.isTrue(iter.hasNext(), inst +
			" found at end of method");

	  Object x = iter.next();

	  Assert.isTrue(x instanceof Label,
			inst + " not followed by label");

	  Label sub = (Label) inst.operand();
	  Label target = (Label) x;

	  // Maintain a mapping between a subroutine (the Label that
	  // begins it) and all return targets.
	  Set targets = (Set) retTargets.get(sub);
	  if(targets == null) {
	    targets = new HashSet();
	    retTargets.put(sub, targets);
	  }
	  targets.add(target);

	  stackHeight = 0;
	  labelPos.put(target, new Integer(i));

	  addLabel(target);
	  heights[i++] = stackHeight;

	} else {
	  heights[i++] = stackHeight;
	}

      } else {
	// Something bad in instruction list
	throw new IllegalArgumentException();
      }
    }

    // Sorry, but we have to make another forward pass over some of
    // the code to determine the subroutine from which a given ret
    // instruction returns.
    Iterator subLabels = retTargets.keySet().iterator();
    while(subLabels.hasNext()) {
      Label subLabel = (Label) subLabels.next();
      int pos = insts.indexOf(subLabel);
      Assert.isTrue(pos != -1, "Label " + subLabel + " not found");
      boolean foundRet = false;
      ListIterator liter = insts.listIterator(pos);
      while(liter.hasNext()) {
	Object o = liter.next();
	if(o instanceof Instruction) {
	  Instruction inst = (Instruction) o;
	  if(inst.isRet()) {
	    retInsts.put(inst, subLabel);
	    foundRet = true;
	    break;
	  }
	}
      }
      Assert.isTrue(foundRet, "No ret for subroutine " + subLabel);
    }

    if(DEBUG) {
      // Print subroutine to return target mapping
      System.out.println("Subroutines and return targets:");
      Iterator subs = retTargets.keySet().iterator();
      while(subs.hasNext()) {
	Label sub = (Label) subs.next();
	System.out.print("  " + sub + ": ");
	Set s = (Set) retTargets.get(sub);
	Assert.isTrue(s != null, "No return targets for " + sub);
	Iterator rets = s.iterator();
	while(rets.hasNext()) {
	  Label ret = (Label) rets.next();
	  System.out.print(ret.toString());
	  if(rets.hasNext()) {
	    System.out.print(", ");
	  }
	}
	System.out.println("");
      }
    }

    // Fix up the stack heights by propagating the heights at each catch
    // and each branch to their targets.  Visit the blocks
    // depth-first.  Remember that the classfile requires the maximum
    // stack height.  I would assume that is why we do all of this
    // stack height calculation stuff.

    Set visited = new HashSet();   // Labels that we've seen
    Stack stack = new Stack();     // Stack of HeightRecords
    Label label;

    // Start with the first Label
    if (insts.size() > 0) {
      Assert.isTrue((insts.get(0) instanceof Label), 
                    "A method must begin with a Label, not " +
                    insts.get(0));
      label = (Label) insts.get(0);
      visited.add(label);
      stack.push(new HeightRecord(label, 0));
    }

    // Also examine each exception handler.  Recall that the exception
    // object is implicitly pushed on the stack.  So, the HeightRecord
    // initially has height 1.
    Iterator e = method.tryCatches().iterator();
    while (e.hasNext()) {
      TryCatch tc = (TryCatch) e.next();
      visited.add(tc.handler());
      stack.push(new HeightRecord(tc.handler(), 1));
    }

    // Examine the HeightRecords on the stack.  Make sure that the
    // stack height has not exceeded 256.   If the height at a given
    // label has changed since we last visited it, then propagate this
    // change to labels following the block begun by the label in
    // question.
    while (! stack.isEmpty()) {
      HeightRecord h = (HeightRecord) stack.pop();

      Assert.isTrue(h.height < 256,
		    "Stack height of " + h.height + " reached. " + h.label
		    + " (" + labelPos.get(h.label) + ")");

      if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	System.out.println(h.label + " has height " + h.height);
      }

      Integer labelIndex = (Integer) labelPos.get(h.label);
      Assert.isTrue(labelIndex != null,
		    "Index of " + h.label + " not found");

      int start = labelIndex.intValue();
      int diff = h.height - heights[start];

      // Propagate the change in height to the next branch.
      // Then push the branch targets.
      if (ClassEditor.DEBUG) {
	/*
	  System.out.println("    " + h.label + ": change " +
	  heights[start] + " to " + h.height);
	*/
      }

      heights[start] = h.height;

      ListIterator blockIter = insts.listIterator(start+1);
      i = start;

      // Examine the instructions following the label
      while (blockIter.hasNext()) {
	Object ce = blockIter.next();

	i++;

	if (ce instanceof Instruction) {
	  Instruction inst = (Instruction) ce;

	  if (inst.isReturn() || inst.isThrow()) {
	    // The method terminates.  The stack is popped empty.
	    heights[i] = 0;

	    if(ClassEditor.DEBUG || CodeArray.DEBUG) {
	      System.out.println("  " + heights[i] + ") " + inst);
	    }

	    // Consider next HeightRecord on stack.
	    break;

	  } else if (inst.isConditionalJump()) {
	    // If the stack height at this Label has changed since we
	    // last saw it or if we have not processed the target of
	    // the jump, add a new HeightRecord for the target Label.

	    heights[i] += diff;

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
		System.out.println("  " + heights[i] + ") " + inst);
	    }

	    label = (Label) inst.operand();

	    if (diff > 0 || ! visited.contains(label)) {
	      visited.add(label);
	      stack.push(new HeightRecord(label, heights[i]));
	    }

	    // Fall through.  That is, process the instruction after
	    // the conditional jump.  Remember that the code is in
	    // trace order so the false block (which is the next block
	    // in a depth first traversal) follows.  The height of the
	    // stack won't change when we fall through.

	  } else if (inst.isGoto() || inst.isJsr()) {
	    // Once again, if we have already visited the target
	    // block, add a HeightRecord to the stack.

	    heights[i] += diff;

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
		System.out.println("  " + heights[i] + ") " + inst);
	    }

	    label = (Label) inst.operand();

	    if (diff > 0 || ! visited.contains(label)) {
	      visited.add(label);
	      stack.push(new HeightRecord(label, heights[i]));
	    }

	    // Deal with the next HeightRecord on the stack.
	    break;

	  } else if (inst.isRet()) {
	    // Process any unvisited return targets (of the current
	    // jsr) or those whose current height is less than the
	    // height at this return instruction.

	    heights[i] += diff;

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	      System.out.println("  " + heights[i] + ") " + inst);
	    }

	    Label subLabel = (Label) retInsts.get(inst);
	    Assert.isTrue(subLabel != null, 
			  "Not inside a subroutine at " + inst);

	    Set targets = (Set) retTargets.get(subLabel);
	    Assert.isTrue(targets != null, "Subroutine " + subLabel + 
			  " has no return targets");

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	      System.out.println("    Returning from: " + subLabel);
	    }

	    Iterator retIter = targets.iterator();

	    while (retIter.hasNext()) {
	      label = (Label) retIter.next();
			    
	      labelIndex = (Integer) labelPos.get(label);
	      Assert.isTrue(labelIndex != null,
			    "Index of " + label + " not found");

	      int idx = labelIndex.intValue();

	      if (heights[idx] < heights[i] ||
		  ! visited.contains(label)) {
		visited.add(label);
		stack.push(new HeightRecord(label, heights[i]));
	      }
	    }

	    break;
			
	  } else if (inst.isSwitch()) {
	    // Visit each unvisited switch target if it increases the
	    // stack height

	    // If the height at this Label has changed since it was
	    // last visited, process each target Label.  Otherwise,
	    // only process unvisited Labels.

	    heights[i] += diff;

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	      System.out.println("  " + heights[i] + ") " + inst);
	    }

	    // A switch.
	    Switch sw = (Switch) inst.operand();

	    label = sw.defaultTarget();

	    if (diff > 0 || ! visited.contains(label)) {
	      visited.add(label);
	      stack.push(new HeightRecord(label, heights[i]));
	    }

	    Label[] targets = sw.targets();

	    for (int j = 0; j < targets.length; j++) {
	      label = targets[j];
	      if (diff > 0 || ! visited.contains(label)) {
		visited.add(label);
		stack.push(new HeightRecord(label, heights[i]));
	      }
	    }

	    break;

	  } else {
	    // No other blocks to visit.  Just adjust the height.

	    heights[i] += diff;

	    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	      System.out.println("  " + heights[i] + ") " + inst);
	    }
	  }

	} else if (ce instanceof Label) {
	  // We've hit the next block.  Update the stack height.
	  // Process this next block if has not been visited or its
	  // current height is different from the previous
	  // instruction.

	  label = (Label) ce;

	  diff = heights[i-1] - heights[i];

	  if (diff > 0 || ! visited.contains(label)) {
	    visited.add(label);
	    heights[i] = heights[i-1];
	  }

	  if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	    System.out.println("  " + heights[i] + ") " + label);
	  }
	}
      }
    }

    // Find the maximum stack height.
    maxStack = 0;

    for (i = 0; i < heights.length; i++) {
      int h = heights[i];

      if (h > maxStack) {
	maxStack = h;
      }
    }
  }

  /**
   * Returns the maximum number of local variables used by this
   * method.
   */
  public int maxLocals() {
    return maxLocals;
  }

  /**
   * Returns the maximum height of the stack at any point in this
   * method.
   */
  public int maxStack() {
    return maxStack;
  }

  /**
   * Returns the index in the byte array of the given label.
   */
  public int labelIndex(Label label) {
    Integer i = (Integer) labels.get(label);

    if (i != null) {
      return i.intValue();
    }

    throw new IllegalArgumentException("Label " + label + " not found");
  }

  /**
   * Returns the byte array after resolving branches.
   */
  public byte[] array() {
    if (branches.size() > 0) {
      if (! longBranch && codeLength >= 0x10000) {
	longBranch = true;
	buildCode();
      }
    }

    byte[] c = new byte[codeLength];
    int i = codeLength;

    for (ByteCell p = codeTail; p != null; p = p.prev) {
      c[--i] = p.value;
    }

    Iterator e;

    e = branches.keySet().iterator();

    while (e.hasNext()) {
      Integer branch = (Integer) e.next();
      int branchIndex = branch.intValue();

      Integer inst = (Integer) branchInsts.get(branch);
      int instIndex = inst.intValue();

      Label label = (Label) branches.get(branch);
      Integer target = (Integer) labels.get(label);

      Assert.isTrue(target != null, "Index of " + label + " not found");

      int diff = target.intValue() - instIndex;

      Assert.isTrue(-diff < 0x10000 && diff < 0x10000,
		    "Branch offset too large: " + diff);

      c[branchIndex] = (byte) ((diff >>> 8) & 0xff);
      c[branchIndex+1] = (byte) (diff & 0xff);
    }

    e = longBranches.keySet().iterator();

    while (e.hasNext()) {
      Integer branch = (Integer) e.next();
      int branchIndex = branch.intValue();

      Integer inst = (Integer) branchInsts.get(branch);
      int instIndex = inst.intValue();

      Label label = (Label) longBranches.get(branch);
      Integer target = (Integer) labels.get(label);

      int diff = target.intValue() - instIndex;

      c[branchIndex]   = (byte) ((diff >>> 24) & 0xff);
      c[branchIndex+1] = (byte) ((diff >>> 16) & 0xff);
      c[branchIndex+2] = (byte) ((diff >>> 8) & 0xff);
      c[branchIndex+3] = (byte) (diff & 0xff);
    }

    return c;
  }

  /**
   * Makes note of a label.
   */
  public void addLabel(Label label) {
    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
      System.out.println("    " + codeLength + ": " + "label " + label);
    }

    labels.put(label, new Integer(codeLength));
  }

  /**
   * Adds a 4-byte branch to a given label.  The branch is from the
   * index of the last opcode added.  
   */
  public void addLongBranch(Label label) {
    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
      System.out.println("    " + codeLength + ": " +
			 "long branch to " + label);
    }

    branchInsts.put(new Integer(codeLength), new Integer(lastInst));
    longBranches.put(new Integer(codeLength), label);
    addByte(0);
    addByte(0);
    addByte(0);
    addByte(0);
  }

  /**
   * Adds a 2-byte branch to a given label.  The branch is from the
   * index of the last opcode added.
   */
  public void addBranch(Label label) {
    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
      System.out.println("    " + codeLength + ": " + "branch to " +
			 label);
    }

    branchInsts.put(new Integer(codeLength), new Integer(lastInst));
    branches.put(new Integer(codeLength), label);
    addByte(0);
    addByte(0);
  }

  /**
   * Add an opcode to the byte array, adjusting for 4-byte alignment for
   * switch instructions and saving the index for calculating branches.
   *
   * @param opcode
   *        The opcode.
   * @see Opcode
   */
  public void addOpcode(int opcode) {
    if (ClassEditor.DEBUG || CodeArray.DEBUG) {
      System.out.println("    " + codeLength + ": " + "opcode " +
			 opcNames[opcode]);
    }

    lastInst = codeLength;

    addByte(opcode);

    if (opcode == opc_tableswitch || opcode == opc_lookupswitch) {
      // Switch instructions are followed by padding so that table
      // starts on a 4-byte boundary.
      while (codeLength % 4 != 0) {
	addByte(0);
      }
    }
  }

  /**
   * Adds a single byte to the array.
   */
  public void addByte(int i) {
    if (ClassEditor.DEBUG) {
      System.out.println("    " + codeLength + ": " + "byte " + i);
    }

    // The bytecode array is represented as a linked list of
    // ByteCells.  This method creates a new ByteCell and appends it
    // to the linked list.

    ByteCell p = new ByteCell();
    p.value = (byte) (i & 0xff);
    p.prev = codeTail;
    codeTail = p;
    codeLength++;
  }

  /**
   * Adds a 2-byte short to the array, high byte first.
   */
  public void addShort(int i) {
    if (ClassEditor.DEBUG) {
      System.out.println("    " + codeLength + ": " + "short " + i);
    }

    addByte(i >>> 8);
    addByte(i);
  }

  /**
   * Adds a 4-byte int to the array, high byte first.
   */
  public void addInt(int i) {
    if (ClassEditor.DEBUG) {
      System.out.println("    " + codeLength + ": " + "int " + i);
    }

    addByte(i >>> 24);
    addByte(i >>> 16);
    addByte(i >>>  8);
    addByte(i);
  }

  public void visit_nop(Instruction inst) {
    // If it must have been put there for a reason.
    addOpcode(opc_nop);
    stackHeight += 0;
  }

  /*
   * Does pretty much what you'd expect.  Examines the instruction's
   * operand to determine if one of the special constant opcodes
   * (e.g. iconst_1) can be used.  Adds the most appropriate instruction.
   */
  public void visit_ldc(Instruction inst) {
    Object operand = inst.operand();

    if (operand == null) {
      addOpcode(opc_aconst_null);
      stackHeight++;

    } else if (operand instanceof Integer) {
      int v = ((Integer) operand).intValue();

      switch (v) {
      case -1:
	addOpcode(opc_iconst_m1);
	break;
      case 0:
	addOpcode(opc_iconst_0);
	break;
      case 1:
	addOpcode(opc_iconst_1);
	break;
      case 2:
	addOpcode(opc_iconst_2);
	break;
      case 3:
	addOpcode(opc_iconst_3);
	break;
      case 4:
	addOpcode(opc_iconst_4);
	break;
      case 5:
	addOpcode(opc_iconst_5);
	break;
      default: {
	  if ((byte) v == v) {
	    addOpcode(opc_bipush);
	    addByte(v);
	  }
	  else if ((short) v == v) {
	    addOpcode(opc_sipush);
	    addShort(v);
	  }
	  else {
	    int index = constants.addConstant(Constant.INTEGER,
					      operand);
	    if (index < 256) {
	      addOpcode(opc_ldc);
	      addByte(index);
	    }
	    else {
	      addOpcode(opc_ldc_w);
	      addShort(index);
	    }
	  }
	  break;
	}
      }

      stackHeight++;

    } else if (operand instanceof Float) {
      float v = ((Float) operand).floatValue();

      if (v == 0.0F) {
	addOpcode(opc_fconst_0);
      }
      else if (v == 1.0F) {
	addOpcode(opc_fconst_1);
      }
      else if (v == 2.0F) {
	addOpcode(opc_fconst_2);
      }
      else {
	int index = constants.addConstant(Constant.FLOAT, operand);
	if (index < 256) {
	  addOpcode(opc_ldc);
	  addByte(index);
	}
	else {
	  addOpcode(opc_ldc_w);
	  addShort(index);
	}
      }

      stackHeight++;

    } else if (operand instanceof Long) {
      long v = ((Long) operand).longValue();

      if (v == 0) {
	addOpcode(opc_lconst_0);
      }
      else if (v == 1) {
	addOpcode(opc_lconst_1);
      }
      else {
	int index = constants.addConstant(Constant.LONG, operand);
	addOpcode(opc_ldc2_w);
	addShort(index);
      }

      stackHeight += 2;

    } else if (operand instanceof Double) {
      double v = ((Double) operand).doubleValue();

      if (v == 0.0) {
	addOpcode(opc_dconst_0);
      }
      else if (v == 1.0) {
	addOpcode(opc_dconst_1);
      }
      else {
	int index = constants.addConstant(Constant.DOUBLE, operand);
	addOpcode(opc_ldc2_w);
	addShort(index);
      }

      stackHeight += 2;

    } else if (operand instanceof String) {
      int index = constants.addConstant(Constant.STRING, operand);

      if (index < 256) {
	addOpcode(opc_ldc);
	addByte(index);
      }
      else {
	addOpcode(opc_ldc_w);
	addShort(index);
      }

      stackHeight++;
    }
    else {
      throw new RuntimeException();
    }
  }

  /*
   * Tries to use the shorter iload_x instructions.
   */
  public void visit_iload(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_iload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_iload);
	addShort(index);
      }
      stackHeight++;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_iload_0);
      break;
    case 1:
      addOpcode(opc_iload_1);
      break;
    case 2:
      addOpcode(opc_iload_2);
      break;
    case 3:
      addOpcode(opc_iload_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_iload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_iload);
	addShort(index);
      }
      break;
    }

    stackHeight++;
  }

  public void visit_lload(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+2 > maxLocals) {
      maxLocals = index+2;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_lload);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_lload);
	addShort(index);
      }
      stackHeight++;
      return;
    }
	
    switch (index) {
    case 0:
      addOpcode(opc_lload_0);
      break;
    case 1:
      addOpcode(opc_lload_1);
      break;
    case 2:
      addOpcode(opc_lload_2);
      break;
    case 3:
      addOpcode(opc_lload_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_lload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_lload);
	addShort(index);
      }
      break;
    }

    stackHeight += 2;
  }

  public void visit_fload(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_fload);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_fload);
	addShort(index);
      }

      stackHeight++;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_fload_0);
      break;
    case 1:
      addOpcode(opc_fload_1);
      break;
    case 2:
      addOpcode(opc_fload_2);
      break;
    case 3:
      addOpcode(opc_fload_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_fload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_fload);
	addShort(index);
      }
      break;
    }

    stackHeight++;
  }

  public void visit_dload(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+2 > maxLocals) {
      maxLocals = index+2;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_dload);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_dload);
	addShort(index);
      }
      stackHeight += 2;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_dload_0);
      break;
    case 1:
      addOpcode(opc_dload_1);
      break;
    case 2:
      addOpcode(opc_dload_2);
      break;
    case 3:
      addOpcode(opc_dload_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_dload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_dload);
	addShort(index);
      }
      break;
    }

    stackHeight += 2;
  }

  public void visit_aload(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_aload);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_aload);
	addShort(index);
      }
      stackHeight++;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_aload_0);
      break;
    case 1:
      addOpcode(opc_aload_1);
      break;
    case 2:
      addOpcode(opc_aload_2);
      break;
    case 3:
      addOpcode(opc_aload_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_aload);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_aload);
	addShort(index);
      }
      break;
    }

    stackHeight++;
  }

  /**
   * Pops an item off the stack.
   */
  public void visit_iaload(Instruction inst) {
    addOpcode(opc_iaload);
    stackHeight--;
  }

  public void visit_laload(Instruction inst) {
    addOpcode(opc_laload);
    stackHeight += 0;
  }

  public void visit_faload(Instruction inst) {
    addOpcode(opc_faload);
    stackHeight--;
  }

  public void visit_daload(Instruction inst) {
    addOpcode(opc_daload);
    stackHeight += 0;
  }

  public void visit_aaload(Instruction inst) {
    addOpcode(opc_aaload);
    stackHeight--;
  }

  public void visit_baload(Instruction inst) {
    addOpcode(opc_baload);
    stackHeight--;
  }

  public void visit_caload(Instruction inst) {
    addOpcode(opc_caload);
    stackHeight--;
  }

  public void visit_saload(Instruction inst) {
    addOpcode(opc_saload);
    stackHeight--;
  }

  /*
   * Try to take advantage of smaller opcodes (e.g. istore_1).
   */
  public void visit_istore(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_istore);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_istore);
	addShort(index);
      }
      stackHeight--;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_istore_0);
      break;
    case 1:
      addOpcode(opc_istore_1);
      break;
    case 2:
      addOpcode(opc_istore_2);
      break;
    case 3:
      addOpcode(opc_istore_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_istore);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_istore);
	addShort(index);
      }
      break;
    }

    stackHeight--;
  }

  public void visit_lstore(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+2 > maxLocals) {
      maxLocals = index+2;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_lstore);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_lstore);
	addShort(index);
      }
      stackHeight -= 2;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_lstore_0);
      break;
    case 1:
      addOpcode(opc_lstore_1);
      break;
    case 2:
      addOpcode(opc_lstore_2);
      break;
    case 3:
      addOpcode(opc_lstore_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_lstore);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_lstore);
	addShort(index);
      }
      break;
    }

    stackHeight -= 2;
  }

  public void visit_fstore(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_fstore);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_fstore);
	addShort(index);
      }
      stackHeight--;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_fstore_0);
      break;
    case 1:
      addOpcode(opc_fstore_1);
      break;
    case 2:
      addOpcode(opc_fstore_2);
      break;
    case 3:
      addOpcode(opc_fstore_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_fstore);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_fstore);
	addShort(index);
      }
      break;
    }

    stackHeight--;
  }

  public void visit_dstore(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+2 > maxLocals) {
      maxLocals = index+2;
    }

    if(inst.useSlow()) {
      if(index < 256) {
	addOpcode(opc_dstore);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_dstore);
	addShort(index);
      }
      stackHeight -= 2;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_dstore_0);
      break;
    case 1:
      addOpcode(opc_dstore_1);
      break;
    case 2:
      addOpcode(opc_dstore_2);
      break;
    case 3:
      addOpcode(opc_dstore_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_dstore);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_dstore);
	addShort(index);
      }
      break;
    }

    stackHeight -= 2;
  }

  public void visit_astore(Instruction inst) {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if(inst.useSlow()) {
      if (index < 256) {
	addOpcode(opc_astore);
	addByte(index);
      } else {
	addOpcode(opc_wide);
	addByte(opc_astore);
	addShort(index);
      }
      stackHeight--;
      return;
    }

    switch (index) {
    case 0:
      addOpcode(opc_astore_0);
      break;
    case 1:
      addOpcode(opc_astore_1);
      break;
    case 2:
      addOpcode(opc_astore_2);
      break;
    case 3:
      addOpcode(opc_astore_3);
      break;
    default:
      if (index < 256) {
	addOpcode(opc_astore);
	addByte(index);
      }
      else {
	addOpcode(opc_wide);
	addByte(opc_astore);
	addShort(index);
      }
      break;
    }

    stackHeight--;
  }

  /*
   * Store into an array.  Pop 3+ items off the stack.
   */
  public void visit_iastore(Instruction inst) {
    addOpcode(opc_iastore);
    stackHeight -= 3;
  }

  public void visit_lastore(Instruction inst) {
    addOpcode(opc_lastore);
    stackHeight -= 4;
  }

  public void visit_fastore(Instruction inst) {
    addOpcode(opc_fastore);
    stackHeight -= 3;
  }

  public void visit_dastore(Instruction inst) {
    addOpcode(opc_dastore);
    stackHeight -= 4;
  }

  public void visit_aastore(Instruction inst) {
    addOpcode(opc_aastore);
    stackHeight -= 3;
  }

  public void visit_bastore(Instruction inst) {
    addOpcode(opc_bastore);
    stackHeight -= 3;
  }

  public void visit_castore(Instruction inst) {
    addOpcode(opc_castore);
    stackHeight -= 3;
  }

  public void visit_sastore(Instruction inst) {
    addOpcode(opc_sastore);
    stackHeight -= 3;
  }

  public void visit_pop(Instruction inst) {
    addOpcode(opc_pop);
    stackHeight--;
  }

  public void visit_pop2(Instruction inst) {
    addOpcode(opc_pop2);
    stackHeight -= 2;
  }

  public void visit_dup(Instruction inst) {
    addOpcode(opc_dup);
    stackHeight++;
  }

  public void visit_dup_x1(Instruction inst) {
    addOpcode(opc_dup_x1);
    stackHeight++;
  }

  public void visit_dup_x2(Instruction inst) {
    addOpcode(opc_dup_x2);
    stackHeight++;
  }

  public void visit_dup2(Instruction inst) {
    addOpcode(opc_dup2);
    stackHeight += 2;
  }

  public void visit_dup2_x1(Instruction inst) {
    addOpcode(opc_dup2_x1);
    stackHeight += 2;
  }

  public void visit_dup2_x2(Instruction inst) {
    addOpcode(opc_dup2_x2);
    stackHeight += 2;
  }

  public void visit_swap(Instruction inst) {
    addOpcode(opc_swap);
  }

  public void visit_iadd(Instruction inst) {
    addOpcode(opc_iadd);
    stackHeight--;
  }

  public void visit_ladd(Instruction inst) {
    addOpcode(opc_ladd);
    stackHeight -= 2;
  }

  public void visit_fadd(Instruction inst) {
    addOpcode(opc_fadd);
    stackHeight--;
  }

  public void visit_dadd(Instruction inst) {
    addOpcode(opc_dadd);
    stackHeight -= 2;
  }

  public void visit_isub(Instruction inst) {
    addOpcode(opc_isub);
    stackHeight--;
  }

  public void visit_lsub(Instruction inst) {
    addOpcode(opc_lsub);
    stackHeight -= 2;
  }

  public void visit_fsub(Instruction inst) {
    addOpcode(opc_fsub);
    stackHeight--;
  }

  public void visit_dsub(Instruction inst) {
    addOpcode(opc_dsub);
    stackHeight -= 2;
  }

  public void visit_imul(Instruction inst) {
    addOpcode(opc_imul);
    stackHeight--;
  }

  public void visit_lmul(Instruction inst) {
    addOpcode(opc_lmul);
    stackHeight -= 2;
  }

  public void visit_fmul(Instruction inst) {
    addOpcode(opc_fmul);
    stackHeight--;
  }

  public void visit_dmul(Instruction inst) {
    addOpcode(opc_dmul);
    stackHeight -= 2;
  }

  public void visit_idiv(Instruction inst) {
    addOpcode(opc_idiv);
    stackHeight--;
  }

  public void visit_ldiv(Instruction inst)
  {
    addOpcode(opc_ldiv);
    stackHeight -= 2;
  }

  public void visit_fdiv(Instruction inst)
  {
    addOpcode(opc_fdiv);
    stackHeight--;
  }

  public void visit_ddiv(Instruction inst)
  {
    addOpcode(opc_ddiv);
    stackHeight -= 2;
  }

  public void visit_irem(Instruction inst)
  {
    addOpcode(opc_irem);
    stackHeight--;
  }

  public void visit_lrem(Instruction inst)
  {
    addOpcode(opc_lrem);
    stackHeight -= 2;
  }

  public void visit_frem(Instruction inst)
  {
    addOpcode(opc_frem);
    stackHeight--;
  }

  public void visit_drem(Instruction inst)
  {
    addOpcode(opc_drem);
    stackHeight -= 2;
  }

  public void visit_ineg(Instruction inst)
  {
    addOpcode(opc_ineg);
    stackHeight += 0;
  }

  public void visit_lneg(Instruction inst)
  {
    addOpcode(opc_lneg);
    stackHeight += 0;
  }

  public void visit_fneg(Instruction inst)
  {
    addOpcode(opc_fneg);
    stackHeight += 0;
  }

  public void visit_dneg(Instruction inst)
  {
    addOpcode(opc_dneg);
    stackHeight += 0;
  }

  public void visit_ishl(Instruction inst)
  {
    addOpcode(opc_ishl);
    stackHeight--;
  }

  public void visit_lshl(Instruction inst)
  {
    addOpcode(opc_lshl);
    stackHeight--;
  }

  public void visit_ishr(Instruction inst)
  {
    addOpcode(opc_ishr);
    stackHeight--;
  }

  public void visit_lshr(Instruction inst)
  {
    addOpcode(opc_lshr);
    stackHeight--;
  }

  public void visit_iushr(Instruction inst)
  {
    addOpcode(opc_iushr);
    stackHeight--;
  }

  public void visit_lushr(Instruction inst)
  {
    addOpcode(opc_lushr);
    stackHeight--;
  }

  public void visit_iand(Instruction inst)
  {
    addOpcode(opc_iand);
    stackHeight--;
  }

  public void visit_land(Instruction inst)
  {
    addOpcode(opc_land);
    stackHeight -= 2;
  }

  public void visit_ior(Instruction inst)
  {
    addOpcode(opc_ior);
    stackHeight--;
  }

  public void visit_lor(Instruction inst)
  {
    addOpcode(opc_lor);
    stackHeight -= 2;
  }

  public void visit_ixor(Instruction inst)
  {
    addOpcode(opc_ixor);
    stackHeight--;
  }

  public void visit_lxor(Instruction inst)
  {
    addOpcode(opc_lxor);
    stackHeight -= 2;
  }

  public void visit_iinc(Instruction inst)
  {
    IncOperand operand = (IncOperand) inst.operand();

    int index = operand.var().index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    int incr = operand.incr();

    if (index < 256 && (byte) incr == incr) {
      addOpcode(opc_iinc);
      addByte(index);
      addByte(incr);
    }
    else {
      addOpcode(opc_wide);
      addByte(opc_iinc);
      addShort(index);
      addShort(incr);
    }

    stackHeight += 0;
  }

  public void visit_i2l(Instruction inst)
  {
    addOpcode(opc_i2l);
    stackHeight++;
  }

  public void visit_i2f(Instruction inst)
  {
    addOpcode(opc_i2f);
    stackHeight += 0;
  }

  public void visit_i2d(Instruction inst)
  {
    addOpcode(opc_i2d);
    stackHeight++;
  }

  public void visit_l2i(Instruction inst)
  {
    addOpcode(opc_l2i);
    stackHeight--;
  }

  public void visit_l2f(Instruction inst)
  {
    addOpcode(opc_l2f);
    stackHeight--;
  }

  public void visit_l2d(Instruction inst)
  {
    addOpcode(opc_l2d);
    stackHeight += 0;
  }

  public void visit_f2i(Instruction inst)
  {
    addOpcode(opc_f2i);
    stackHeight += 0;
  }

  public void visit_f2l(Instruction inst)
  {
    addOpcode(opc_f2l);
    stackHeight++;
  }

  public void visit_f2d(Instruction inst)
  {
    addOpcode(opc_f2d);
    stackHeight++;
  }

  public void visit_d2i(Instruction inst)
  {
    addOpcode(opc_d2i);
    stackHeight--;
  }

  public void visit_d2l(Instruction inst)
  {
    addOpcode(opc_d2l);
    stackHeight += 0;
  }

  public void visit_d2f(Instruction inst)
  {
    addOpcode(opc_d2f);
    stackHeight--;
  }

  public void visit_i2b(Instruction inst)
  {
    addOpcode(opc_i2b);
    stackHeight += 0;
  }

  public void visit_i2c(Instruction inst)
  {
    addOpcode(opc_i2c);
    stackHeight += 0;
  }

  public void visit_i2s(Instruction inst)
  {
    addOpcode(opc_i2s);
    stackHeight += 0;
  }

  public void visit_lcmp(Instruction inst)
  {
    addOpcode(opc_lcmp);
    stackHeight -= 3;
  }

  public void visit_fcmpl(Instruction inst)
  {
    addOpcode(opc_fcmpl);
    stackHeight--;
  }

  public void visit_fcmpg(Instruction inst)
  {
    addOpcode(opc_fcmpg);
    stackHeight--;
  }

  public void visit_dcmpl(Instruction inst)
  {
    addOpcode(opc_dcmpl);
    stackHeight -= 3;
  }

  public void visit_dcmpg(Instruction inst)
  {
    addOpcode(opc_dcmpg);
    stackHeight -= 3;
  }

  /*
   * Handle long branches.
   */
  public void visit_ifeq(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifne);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifeq);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_ifne(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifeq);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifne);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_iflt(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifge);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_iflt);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_ifge(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_iflt);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifge);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_ifgt(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifle);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifgt);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_ifle(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifgt);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifle);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_if_icmpeq(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmpne);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmpeq);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_icmpne(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmpeq);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmpne);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_icmplt(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmpge);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmplt);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_icmpge(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmplt);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmpge);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_icmpgt(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmple);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmpgt);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_icmple(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_icmpgt);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_icmple);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_acmpeq(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_acmpne);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_acmpeq);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_if_acmpne(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_if_acmpeq);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_if_acmpne);
      addBranch((Label) inst.operand());
    }

    stackHeight -= 2;
  }

  public void visit_goto(Instruction inst)
  {
    if (longBranch) {
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
    }
    else {
      addOpcode(opc_goto);
      addBranch((Label) inst.operand());
    }

    stackHeight += 0;
  }

  public void visit_jsr(Instruction inst)
  {
    if (longBranch) {
      addOpcode(opc_jsr_w);
      addLongBranch((Label) inst.operand());
    }
    else {
      addOpcode(opc_jsr);
      addBranch((Label) inst.operand());
    }

    stackHeight++;
  }

  public void visit_ret(Instruction inst)
  {
    int index = ((LocalVariable) inst.operand()).index();

    if (index+1 > maxLocals) {
      maxLocals = index+1;
    }

    if (index < 256) {
      addOpcode(opc_ret);
      addByte(index);
    }
    else {
      addOpcode(opc_wide);
      addByte(opc_ret);
      addShort(index);
    }

    stackHeight += 0;
  }

  public void visit_switch(Instruction inst)
  {
    Switch sw = (Switch) inst.operand();

    int[] values = sw.values();
    Label[] targets = sw.targets();

    if (values.length == 0) {
      if (longBranch) {
        addOpcode(opc_pop);  // Pop switch "index" off stack
	addOpcode(opc_goto_w);
	addLongBranch(sw.defaultTarget());
      }
      else {
        addOpcode(opc_pop);  // Pop switch "index" off stack
	addOpcode(opc_goto);
	addBranch(sw.defaultTarget());
      }
    }
    else if (sw.hasContiguousValues()) {
      addOpcode(opc_tableswitch);
      addLongBranch(sw.defaultTarget());

      addInt(values[0]);
      addInt(values[values.length-1]);

      for (int i = 0; i < targets.length; i++) {
	addLongBranch(targets[i]);
      }
    }
    else {
      addOpcode(opc_lookupswitch);
      addLongBranch(sw.defaultTarget());

      addInt(values.length);

      for (int i = 0; i < targets.length; i++) {
	addInt(values[i]);
	addLongBranch(targets[i]);
      }
    }

    stackHeight--;
  }

  public void visit_ireturn(Instruction inst)
  {
    addOpcode(opc_ireturn);
    stackHeight = 0;
  }

  public void visit_lreturn(Instruction inst)
  {
    addOpcode(opc_lreturn);
    stackHeight = 0;
  }

  public void visit_freturn(Instruction inst)
  {
    addOpcode(opc_freturn);
    stackHeight = 0;
  }

  public void visit_dreturn(Instruction inst)
  {
    addOpcode(opc_dreturn);
    stackHeight = 0;
  }

  public void visit_areturn(Instruction inst)
  {
    addOpcode(opc_areturn);
    stackHeight = 0;
  }

  public void visit_return(Instruction inst)
  {
    addOpcode(opc_return);
    stackHeight = 0;
  }

  public void visit_getstatic(Instruction inst)
  {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_getstatic);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight += type.stackHeight();
  }

  public void visit_putstatic(Instruction inst)
  {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_putstatic);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight();
  }

  public void visit_putstatic_nowb(Instruction inst) {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_putstatic_nowb);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight();
  }

  public void visit_getfield(Instruction inst)
  {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_getfield);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight += type.stackHeight() - 1;
  }

  public void visit_putfield(Instruction inst) {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_putfield);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight() + 1;
  }

  public void visit_putfield_nowb(Instruction inst) {
    int index = constants.addConstant(Constant.FIELD_REF, inst.operand());
    addOpcode(opc_putfield_nowb);
    addShort(index);

    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight() + 1;
  }

  public void visit_invokevirtual(Instruction inst)
  {
    int index = constants.addConstant(Constant.METHOD_REF, inst.operand());
    addOpcode(opc_invokevirtual);
    addShort(index);

    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
  }

  public void visit_invokespecial(Instruction inst)
  {
    int index = constants.addConstant(Constant.METHOD_REF, inst.operand());
    addOpcode(opc_invokespecial);
    addShort(index);

    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
  }

  public void visit_invokestatic(Instruction inst)
  {
    int index = constants.addConstant(Constant.METHOD_REF, inst.operand());
    addOpcode(opc_invokestatic);
    addShort(index);

    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    Assert.isTrue(type.isMethod(), 
                  "Trying to invoke a type that is not a method: " + method);

    stackHeight += type.returnType().stackHeight() - type.stackHeight();
  }

  public void visit_invokeinterface(Instruction inst)
  {
    int index = constants.addConstant(
				      Constant.INTERFACE_METHOD_REF, inst.operand());
    MemberRef method = (MemberRef) constants.constantAt(index);
    Type type = method.nameAndType().type();

    addOpcode(opc_invokeinterface);
    addShort(index);
    addByte(type.stackHeight()+1);
    addByte(0);

    stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
  }

  public void visit_new(Instruction inst)
  {
    int index = constants.addConstant(Constant.CLASS, inst.operand());
    addOpcode(opc_new);
    addShort(index);

    stackHeight++;
  }

  public void visit_newarray(Instruction inst)
  {
    Type type = (Type) inst.operand();

    if (type.isReference()) {
      int index = constants.addConstant(Constant.CLASS, type);
      addOpcode(opc_anewarray);
      addShort(index);
    }
    else {
      addOpcode(opc_newarray);
      addByte(type.typeCode());
    }

    stackHeight += 0;
  }

  public void visit_arraylength(Instruction inst)
  {
    addOpcode(opc_arraylength);
    stackHeight += 0;
  }

  public void visit_athrow(Instruction inst)
  {
    addOpcode(opc_athrow);
    stackHeight = 0;
  }

  public void visit_checkcast(Instruction inst)
  {
    int index = constants.addConstant(Constant.CLASS, inst.operand());
    addOpcode(opc_checkcast);
    addShort(index);
    stackHeight += 0;
  }

  public void visit_instanceof(Instruction inst)
  {
    int index = constants.addConstant(Constant.CLASS, inst.operand());
    addOpcode(opc_instanceof);
    addShort(index);
    stackHeight += 0;
  }

  public void visit_monitorenter(Instruction inst)
  {
    addOpcode(opc_monitorenter);
    stackHeight--;
  }

  public void visit_monitorexit(Instruction inst)
  {
    addOpcode(opc_monitorexit);
    stackHeight--;
  }

  public void visit_multianewarray(Instruction inst)
  {
    MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
    Type type = operand.type();
    int dim = operand.dimensions();
    int index = constants.addConstant(Constant.CLASS, type);
    addOpcode(opc_multianewarray);
    addShort(index);
    addByte(dim);

    stackHeight += 1 - dim;
  }

  public void visit_ifnull(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifnonnull);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifnull);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_ifnonnull(Instruction inst)
  {
    if (longBranch) {
      Label tmp = method.newLabel();
      addOpcode(opc_ifnull);
      addBranch(tmp);
      addOpcode(opc_goto_w);
      addLongBranch((Label) inst.operand());
      addLabel(tmp);
    }
    else {
      addOpcode(opc_ifnonnull);
      addBranch((Label) inst.operand());
    }

    stackHeight--;
  }

  public void visit_rc(Instruction inst)
  {
    Integer operand = (Integer) inst.operand();
    addOpcode(opc_rc);
    addByte(operand.intValue());
    stackHeight += 0;
  }

  public void visit_aswizzle(Instruction inst)
  {	
    addOpcode(opc_aswizzle);
    stackHeight -= 2;
  }

  public void visit_aswrange(Instruction inst)
  {	
    addOpcode(opc_aswrange);
    stackHeight -= 3;
  }

  public void visit_aupdate(Instruction inst)
  {
    Integer operand = (Integer) inst.operand();
    addOpcode(opc_aupdate);
    addByte(operand.intValue());
    stackHeight += 0;
  }

  public void visit_supdate(Instruction inst)
  {
    Integer operand = (Integer) inst.operand();
    addOpcode(opc_supdate);
    addByte(operand.intValue());
    stackHeight += 0;
  }

  /**
   * Represents the height of the stack at given Label.
   */
  class HeightRecord
  {
    Label label;
    int height;

    public HeightRecord(Label label, int height)
    {
      if (ClassEditor.DEBUG || CodeArray.DEBUG) {
	System.out.println("    push " + label + " at " + height);
      }

      this.label = label;
      this.height = height;
    }
  }

  /**
   * Used to represent the byte array.
   */
  class ByteCell
  {
    byte value;
    ByteCell prev;
  }
}
