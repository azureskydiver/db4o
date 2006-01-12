/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 *
 * <p>
 *
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

package EDU.purdue.cs.bloat.codegen;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;
import java.io.*;
import java.util.*;

/** 
 * CodeGenerator performs some final optimizations and is used (via a
 * visitor) to generate bytecode for the contents of a control flow
 * graph.
 *
 * @see FlowGraph 
 */
public class CodeGenerator extends TreeVisitor implements Opcode
{
  public static boolean DEBUG = false;
  public static boolean USE_PERSISTENT = false;  // Generate _nowb

    /**
     * Use information about placement of local variables to
     * eliminate loads and stores in favor of stack manipulations
     */
  public static boolean OPT_STACK = false; 
  public static boolean DB_OPT_STACK = false;

  protected MethodEditor method;
  protected Set visited;
  protected Map postponedInstructions;
  protected Block next;

  protected int stackHeight;   // The current height of the stack


  StackOptimizer currentSO;  // object used to determine where to apply
                                  // stack optimization


  /**
   * Constructor.
   *
   * @param method
   *        The method for which bytecode is generated.
   */
  public CodeGenerator(MethodEditor method)
  {
    this.method = method;
    this.postponedInstructions = new HashMap();
  }

  /**
   * Visits the nodes in the method's control flow graph and ensures that
   * information about the method's basic blocks is consistent and correct.
   *
   * @param cfg
   *        The control flow graph associated with this method.
   */
  public void visitFlowGraph(final FlowGraph cfg) {
    // Generate the code.


    visited = new HashSet();
    visited.add(cfg.source());
    visited.add(cfg.sink());

    Iterator e = cfg.trace().iterator();

    Assert.isTrue(e.hasNext(), "trace is empty");

    stackHeight = 0;  // At beginning of method stack has height 0

    Block block = (Block) e.next();

    // Visit each block in the method (via the trace in the method's CFG)
    // and ensure that the first (and ONLY the first) label in the code
    // is marked as starting a block.
    while (block != null) {
      if (e.hasNext()) {
	next = (Block) e.next();

      } else {
	next = null;
      }

      if (CodeGenerator.DEBUG) {
	System.out.println("code for " + block);
      }

      // Make sure the first label is marked as starting a block
      // and the rest are marked as not starting a block.
      block.visit(new TreeVisitor() {
	boolean startsBlock = true;

	public void visitLabelStmt(LabelStmt stmt) {
	  stmt.label().setStartsBlock(startsBlock);
	  startsBlock = false;
	}

	public void visitStmt(Stmt stmt)
	  {
	  }
      });

      // Generate the code for each block
      visited.add(block);
      // currentSO is the StackOptimizer object that discerns
      // where dups may be used instead of loads
      if (OPT_STACK) 
	  currentSO = block.stackOptimizer();
      block.visitChildren(this);

      block = next;
    }

    Assert.isTrue(visited.size() == cfg.size(),
		  "did not visit all blocks while generating code");

    next = null;
    visited = null;

    // Go through the catch blocks and determine the what the
    // protected regions are that correspond to the catch blocks.
    // Create TryCatch objects to represent the protected regions.

    Iterator iter = cfg.catchBlocks().iterator();

    while (iter.hasNext()) {
      Block catchBlock = (Block) iter.next();
      Handler handler = (Handler) cfg.handlersMap().get(catchBlock);

      Type type = handler.catchType();

      if (type.isNull()) {
	type = null;
      }

      // First block in protected block
      Block begin = null; 

      Iterator blocks = cfg.trace().iterator();

      while (blocks.hasNext()) {
	block = (Block) blocks.next();

	if (handler.protectedBlocks().contains(block)) {
	  if (begin == null) {
	    begin = block;
	  }

	} else if (begin != null) {
	  // The block is no longer protected, its the end of the
	  // protected region
	  TryCatch tc = new TryCatch(begin.label(), block.label(),
				     catchBlock.label(), type);
	  method.addTryCatch(tc);

	  begin = null;
	}
      }
    }
  }

  /** 
   * Simplifies the control flow of a method by changing jump and
   * return statements into gotos where appropriate.
   */
  public void simplifyControlFlow(final FlowGraph cfg) {
    // Remove any blocks from the CFG that consist of solely jumps
    removeEmptyBlocks(cfg);

    cfg.visit(new TreeVisitor() {
      public void visitJsrStmt(JsrStmt stmt) {
	final Subroutine sub = stmt.sub();

	Block entry = sub.entry();

	// If there is only 1 path through the sub, replace both
	// the jsr and the ret with gotos.
	if (sub.numPaths() == 1) {
	  Block exit = sub.exit();
	  
	  // Remember that it is not required for a subroutine to have
	  // a ret.  So, no exit block may be identified and we'll
	  // have to make sure one exists.
	  if (exit != null) {
	    JumpStmt oldJump = (JumpStmt) exit.tree().lastStmt();
	    JumpStmt jump = new GotoStmt(stmt.follow());
	    jump.catchTargets().addAll(oldJump.catchTargets());
	    oldJump.replaceWith(jump);
	  }
	  
	  JumpStmt jump = new GotoStmt(sub.entry());
	  jump.catchTargets().addAll(stmt.catchTargets());
	  stmt.replaceWith(jump);
	  
	  // The subroutine is no longer really a subroutine
	  cfg.removeSub(sub);
	  
	  // Clean up the CFG by removing all AddressStoreStmts that
	  // store the address of the "removed" subroutine.
	  cfg.visit(new TreeVisitor() {
	    Iterator iter;
	    
	    public void visitTree(Tree tree) {
	      iter = tree.stmts().iterator();
	      
	      while (iter.hasNext()) {
		Stmt s = (Stmt) iter.next();
		
		if (s instanceof AddressStoreStmt) {
		  AddressStoreStmt store =
		    (AddressStoreStmt) s;
		  
		  if (store.sub() == sub) {
		    iter.remove();
		  }
		}
	      }
	    }
	  });
	}
      }
      
      public void visitStmt(Stmt stmt)
	{
	}
    });
  }


  /**
   * Replace PhiStmts with copies that accomplish what the PhiStmts
   * represent.  Then remove the PhiStmts from the control flow graph.
   */
  public void replacePhis(final FlowGraph cfg) {
    replaceCatchPhis(cfg);
    replaceJoinPhis(cfg);

    // Remove the phis.
    cfg.visit(new TreeVisitor() {
      public void visitTree(Tree tree) {
	Iterator e = tree.stmts().iterator();

	while (e.hasNext()) {
	  Stmt s = (Stmt) e.next();

	  if (s instanceof PhiStmt) {
	    e.remove();
	  }
	}
      }
    });
  }

