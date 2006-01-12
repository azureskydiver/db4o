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

package EDU.purdue.cs.bloat.tree;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;
import java.io.*;
import java.util.*;

/**
 * ReplaceVisitor traverses a tree and replaces each occurrence of one 
 * Node with another Node.
 */
public class ReplaceVisitor extends TreeVisitor {
  Node from;
  Node to;
  
  /**
   * Constructor.
   *
   * @param from
   *        The "old" Node.
   * @param to
   *        The "new" Node.
   */
  public ReplaceVisitor(Node from, Node to) {
    this.from = from;
    this.to = to;
    
    if (Tree.DEBUG) {
      System.out.println("replace " + from +
			 " VN=" + from.valueNumber() + " in " + from.parent +
			 " with " + to);
    }
  }
  
  public void visitTree(Tree tree) {
    if (to instanceof Stmt) {
      ((Stmt) to).setParent(tree);
      
      // The most common statement replacement is the last statement.
      // so search from the end of the statement list. 
      ListIterator iter = tree.stmts.listIterator(tree.stmts.size());
      
      while (iter.hasPrevious()) {
	Stmt s = (Stmt) iter.previous();
	if (s == from) {
	  iter.set(to);
	  break;
	}
      }
    }
    else {
      tree.visitChildren(this);
    }
  }
  
