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
import EDU.purdue.cs.bloat.util.*;
import java.io.*;
import java.util.*;

/**
 * TreeVisitor performs a traversal of a tree.  It does so by having a method
 * of every kind of node in the tree.  This abstract class performs default
 * operations for each kind of node visited.  It must be subclasses to perform
 * a more interesting traversal.
 *
 * @see Node
 * @see Tree
 *
 * @see PrintVisitor
 * @see ReplaceVisitor
 *
 */
public abstract class TreeVisitor {
  public static final int FORWARD = 0;
  public static final int REVERSE = 1;
  
  boolean prune;
  int direction;
  
  public TreeVisitor() {
    this(FORWARD);
  }
  
  public TreeVisitor(int direction) {
    this.direction = direction;
  }

  /**
   * @param prune
   *        Is the tree pruned during traversal?
   */  
  public void setPrune(boolean prune) {
    this.prune = prune;
  }
  
  public boolean prune() {
    return prune;
  }
  
  /**
   * @return The direction in which the tree is traversed.
   */
  public int direction() {
    return direction;
  }
  
  /**
   * Returns <tt>true</tt> if the traversal traverses in the forward
   * direction?  
   */
  public boolean forward() {
    return direction == FORWARD;
  }
  
  public boolean reverse() {
    return direction == REVERSE;
  }
  
  public void visitFlowGraph(FlowGraph graph)
  {
    graph.visitChildren(this);
  }
  
  public void visitBlock(Block block)
  {
    block.visitChildren(this);
  }
  
  public void visitTree(Tree tree)
  {
    visitNode(tree);
  }
  
  public void visitExprStmt(ExprStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitIfStmt(IfStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitIfCmpStmt(IfCmpStmt stmt)
  {
    visitIfStmt(stmt);
  }
  
  public void visitIfZeroStmt(IfZeroStmt stmt)
  {
    visitIfStmt(stmt);
  }
  
  public void visitInitStmt(InitStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitGotoStmt(GotoStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitLabelStmt(LabelStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitMonitorStmt(MonitorStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitPhiStmt(PhiStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitCatchExpr(CatchExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitDefExpr(DefExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitStackManipStmt(StackManipStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitPhiCatchStmt(PhiCatchStmt stmt)
  {
    visitPhiStmt(stmt);
  }
  
  public void visitPhiJoinStmt(PhiJoinStmt stmt)
  {
    visitPhiStmt(stmt);
  }
  
  public void visitRetStmt(RetStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitReturnExprStmt(ReturnExprStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitReturnStmt(ReturnStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitAddressStoreStmt(AddressStoreStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitStoreExpr(StoreExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitJsrStmt(JsrStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitSwitchStmt(SwitchStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitThrowStmt(ThrowStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitStmt(Stmt stmt)
  {
    visitNode(stmt);
  }
  
  public void visitSCStmt(SCStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitSRStmt(SRStmt stmt)
  {
    visitStmt(stmt);
  }
  
  public void visitArithExpr(ArithExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitArrayLengthExpr(ArrayLengthExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitMemExpr(MemExpr expr)
  {
    visitDefExpr(expr);
  }
  
  public void visitMemRefExpr(MemRefExpr expr)
  {
    visitMemExpr(expr);
  }
  
  public void visitArrayRefExpr(ArrayRefExpr expr)
  {
    visitMemRefExpr(expr);
  }
  
  public void visitCallExpr(CallExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitCallMethodExpr(CallMethodExpr expr)
  {
    visitCallExpr(expr);
  }
  
  public void visitCallStaticExpr(CallStaticExpr expr)
  {
    visitCallExpr(expr);
  }
  
  public void visitCastExpr(CastExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitConstantExpr(ConstantExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitFieldExpr(FieldExpr expr)
  {
    visitMemRefExpr(expr);
  }
  
  public void visitInstanceOfExpr(InstanceOfExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitLocalExpr(LocalExpr expr)
  {
    visitVarExpr(expr);
  }
  
  public void visitNegExpr(NegExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitNewArrayExpr(NewArrayExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitNewExpr(NewExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)
    {
      visitExpr(expr);
    }
  
  public void visitCheckExpr(CheckExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitZeroCheckExpr(ZeroCheckExpr expr)
  {
    visitCheckExpr(expr);
  }
  
  public void visitRCExpr(RCExpr expr)
  {
    visitCheckExpr(expr);
  }
  
  public void visitUCExpr(UCExpr expr)
  {
    visitCheckExpr(expr);
  }
  
  public void visitReturnAddressExpr(ReturnAddressExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitShiftExpr(ShiftExpr expr)
  {
    visitExpr(expr);
  }
  
  public void visitStackExpr(StackExpr expr)
  {
    visitVarExpr(expr);
  }
  
  public void visitVarExpr(VarExpr expr)
  {
    visitMemExpr(expr);
  }
  
  public void visitStaticFieldExpr(StaticFieldExpr expr)
  {
    visitMemRefExpr(expr);
  }
  
  public void visitExpr(Expr expr)
  {
    visitNode(expr);
  }
  
  public void visitNode(Node node)
  {
    node.visitChildren(this);
  }
}
