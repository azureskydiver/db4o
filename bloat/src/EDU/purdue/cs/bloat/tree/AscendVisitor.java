/*
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

import java.util.*;


/** 
 * AscendVisitor is the superclass of Type0Visitor and Type1Visitor,
 * conveniently containing the common code. It makes an upward
 * traversal of the tree as if it were a binary tree (nodes with
 * more than two children, such as a method call, are considered in
 * a form similar to curried form).
 * @author Thomas VanDrunen
 */

public abstract class AscendVisitor extends TreeVisitor {

    Hashtable defInfoMap;  /* the same as the fields of Stack Optimizer */
    Hashtable useInfoMap;  /* of the same name */
    LocalExpr start;       /* where we start the search from */
    Node previous;

    Vector visited;

    public AscendVisitor(Hashtable defInfoMap, Hashtable useInfoMap) {
	this.defInfoMap = defInfoMap;
	this.useInfoMap = useInfoMap;

	visited = new Vector();
    }


    abstract public void check(Node node);

    public void visitTree(Tree tree) {


	ListIterator iter = tree.stmts()
	    .listIterator(tree.stmts().lastIndexOf(previous));
	
	if (iter.hasPrevious()) {
	    Stmt p = (Stmt) iter.previous();
	    check(p); 
	}
	/*
	  Object prev = iter.previous();
	  if (prev instanceof LocalExpr)
  	    check(prev);
	*/
    } 
  
    public void visitExprStmt(ExprStmt stmt) {

	previous = stmt;
	stmt.parent().visit(this);
    }
  
    public void visitIfCmpStmt(IfCmpStmt stmt) {

	if (stmt.right() == previous)
	    check(stmt.left());
	else if (stmt.left() == previous) {
	    previous = stmt;
	    stmt.parent().visit(this);
	}
    }
  
    public void visitIfZeroStmt(IfZeroStmt stmt) {

	previous = stmt;
	stmt.parent.visit(this);
    }
  
    public void visitInitStmt(InitStmt stmt) {

	LocalExpr[] targets = stmt.targets();
	for (int i = 0; i < targets.length; i++)
	    if (targets[i] == previous)
		if (i > 0) check(targets[i - 1]);
		else break;
    }

  
    public void visitGotoStmt(GotoStmt stmt) {
    
    }
  
    public void visitLabelStmt(LabelStmt stmt) {

    }
  
    public void visitMonitorStmt(MonitorStmt stmt) {

	previous = stmt;
	stmt.parent().visit(this);
    }
  
    public void visitPhiStmt(PhiStmt stmt) {

	if (stmt instanceof PhiCatchStmt)
	    visitPhiCatchStmt((PhiCatchStmt) stmt);
	else if (stmt instanceof PhiJoinStmt)
	    visitPhiJoinStmt((PhiJoinStmt) stmt);
	/*
	else if (stmt instanceof PhiReturnStmt)
	    visitPhiReturnStmt((PhiReturnStmt) stmt);
	*/
    }
  
    public void visitCatchExpr(CatchExpr expr) {

    }
  
    public void visitDefExpr(DefExpr expr) {
	if (expr instanceof MemExpr)
	    visitMemExpr((MemExpr) expr);
    }
  
    public void visitStackManipStmt(StackManipStmt stmt) {
	
    }
  
    public void visitPhiCatchStmt(PhiCatchStmt stmt) {

    }
  
    public void visitPhiJoinStmt(PhiJoinStmt stmt) {

    }
  
    public void visitRetStmt(RetStmt stmt) {
	
    }
  
    public void visitReturnExprStmt(ReturnExprStmt stmt) {


	previous = stmt;
	stmt.parent.visit(this);
    }
  
    public void visitReturnStmt(ReturnStmt stmt) {

    }
  
    public void visitAddressStoreStmt(AddressStoreStmt stmt) {

    }
  
    public void visitStoreExpr(StoreExpr expr) {


	if (expr.target() instanceof LocalExpr
	    || expr.target() instanceof StaticFieldExpr) {
	    if (previous == expr.expr()) {  // can't be target, because then
		   //it would be a definition, for which
		   //Type0Visitor is not called
		previous = expr;
		expr.parent.visit(this);
	    }
	}

	else if (expr.target() instanceof ArrayRefExpr) {
	    if (previous == expr.expr())
		check(((ArrayRefExpr) expr.target()).index());
	    else if (previous == expr.target()){
		previous = expr;
		expr.parent.visit(this);
	    }
	}

	else if (expr.target() instanceof FieldExpr) {
	    if (previous == expr.expr())
		check(expr.target());
	    else if (previous == expr.target()) {
		previous = expr;
		expr.parent.visit(this);
	    }
	}
    }
  
    public void visitJsrStmt(JsrStmt stmt) {

    }
  
    public void visitSwitchStmt(SwitchStmt stmt) {

	if (previous == stmt.index()) {
	    previous = stmt;
	    stmt.parent.visit(this);
	}
    }  


    public void visitThrowStmt(ThrowStmt stmt)  {
	
    }
  
    public void visitStmt(Stmt stmt) {
	
    }
  
    public void visitSCStmt(SCStmt stmt) {

    }
  
