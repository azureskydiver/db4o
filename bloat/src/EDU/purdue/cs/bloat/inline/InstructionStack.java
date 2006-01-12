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

import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

/**
 * The of <tt>InstructionStack</tt> keeps track of which instructions
 * pushed a certain element of the stack.  It is a stack of sets of
 * instructions.  You can think of it like this: (1, {4,6}, 8) means
 * that instruction 1 pushed the item on the bottom of the stack,
 * instructions 4 and 6 both push the second element on the stack
 * (depending on control flow), and instruction 8 pushed the element
 * on top of the stack.  We use this information at a call site to
 * determine what instruction(s) pushed the receiver object onto the
 * stack.  Special thanks to Jan Vitek for helping me come up with
 * this algorithm.
 *
 * <p>
 *
 * This class is an <tt>InstructionVisitor</tt> that updates the
 * instruction stack representation appropriately.  When there is a
 * merge in control flow, two <tt>InstructionStack</tt>s are merged
 * using the <tt>merge</tt> method.
 *
 * <p>
 *
 * This class is also used to determine whether the object at a given
 * stack depth is "preexistent".  An object preexists if we can
 * guarantee that it was created outside of the method in which it is
 * used.  While it is possible to determine which fields are
 * preexistent (see "Inlining of Virtual Methods" by Detlefs and
 * Ageson in ECOOP99), we only keep track of local variables that
 * preexist.
 *
 * <p>
 *
 * We determine which local variables preexist as follows.  Initially,
 * only the local variables for method parameters preexist.  When a
 * store is encoutered, we determine if the set of instructions on top
 * of the stack consist of loads preexistent variables.  If so, then
 * the variable being stored into is preexistent.  However, objects
 * that are the result of an allocation (constructor call) in the
 * method are preexist.  Thus, we maintain the preexist information as
 * a set.  If the set is null, then the object does not preexist.
 * If the set is empty, then it preexists and came from at least one
 * argument.  If the set is non-empty, then it contains the type(s)
 * of the constructor(s) from which it originated.  Pretty neat, huh?
 */
public class InstructionStack extends InstructionAdapter {

  MethodEditor method;       // Method we're dealing with
  HashMap stacks;            // Maps Labels to their stacks
  LinkedList currStack;      // The current stack
  HashMap preexists;         // Maps Labels to their preexists
  HashMap currPreexists;     // The current preexist (var -> Set)

  private static void db(String s) {
    if(false) {
      System.out.println(s);
    }
  }

  private static void pre(String s) {
    // Debug preexistence
    if(false) {
      System.out.println(s);
    }
  }

  /**
   * Constructor.  Creates an empty <tt>InstructionStack</tt>.
   */
  public InstructionStack(MethodEditor method) {
    this.method = method;
    this.stacks = new HashMap();
    this.preexists = new HashMap();

    // Initially only the parameters to the method prexist
    Type[] paramTypes = method.paramTypes();
    this.currPreexists = new HashMap();
    for(int i = 0; i < paramTypes.length; i++) {
      // We only care about the preexistence of objects (not arrays)
      if(paramTypes[i] != null && paramTypes[i].isObject()) {
	this.currPreexists.put(method.paramAt(i), new HashSet());
      }
    }
  }

  /**
   * Deals with a Label.  
   */
  public void handle(Label label) {
    LinkedList stack = (LinkedList) stacks.get(label);

    if(stack == null) {
      // If this label starts an exception handler, account for the
      // exception being pushed on the stack
      Iterator tryCatches = method.tryCatches().iterator();
      while(tryCatches.hasNext()) {
	TryCatch tc = (TryCatch) tryCatches.next();

	if(tc.handler().equals(label)) {
	  // Kluge to push the exception object on the stack.  I don't
	  // think it matters much.
	  Instruction aload = new Instruction(Opcode.opcx_ldc, "Exception");
	  currStack = new LinkedList();
	  aload.visit(this);
	  stacks.put(label, stack);
	  label.setStartsBlock(true);

	  // We have no idea from where the exception will be thrown,
	  // so we can't make any assumptions about the preexistence
	  // of any variables.
	  currPreexists = new HashMap();
	  return;
	}
      }

      if(currStack == null) {
	// Make a new stack
	currStack = new LinkedList();
	stacks.put(label, currStack);
      
	// I don't think we need to worry about the currPreexists.  It
	// was taken care of in the constructor.

      } else {
	// Otherwise, keep the current stack
	currStack = (LinkedList) currStack.clone();
	stacks.put(label, currStack);
	
	// And the current preexists
	currPreexists = clonePreexists(currPreexists);
	this.preexists.put(label, currPreexists);
      }

    } else {
      // Merge the old stack with the current one
      currStack = merge(currStack, stack);
      stacks.put(label, currStack);

      HashMap oldPreexists = (HashMap) this.preexists.get(label);
      currPreexists = merge(oldPreexists, currPreexists);
      this.preexists.put(label, currPreexists);
    }
  }

