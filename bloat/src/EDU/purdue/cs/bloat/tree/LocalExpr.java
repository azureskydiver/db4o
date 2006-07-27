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

import EDU.purdue.cs.bloat.editor.*;

/**
 * LocalExpr represents an expression that accesses a variable in a method's
 * local variable table. Note that during register allocation the index becomes
 * the color that the LocalExpr (variable) is assigned.
 * 
 * @see Tree#newStackLocal
 * @see Tree#newLocal(Type)
 */
public class LocalExpr extends VarExpr implements LeafExpr {
	boolean fromStack;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            Index into the local variable table for this expression.
	 * @param fromStack
	 *            Is the local allocated on the stack?
	 * @param type
	 *            The type of this expression
	 */
	public LocalExpr(final int index, final boolean fromStack, final Type type) {
		super(index, type);
		this.fromStack = fromStack;
	}

	/**
	 * Constructor. LocalExpr is not allocated on the stack.
	 * 
	 * @param index
	 *            Index into the local variable table for this expression.
	 * @param type
	 *            The type of this expression.
	 */
	public LocalExpr(final int index, final Type type) {
		this(index, false, type);
	}

	public boolean fromStack() {
		return fromStack;
	}

	/**
	 * Returns true if the type of this expression is a return address.
	 */
	public boolean isReturnAddress() {
		return type().equals(Type.ADDRESS);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitLocalExpr(this);
	}

	/**
	 * @param other
	 *            The other expression to compare against.
	 */
	public boolean equalsExpr(final Expr other) {
		return (other instanceof LocalExpr)
				&& ((LocalExpr) other).type.simple().equals(type.simple())
				&& (((LocalExpr) other).fromStack == fromStack)
				&& (((LocalExpr) other).index == index);
	}

	public int exprHashCode() {
		return 13 + (fromStack ? 0 : 1) + index + type.simple().hashCode();
	}

	public Object clone() {
		return copyInto(new LocalExpr(index, fromStack, type));
	}

}
