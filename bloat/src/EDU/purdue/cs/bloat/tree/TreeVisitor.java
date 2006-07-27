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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * TreeVisitor performs a traversal of a tree. It does so by having a method of
 * every kind of node in the tree. This abstract class performs default
 * operations for each kind of node visited. It must be subclasses to perform a
 * more interesting traversal.
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
		this(TreeVisitor.FORWARD);
	}

	public TreeVisitor(final int direction) {
		this.direction = direction;
	}

	/**
	 * @param prune
	 *            Is the tree pruned during traversal?
	 */
	public void setPrune(final boolean prune) {
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
		return direction == TreeVisitor.FORWARD;
	}

	public boolean reverse() {
		return direction == TreeVisitor.REVERSE;
	}

	public void visitFlowGraph(final FlowGraph graph) {
		graph.visitChildren(this);
	}

	public void visitBlock(final Block block) {
		block.visitChildren(this);
	}

	public void visitTree(final Tree tree) {
		visitNode(tree);
	}

	public void visitExprStmt(final ExprStmt stmt) {
		visitStmt(stmt);
	}

	public void visitIfStmt(final IfStmt stmt) {
		visitStmt(stmt);
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {
		visitIfStmt(stmt);
	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {
		visitIfStmt(stmt);
	}

	public void visitInitStmt(final InitStmt stmt) {
		visitStmt(stmt);
	}

	public void visitGotoStmt(final GotoStmt stmt) {
		visitStmt(stmt);
	}

	public void visitLabelStmt(final LabelStmt stmt) {
		visitStmt(stmt);
	}

	public void visitMonitorStmt(final MonitorStmt stmt) {
		visitStmt(stmt);
	}

	public void visitPhiStmt(final PhiStmt stmt) {
		visitStmt(stmt);
	}

	public void visitCatchExpr(final CatchExpr expr) {
		visitExpr(expr);
	}

	public void visitDefExpr(final DefExpr expr) {
		visitExpr(expr);
	}

	public void visitStackManipStmt(final StackManipStmt stmt) {
		visitStmt(stmt);
	}

	public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
		visitPhiStmt(stmt);
	}

	public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
		visitPhiStmt(stmt);
	}

	public void visitRetStmt(final RetStmt stmt) {
		visitStmt(stmt);
	}

	public void visitReturnExprStmt(final ReturnExprStmt stmt) {
		visitStmt(stmt);
	}

	public void visitReturnStmt(final ReturnStmt stmt) {
		visitStmt(stmt);
	}

	public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
		visitStmt(stmt);
	}

	public void visitStoreExpr(final StoreExpr expr) {
		visitExpr(expr);
	}

	public void visitJsrStmt(final JsrStmt stmt) {
		visitStmt(stmt);
	}

	public void visitSwitchStmt(final SwitchStmt stmt) {
		visitStmt(stmt);
	}

	public void visitThrowStmt(final ThrowStmt stmt) {
		visitStmt(stmt);
	}

	public void visitStmt(final Stmt stmt) {
		visitNode(stmt);
	}

	public void visitSCStmt(final SCStmt stmt) {
		visitStmt(stmt);
	}

	public void visitSRStmt(final SRStmt stmt) {
		visitStmt(stmt);
	}

	public void visitArithExpr(final ArithExpr expr) {
		visitExpr(expr);
	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
		visitExpr(expr);
	}

	public void visitMemExpr(final MemExpr expr) {
		visitDefExpr(expr);
	}

	public void visitMemRefExpr(final MemRefExpr expr) {
		visitMemExpr(expr);
	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {
		visitMemRefExpr(expr);
	}

	public void visitCallExpr(final CallExpr expr) {
		visitExpr(expr);
	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {
		visitCallExpr(expr);
	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {
		visitCallExpr(expr);
	}

	public void visitCastExpr(final CastExpr expr) {
		visitExpr(expr);
	}

	public void visitConstantExpr(final ConstantExpr expr) {
		visitExpr(expr);
	}

	public void visitFieldExpr(final FieldExpr expr) {
		visitMemRefExpr(expr);
	}

	public void visitInstanceOfExpr(final InstanceOfExpr expr) {
		visitExpr(expr);
	}

	public void visitLocalExpr(final LocalExpr expr) {
		visitVarExpr(expr);
	}

	public void visitNegExpr(final NegExpr expr) {
		visitExpr(expr);
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		visitExpr(expr);
	}

	public void visitNewExpr(final NewExpr expr) {
		visitExpr(expr);
	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		visitExpr(expr);
	}

	public void visitCheckExpr(final CheckExpr expr) {
		visitExpr(expr);
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		visitCheckExpr(expr);
	}

	public void visitRCExpr(final RCExpr expr) {
		visitCheckExpr(expr);
	}

	public void visitUCExpr(final UCExpr expr) {
		visitCheckExpr(expr);
	}

	public void visitReturnAddressExpr(final ReturnAddressExpr expr) {
		visitExpr(expr);
	}

	public void visitShiftExpr(final ShiftExpr expr) {
		visitExpr(expr);
	}

	public void visitStackExpr(final StackExpr expr) {
		visitVarExpr(expr);
	}

	public void visitVarExpr(final VarExpr expr) {
		visitMemExpr(expr);
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {
		visitMemRefExpr(expr);
	}

	public void visitExpr(final Expr expr) {
		visitNode(expr);
	}

	public void visitNode(final Node node) {
		node.visitChildren(this);
	}
}