  /**
   * Deals with an <tt>Instruction</tt> handles branches, jsrs, and
   * the like.
   */
  public void handle(Instruction inst) {
    // Visit first
    inst.visit(this);

    if(inst.isJump()) {
      Label target = (Label) inst.operand();
      target.setStartsBlock(true);
      
      // Merge the target's stack with any other stacks at that
      // label 
      LinkedList targetStack = (LinkedList) stacks.get(target);
      if(targetStack != null) {
	// Don't change currStack, but do the merge
	stacks.put(target, merge(currStack, targetStack));
	
	HashMap oldPreexists = (HashMap) this.preexists.get(target);
	this.preexists.put(target, merge(currPreexists, oldPreexists));

      } else {
	// Put a new stack at the target
	stacks.put(target, (List) currStack.clone());
	this.preexists.put(target, clonePreexists(currPreexists));
      }

      if(!inst.isConditionalJump()) {
	// The next instruction should be a Label.  But since it is
	// not the next instruction executed, we don't want to merge
	// the contents of the label's stack and the current stack.
	// So, null out the current stack.
	currStack = new LinkedList();
      }

    } else if(inst.isSwitch()) {
      // Propagate the current stack to all targets
      Switch sw = (Switch) inst.operand();
      Label defaultTarget = sw.defaultTarget();
      defaultTarget.setStartsBlock(true);

      LinkedList defaultStack = (LinkedList) stacks.get(defaultTarget);
      if(defaultStack != null) {
	Assert.isTrue(defaultStack.size() == currStack.size(),
		      "Stack height mismatch (" + defaultStack.size()
		      + " != " + currStack.size() + ") at " + inst);
	// Merge stacks for good measure
	stacks.put(defaultTarget, merge(currStack, defaultStack));

	HashMap defaultPreexists = 
	  (HashMap) this.preexists.get(defaultTarget);
	this.preexists.put(defaultTarget, 
			   merge(currPreexists, defaultPreexists));

      } else {
	// Put copy of stack at target
	stacks.put(defaultTarget, (LinkedList) currStack.clone());
	this.preexists.put(defaultTarget, clonePreexists(currPreexists));
      }

      Label[] targets = sw.targets();
      for(int t = 0; t < targets.length; t++) {
	Label target = targets[t];
	target.setStartsBlock(true);
	LinkedList targetStack = (LinkedList) stacks.get(target);
	if(targetStack != null) {
	  Assert.isTrue(targetStack.size() == currStack.size(),
			"Stack height mismatch (" + targetStack.size()
			+ " != " + currStack.size() + ") at " + inst);
	  // Merge stacks for good measure
	  stacks.put(target, merge(currStack, targetStack));

	  HashMap oldPreexists = (HashMap) this.preexists.get(target);
	  this.preexists.put(target, merge(oldPreexists, currPreexists));

	} else {
	  stacks.put(target, (List) currStack.clone());
	  this.preexists.put(target, clonePreexists(currPreexists));
	}
      }

    } else if(inst.isJsr()) {
      // Someday we might have to deal with subroutines that push
      // stuff on the stack.  That complicates things.  I'm going to
      // pretend it doesn't exist.  It was good enough for Nate.

      // In the meantime, we have to propagate the fact that the jsr
      // pushes the return address to the subroutine.  We use an empty
      // stack because it is possible that a subroutine could be
      // called with different stack heights.  Here is another thing
      // that needs to be fixed someday.
      LinkedList subStack = new LinkedList();
      LinkedList oldStack = currStack;

      // Push the return address on stack
      currStack = subStack;
      inst.visit(this);

      currStack = oldStack;  // Should be okay unless sub effects stack

      // Propagate subStack to subroutine
      Label subroutine = (Label) inst.operand();
      subroutine.setStartsBlock(true);
      stacks.put(subroutine, subStack);
      this.preexists.put(subroutine, new HashMap());

    } else if(inst.isReturn() || inst.isThrow()) {
      // We don't what comes next, but we don't want to merge with the
      // current stack.
      currStack = new LinkedList();
    }
  }