  /**
   * Replace each PhiCatchStmt with assignments at its operands' defs.
   */
  private void replaceCatchPhis(final FlowGraph cfg) {
    cfg.visit(new TreeVisitor() {
      HashMap seen = new HashMap();

      public void visitFlowGraph(FlowGraph graph) {
	Iterator iter = graph.catchBlocks().iterator();

	// Examine each block that begins an exception handler
	while (iter.hasNext()) {
	  Block block = (Block) iter.next();
	  block.visit(this);
	}
      }

      public void visitPhiCatchStmt(PhiCatchStmt phi) {
	LocalExpr target = (LocalExpr) phi.target();
	int index = target.index();

	Iterator iter = phi.operands().iterator();

	// Examine every operand of the PhiCatchStmt.  If necessary,
	// insert copies of the operand to the target after the last
	// occurrence of the operand.
	while (iter.hasNext()) {
	  LocalExpr expr = (LocalExpr) iter.next();
	  LocalExpr def = (LocalExpr) expr.def();

	  if (def == null) {
	    continue;
	  }

	  if (DEBUG) {
	    System.out.println("inserting for " + phi + " at " + def);
	  }

	  BitSet s = (BitSet) seen.get(def);

	  if (s == null) {
	    s = new BitSet();
	    seen.put(def, s);

	    final BitSet t = s;

	    // Visit the parent expression and make note of which
	    // local variables were encountered in StoreExprs.  That
	    // is, have we already generated a copy for the operand of
	    // interest?
	    def.parent().visit(new TreeVisitor() {
	      public void visitStoreExpr(StoreExpr expr) {
		if (DEBUG) {
		  System.out.println("    merging with " + expr);
		}

		Expr lhs = expr.target();
		Expr rhs = expr.expr();

		if (lhs instanceof LocalExpr) {
		  t.set(((LocalExpr) lhs).index());
		}

		if (rhs instanceof LocalExpr) {
		  t.set(((LocalExpr) rhs).index());

		} else if (rhs instanceof StoreExpr) {
		  // Visit RHS.  LHS be ignored by visitNode.
		  super.visitStoreExpr(expr);
		}
	      }

	      public void visitNode(Node node)
		{
		}
	    });
	  }

	  // If we've already inserted a copy (StoreStmt) for the
	  // local variable, skip it
	  if (s.get(index)) {
	    continue;
	  }

	  s.set(index);

	  Assert.isTrue(def != null);

	  if (def.parent() instanceof Stmt) {
	    // Insert a new Stmt to copy into the target

	    Stmt stmt = (Stmt) def.parent();
	    Stmt store = createStore(target, def);
	    def.block().tree().addStmtAfter(store, stmt);

	  } else {
	    Assert.isTrue(def.parent() instanceof StoreExpr);

	    // Replace s := r with s := (t := r)
	    StoreExpr p = (StoreExpr) def.parent();
	    Expr rhs = p.expr();

	    if (rhs instanceof LocalExpr &&
		((LocalExpr) rhs).index() == def.index()) {
	      // No need to insert a copy.  Just change the index
	      // (local variable to which LocalExpr is assigned) to be
	      // the same as the target
	      def.setIndex(index);

	    } else {
	      rhs.setParent(null);

	      // Copy the rhs into the target
	      StoreExpr store = new StoreExpr((LocalExpr)
					      target.clone(), rhs, 
					      rhs.type());

	      p.visit(new ReplaceVisitor(rhs, store));
	    }
	  }
	}
      }

      public void visitStmt(Stmt stmt)
	{
	}
    });
  }

  /** 
   * Replace PhiJoinStmts with assignments at the end of the
   * predecessor blocks.  Note that from now on the FUD chains are
   * broken since there can be more than one def of a variable.  
   */
  private void replaceJoinPhis(final FlowGraph cfg) {
    // Go in trace order since liveness was computed under this
    // assumption.

    Iterator iter = cfg.trace().iterator();

    while (iter.hasNext()) {
      Block block = (Block) iter.next();

      if (block == cfg.sink()) {
	continue;
      }

      block.visit(new TreeVisitor() {
	public void visitPhiJoinStmt(PhiJoinStmt stmt) {
	  // If an operand of the Phi statement is undefined, insert
	  // code to assign 0 to the operand.  The value should never
	  // be used, but the verifier will squawk about using an
	  // undefined local variable.

	  Iterator preds = cfg.preds(stmt.block()).iterator();

	  while (preds.hasNext()) {
	    Block pred = (Block) preds.next();

	    Expr operand = stmt.operandAt(pred);

	    if (stmt.target() instanceof LocalExpr &&
		operand instanceof LocalExpr) {

	      LocalExpr t = (LocalExpr) stmt.target();
	      LocalExpr s = (LocalExpr) operand;

	      if (t.index() == s.index()) {
		// The target and the operand are already allocated to
		// the same variable.  Don't bother making a copy.
		continue;
	      }
	    }

	    final Tree tree = pred.tree();

	    // Insert stores before the last stmt to ensure
	    // we don't redefine locals used the the branch stmt.
	    final Stmt last = tree.lastStmt();

	    last.visitChildren(new TreeVisitor() {
	      // The last statement in the block should be a jump.  If
	      // the jump statement contains an expression, replace
	      // that expression with a stack variable.  Before the
	      // jump, insert a store of the expression into the stack
	      // variable.  This is done so that the store to the
	      // PhiJoinStmt's operand does not interfere with any
	      // local variables that might appear in the expression.
	      //
	      //          operand = ...
	      //          JUMP (exp)
	      //            |
	      //            v
              //          target = PhiJoin(operand)
	      //          ...
	      // Becomes
	      //
	      //          operand = ...
	      //          var = exp
	      //          target = operand
	      //          JUMP (var)
	      //            |
	      //            v
	      //          target = PhiJoin(operand)  // Removed later
	      //          ...

	      public void visitExpr(Expr expr) {
		StackExpr var = tree.newStack(expr.type()); 
		var.setValueNumber(expr.valueNumber());

		Node p = expr.parent();
		expr.setParent(null);
		p.visit(new ReplaceVisitor(expr, var));

		var = (StackExpr) var.clone();
		StoreExpr store = new StoreExpr(var, expr,
						expr.type());
		store.setValueNumber(expr.valueNumber());

		Stmt storeStmt = new ExprStmt(store);
		storeStmt.setValueNumber(expr.valueNumber());

		tree.addStmtBeforeJump(storeStmt);
	      }

	      public void visitStackExpr(StackExpr expr) 
		{
		}
	    });

	    Stmt store = createStore(stmt.target(), operand);

	    if (CodeGenerator.DEBUG) {
	      System.out.println("insert for " + stmt +
				 " " + store + " in " + pred);
	    }

	    tree.addStmtBeforeJump(store);
	  }
	}

	public void visitStmt(Stmt stmt)
	  {
	  }
      });
    }
  }