    public void visitSRStmt(SRStmt stmt) {

    }
  
    public void visitArithExpr(ArithExpr expr) {

	if (previous == expr.left()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
	else if (previous == expr.right()) 
	    check(expr.left());
		    
    }
  
    public void visitArrayLengthExpr(ArrayLengthExpr expr) {

    }
  
    public void visitMemExpr(MemExpr expr) {

	if (expr instanceof MemRefExpr)
	    visitMemRefExpr((MemRefExpr) expr);
	else if (expr instanceof VarExpr)
	    visitVarExpr((VarExpr) expr);

    }
  
    public void visitMemRefExpr(MemRefExpr expr)  {
	if (expr instanceof FieldExpr)
	    visitFieldExpr((FieldExpr) expr);
	else if (expr instanceof StaticFieldExpr)
	    visitStaticFieldExpr((StaticFieldExpr) expr);
	else if (expr instanceof ArrayRefExpr)
	    visitArrayRefExpr((ArrayRefExpr) expr);

    }
  
    public void visitArrayRefExpr(ArrayRefExpr expr)  {

	if (previous == expr.array()) { // the array reference is like the 
	    previous = expr;   // left child
	    expr.parent().visit(this);
	}
	else if (previous == expr.index())  // the index is like the
	    check(expr.array());   //right child
    }
  
    public void visitCallExpr(CallExpr expr)  {
	if (expr instanceof CallMethodExpr)
	    visitCallMethodExpr((CallMethodExpr) expr);
	if (expr instanceof CallStaticExpr)
	    visitCallStaticExpr((CallStaticExpr) expr);

    }
  
    public void visitCallMethodExpr(CallMethodExpr expr)  {

	if (previous == expr.receiver()) {
	    previous = expr;
	    expr.parent.visit(this);
	}

	else {
	    Expr[] params = expr.params();
	    for (int i = 0; i < params.length; i++)
		if (params[i] == previous)
		    if (i > 0) check(params[i - 1]);
		    else check(expr.receiver());
	}
	    
    }
  
    public void visitCallStaticExpr(CallStaticExpr expr)  {

	Expr[] params = expr.params();
	for (int i = 0; i < params.length; i++)
	    if (params[i] == previous) {
		if (i > 0) check(params[i - 1]);
		else {
		    previous = expr;
		    expr.parent().visit(this);
		}
		break;
	    }
	
    }
  
    public void visitCastExpr(CastExpr expr)  {

	previous = expr;
	expr.parent.visit(this);
    }
  
    public void visitConstantExpr(ConstantExpr expr)  {

    }
  
    public void visitFieldExpr(FieldExpr expr)  {

	if (previous == expr.object()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
    }
  
    public void visitInstanceOfExpr(InstanceOfExpr expr)  {

	if (previous == expr.expr()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
    }
  
    public void visitLocalExpr(LocalExpr expr)  {
	
    }
  
    public void visitNegExpr(NegExpr expr)  {

	if (previous == expr.expr()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
    }
  
    public void visitNewArrayExpr(NewArrayExpr expr)  {

	if (previous == expr.size()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
    }
  
    public void visitNewExpr(NewExpr expr)  {


    }
  
    public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)    {

	Expr[] dims = expr.dimensions;
	for (int i = 0; i < dims.length; i++)
	    if (dims[i] == previous)
		if (i > 0) check(dims[i - 1]);
		else {
		    previous = expr;
		    expr.parent().visit(this);
		}

    }
  
    public void visitCheckExpr(CheckExpr expr)   {
	if (expr instanceof ZeroCheckExpr)
	    visitZeroCheckExpr((ZeroCheckExpr) expr);
	else if (expr instanceof RCExpr)
	    visitRCExpr((RCExpr) expr);
	else if (expr instanceof UCExpr)
	    visitUCExpr((UCExpr) expr);
    }
  
    public void visitZeroCheckExpr(ZeroCheckExpr expr)  {
	/*
	if (previous == expr.expr()) {
	    previous = expr;
	    expr.parent.visit(this);
	}
	*/
    } 
  
    public void visitRCExpr(RCExpr expr)  {

    }
  
    public void visitUCExpr(UCExpr expr)  {
	
    }
  
    public void visitReturnAddressExpr(ReturnAddressExpr expr)  {
	
    }
  
    public void visitShiftExpr(ShiftExpr expr)  {

	if (previous == expr.expr()) { // the expression to be shifted is like
	    previous = expr;   // the left child
	    expr.parent().visit(this);
	}
	else if (previous == expr.bits())  // the bits shifted is like
	    check(expr.expr());   // the right child
    }
  
    public void visitStackExpr(StackExpr expr)  {

    }
  
    public void visitVarExpr(VarExpr expr)  {
	if (expr instanceof LocalExpr)
	    visitLocalExpr((LocalExpr) expr);
	if (expr instanceof StackExpr)
	    visitStackExpr((StackExpr) expr);
    }
  
    public void visitStaticFieldExpr(StaticFieldExpr expr)  {
	
    }
  
    public void visitExpr(Expr expr)  {
	
    }
  
    public void visitNode(Node node)  {
	
    }
}