  /**
   * Pushes an instruction onto the stack
   */
  private void push(Instruction inst) {
    // Create a new set for the top element of the stack
    Set set = new HashSet();
    set.add(inst);
    currStack.add(set);
  }

  /**
   * Pops the top of the stack.
   */
  private void pop() {
    currStack.removeLast();
  }

  /**
   * Pops a given number of elements off the stack.
   */
  private void pop(int n) {
    for(int i = 0; i < n; i++) {
      currStack.removeLast();
    }
  }

  /**
   * Returns the number of elements in this instruction stack.
   */
  public int height() {
    return(currStack.size());
  }

  /**
   * Returns the set of <tt>Instruction</tt>s at depth <tt>n</tt> of
   * the instruction stack.  Depth 0 is the top of the stack.  The
   * bottom of the stack is at depth stackSize - 1.
   */
  public Set atDepth(int n) {
    Set set = (Set) currStack.get(currStack.size() - 1 - n);
    return(set);
  }

  /**
   * Returns a <tt>Set</tt> representing whether or not the
   * instructions at a given depth push a preexistent object onto the
   * stack.  If the list is <tt>null</tt>, then the push is not
   * preexistent.  If the list is empty, then the push is
   * preexistent.  If the list is non-empty, it contains the
   * <tt>Type</tt>(s) of objects that are known to be on the stack.
   * These types are the results of calls to constructors.
   */
  public HashSet preexistsAtDepth(int n) {
    // How do we determine whether a set of instructions pushes a
    // preexist object?  All of the instructions must be loads of
    // objects from preexistent variable or the result of an
    // object creation.  Note that we can deal with arrays because
    // we'd have to keep track of indices.

    pre("  Preexisting variables: " + db(currPreexists));

    HashSet atDepth = null;
    Iterator insts = this.atDepth(n).iterator();
    Assert.isTrue(insts.hasNext(), "No instructions at depth " + n);
    while(insts.hasNext()) {
      Instruction inst = (Instruction) insts.next();
      pre("    Instruction at " + n + ": " + inst);
      if(inst.opcodeClass() == Opcode.opcx_aload) {
	LocalVariable var = (LocalVariable) inst.operand();
	Set set = (Set) this.currPreexists.get(var);
	if(set != null) {
	  if(set.isEmpty()) {
	    // If the set is empty, then this local variable came from
	    // a method argument.
	    atDepth = new HashSet();

	  } else {
	    // The list contains types that are the result of a
	    // constructor call.  Add them to the preexists list.
	    if(atDepth == null) {
	      atDepth = new HashSet();
	    }
	    atDepth.addAll(set);
	  }
	  continue;
	}

	// Instruction loads a non-preexistent variable, fall through

      } else if(inst.opcodeClass() == Opcode.opcx_new) {
	// We look for the new instruction instead of the constructor
	// call because of the way we represent the stack.

	if(atDepth != null && atDepth.isEmpty()) {
	  // We already know that the object pushed at this depth are
	  // one of the arguments.  We don't the exact type of the
	  // argument, so we can't safely add the type being
	  // instantiated to the preexist list.
	  continue;
	}

	// Figure out the type being created and add it to the
	// preexists list.
	Type type = (Type) inst.operand();
	pre("      Constructing " + Type.truncatedName(type));
	if(atDepth == null) {
	  atDepth = new HashSet();
	}
	atDepth.add(type);
	continue;


	// A non-constructor invokespecial was called, fall through

      } else if(inst.opcodeClass() == Opcode.opcx_dup) {
	Set set = this.preexistsAtDepth(n - 1);
	if(set != null) {
	  if(set.isEmpty()) {
	    // If list is empty, then this preexist must also be empty
	    atDepth = new HashSet();

	  } else {
	    // Add the classes instantiated to the list
	    atDepth.addAll(set);
	  }
	  continue;
	}
      }

      pre("  Doesn't preexist");
      return(null);
    }

    // If we got down here every instruction was preexistent
    pre("  Preexists");
    return(atDepth);
    
  }