  /**
   * Removes blocks that contain no other statements than gotos, jumps,
   * returns, or labels.  Other blocks that are invovled with the blocks
   * being removed are updated appropriately.
   */
  private void removeEmptyBlocks(final FlowGraph cfg) {
    Set emptyBlocks = new HashSet();

    Iterator e = cfg.nodes().iterator();

  BLOCKS:
    while (e.hasNext()) {
      Block block = (Block) e.next();

      // Collect any blocks that contain only gotos,
      // jsrs, rets, or labels.
      Iterator stmts = block.tree().stmts().iterator();

      while (stmts.hasNext()) {
	Stmt stmt = (Stmt) stmts.next();

	if (stmt instanceof GotoStmt ||
	    stmt instanceof JsrStmt ||
	    stmt instanceof RetStmt ||
	    stmt instanceof LabelStmt) {
	  continue;
	}

	// The block contains something other than the above, it is
	// not empty.
	continue BLOCKS;
      }

      emptyBlocks.add(block);
    }

    // We want to keep the source, init, and sink blocks even if
    // they're empty
    emptyBlocks.remove(cfg.source());
    emptyBlocks.remove(cfg.init());
    emptyBlocks.remove(cfg.sink());

    // Did the CFG change?
    boolean changed = true;

    while (changed) {
      changed = false;

      // Exclude the blocks that are control dependent on other blocks.
      Set empty = new HashSet(emptyBlocks);
      empty.removeAll(cfg.iteratedPdomFrontier(cfg.nodes()));

      e = empty.iterator();

    EMPTY:
      while (e.hasNext()) {
	Block block = (Block) e.next();

	if (DEBUG) {
	  System.out.println("removing empty " + block);
	}

	Stmt last = block.tree().lastStmt();

	Assert.isTrue(last instanceof GotoStmt ||
		      last instanceof JsrStmt ||
		      last instanceof RetStmt);

	if (last instanceof GotoStmt) {
	  // All a block does is jump to another block
	  //
	  //    jmp ... L
	  // L: goto M
	  // =>
	  //    jmp ... M
	  Block target = ((GotoStmt) last).target();

	  Iterator preds = new ImmutableIterator(cfg.preds(block));

	  while (preds.hasNext()) {
	    Block pred = (Block) preds.next();
	    Assert.isTrue(pred != cfg.source()); 

	    Stmt predLast = pred.tree().lastStmt();
	    predLast.visit(new ReplaceTarget(block, target));

	    cfg.removeEdge(pred, block);
	    cfg.addEdge(pred, target);

	    changed = true;
	  }

	} else if (last instanceof RetStmt) {
	  // All a subroutine does is return

	  Iterator preds = new ImmutableIterator(cfg.preds(block));

	  while (preds.hasNext()) {
	    Block pred = (Block) preds.next();
	    Assert.isTrue(pred != cfg.source()); 

	    Stmt predLast = pred.tree().lastStmt();

	    if (predLast instanceof JsrStmt) {
	      // The previous block is the jsr...
	      //
	      //    jsr L ret to M
	      // M: ...
	      // L: ret       // The body of the subroutine is empty
	      // =>
	      //    goto M
	      // M: ...

	      JsrStmt stmt = (JsrStmt) predLast;

	      JumpStmt jump = new GotoStmt(stmt.follow());
	      jump.catchTargets().addAll(stmt.catchTargets());
	      stmt.replaceWith(jump);

	      stmt.sub().removePathsContaining(pred);

	    } else if (predLast instanceof GotoStmt) {
	      // The previous block ends in a goto.  Move the ret up
	      // into the previous block, update catch targets of any
	      // exceptions thrown by the block terminated by the
	      // jump, and update the subroutine's exit block to be
	      // the previous block (in which the ret now resides).

	      JumpStmt jump = (RetStmt) last.clone();
	      jump.catchTargets().addAll(((JumpStmt) predLast).catchTargets());
	      predLast.replaceWith(jump);
	      ((RetStmt) last).sub().setExit(pred);
	    }

	    // Remove the block from the CFG
	    cfg.succs(pred).remove(block);
	    cfg.succs(pred).addAll(cfg.succs(block));

	    changed = true;
	  }

	} else if (last instanceof JsrStmt) {
	  // All the block does is a jsr
	  //
	  //    goto L
	  // L: jsr M
	  // =>
	  //    jsr M
	  // L: jsr M
	  Iterator preds = new ImmutableIterator(cfg.preds(block));

	  while (preds.hasNext()) {
	    Block pred = (Block) preds.next();
	    Assert.isTrue(pred != cfg.source()); 

	    Stmt predLast = pred.tree().lastStmt();

	    if (predLast instanceof GotoStmt) {
	      JsrStmt stmt = (JsrStmt) last;

	      JumpStmt jump = new JsrStmt(stmt.sub(),
					  stmt.follow());
	      jump.catchTargets().addAll(((JumpStmt) predLast).catchTargets());
	      predLast.replaceWith(jump);

	      // The block is no longer a viable caller of the subroutine
	      stmt.sub().removePathsContaining(block);
	      stmt.sub().addPath(pred, stmt.follow());

	      cfg.addEdge(pred, stmt.sub().entry());
	      cfg.removeEdge(pred, block);

	      changed = true;
	    }
	  }

	} else {
	  throw new RuntimeException();
	}
      }

      if (changed) {
	cfg.removeUnreachable();

	// Remove any empty blocks that we've already deleted.
	emptyBlocks.retainAll(cfg.nodes());
      }
    }
  }

  /**
   * Allocate "registers" (LocalVariables) for the return addresses for
   * each subroutine in the method.
   *
   * @param cfg
   *        Control flow graph for the method
   * @param alloc
   *        Allocation (and information about) the local variables in 
   *        the method.
   *
   * @see LocalVariable
   * @see LocalExpr
   */
  public void allocReturnAddresses(FlowGraph cfg,
				   final RegisterAllocator alloc) {
    // Allocate registers for the returnAddresses.  Don't bother trying
    // to minimize the number of locals, just get a new one.
    Iterator e = cfg.subroutines().iterator();

    while (e.hasNext()) {
      Subroutine sub = (Subroutine) e.next();
      LocalVariable var = alloc.newLocal(Type.ADDRESS);
      sub.setReturnAddress(var);
    }
  }

  /**
   * Create a ExprStmt that initializes a target variable to a default
   * value based on the type of the target.
   */
  protected Stmt createUndefinedStore(VarExpr target) {
    if (target.type().isReference()) {
      return new ExprStmt(new StoreExpr(target,
					new ConstantExpr(null, Type.OBJECT), 
					target.type()));
    }

    if (target.type().isIntegral()) {
      return new ExprStmt(new StoreExpr(target,
					new ConstantExpr(new Integer(0), 
							 Type.INTEGER), 
					target.type()));
    }

    if (target.type().equals(Type.LONG)) {
      return new ExprStmt(new StoreExpr(target,
					new ConstantExpr(new Long(0),
							 Type.LONG), 
					target.type()));
    }

    if (target.type().equals(Type.FLOAT)) {
      return new ExprStmt(new StoreExpr(target,
					new ConstantExpr(new Float(0.0F), 
							 Type.FLOAT), 
					target.type()));
    }

    if (target.type().equals(Type.DOUBLE)) {
      return new ExprStmt(new StoreExpr(target,
					new ConstantExpr(new Double(0.0), 
							 Type.DOUBLE), 
					target.type()));
    }

    throw new RuntimeException("Illegal type: " + target.type());
  }

  /**
   * Returns an ExprStmt that contains a store of the source into the
   * target.
   */
  protected Stmt createStore(VarExpr target, Expr source) {
    target = (VarExpr) target.clone();

    // Source is an undefined variable, initialize it
    if (source instanceof VarExpr && source.def() == null) {
      return createUndefinedStore(target);
    }

    return new ExprStmt(new StoreExpr(target,
				      (Expr) source.clone(), target.type()));
  }

  /*
   * Using an InstructionVisitor generate the code...
   */

  
    //     Several of the visit methods contain code for stack
    //     optimization (place dups and swaps and eliminate temporary
    //     variables). Nodes where swaps are to be placed are so
    //     marked. The markings may appear at IfCmpStmt, InitStmt,
    //     StoreExpr, ArithExpr, ArrayRefExpr, CallMethodExpr,
    //     CallStaticExpr, NewMultiArrayExpr, ShiftExpr. 

  public void visitExpr(Expr expr)
  {
    throw new RuntimeException("Unhandled expression type: " +
			       expr.getClass().getName());
  }

  public void visitExprStmt(ExprStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    if (! (stmt.expr() instanceof StoreExpr)) {
      if (! stmt.expr().type().isVoid()) {
	if (stmt.expr().type().isWide()) {
	  method.addInstruction(opcx_pop2);
	  stackHeight -= 2;

	} else {
	  method.addInstruction(opcx_pop);
	  stackHeight -= 1;
	}
      }
    }
  }

