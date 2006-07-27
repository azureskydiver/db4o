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
 * Swizzler represents an induction variable that is used as an index into an
 * array. Analysis can be done to determine if array swizzle (aswizzle)
 * instruction(s) can be hoisted out of the loop.
 * 
 * @see EDU.purdue.cs.bloat.diva.InductionVarAnalyzer InductionVarAnalyzer
 */
public class Swizzler {
	Expr ind_var; // induction variable (iv)

	Expr target; // target of the phi defining the ind_var

	Expr init_val; // initial value of the iv

	Expr end_val; // terminating value of the iv

	Expr array; // arrayref which uses the iv as the index

	Block phi_block; // block of the phi defining the ind_var

	SCStmt aswizzle; // the aswizzle stmt that could be removed

	/**
	 * Constructor.
	 * 
	 * @param var
	 *            Induction variable. (An index variable for an array.)
	 * @param tgt
	 *            Target of the phi statement that defines the induction
	 *            variable.
	 * @param val
	 *            Initial value of the induction variable.
	 * @param phiblock
	 *            The block in which the phi statement resides.
	 */
	public Swizzler(final Expr var, final Expr tgt, final Expr val,
			final Block phiblock) {
		this.ind_var = var;
		this.target = tgt;
		this.init_val = val;
		this.end_val = null;
		this.array = null;
		this.phi_block = phiblock;
		this.aswizzle = null;
	}

	/**
	 * Sets the ending value for the induction variable.
	 * 
	 * @param end
	 *            The final value the induction variable will take on.
	 */
	public void set_end_val(final Expr end) {
		this.end_val = end;
	}

	/**
	 * @param a
	 *            The array that is indexed by the induction variable.
	 */
	public void set_array(final Expr a) {
		this.array = a;
	}

	/**
	 * @param sc
	 *            The aswizzle statement that could be removed from the block.
	 */
	public void set_aswizzle(final SCStmt sc) {
		this.aswizzle = sc;
	}

	public Expr ind_var() {
		return ind_var;
	}

	public Expr target() {
		return target;
	}

	public Expr init_val() {
		return init_val;
	}

	public Expr end_val() {
		return end_val;
	}

	public Expr array() {
		return array;
	}

	public Block phi_block() {
		return phi_block;
	}

	public SCStmt aswizzle() {
		return aswizzle;
	}
}