  /**
   * Merges two stacks together and returns their union.  Note that
   * stacks of unequal height cannot be merged.
   */
  private static LinkedList merge(LinkedList stack1,
				 LinkedList stack2) {

    Assert.isFalse(stack1 == null && stack2 == null, 
		   "Cannot merge two null stacks");

    LinkedList merge = new LinkedList();

    // If either stack is null or empty, just use the other one
    if(stack1 == null || stack1.size() == 0) {
      merge.addAll(stack2);
      return(merge);
    }

    if(stack2 == null || stack2.size() == 0) {
      merge.addAll(stack1);
      return(merge);
    }

    Assert.isTrue(stack1.size() == stack2.size(), 
		  "Stacks of unequal height cannot be merged (" +
		  stack1.size() + " != " + stack2.size() + ")");

    for(int i = 0; i < stack1.size(); i++) {
      Set mergeSet = new HashSet();
      mergeSet.addAll((Set) stack1.get(i));
      mergeSet.addAll((Set) stack2.get(i));
      merge.add(i, mergeSet);
    }

    return(merge);
  }

  /**
   * Merges two preexists lists.  For a given variable, if either of
   * the two input indices is <tt>null</tt>, then the result is
   * <tt>null</tt>.  If either of the two input indices is empty, then
   * the result is empty.  Otherwise, the result is the union of the
   * two input sets.
   */
  private static HashMap merge(HashMap one, HashMap two) {
    Assert.isFalse(one == null && two == null, 
		   "Can't merge null preexists");

    if(one == null) {
      return(clonePreexists(two));

    } else if(two == null) {
      return(clonePreexists(one));
    }

    // Go through all of the variables in both sets.  If one is not
    // contained in the other, then the set (or null) from the other
    // is used.  If one is mapped to null, then the result is null.
    // If one has an empty set, then the result has an empty set.
    // Otherwise, the two non-empty sets are merge.
    HashMap result = new HashMap();
    Set allVars = new HashSet();
    allVars.addAll(one.keySet());
    allVars.addAll(two.keySet());
    Iterator iter = allVars.iterator();
    while(iter.hasNext()) {
      LocalVariable var = (LocalVariable) iter.next();
      if(!one.containsKey(var)) {
	HashSet set = (HashSet) two.get(var);
	if(set != null) {
	  set = (HashSet) set.clone();
	}

	result.put(var, set);

      } else if(!two.containsKey(var)) {
	HashSet set = (HashSet) one.get(var);
	if(set != null) {
	  set = (HashSet) set.clone();
	}

	result.put(var, set);

      } else {
	HashSet oneSet = (HashSet) one.get(var);
	HashSet twoSet = (HashSet) two.get(var);
	if(oneSet == null || twoSet == null) {
	  result.put(var, null);

	} else if(oneSet.isEmpty() || twoSet.isEmpty()) {
	  result.put(var, new HashSet());

	} else {
	  Set set = new HashSet();
	  set.addAll(oneSet);
	  set.addAll(twoSet);
	  result.put(var, set);
	}
      }
    }

    pre("Merge of " + db(one) + " and " + db(two) + " is " +
       db(result));

    return(result);
  }

  /**
   * Returns a textual representation of a preexists mapping.
   */
  static String db(HashMap preexists) {
    if(preexists == null) {
      return("\n  null?\n");
    }

    StringBuffer sb = new StringBuffer("\n");
    Iterator vars = preexists.keySet().iterator();
    while(vars.hasNext()) {
      LocalVariable var = (LocalVariable) vars.next();
      Set set = (Set) preexists.get(var);
      if(set == null) {
	sb.append("  " + var + ": null\n");

      } else {
	sb.append("  " + var + ": ");
	Iterator iter = set.iterator();
	while(iter.hasNext()) {
	  Type type = (Type) iter.next();
	  sb.append(Type.truncatedName(type));
	  if(iter.hasNext()) {
	    sb.append(", ");
	  }
	}
	sb.append("\n");
      }
    }

    return(sb.toString());
  }

  /**
   * Makes a deep copy of a List containing preexists information.
   */
  private static HashMap clonePreexists(HashMap old) {
    HashMap clone = new HashMap();
    Iterator vars = old.keySet().iterator();
    while(vars.hasNext()) {
      LocalVariable var = (LocalVariable) vars.next();
      HashSet set = (HashSet) old.get(var);
      if(set == null) {
	clone.put(var, null);
      } else {
	clone.put(var, set.clone());
      }
    }
    return(clone);
  }

  public void visit_nop(Instruction inst) {}