  public void visitInitStmt(InitStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }
  }

  public void visitGotoStmt(GotoStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    Block target = stmt.target();

    if (target != next) {
      method.addInstruction(opcx_goto, stmt.target().label());
    }
  }

  public void visitIfCmpStmt(IfCmpStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    Block t = stmt.trueTarget();
    Block f = stmt.falseTarget();

    if (f == next) {
      // Fall through to the false branch.
      genIfCmpStmt(stmt);

    } else if (t == next) {
      // Fall through to the true branch.
      stmt.negate();
      genIfCmpStmt(stmt);

    } else {
      // Generate a goto to the false branch after the if statement.
      genIfCmpStmt(stmt); 

      method.addLabel(method.newLabelTrue());  //Tom changed to say "True"
      method.addInstruction(opcx_goto, f.label());
    }
  }

  private void genIfCmpStmt(IfCmpStmt stmt) {
    int opcode;

    stmt.visitChildren(this);

    genPostponed(stmt);

    int cmp = stmt.comparison();

    if (stmt.left().type().isReference()) {
      Assert.isTrue(stmt.right().type().isReference(),
		    "Illegal statement: " + stmt);

      switch (cmp) {
      case IfStmt.EQ:
	opcode = opcx_if_acmpeq;
	break;
      case IfStmt.NE:
	opcode = opcx_if_acmpne;
	break;
      default:
	throw new RuntimeException();
      }

    } else {
      Assert.isTrue(stmt.left().type().isIntegral(),
		    "Illegal statement: " + stmt);
      Assert.isTrue(stmt.right().type().isIntegral(),
		    "Illegal statement: " + stmt);

      switch (cmp) {
      case IfStmt.EQ:
	opcode = opcx_if_icmpeq;
	break;
      case IfStmt.NE:
	opcode = opcx_if_icmpne;
	break;
      case IfStmt.GT:
	opcode = opcx_if_icmpgt;
	break;
      case IfStmt.GE:
	opcode = opcx_if_icmpge;
	break;
      case IfStmt.LT:
	opcode = opcx_if_icmplt;
	break;
      case IfStmt.LE:
	opcode = opcx_if_icmple;
	break;
      default:
	throw new RuntimeException();
      }
    }

    method.addInstruction(opcode, stmt.trueTarget().label());
    stackHeight -= 2;
  }

  public void visitIfZeroStmt(IfZeroStmt stmt) {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    Block t = stmt.trueTarget();
    Block f = stmt.falseTarget();

    if (f == next) {
      // Fall through to the false branch.
      genIfZeroStmt(stmt);
    }
    else if (t == next) {
      // Fall through to the true branch.
      stmt.negate();
      genIfZeroStmt(stmt);
    }
    else {
      // Generate a goto to the false branch after the if statement.
      genIfZeroStmt(stmt);
      method.addLabel(method.newLabelTrue());   //Tom added "True"
      method.addInstruction(opcx_goto, f.label());
    }
  }

  private void genIfZeroStmt(IfZeroStmt stmt) {
    int opcode;

    stmt.expr().visit(this);

    genPostponed(stmt);

    int cmp = stmt.comparison();

    if (stmt.expr().type().isReference()) {
      switch (cmp) {
      case IfStmt.EQ:
	opcode = opcx_ifnull;
	break;
      case IfStmt.NE:
	opcode = opcx_ifnonnull;
	break;
      default:
	throw new RuntimeException();
      }

    } else {
      Assert.isTrue(stmt.expr().type().isIntegral(),
		    "Illegal statement: " + stmt);

      switch (cmp) {
      case IfStmt.EQ:
	opcode = opcx_ifeq;
	break;
      case IfStmt.NE:
	opcode = opcx_ifne;
	break;
      case IfStmt.GT:
	opcode = opcx_ifgt;
	break;
      case IfStmt.GE:
	opcode = opcx_ifge;
	break;
      case IfStmt.LT:
	opcode = opcx_iflt;
	break;
      case IfStmt.LE:
	opcode = opcx_ifle;
	break;
      default:
	throw new RuntimeException();
      }
    }
    method.addInstruction(opcode, stmt.trueTarget().label());
    stackHeight -= 1;
  }

  public void visitLabelStmt(LabelStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    method.addLabel(stmt.label());
  }

  public void visitMonitorStmt(MonitorStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    if (stmt.kind() == MonitorStmt.ENTER) {
      method.addInstruction(opcx_monitorenter);
      stackHeight -= 1;

    } else if (stmt.kind() == MonitorStmt.EXIT) {
      method.addInstruction(opcx_monitorexit);
      stackHeight -= 1;

    } else {
      throw new IllegalArgumentException();
    }
  }

  public void visitPhiStmt(PhiStmt stmt)
  {
    throw new RuntimeException("Cannot generate code for " + stmt);
  }

  public void visitRCExpr(RCExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    // Move the rc forward as far as possible. 
    //
    // For example, for the expression:
    //
    //     rc(x).m(rc(a).b)
    //
    // we want to generate:
    //
    //     aload x
    //     aload a
    //     rc 0
    //     getfield <A.b>
    //     rc 1
    //     invoke <X.m>
    //
    // rather than:
    //
    //     aload x
    //     rc 0
    //     aload a
    //     rc 0
    //     getfield <A.b>
    //     invoke <X.m>
    //
    Instruction postpone = null;

    Node parent = expr.parent();

    // If the parent is wrapped in a ZeroCheckExpr, then look at the
    // parent's parent.

    if(parent instanceof ZeroCheckExpr)
      parent = parent.parent();

    // If the depth is going to be > 0, postpone the rc instruction
    // to just before the getfield, putfield, invoke, xaload, xastore, etc.
    // If the stack depth for the rc is going to be 0, the rc will (be)
    // the next instruction generated anyway, so don't postpone.

    if (parent instanceof ArrayRefExpr) {
      ArrayRefExpr p = (ArrayRefExpr) parent;

      if (expr == p.array()) {
	if (p.isDef()) {
	  // a[i] := r
	  // Stack at the xastore: ... a i r
	  postpone = new Instruction(opcx_rc,
				     new Integer(p.type().stackHeight() + 1));
	}
	else {
	  // use a[i]
	  // Stack at the xaload: ... a i
	  postpone = new Instruction(opcx_rc, new Integer(1));
	}
      }

    } else if (parent instanceof CallMethodExpr) {
      CallMethodExpr p = (CallMethodExpr) parent;

      if (expr == p.receiver()) {
	// a.m(b, c)
	// Stack at the invoke: ... a b c
	MemberRef method = p.method();
	int depth = method.nameAndType().type().stackHeight();
	postpone = new Instruction(opcx_rc, new Integer(depth));
      }

    } else if (parent instanceof FieldExpr) {
      FieldExpr p = (FieldExpr) parent;

      if (expr == p.object()) {
	if (p.isDef()) {
	  // a.b := r
	  // Stack at the putfield: ... a r
	  postpone = new Instruction(opcx_rc,
				     new Integer(p.type().stackHeight()));
	}
      }
    }

    if (postpone == null) {
      int depth = 0;

      if(expr.expr() instanceof StackExpr) {
	// If the rc works on a StackExpr, calculate its depth in the
	// stack.  In all other cases, the rc will operate on whatever
	// is on top of the stack.
	StackExpr stackVar = (StackExpr) expr.expr();
	depth = stackHeight - stackVar.index() - 1;
      }

      method.addInstruction(opcx_rc, new Integer(depth));

    } else {
      postponedInstructions.put(parent, postpone);
    }
  }

  public void visitUCExpr(UCExpr expr) {
    expr.visitChildren(this);

    if(true)
      return;

    genPostponed(expr);

    // Move the uc forward as far as possible. 
    Instruction postpone = null;

    Node parent = expr.parent();

    // If the depth is going to be > 0, postpone the uc instruction
    // to just before the putfield.  If the stack depth for the
    // uc is going to be 0, the uc will the next instruction
    // generated anyway, so don't postpone.

    if (parent instanceof FieldExpr) {
      FieldExpr p = (FieldExpr) parent;

      if (expr == p.object()) {
	if (p.isDef()) {
	  // a.b := r
	  // Stack at the putfield: ... a r
	  if (expr.kind() == UCExpr.POINTER) {
	    postpone = new Instruction(opcx_aupdate,
				       new Integer(p.type().stackHeight()));

	  } else if (expr.kind() == UCExpr.SCALAR) {
	    postpone = new Instruction(opcx_supdate,
				       new Integer(p.type().stackHeight()));

	  } else {
	    throw new RuntimeException();
	  }
	}
      }
    } 

    if (postpone == null) {
      int depth = 0;

      if(expr.expr() instanceof StackExpr) {
	// If the UCExpr operates on a stack variable, use that to
	// determine the depth.  In all other cases, use 0.
	StackExpr stackVar = (StackExpr) expr.expr();
	depth = stackHeight - stackVar.index() - 1;
      }

      if (expr.kind() == UCExpr.POINTER) {
	method.addInstruction(opcx_aupdate, new Integer(depth));
      }
      else if (expr.kind() == UCExpr.SCALAR) {
	method.addInstruction(opcx_supdate, new Integer(depth));
      }
      else {
	throw new RuntimeException();
      }

    } else {
      postponedInstructions.put(parent, postpone);
    }
  }

  public void visitRetStmt(RetStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    Subroutine sub = stmt.sub();
    Assert.isTrue(sub.returnAddress() != null);
    method.addInstruction(opcx_ret, sub.returnAddress());
  }

  public void visitReturnExprStmt(ReturnExprStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    Type type = stmt.expr().type();

    // Stack should be empty after return

    if (type.isReference()) {
      method.addInstruction(opcx_areturn);
      stackHeight = 0;
    }
    else if (type.isIntegral()) {
      method.addInstruction(opcx_ireturn);
      stackHeight = 0;
    }
    else if (type.equals(Type.LONG)) {
      method.addInstruction(opcx_lreturn);
      stackHeight = 0;
    }
    else if (type.equals(Type.FLOAT)) {
      method.addInstruction(opcx_freturn);
      stackHeight = 0;
    }
    else if (type.equals(Type.DOUBLE)) {
      method.addInstruction(opcx_dreturn);
      stackHeight = 0;
    }
  }

  public void visitReturnStmt(ReturnStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    stmt.visitChildren(this);
    method.addInstruction(opcx_return);

    // Stack height is zero after return
    stackHeight = 0;
  }

  public void visitStoreExpr(StoreExpr expr) {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + expr);
    }

    MemExpr lhs = expr.target();
    Expr rhs = expr.expr();

    boolean returnsValue = ! (expr.parent() instanceof ExprStmt);

    // eliminate the store if the both sides have the same target

    if (! returnsValue) {
      if (lhs instanceof LocalExpr && rhs instanceof LocalExpr) {

	  //The second condition checks that the right sides have indeed
	  //been stored-- otherwise (ie, if we're just keeping it on the 
	  //stack), we should not eliminate this store 

	if (((LocalExpr) lhs).index() == ((LocalExpr) rhs).index()

	    && (!OPT_STACK ||
		currentSO.shouldStore(((LocalExpr) ((LocalExpr) rhs).def())))) {
	  return;
	}
      }

      // Special case to handle L := L + k.
      // Generate "iinc L, k" instead of "iload L; ldc k; iadd; istore L".
      //
      // TODO: for L := M + k, generate "iload M; istore L; iinc L, k".
      //

      /* This next special case was modified for stack
	 optimization. If L is marked for a dup, the fact that its
	 value is never on the stack means we don't have anything to
	 dup-- we need to load instead.  (Things get more complicated
	 if it was marked for a dup_x2, but that's not likely to
	 happen) */

      if (lhs instanceof LocalExpr && lhs.type().isIntegral()) {
	Integer value = null;
    // eliminate the store if the both sides have the same target

	int index = ((LocalExpr) lhs).index();

	if (rhs instanceof ArithExpr) {
	  ArithExpr arith = (ArithExpr) rhs;

	  Expr left = arith.left();
	  Expr right = arith.right();

	  if (left instanceof LocalExpr &&
	      index == ((LocalExpr) left).index() &&
	      right instanceof ConstantExpr) {

	    ConstantExpr c = (ConstantExpr) right;

	    if (c.value() instanceof Integer) {
	      value = (Integer) c.value();
	    }

	  } else if (right instanceof LocalExpr &&
		     index == ((LocalExpr) right).index() &&
		     left instanceof ConstantExpr &&
		     arith.operation() == ArithExpr.ADD) {

	    // This will not work for x = c - x because it is not the
	    // same as x = x - c.

	    ConstantExpr c = (ConstantExpr) left;

	    if (c.value() instanceof Integer) {
	      value = (Integer) c.value();
	    }
	  }

	  if (value != null && arith.operation() == ArithExpr.SUB) {
	    value = new Integer(-value.intValue());
	  }
	  else if (arith.operation() != ArithExpr.ADD) {
	    value = null;
	  }
	}

	if (value != null) {
	  int incr = value.intValue();

	  if (incr == 0) {
	    // No need to increment by 0.

	      // for a better understanding of what's going on in these
	      // additions, see VisitLocalExpr, where we do basically the
	      // same thing.

	    if (OPT_STACK) {
		int dups, dup_x1s, dup_x2s;
		dups = currentSO.dups((LocalExpr) lhs);
		dup_x1s = currentSO.dup_x1s((LocalExpr) lhs);
		dup_x2s = currentSO.dup_x2s((LocalExpr) lhs);
		for (int i = 0; i < dup_x2s; i++) {
		    //This is really awful, but be consoled in that it is
		    // highly improbable to happen... this is just
		    // to make correct code in the chance that we
		    // have something like this.
		    method.addInstruction(opcx_ldc, new Integer(0));
		    method.addInstruction(opc_dup_x2);
		    method.addInstruction(opc_pop);
		    stackHeight += 1;
		}
		for (int i = 0; i < dup_x1s; i++) {
		    method.addInstruction(opcx_ldc, new Integer(0));
		    method.addInstruction(opc_swap);
		    stackHeight += 1;
		}
		for (int i = 0; i < dups; i++) {
		    method.addInstruction(opcx_ldc, new Integer(0));
		    stackHeight += 1;
		}
	    }

		
	    return;

	  } else if ((short) incr == incr) {
	    // Only generate an iinc if the increment fits in
	    // a short.
	    method.addInstruction(opcx_iinc,
				  new IncOperand(new LocalVariable(index),
						 incr));

	    if (OPT_STACK) {
		int dups, dup_x1s, dup_x2s;
		dups = currentSO.dups((LocalExpr) lhs);
		dup_x1s = currentSO.dup_x1s((LocalExpr) lhs);
		dup_x2s = currentSO.dup_x2s((LocalExpr) lhs);
		for (int i = 0; i < dup_x2s; i++) {
		    method.addInstruction(opcx_istore, 
					  new LocalVariable(((LocalExpr) lhs)
							    .index()));
		    method.addInstruction(opc_dup_x2);
		    method.addInstruction(opc_pop);
		    stackHeight += 1;
		}
		for (int i = 0; i < dup_x1s; i++) {
		    method.addInstruction(opcx_iload,
					  new LocalVariable(((LocalExpr) lhs)
							    .index()));
		    method.addInstruction(opc_swap);
		    stackHeight += 1;
		}
		for (int i = 0; i < dups; i++) {
		    method.addInstruction(opcx_iload,
					  new LocalVariable(((LocalExpr) lhs)
							    .index()));
		    stackHeight += 1;
		}
	    }

	    return;
	  }
	}
      }
    }

    // Generate, and return the value.
    lhs.visitChildren(this);
    rhs.visit(this);

    if (returnsValue) {
      if (lhs instanceof ArrayRefExpr) {
	// array index rhs --> rhs array index rhs
	if (rhs.type().isWide()) {
	  method.addInstruction(opcx_dup2_x2);
	  stackHeight += 2;
	}
	else {
	  method.addInstruction(opcx_dup_x2);
	  stackHeight += 1;
	}

      } else if (lhs instanceof FieldExpr) {
	// object rhs --> rhs object rhs
	if (rhs.type().isWide()) {
	  method.addInstruction(opcx_dup2_x1);
	  stackHeight += 2;
	}
	else {
	  method.addInstruction(opcx_dup_x1);
	  stackHeight += 1;
	}

      } else {
	// rhs --> rhs rhs
	if (rhs.type().isWide()) {
	  method.addInstruction(opcx_dup2);
	  stackHeight += 2;
	}
	else {
	  method.addInstruction(opcx_dup);
	  stackHeight += 1;
	}
      }
    }

    genPostponed(expr);
    lhs.visitOnly(this);
  }

  public void visitAddressStoreStmt(AddressStoreStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    Subroutine sub = stmt.sub();
    Assert.isTrue(sub.returnAddress() != null);
    method.addInstruction(opcx_astore, sub.returnAddress());
    stackHeight -= 1;
  }

  public void visitJsrStmt(JsrStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    Block entry = stmt.sub().entry();
    method.addInstruction(opcx_jsr, entry.label());
    stackHeight += 1;

    if (stmt.follow() != next) {
      method.addLabel(method.newLabelTrue());
      method.addInstruction(opcx_goto, stmt.follow().label());
    }
  }

  public void visitSwitchStmt(SwitchStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    Label[] targets = new Label[stmt.targets().length];

    for (int i = 0; i < targets.length; i++) {
      targets[i] = stmt.targets()[i].label();
    }

    method.addInstruction(opcx_switch,
			  new Switch(stmt.defaultTarget().label(), 
				     targets, stmt.values()));
    stackHeight -= 1;
  }

  public void visitStackManipStmt(StackManipStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    genPostponed(stmt);

    // All the children are stack variables, so don't so anything.

    switch (stmt.kind()) {
    case StackManipStmt.SWAP:
      method.addInstruction(opcx_swap);
      break;
    case StackManipStmt.DUP:
      method.addInstruction(opcx_dup);
      stackHeight += 1;
      break;
    case StackManipStmt.DUP_X1:
      method.addInstruction(opcx_dup_x1);
      stackHeight += 1;
      break;
    case StackManipStmt.DUP_X2:
      method.addInstruction(opcx_dup_x2);
      stackHeight += 1;
      break;
    case StackManipStmt.DUP2:
      method.addInstruction(opcx_dup2);
      stackHeight += 2;
      break;
    case StackManipStmt.DUP2_X1:
      method.addInstruction(opcx_dup2_x1);
      stackHeight += 2;
      break;
    case StackManipStmt.DUP2_X2:
      method.addInstruction(opcx_dup2_x2);
      stackHeight += 2;
      break;
    }
  }

  public void visitThrowStmt(ThrowStmt stmt)
  {
    if (CodeGenerator.DEBUG) {
      System.out.println("code for " + stmt);
    }

    stmt.visitChildren(this);

    genPostponed(stmt);

    method.addInstruction(opcx_athrow);
  }

  public void visitSCStmt(SCStmt stmt)
  {
    stmt.visitChildren(this);
    genPostponed(stmt);
    method.addInstruction(opcx_aswizzle);
    stackHeight -= 2;
  }
  
  public void visitSRStmt(SRStmt stmt)
  {
    stmt.visitChildren(this);
    genPostponed(stmt);
    method.addInstruction(opcx_aswrange);
    stackHeight -= 3;
  }

  public void visitArithExpr(ArithExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    int[][] opcode = new int[][] {
      { opcx_iadd, opcx_ladd, opcx_fadd,  opcx_dadd  },
	{ opcx_iand, opcx_land, opcx_nop,   opcx_nop   },
	  { opcx_idiv, opcx_ldiv, opcx_fdiv,  opcx_ddiv  },
	    { opcx_imul, opcx_lmul, opcx_fmul,  opcx_dmul  },
	      { opcx_ior,  opcx_lor,  opcx_nop,   opcx_nop   },
		{ opcx_irem, opcx_lrem, opcx_frem,  opcx_drem  },
		  { opcx_isub, opcx_lsub, opcx_fsub,  opcx_dsub  },
		    { opcx_ixor, opcx_lxor, opcx_nop,   opcx_nop   },
		      { opcx_nop,  opcx_lcmp, opcx_nop,   opcx_nop   },
			{ opcx_nop,  opcx_nop,  opcx_fcmpl, opcx_dcmpl },
			  { opcx_nop,  opcx_nop,  opcx_fcmpg, opcx_dcmpg }
    };

    int[][] stackChange = new int[][] {
      { -1, -2, -1, -2 }, 
	{ -1, -2, 0, 0 },
	  { -1, -2, -1, -2 },
	    { -1, -2, -1, -2 },
	      { -1, -2, 0, 0 },
		{ -1, -2, -1, -2 },
		  { -1, -2, -1, -2 },
		    { -1, -2, 0, 0 },
		      { 0, -3, 0, 0},
			{ 0, 0, -1, -3 },
			  { 0, 0, -1, -3 }
    };

    int type;

    if (expr.left().type().isIntegral()) {
      type = 0;

    } else if (expr.left().type().equals(Type.LONG)) {
      type = 1;

    } else if (expr.left().type().equals(Type.FLOAT)) {
      type = 2;

    } else if (expr.left().type().equals(Type.DOUBLE)) {
      type = 3;

    } else {
      throw new IllegalArgumentException("Can't generate code for type: " + 
					 expr.left().type() +
					 " (expr " + expr + ")");
    }

    switch (expr.operation()) {
    case ArithExpr.ADD:
      method.addInstruction(opcode[0][type]);
      stackHeight += stackChange[0][type];
      break;
    case ArithExpr.AND:
      method.addInstruction(opcode[1][type]);
      stackHeight += stackChange[1][type];
      break;
    case ArithExpr.DIV:
      method.addInstruction(opcode[2][type]);
      stackHeight += stackChange[2][type];
      break;
    case ArithExpr.MUL:
      method.addInstruction(opcode[3][type]);
      stackHeight += stackChange[3][type];
      break;
    case ArithExpr.IOR:
      method.addInstruction(opcode[4][type]);
      stackHeight += stackChange[4][type];
      break;
    case ArithExpr.REM:
      method.addInstruction(opcode[5][type]);
      stackHeight += stackChange[5][type];
      break;
    case ArithExpr.SUB:
      method.addInstruction(opcode[6][type]);
      stackHeight += stackChange[6][type];
      break;
    case ArithExpr.XOR:
      method.addInstruction(opcode[7][type]);
      stackHeight += stackChange[7][type];
      break;
    case ArithExpr.CMP:
      method.addInstruction(opcode[8][type]);
      stackHeight += stackChange[8][type];
      break;
    case ArithExpr.CMPL:
      method.addInstruction(opcode[9][type]);
      stackHeight += stackChange[9][type];
      break;
    case ArithExpr.CMPG:
      method.addInstruction(opcode[10][type]);
      stackHeight += stackChange[10][type];
      break;
    }
  }

  public void visitArrayLengthExpr(ArrayLengthExpr expr)
  {
    expr.visitChildren(this);
    method.addInstruction(opcx_arraylength);
  }

  public void visitArrayRefExpr(ArrayRefExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    int opcode;

    if (expr.isDef()) {
      if (expr.elementType().isReference()) {
	opcode = opcx_aastore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.BYTE)) {
	opcode = opcx_bastore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.CHARACTER)) {
	opcode = opcx_castore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.SHORT)) {
	opcode = opcx_sastore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.INTEGER)) {
	opcode = opcx_iastore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.LONG)) {
	opcode = opcx_lastore;
	stackHeight -= 4;
      }
      else if (expr.elementType().equals(Type.FLOAT)) {
	opcode = opcx_fastore;
	stackHeight -= 3;
      }
      else if (expr.elementType().equals(Type.DOUBLE)) {
	opcode = opcx_dastore;
	stackHeight -= 4;
      }
      else {
	throw new IllegalArgumentException("Can't generate code for type: " + 
					   expr.type() +
					   " (expr " + expr + ")");
      }
    }
    else {
      if (expr.elementType().isReference()) {
	opcode = opcx_aaload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.BYTE)) {
	opcode = opcx_baload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.CHARACTER)) {
	opcode = opcx_caload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.SHORT)) {
	opcode = opcx_saload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.INTEGER)) {
	opcode = opcx_iaload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.LONG)) {
	opcode = opcx_laload;
	stackHeight -= 0;
      }
      else if (expr.elementType().equals(Type.FLOAT)) {
	opcode = opcx_faload;
	stackHeight -= 1;
      }
      else if (expr.elementType().equals(Type.DOUBLE)) {
	opcode = opcx_daload;
	stackHeight -= 0;
      }
      else {
	throw new IllegalArgumentException("Can't generate code for type: " + 
					   expr.type() +
					   " (expr " + expr + ")");
      }
    }

    method.addInstruction(opcode);
  }

  public void visitCallMethodExpr(CallMethodExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    int opcode;

    if (expr.kind() == CallMethodExpr.VIRTUAL) {
      opcode = opcx_invokevirtual;
    }
    else if (expr.kind() == CallMethodExpr.NONVIRTUAL) {
      opcode = opcx_invokespecial;
    }
    else if (expr.kind() == CallMethodExpr.INTERFACE) {
      opcode = opcx_invokeinterface;
    }
    else {
      throw new IllegalArgumentException();
    }

    method.addInstruction(opcode, expr.method());

    // Pop reciever object off stack
    stackHeight -= 1;

    // Pop each parameter off stack
    Expr[] params = expr.params();
    for(int i = 0; i < params.length; i++) {
      stackHeight -= params[i].type().stackHeight();
    }
  }

  public void visitCallStaticExpr(CallStaticExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    method.addInstruction(opcx_invokestatic, expr.method());

    // Pop each parameter off stack
    Expr[] params = expr.params();
    for(int i = 0; i < params.length; i++) {
      stackHeight -= params[i].type().stackHeight();
    }
  }

  public void visitCastExpr(CastExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    if (expr.castType().isReference()) {
      method.addInstruction(opcx_checkcast, expr.castType());
      return;
    }

    int opType = expr.expr().type().typeCode();
    int castType = expr.castType().typeCode();

    switch (opType) {
    case Type.BYTE_CODE:
    case Type.SHORT_CODE:
    case Type.CHARACTER_CODE:
    case Type.INTEGER_CODE:
      switch (castType) {
      case Type.BYTE_CODE:
	method.addInstruction(opcx_i2b);
	return;
      case Type.SHORT_CODE:
	method.addInstruction(opcx_i2s);
	return;
      case Type.CHARACTER_CODE:
	method.addInstruction(opcx_i2c);
	return;
      case Type.INTEGER_CODE:
	return;
      case Type.LONG_CODE:
	method.addInstruction(opcx_i2l);
	stackHeight += 1;
	return;
      case Type.FLOAT_CODE:
	method.addInstruction(opcx_i2f);
	return;
      case Type.DOUBLE_CODE:
	method.addInstruction(opcx_i2d);
	stackHeight += 1;
	return;
      }
      throw new IllegalArgumentException("Can't generate cast for type " +
                                         Type.getType(castType));
                                         //new Type(castType));

    case Type.LONG_CODE:
      switch (castType) {
      case Type.BYTE_CODE:
	method.addInstruction(opcx_l2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2b);
	return;
      case Type.SHORT_CODE:
	method.addInstruction(opcx_l2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2s);
	return;
      case Type.CHARACTER_CODE:
	method.addInstruction(opcx_l2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2c);
	return;
      case Type.INTEGER_CODE:
	method.addInstruction(opcx_l2i);
	stackHeight -= 1;
	return;
      case Type.LONG_CODE:
	return;
      case Type.FLOAT_CODE:
	method.addInstruction(opcx_l2f);
	stackHeight -= 1;
	return;
      case Type.DOUBLE_CODE:
	method.addInstruction(opcx_l2d);
	return;
      }

      throw new IllegalArgumentException("Can't generate cast for type " + 
					 Type.getType(castType));
					 //new Type(castType));

    case Type.FLOAT_CODE:
      switch (castType) {
      case Type.BYTE_CODE:
	method.addInstruction(opcx_f2i);
	method.addInstruction(opcx_i2b);
	return;
      case Type.SHORT_CODE:
	method.addInstruction(opcx_f2i);
	method.addInstruction(opcx_i2s);
	return;
      case Type.CHARACTER_CODE:
	method.addInstruction(opcx_f2i);
	method.addInstruction(opcx_i2c);
	return;
      case Type.INTEGER_CODE:
	method.addInstruction(opcx_f2i);
	return;
      case Type.LONG_CODE:
	method.addInstruction(opcx_f2l);
	stackHeight += 1;
	return;
      case Type.FLOAT_CODE:
	return;
      case Type.DOUBLE_CODE:
	method.addInstruction(opcx_f2d);
	stackHeight += 1;
	return;
      }

      throw new IllegalArgumentException("Can't generate cast for type " + 
					 Type.getType(castType));
					 //new Type(castType));

    case Type.DOUBLE_CODE:
      switch (castType) {
      case Type.BYTE_CODE:
	method.addInstruction(opcx_d2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2b);
	return;
      case Type.SHORT_CODE:
	method.addInstruction(opcx_d2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2s);
	return;
      case Type.CHARACTER_CODE:
	method.addInstruction(opcx_d2i);
	stackHeight -= 1;
	method.addInstruction(opcx_i2c);
	return;
      case Type.INTEGER_CODE:
	method.addInstruction(opcx_d2i);
	stackHeight -= 1;
	return;
      case Type.LONG_CODE:
	method.addInstruction(opcx_d2l);
	return;
      case Type.FLOAT_CODE:
	method.addInstruction(opcx_d2f);
	return;
      case Type.DOUBLE_CODE:
	return;
      }

      throw new IllegalArgumentException("Can't generate cast for type " + 
					 Type.getType(castType));
					 //new Type(castType));
    default:
      throw new IllegalArgumentException("Can't generate cast from type " + 
					 Type.getType(opType));
					 //new Type(castType));
    }
  }

  public void visitConstantExpr(ConstantExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    method.addInstruction(opcx_ldc, expr.value());
    stackHeight += expr.type().stackHeight();
  }

  public boolean nowb = false;

  public void visitFieldExpr(FieldExpr expr) {
    expr.visitChildren(this);
    genPostponed(expr);

    if (expr.isDef()) {

      boolean UC = false;      // Do we need an UC?

      // Look at the FieldExpr's object for a UCExpr
      Node check = expr.object();
      while(check instanceof CheckExpr) {
	if(check instanceof UCExpr) {
	  UC = true;
	  break;
	}

	CheckExpr c = (CheckExpr) check;
	check = c.expr();
      }

      // Do we need to perform the write barrier?
      if(!UC && USE_PERSISTENT) {
	/*
	System.out.println("Emitting a putfield_nowb in " + 
			   this.method.declaringClass().classInfo().name() + 
			   "." + this.method.name());
			   */
	nowb = true;

	//I commented out the next line because it generated a compiler
	// error, and I figured it was just about some unimportant
	// persistance stuff     --Tom
	//method.addInstruction(opcx_putfield_nowb, expr.field());

      } else {
	method.addInstruction(opcx_putfield, expr.field());

      }

      stackHeight -= 1;  // object
      stackHeight -= expr.type().stackHeight();

    } else {
      method.addInstruction(opcx_getfield, expr.field());
      stackHeight -= 1;  // pop object
      stackHeight += expr.type().stackHeight();
    }
  }

  public void visitInstanceOfExpr(InstanceOfExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);
    method.addInstruction(opcx_instanceof, expr.checkType());
  }

  public void visitLocalExpr(LocalExpr expr)
  {

    genPostponed(expr);

    boolean cat2 = expr.type().isWide();  // how many stack positions does
                               //this take up?


    int opcode = -1;  // -1 is the flag that it hasn't yet been assigned 
                     // a real value

    if (DB_OPT_STACK) 
	currentSO.infoDisplay(expr);
    
    if (expr.isDef()) {

	if (!OPT_STACK 
	    || currentSO.shouldStore(expr))  { 

	    if (expr.type().isAddress()) {
		opcode = opcx_astore;
		stackHeight -= 1;
	    }
	    else if (expr.type().isReference()) {
		opcode = opcx_astore;
		stackHeight -= 1;
	    }
	    else if (expr.type().isIntegral()) {
		opcode = opcx_istore;
		stackHeight -= 1;
	    }
	    else if (expr.type().equals(Type.LONG)) {
		opcode = opcx_lstore;
		stackHeight -= 2;
	    }
	    else if (expr.type().equals(Type.FLOAT)) {
		opcode = opcx_fstore;
		stackHeight -= 1;
	    }
	    else if (expr.type().equals(Type.DOUBLE)) {
		opcode = opcx_dstore;
		stackHeight -= 2;
	    }
	    else {
		throw new IllegalArgumentException(
						   "Can't generate code for type: " + expr.type() +
						   " (expr " + expr + ")");
	    }
	}  
    }

    else {

	if (OPT_STACK
	    && currentSO.onStack(expr)) {  //don't load if it's already on
         	    // the stack
	    if (currentSO.shouldSwap(expr)) {
		if (cat2) throw new IllegalArgumentException(
							     "Can't swap for wide expression " +
							     expr.toString() +
							     " of type " +
							     expr.type().toString());
		else  {
		    opcode = opc_swap;
		    stackHeight -= 1;
		}
	    }
	}
	else {

	    if (expr.type().isReference()) {
		opcode = opcx_aload;
		stackHeight += 1;
	    }
	    else if (expr.type().isIntegral()) {
		opcode = opcx_iload;
		stackHeight += 1;
	    }
	    else if (expr.type().equals(Type.LONG)) {
		opcode = opcx_lload;
		stackHeight += 2;
	    }
	    else if (expr.type().equals(Type.FLOAT)) {
		opcode = opcx_fload;
		stackHeight += 1;
	    }
	    else if (expr.type().equals(Type.DOUBLE)) {
		opcode = opcx_dload;
		stackHeight += 2;
	    }
	    else {
		throw new IllegalArgumentException(
						   "Can't generate code for type: " + expr.type() +
						   " (expr " + expr + ")");
	    }
	}
    }

	    
    if (opcode == opc_swap) method.addInstruction(opcode); // don't give
                                                     //a swap an operand
    else if (opcode != -1 && !(expr.isDef())) {  // if this is a load, we want
        	//the load before any dups.
	method.addInstruction(opcode, new LocalVariable(expr.index()));

	if (MethodEditor.OPT_STACK_2)   // remember the definition of this use
	    method.rememberDef(expr);

    }


    if (OPT_STACK) {   
	//generate dups for this value on top of the stack
	int dups, dup_x1s, dup_x2s;
	dups = currentSO.dups(expr);
	dup_x1s = currentSO.dup_x1s(expr);
	dup_x2s = currentSO.dup_x2s(expr);


	for (int i = 0; i < dup_x2s; i++)
	    if (cat2) {  //(cat2 is for wide types)
		method.addInstruction(opc_dup2_x2);
		stackHeight += 2;
	    }
	    else {
		method.addInstruction(opc_dup_x2);
		stackHeight += 1;
	    }
	for (int i = 0; i < dup_x1s; i++)
	    if (cat2) {
		method.addInstruction(opc_dup2_x1);
		stackHeight += 2;
	    }
	    else {
		method.addInstruction(opc_dup_x1);
		stackHeight += 1;
	    }
	for (int i = 0; i < dups; i++)
	    if (cat2) {
		method.addInstruction(opc_dup2);
		stackHeight += 2;
	    }
	    else {
		method.addInstruction(opc_dup);
		stackHeight += 1;
	    }
    }

    // if we have an opcode for a def (i.e., a store), generate it
    if (opcode != -1 && expr.isDef()) {
	    method.addInstruction(opcode, new LocalVariable(expr.index()));

	    if (MethodEditor.OPT_STACK_2)
		method.rememberDef(expr);

    }


    if (OPT_STACK                               // if we shouldn't store,
	&& !currentSO.shouldStore(expr)) {      // an extra thing will be
	if (cat2) {                             // on the stack. pop it
	    method.addInstruction(opc_pop2);
	    stackHeight -= 2;
	}
	else {
	    method.addInstruction(opc_pop);
	    stackHeight -= 1;
	}
    }
    // (if this leaves a useless dup/pop combination, let peephole fix it)
 
    //    method.addInstruction(opcode, new LocalVariable(expr.index()));
  }


  public void visitNegExpr(NegExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    if (expr.type().isIntegral()) {
      method.addInstruction(opcx_ineg);
    }
    else if (expr.type().equals(Type.FLOAT)) {
      method.addInstruction(opcx_fneg);
    }
    else if (expr.type().equals(Type.LONG)) {
      method.addInstruction(opcx_lneg);
    }
    else if (expr.type().equals(Type.DOUBLE)) {
      method.addInstruction(opcx_dneg);
    }
    else {
      throw new IllegalArgumentException(
					 "Can't generate code for type: " + expr.type() +
					 " (expr " + expr + ")");
    }
  }

  public void visitNewArrayExpr(NewArrayExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    method.addInstruction(opcx_newarray, expr.elementType());
  }

  public void visitNewExpr(NewExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    method.addInstruction(opcx_new, expr.objectType());
    stackHeight += 1;
  }

  public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    method.addInstruction(opcx_multianewarray,
			  new MultiArrayOperand(
						expr.elementType().arrayType(expr.dimensions().length),
						expr.dimensions().length));
    stackHeight -= expr.dimensions().length;
    stackHeight += 1;
  }

  public void visitReturnAddressExpr(ReturnAddressExpr expr)
  {
    genPostponed(expr);
  }

  public void visitShiftExpr(ShiftExpr expr)
  {
    expr.visitChildren(this);

    genPostponed(expr);

    if (expr.type().isIntegral()) {
      if (expr.dir() == ShiftExpr.LEFT) {
	method.addInstruction(opcx_ishl);
	stackHeight -= 1;
      }
      else if (expr.dir() == ShiftExpr.RIGHT) {
	method.addInstruction(opcx_ishr);
	stackHeight -= 1;
      }
      else {
	method.addInstruction(opcx_iushr);
	stackHeight -= 1;
      }
    }
    else if (expr.type().equals(Type.LONG)) {
      if (expr.dir() == ShiftExpr.LEFT) {
	method.addInstruction(opcx_lshl);
	stackHeight -= 1;
      }
      else if (expr.dir() == ShiftExpr.RIGHT) {
	method.addInstruction(opcx_lshr);
	stackHeight -= 1;
      }
      else {
	method.addInstruction(opcx_lushr);
	stackHeight -= 1;
      }
    }
    else {
      throw new IllegalArgumentException(
					 "Can't generate code for type: " + expr.type() +
					 " (expr " + expr + ")");
    }
  }

  public void visitDefExpr(DefExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);
  }

  public void visitCatchExpr(CatchExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);
  }

  public void visitStackExpr(StackExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);
  }

  public void visitStaticFieldExpr(StaticFieldExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);

    if (expr.isDef()) {
      method.addInstruction(opcx_putstatic, expr.field());
      stackHeight -= expr.type().stackHeight();
    }
    else {
      method.addInstruction(opcx_getstatic, expr.field());
      stackHeight += expr.type().stackHeight();
    }
  }

  public void visitZeroCheckExpr(ZeroCheckExpr expr)
  {
    expr.visitChildren(this);
    genPostponed(expr);
  }

  private void genPostponed(Node node)
  {
    Instruction inst = (Instruction) postponedInstructions.get(node);

    if (inst != null) {
      method.addInstruction(inst);
      // Luckily, the rc and aupdate don't change the stack!
      postponedInstructions.remove(node);
    }
  }
}
