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

package EDU.purdue.cs.bloat.trans;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.util.*;
import java.util.*;

/**  
 * DeadCodeElimination performs SSA-based dead code elimination as
 * described in [Cytron, et. al. 91].  The idea behind dead code
 * elimination is that there are some instructions that do not
 * contribute anything useful to the result of the program.  Most dead
 * code is introduced by other optimizations.
 *
 * A program statement is live if one or more of the following holds:
 *
 * <ol>
 *
 * <li>The statement effects program output.  In Java this is a lot
 * more than just I/O.  We must be conservative and assume that
 * exceptions and monitor expression are always live. 
 *
 * <li>The statement is an assignment statement whose target is used
 * in a live statement.
 *
 * <li>The statement is a conditional branch and there are live
 * statements whose execution depend on the conditional branch.
 *
 * <ol>
 *
 * Basically, the algorithm proceeds by marking a number of statements
 * as being pre-live and then uses a worklist to determine which
 * statements must also be live by the above three conditions.  
 */
public class DeadCodeElimination {
  public static boolean DEBUG = false;

  private static final int DEAD = 0;
  private static final int LIVE = 1;

  FlowGraph cfg;

  /**
   * Constructor.
   */
  public DeadCodeElimination(FlowGraph cfg) {
    this.cfg = cfg;
  }

  // Keep a work list of expressions that need to be made live.
  LinkedList worklist;      