  public void visit_ldc(Instruction inst) {
    push(inst);
  } 

  public void visit_iload(Instruction inst) {
    push(inst);
  }

  public void visit_lload(Instruction inst) {
    push(inst);
  }

  public void visit_fload(Instruction inst) {
    push(inst);
  }

  public void visit_dload(Instruction inst) {
    push(inst);
  }

  public void visit_aload(Instruction inst) {
    push(inst);
  }

  public void visit_iaload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_laload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_faload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_daload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_aaload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_baload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_caload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_saload(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_istore(Instruction inst) {
    pop();
  }

  public void visit_lstore(Instruction inst) {
    pop();
  }

  public void visit_fstore(Instruction inst) {
    pop();
  }

  public void visit_dstore(Instruction inst) {
    pop();
  }

  public void visit_astore(Instruction inst) {
    // When we store an object to a local variable, we need to keep
    // track of whether or not the object being stored (and hence the
    // variable into which it is stored) is preexistent.
    LocalVariable var = (LocalVariable) inst.operand();
    HashSet set = preexistsAtDepth(0);
    
    if(set == null) {
      pre("      " + var + " does not preexist");
      this.currPreexists.put(var, null);

    } else if(set.isEmpty()) {
      pre("      " + var + " preexists");
      this.currPreexists.put(var, new HashSet());

    } else {
      // This store superceeds anything else that was already in this
      // local, so don't merge the sets.
      pre("      " + var + " preexists with types");
      this.currPreexists.put(var, set.clone());
    }

    pop();
  }

  public void visit_iastore(Instruction inst) {
    pop(3);
  }

  public void visit_lastore(Instruction inst) {
    pop(3);
  }

  public void visit_fastore(Instruction inst) {
    pop(3);
  }

  public void visit_dastore(Instruction inst) {
    pop(3);
  }

  public void visit_aastore(Instruction inst) {
    pop(3);
  }

  public void visit_bastore(Instruction inst) {
    pop(3);
  }

  public void visit_castore(Instruction inst) {
    pop(3);
  }

  public void visit_sastore(Instruction inst) {
    pop(3);
  }

  /**
   * Helper method for asserting that all of the instructions are of a
   * certain category.
   */
  private void checkCategory(Set insts, int category) {
    Iterator iter = insts.iterator();
    while(iter.hasNext()) {
      Instruction inst = (Instruction) iter.next();
      Assert.isTrue(inst.category() == category, 
		    "Category mismatch: " + inst.category() + " != " +
		    category);
    }
  }

  /**
   * Helper method for asserting that all of the instructions have the
   * same category.  The category is returned.
   */
  private int checkCategory(Set insts) {
    int category = 0;
    Iterator iter = insts.iterator();
    while(iter.hasNext()) {
      Instruction inst = (Instruction) iter.next();
      if(category == 0) {
	category = inst.category();

      } else {
	Assert.isTrue(inst.category() == category, 
		      "Category mismatch in instruction set");
      }
    }

    Assert.isTrue(category != 0, "No instructions in set");
    return(category);
  }

  public void visit_pop(Instruction inst) {
    Set insts = atDepth(0);

    checkCategory(insts, 1);

    pop();
  }

  public void visit_pop2(Instruction inst) {
    // Form 1 pops two category 1 values off the stack.  Form 2 pops
    // one category 2 value off the stack.

    Set top1 = (Set) currStack.removeLast();

    int category = checkCategory(top1);

    if(category == 1) {
      // Pop another category 1 off
      Set top2 = (Set) currStack.removeLast();
      checkCategory(top2, 1);
    }
  }

  public void visit_dup(Instruction inst) {
    // Duplicate the category 1 value on the top of the stack
    Set dup = atDepth(0);

    checkCategory(dup, 1);
    
    currStack.add(new HashSet(dup));
  }

  public void visit_dup_x1(Instruction inst) {
    Set dup = atDepth(0);

    checkCategory(dup, 1);

    currStack.add(currStack.size() - 2, new HashSet(dup));
  }

  public void visit_dup_x2(Instruction inst) {
    // Top value on stack must be category 1.
    Set top1 = atDepth(0);

    checkCategory(top1, 1);

    Set top2 = atDepth(1);

    int category = checkCategory(top2);

    if(category == 1) {
      Set top3 = atDepth(2);
      checkCategory(top3, 1);

      // Form 1: Dup top value and put three down

      currStack.add(currStack.size() - 3, new HashSet(top1));      

    } else {
      // Form 2: Dup top value and put two down

      currStack.add(currStack.size() - 2, new HashSet(top1));
    }
  }

  public void visit_dup2(Instruction inst) {
    // If top two values are both category 1, dup them both.
    // Otherwise, dup the one category 2 value.
    Set top = atDepth(0);

    int category = checkCategory(top);

    if(category == 1) {
      Set top1 = atDepth(1);
      checkCategory(top1, 1);

      // Form 1: Dup top two values

      currStack.add(new HashSet(top1));
      currStack.add(new HashSet(top));

    } else {
      // Form 2: Dup top value

      currStack.add(new HashSet(top));      
    }
  }

  public void visit_dup2_x1(Instruction inst) {
    // If the top two values are of category 1, then dup them and put
    // them three down.  Otherwise, the top two values are of category
    // 2 and the top value is put two down.
    Set top = atDepth(0);

    int category = checkCategory(top);

    if(category == 1) {
      Set top1 = atDepth(1);
      checkCategory(top1, 1);

      // Form 1: Dup top two values and put three down

      int n = currStack.size() - 3;
      currStack.add(n, top1);
      currStack.add(n, top);

    } else {
      Set top1 = atDepth(1);
      checkCategory(top1, 1);

      // Form 2: Dup top value and put two down

      currStack.add(currStack.size() - 2, new HashSet(top));
    }
  }

  public void visit_dup2_x2(Instruction inst) {
    // If the two four values are all category 1, then duplicate the
    // top two values and put them four down.  If the top value is of
    // category 2 and the next two are of type 1, then dup the top
    // value and put it three down.  If the top two values are both
    // category 1 and the third value is type 2, then dup the top two
    // values and put them three down.  If the top two values are both
    // category 2, then dup the top one and put it two down.

    Set top = atDepth(0);
    int category = checkCategory(top);
    
    if(category == 1) {
      Set top1 = atDepth(1);
      int category1 = checkCategory(top1);
      if(category1 == 1) {
	Set top2 = atDepth(2);
	int category2 = checkCategory(top2);
	if(category2 == 1) {
	  checkCategory(atDepth(3), 1);

	  // Form 1: Dup top two values and put four down
	  int n = currStack.size() - 4;
	  currStack.add(n, new HashSet(top1));
	  currStack.add(n, new HashSet(top));

	} else {
	  // Form 3: Dup top two values and put three down
	  int n = currStack.size() - 3;
	  currStack.add(n, new HashSet(top1));
	  currStack.add(n, new HashSet(top));

	}

      } else {
	Assert.isTrue(false, "Impossible stack combination for " +
		      "dup2_x1: ... 2 1");
      }

    } else {
      Set top1 = atDepth(1);
      int category1 = checkCategory(top1);
      if(category1 == 1) {
	int category2 = checkCategory(atDepth(2));
	if(category2 == 1) {
	  // Form 2: Dup top value and put three down
	  currStack.add(currStack.size() - 3, new HashSet(top));

	} else {
	  Assert.isTrue(false, "Impossible stack combination for " +
			"dup2_x1: ... 2 1 2");
	}

      } else {
	// Form 4: Dup top and put two down
	currStack.add(currStack.size() - 2, new HashSet(top));
      }
    }
  }

  public void visit_swap(Instruction inst) {
    Set top = (Set) currStack.removeLast();
    Set next = (Set) currStack.removeLast();

    checkCategory(top, 1);
    checkCategory(next, 1);

    currStack.add(top);
    currStack.add(next);
  }

  public void visit_iadd(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_ladd(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_fadd(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_dadd(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_isub(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lsub(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_fsub(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_dsub(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_imul(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lmul(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_fmul(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_dmul(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_idiv(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ldiv(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_fdiv(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ddiv(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_irem(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lrem(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_frem(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_drem(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ineg(Instruction inst) {
    pop();
    push(inst);
 }

  public void visit_lneg(Instruction inst) {
    pop();
    push(inst);
 }

  public void visit_fneg(Instruction inst) {
    pop();
    push(inst);
 }

  public void visit_dneg(Instruction inst) {
    pop();
    push(inst);
 }

  public void visit_ishl(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lshl(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ishr(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lshr(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_iushr(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lushr(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_iand(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_land(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ior(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lor(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_ixor(Instruction inst) {
    pop(2);
    push(inst);
 }

  public void visit_lxor(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_iinc(Instruction inst) {
    // Kind of a fine point here.  The instruction doesn't change the
    // stack, iinc increments a local variable.
  }

  public void visit_i2l(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_i2f(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_i2d(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_l2i(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_l2f(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_l2d(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_f2i(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_f2l(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_f2d(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_d2i(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_d2l(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_d2f(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_i2b(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_i2c(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_i2s(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_lcmp(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_fcmpl(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_fcmpg(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_dcmpl(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_dcmpg(Instruction inst) {
    pop(2);
    push(inst);
  }

  public void visit_ifeq(Instruction inst) {
    pop();
  }

  public void visit_ifne(Instruction inst) {
    pop();
  }

  public void visit_iflt(Instruction inst) {
    pop();
  }

  public void visit_ifge(Instruction inst) {
    pop();
  }

  public void visit_ifgt(Instruction inst) {
    pop();
  }

  public void visit_ifle(Instruction inst) {
    pop();
  }

  public void visit_if_icmpeq(Instruction inst) {
    pop(2);
  }

  public void visit_if_icmpne(Instruction inst) {
    pop(2);
  }

  public void visit_if_icmplt(Instruction inst) {
    pop(2);
  }

  public void visit_if_icmpge(Instruction inst) {
    pop(2);
  }

  public void visit_if_icmpgt(Instruction inst) {
    pop(2);
  }

  public void visit_if_icmple(Instruction inst) {
    pop(2);
  }

  public void visit_if_acmpeq(Instruction inst) {
    pop(2);
  }

  public void visit_if_acmpne(Instruction inst) {
    pop(2);
  }

  public void visit_goto(Instruction inst) {
    // Nothing to do
  }

  public void visit_jsr(Instruction inst) {
    push(inst);
  }

  public void visit_ret(Instruction inst) {
    // Nothing to do
  }

  public void visit_switch(Instruction inst) {
    pop();
  }

  // Return stuff performed by handle(Instruction)
  public void visit_ireturn(Instruction inst) {

  }

  public void visit_lreturn(Instruction inst) {

  }

  public void visit_freturn(Instruction inst) {

  }

  public void visit_dreturn(Instruction inst) {

  }

  public void visit_areturn(Instruction inst) {

  }

  public void visit_return(Instruction inst) {

  }

  public void visit_getstatic(Instruction inst) {
    push(inst);
  }

  public void visit_putstatic(Instruction inst) {
    pop();
  }

  public void visit_putstatic_nowb(Instruction inst) {
    pop();
  }

  public void visit_getfield(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_putfield(Instruction inst) {
    pop(2);
  }

  public void visit_putfield_nowb(Instruction inst) {
    pop(2);
  }

  public void visit_invokevirtual(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    pop(type.paramTypes().length);
    pop();      // Pop receiver

    if(type.returnType() != Type.VOID) {
      push(inst);
    }
  }

  public void visit_invokespecial(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    pop(type.paramTypes().length);
    pop();      // Pop receiver

    if(type.returnType() != Type.VOID) {
      push(inst);
    }
  }

  public void visit_invokestatic(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    pop(type.paramTypes().length);

    if(type.returnType() != Type.VOID) {
      push(inst);
    }
  }

  public void visit_invokeinterface(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    pop(type.paramTypes().length);
    pop();       // Pop receiver

    if(type.returnType() != Type.VOID) {
      push(inst);
    }
  }

  public void visit_new(Instruction inst) {
    push(inst);
  }

  public void visit_newarray(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_arraylength(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_athrow(Instruction inst) {
    // I guess...
    pop();
    push(inst);
  }

  public void visit_checkcast(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_instanceof(Instruction inst) {
    pop();
    push(inst);
  }

  public void visit_monitorenter(Instruction inst) {
    pop();
  }

  public void visit_monitorexit(Instruction inst) {
    pop();
  }

  public void visit_multianewarray(Instruction inst) {
    MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
    int dim = operand.dimensions();

    pop(dim);

    push(inst);
  }

  public void visit_ifnull(Instruction inst) {
    pop();
  }

  public void visit_ifnonnull(Instruction inst) {
    pop();
  }

  public void visit_rc(Instruction inst) {}

  public void visit_aupdate(Instruction inst) {}

  public void visit_supdate(Instruction inst) {}

  public void visit_aswizzle(Instruction inst) {}

  public void visit_aswrange(Instruction inst) {}

}