  public void visitExprStmt(ExprStmt stmt)
  {
    if (stmt.expr == from) {
      stmt.expr = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitInitStmt(InitStmt stmt)
  {
    for (int i = 0; i < stmt.targets.length; i++) {
      if (stmt.targets[i] == from) {
	stmt.targets[i] = (LocalExpr) to;
	((LocalExpr) to).setParent(stmt);
	return;
      }
    }
    
    stmt.visitChildren(this);
  }
  
  public void visitGotoStmt(GotoStmt stmt)
  {
    stmt.visitChildren(this);
  }
  
  public void visitMonitorStmt(MonitorStmt stmt)
  {
    if (stmt.object == from) {
      stmt.object = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitStackManipStmt(StackManipStmt stmt)
  {
    for (int i = 0; i < stmt.target.length; i++) {
      if (stmt.target[i] == from) {
	stmt.target[i] = (StackExpr) to;
	((Expr) to).setParent(stmt);
	return;
      }
    }
    
    for (int i = 0; i < stmt.source.length; i++) {
      if (stmt.source[i] == from) {
	stmt.source[i] = (StackExpr) to;
	((Expr) to).setParent(stmt);
	return;
      }
    }
    
    stmt.visitChildren(this);
  }
  
  public void visitCatchExpr(CatchExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitPhiJoinStmt(PhiJoinStmt stmt)
  {
    if (stmt.target == from) {
      stmt.target = (VarExpr) to;
      ((VarExpr) to).setParent(stmt);
    }
    else {
      Iterator e = stmt.operands.keySet().iterator();
      
      while (e.hasNext()) {
	Block block = (Block) e.next();
	
	if (stmt.operandAt(block) == from) {
	  stmt.setOperandAt(block, (Expr) to);
	  ((Expr) to).setParent(stmt);
	  return;
	}
      }
      
      stmt.visitChildren(this);
    }
  }
  
  public void visitPhiCatchStmt(PhiCatchStmt stmt)
  {
    if (stmt.target == from) {
      stmt.target = (LocalExpr) to;
      ((LocalExpr) to).setParent(stmt);
    }
    else {
      ListIterator e = stmt.operands.listIterator();
      
      while (e.hasNext()) {
	LocalExpr expr = (LocalExpr) e.next();
	
	if (expr == from) {
	  e.set(to);
	  from.cleanup();
	  ((LocalExpr) to).setParent(stmt);
	  return;
	}
      }
      
      stmt.visitChildren(this);
    }
  }
  
  public void visitRetStmt(RetStmt stmt)
  {
    stmt.visitChildren(this);
  }
  
  public void visitReturnExprStmt(ReturnExprStmt stmt)
  {
    if (stmt.expr == from) {
      stmt.expr = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitReturnStmt(ReturnStmt stmt)
  {
    stmt.visitChildren(this);
  }
  
  public void visitAddressStoreStmt(AddressStoreStmt stmt)
  {
    stmt.visitChildren(this);
  }
  
  public void visitStoreExpr(StoreExpr expr)
  {
    if (expr.target == from) {
      expr.target = (MemExpr) to;
      ((MemExpr) to).setParent(expr);
    }
    else if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitSwitchStmt(SwitchStmt stmt)
  {
    if (stmt.index == from) {
      stmt.index = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitThrowStmt(ThrowStmt stmt)
  {
    if (stmt.expr == from) {
      stmt.expr = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitSCStmt(SCStmt stmt)
  {
    if (stmt.array == from) {
      stmt.array = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else if (stmt.index == from) {
      stmt.index = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitSRStmt(SRStmt stmt)
  {
    if (stmt.array == from) {
      stmt.array = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else if (stmt.start == from) {
      stmt.start = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else if (stmt.end == from) {
      stmt.end = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitDefExpr(DefExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitArrayLengthExpr(ArrayLengthExpr expr)
  {
    if (expr.array == from) {
      expr.array = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitArithExpr(ArithExpr expr)
  {
    if (expr.left == from) {
      expr.left = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else if (expr.right == from) {
      expr.right = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitArrayRefExpr(ArrayRefExpr expr)
  {
    if (expr.array == from) {
      expr.array = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else if (expr.index == from) {
      expr.index = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitCallMethodExpr(CallMethodExpr expr)
  {
    if (expr.receiver == from) {
      expr.receiver = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      for (int i = 0; i < expr.params.length; i++) {
	if (expr.params[i] == from) {
	  expr.params[i] = (Expr) to;
	  ((Expr) to).setParent(expr);
	  return;
	}
      }
      
      expr.visitChildren(this);
    }
  }
  
  public void visitCallStaticExpr(CallStaticExpr expr)
  {
    for (int i = 0; i < expr.params.length; i++) {
      if (expr.params[i] == from) {
	expr.params[i] = (Expr) to;
	((Expr) to).setParent(expr);
	return;
      }
    }
    
    expr.visitChildren(this);
  }
  
  public void visitCastExpr(CastExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitConstantExpr(ConstantExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitFieldExpr(FieldExpr expr)
  {
    if (expr.object == from) {
      expr.object = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitInstanceOfExpr(InstanceOfExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitLocalExpr(LocalExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitNegExpr(NegExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitNewArrayExpr(NewArrayExpr expr)
  {
    if (expr.size == from) {
      expr.size = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitNewExpr(NewExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)
  {
    for (int i = 0; i < expr.dimensions.length; i++) {
      if (expr.dimensions[i] == from) {
	expr.dimensions[i] = (Expr) to;
	((Expr) to).setParent(expr);
	return;
      }
    }
    
    expr.visitChildren(this);
  }
  
  public void visitIfZeroStmt(IfZeroStmt stmt)
  {
    if (stmt.expr == from) {
      stmt.expr = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitIfCmpStmt(IfCmpStmt stmt)
  {
    if (stmt.left == from) {
      stmt.left = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else if (stmt.right == from) {
      stmt.right = (Expr) to;
      ((Expr) to).setParent(stmt);
    }
    else {
      stmt.visitChildren(this);
    }
  }
  
  public void visitReturnAddressExpr(ReturnAddressExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitShiftExpr(ShiftExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else if (expr.bits == from) {
      expr.bits = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitZeroCheckExpr(ZeroCheckExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitRCExpr(RCExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitUCExpr(UCExpr expr)
  {
    if (expr.expr == from) {
      expr.expr = (Expr) to;
      ((Expr) to).setParent(expr);
    }
    else {
      expr.visitChildren(this);
    }
  }
  
  public void visitStackExpr(StackExpr expr)
  {
    expr.visitChildren(this);
  }
  
  public void visitStaticFieldExpr(StaticFieldExpr expr)
  {
    expr.visitChildren(this);
  }
}