  /**
   * Performs dead code elimination.
   */
  public void transform() {
    // Mark all nodes in the tree as DEAD.
    cfg.visit(new TreeVisitor() {
      public void visitNode(Node node) {
	node.visitChildren(this);
	node.setKey(DEAD);
      }
    });

    worklist = new LinkedList();

    // Visit the nodes in the tree and mark nodes that we know must be
    // LIVE.
    cfg.visit(new TreeVisitor() {
      public void visitMonitorStmt(MonitorStmt stmt) {
	// NullPointerException, IllegalMonitorStateException
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitInitStmt(InitStmt stmt) {
	// Needed to correctly initialize the formal parameters when
	// coloring
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitJsrStmt(JsrStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitAddressStoreStmt(AddressStoreStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitRetStmt(RetStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitSRStmt(SRStmt stmt) {
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }
	    
      public void visitSCStmt(SCStmt stmt) {
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitNewMultiArrayExpr(NewMultiArrayExpr expr) {
	// Memory allocation
	// NegativeArraySizeException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitNewArrayExpr(NewArrayExpr expr) {
	// Memory allocation
	// NegativeArraySizeException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitNewExpr(NewExpr expr) {
	// Memory allocation
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitStackExpr(StackExpr expr) {
	if (expr.stmt() instanceof PhiStmt) {
	  return;
	}

	// Stack change
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitZeroCheckExpr(ZeroCheckExpr expr) {
	// NullPointerException or DivideByZeroException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitRCExpr(RCExpr expr) {
	// Residency check
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitUCExpr(UCExpr expr) {
	// Update check
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitCastExpr(CastExpr expr) {
	// ClassCastException
	if (expr.castType().isReference()) {
	  if (DEBUG) {
	    System.out.println(expr + " is prelive");
	  }

	  makeLive(expr);
	}
	else {
	  expr.visitChildren(this);
	}
      }

      public void visitArithExpr(ArithExpr expr) {
	// DivideByZeroException
	if (expr.operation() == ArithExpr.DIV ||
	    expr.operation() == ArithExpr.REM) {

	  if (expr.type().isIntegral()) {
	    if (DEBUG) {
	      System.out.println(expr + " is prelive");
	    }

	    makeLive(expr);
	    return;
	  }
	}

	expr.visitChildren(this);
      }

      public void visitArrayLengthExpr(ArrayLengthExpr expr) {
	// NullPointerException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitArrayRefExpr(ArrayRefExpr expr) {
	// NullPointerException, ArrayIndexOutOfBoundsException,
	// ArrayStoreException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitFieldExpr(FieldExpr expr) {
	// NullPointerException
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitCallStaticExpr(CallStaticExpr expr) {
	// Call
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitCallMethodExpr(CallMethodExpr expr) {
	// Call
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitCatchExpr(CatchExpr expr) {
	// Stack change
	if (DEBUG) {
	  System.out.println(expr + " is prelive");
	}

	makeLive(expr);
      }

      public void visitStackManipStmt(StackManipStmt stmt) {
	// Stack change
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitThrowStmt(ThrowStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitSwitchStmt(SwitchStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitIfStmt(IfStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitGotoStmt(GotoStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitReturnStmt(ReturnStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitReturnExprStmt(ReturnExprStmt stmt) {
	// Branch
	if (DEBUG) {
	  System.out.println(stmt + " is prelive");
	}

	makeLive(stmt);
      }

      public void visitStoreExpr(StoreExpr expr) {
	// Can change a variable visible outside the method.
	if (! (expr.target() instanceof LocalExpr)) {
	  if (DEBUG) {
	    System.out.println(expr + " is prelive");
	  }

	  makeLive(expr);

	} else {
	  expr.visitChildren(this);
	}
      }
    });

    // Go through the nodes in the worklist and make the nodes that
    // defined the VarExprs live.
    while (! worklist.isEmpty()) {
      VarExpr expr = (VarExpr) worklist.removeFirst();

      DefExpr def = expr.def();

      if (def != null) {
	if (DEBUG) {
	  System.out.println("making live def of " + expr);
	  System.out.println("    def = " + def);
	}

	makeLive(def.parent());
      }
    }

    // Remove dead stores.
    cfg.visit(new TreeVisitor() {
      public void visitStoreExpr(StoreExpr expr) {
	Expr lhs = expr.target();
	Expr rhs = expr.expr();

	if (lhs.key() == DEAD && rhs.key() == LIVE) {
	  rhs.setParent(null);
	  expr.replaceWith(rhs, false);

	  lhs.cleanup();
	  expr.cleanupOnly();

	  lhs.setKey(DEAD);
	  expr.setKey(DEAD);

	  rhs.visit(this);

	} else {
	  expr.visitChildren(this);
	}
      }
    });

    // Pull out live expressions from their dead parents.  Gee, Nate,
    // what a lovely sentiment.  I'll think I'll send that one to
    // Hallmark.
    cfg.visit(new TreeVisitor() {
      public void visitStmt(Stmt stmt) {
	if (stmt.key() == DEAD) {
	  stmt.visitChildren(this);
	}
      }

      public void visitExpr(Expr expr) {
	if (expr.key() == DEAD) {
	  expr.visitChildren(this);
	  return;
	}

	Node parent = expr.parent();

	if (parent.key() == LIVE) {
	  // expr will removed later
	  return;
	}

	if (parent instanceof ExprStmt) {
	  // The expr and its parent are both dead, but expr resides
	  // in an ExprStmt.  We want the parent after all.
	  parent.setKey(LIVE);
	  return;
	}

	// We are going to remove the expr's parent, but keep the
	// expr.  Add eval(expr) [ExprStmt] before the stmt containing
	// the expr.  This is safe, since any exprs to the left in the
	// statement's tree which are live have already been
	// extracted.

	Stmt oldStmt = expr.stmt();

	Tree tree = parent.block().tree();

	// Replace the expr with an unused stack expr.
	StackExpr t = tree.newStack(expr.type());
	expr.replaceWith(t, false);
	t.setValueNumber(expr.valueNumber());

	ExprStmt stmt = new ExprStmt(expr);
	stmt.setValueNumber(expr.valueNumber());
	stmt.setKey(LIVE);

	tree.addStmtBefore(stmt, oldStmt);

	// The old statement is dead and will be removed later.
	Assert.isTrue(oldStmt.key() == DEAD,
		      oldStmt + " should be dead");
      }
    });

    // Finally, remove the dead statements from the Tree.
    cfg.visit(new TreeVisitor() {
      public void visitTree(Tree tree) {
	Iterator e = tree.stmts().iterator();

	while (e.hasNext()) {
	  Stmt stmt = (Stmt) e.next();

	  if (stmt.key() == DEAD) {
	    if (stmt instanceof LabelStmt) {
	      continue;
	    }

	    if (stmt instanceof JumpStmt) {
	      continue;
	    }

	    if (DEBUG) {
	      System.out.println("Removing DEAD " + stmt);
	    }

	    e.remove();
	  }
	}
      }
    });

    worklist = null;
  }

//    /**
//     * Make all of a statement's children LIVE.  I don't think its used.
//     *
//     * @param stmt
//     *        A statement whose children to make live.
//     */
//    void reviveStmt(Stmt stmt) {
//      stmt.visit(new TreeVisitor() {
//        public void visitExpr(Expr expr) {
//  	expr.setKey(LIVE);
//  	expr.visitChildren(this);
//        }
//      });
//    }

  /**
   * Make a node and all of its children (recursively) LIVE.
   *
   * @param node
   *        A node to make LIVE.
   */
  void makeLive(Node node) {

    if (node instanceof StoreExpr) {
      // Make the StoreExpr, its target, and its RHS live.  Add the
      // target and the RHS to the worklist.

      StoreExpr expr = (StoreExpr) node;

      if (expr.key() == DEAD) {
	if (DEBUG) {
	  System.out.println("making live " + expr +
			     " in " + expr.parent());
	}

	expr.setKey(LIVE);
      }

      if (expr.target().key() == DEAD) {
	if (DEBUG) {
	  System.out.println("making live " + expr.target() +
			     " in " + expr);
	}

	expr.target().setKey(LIVE);

	if (expr.target() instanceof VarExpr) {
	  worklist.add(expr.target());
	}
      }

      if (expr.expr().key() == DEAD) {
	if (DEBUG) {
	  System.out.println("making live " + expr.expr() +
			     " in " + expr);
	}

	expr.expr().setKey(LIVE);

	if (expr.expr() instanceof VarExpr) {
	  worklist.add(expr.expr());
	}
      }
    }

    if (node instanceof Expr) {
      // If one expression inside an ExprStmt is live, then the entire
      // ExprStmt is live.
      Node parent = ((Expr) node).parent();

      if (parent instanceof ExprStmt) {
	node = parent;
      }
    }

    node.visit(new TreeVisitor() {
      public void visitStoreExpr(StoreExpr expr) {
	// Don't make local variable targets live yet.  If the
	// variable is used in a live expression, the target will be
	// made live later.
	if (expr.target() instanceof LocalExpr) {
	  expr.expr().visit(this);

	} else {
	  visitExpr(expr);
	}
      }

      public void visitVarExpr(VarExpr expr) {
	if (expr.key() == DEAD) {
	  if (DEBUG) {
	    System.out.println("making live " + expr +
			       " in " + expr.parent());
	  }

	  expr.setKey(LIVE);
	  worklist.add(expr);
	}
      }

      public void visitExpr(Expr expr) {
	if (expr.key() == DEAD) {
	  if (DEBUG) {
	    System.out.println("making live " + expr +
			       " in " + expr.parent());
	  }

	  expr.setKey(LIVE);
	}

	expr.visitChildren(this);
      }

      public void visitStmt(Stmt stmt) {
	if (stmt.key() == DEAD) {
	  if (DEBUG) {
	    System.out.println("making live " + stmt);
	  }

	  stmt.setKey(LIVE);
	}

	stmt.visitChildren(this);
      }
    });
  }
}
