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

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.util.*;
import java.util.*;

/**
 * Eliminate partially redundant local variable loads and stores by
 * replacing them with stack variables and dups.
 *
 * The algorithm is similar to SSAPRE, except:
 *
 * We need to place phis for locals at the IDF of the blocks
 * containing defs and uses (not just defs).
 */
public class StackPRE {
    public static boolean DEBUG = false;

    protected FlowGraph cfg;

    protected List[] varphis;
    protected List[] stackvars;
    protected Worklist worklist;

    int next = 0;

    public StackPRE(FlowGraph cfg)
    {
	this.cfg = cfg;
    }

    public void transform()
    {
	stackvars = new ArrayList[cfg.size()];

	for (int i = 0; i < stackvars.length; i++) {
	    stackvars[i] = new ArrayList();
	}

	varphis = new ArrayList[cfg.size()];

	for (int i = 0; i < varphis.length; i++) {
	    varphis[i] = new ArrayList();
	}

	// Collect local and stack variables into a worklist.
	// Mark the variables that are pushed before any are popped.
	worklist = new Worklist();

	cfg.visit(new TreeVisitor()
	{
	    public void visitPhiJoinStmt(PhiJoinStmt stmt)
	    {
		worklist.addVarPhi(stmt);
	    }

	    public void visitPhiCatchStmt(PhiCatchStmt stmt)
	    {
		worklist.addLocalVar((LocalExpr) stmt.target());
	    }

	    public void visitLocalExpr(LocalExpr expr)
	    {
		worklist.addLocalVar(expr);
	    }

	    public void visitStackExpr(StackExpr expr)
	    {
		worklist.addStackVar(expr);
	    }
	});

	while (! worklist.isEmpty()) {
	    ExprInfo exprInfo = worklist.removeFirst();

	    if (DEBUG) {
		System.out.println("PRE for " +
		    exprInfo.def() + " -------------------------");
		System.out.println("Placing Phis for " +
		    exprInfo.def() + " -------------------------");
	    }

	    placePhiFunctions(exprInfo);

	    if (DEBUG) {
		exprInfo.print();
		System.out.println("Renaming for " +
		    exprInfo.def() + " -------------------------");
	    }

	    rename(exprInfo);

	    if (DEBUG) {
		exprInfo.print();
		System.out.println("Down safety for " +
		    exprInfo.def() + " -------------------------");
	    }

	    downSafety(exprInfo);

	    if (DEBUG) {
		System.out.println("Will be available for " +
		    exprInfo.def() + " -------------------------");
	    }

	    willBeAvail(exprInfo);

	    if (DEBUG) {
		System.out.println("Finalize for " +
		    exprInfo.def() + " -------------------------");
	    }

	    finalize(exprInfo);

	    if (DEBUG) {
		System.out.println("Code motion for " +
		    exprInfo.def() + " -------------------------");
	    }

	    Type type = exprInfo.def().type();
	    StackExpr tmp = new StackExpr(0, type);
	    SSAConstructionInfo consInfo = new SSAConstructionInfo(cfg, tmp);

	    codeMotion(exprInfo, tmp, consInfo);

	    if (DEBUG) {
		System.out.println("Performing incremental SSA for " +
		    exprInfo.def() + " -------------------------");
	    }

	    SSA.transform(cfg, consInfo);

	    // Get the stack variable phis.
	    Collection defBlocks = consInfo.defBlocks();
	    Iterator e = cfg.iteratedDomFrontier(defBlocks).iterator();

	    while (e.hasNext()) {
		Block block = (Block) e.next();

		Iterator stmts = block.tree().stmts().iterator();

		while (stmts.hasNext()) {
		    Stmt stmt = (Stmt) stmts.next();
		    if (stmt instanceof PhiJoinStmt) {
			worklist.prependVarPhi((PhiJoinStmt) stmt);
		    }
		    else if (! (stmt instanceof LabelStmt)) {
			// Only labels occur before phis.  If we hit
			// something else, there are no more phis.
			break;
		    }
		}
	    }

	    if (DEBUG) {
		exprInfo.print();
		System.out.println("Done with PRE for " +
		    exprInfo.def() + " -------------------------");
	    }

            exprInfo.cleanup();
	}

	varphis = null;
	worklist = null;
    }

    /**
     * For an local variable, v, insert a Phi at the iterated dominance
     * frontier of the blocks containing defs and uses of v.  This differs
     * from SSA phi placement in that uses, not just defs are considered in
     * computing the IDF.
     */
    private void placePhiFunctions(ExprInfo exprInfo)
    {
	final ArrayList w = new ArrayList(cfg.size());

	Iterator uses = exprInfo.def().uses().iterator();

	w.add(exprInfo.def().block());

	while (uses.hasNext()) {
	    LocalExpr use = (LocalExpr) uses.next();

	    if (use.parent() instanceof PhiJoinStmt) {
		PhiJoinStmt phi = (PhiJoinStmt) use.parent();

		Iterator preds = cfg.preds(use.block()).iterator();

		while (preds.hasNext()) {
		    Block pred = (Block) preds.next();

		    if (phi.operandAt(pred) == use) {
			w.add(pred);
			break;
		    }
		}
	    }
	    else if (! (use.parent() instanceof PhiCatchStmt)) {
		w.add(use.block());
	    }
	}

        Iterator df = cfg.iteratedDomFrontier(w).iterator();

        while (df.hasNext()) {
            Block block = (Block) df.next();
	    exprInfo.addPhi(block);
        }

	// Don't bother with placing phis for catch blocks, since the
	// operand stack is zeroed at catch blocks.
    }

    /**
     * Set the definition for the variable occurences.  After this step
     * all occurences of the variable which are at different heights
     * will have different definitions.
     */
    private void rename(ExprInfo exprInfo)
    {
	search(cfg.source(), exprInfo, null, 0, false);
    }

    private void search(Block block, final ExprInfo exprInfo,
	Def top, int totalBalance, boolean seenDef)
    {
	if (DEBUG) {
	    System.out.println("    renaming in " + block);
	}

	if (cfg.catchBlocks().contains(block)) {
            if (top != null) {
                top.setDownSafe(false);
            }

            top = null;
        }

	Phi phi = exprInfo.exprPhiAtBlock(block);

	if (phi != null) {
	    if (top != null) {
		top.setDownSafe(false);
	    }

	    top = phi;

	    if (! seenDef) {
		top.setDownSafe(false);
	    }
	}

	Node parent = null;
	int balance = 0;

	Iterator iter = exprInfo.varsAtBlock(block).iterator();

	while (iter.hasNext()) {
	    VarExpr node = (VarExpr) iter.next();

	    // Get the parent of the node.  If the parent is a putfield
	    // or array store, then the node is popped when the grandparent
	    // is evaluated, not when the parent is evaluated.
	    // We keep track of the parent so that when it changes, we
	    // know to update the operand stack balance.

	    Node p = node.parent();

	    if (p instanceof MemRefExpr && ((MemRefExpr) p).isDef()) {
		p = p.parent();
	    }

	    if (parent != p) {
		parent = p;
		totalBalance += balance;
		balance = 0;

		if (top != null && totalBalance < 0) {
		    top.setDownSafe(false);
		}
	    }

	    if (node instanceof StackExpr) {
		if (parent instanceof StackManipStmt) {
		    switch (((StackManipStmt) parent).kind()) {
		        case StackManipStmt.DUP:
		        case StackManipStmt.DUP_X1:
		        case StackManipStmt.DUP_X2:
			    balance += 1;
			    break;
		        case StackManipStmt.DUP2:
		        case StackManipStmt.DUP2_X1:
		        case StackManipStmt.DUP2_X2:
			    balance += 2;
			    break;
		        default:
			    break;
		    }
		}
		else if (node.isDef()) {
		    balance += node.type().stackHeight();
		}
		else {
		    balance -= node.type().stackHeight();
		}
	    }
	    else {
		LocalExpr var = (LocalExpr) node;

		if (var.isDef()) {
		    seenDef = true;
		}

		if (DEBUG) {
		    System.out.println("node = " + var + " in " + parent);
		}

		if (totalBalance == 0 && onBottom(var, false)) {
		    // Copy the def from the top of the stack and
		    // create a new def.
		    exprInfo.setDef(var, top);
		    top = new RealDef(var);

		    if (balance != 0 || ! onBottom(var, true)) {
			top.setDownSafe(false);
		    }

		    if (DEBUG) {
			System.out.println("New def " + top +
			    " with balance " + totalBalance + " + " + balance);
		    }
		}
		else {
		    // The occurence is not on the bottom, so it
		    // must be reloaded from a local.
		    exprInfo.setDef(var, null);
		}
	    }

	    if (DEBUG) {
		System.out.println("after " + parent + " top = " + top);
	    }
	}

	totalBalance += balance;

	if (top != null && totalBalance < 0) {
	    top.setDownSafe(false);
	}

	// If we hit the sink node, a def at the top of the stack is not
	// down safe.
	if (block == cfg.sink() || cfg.succs(block).contains(cfg.sink())) {
	    if (top != null) {
		top.setDownSafe(false);
	    }
	}

	// First, fill in the operands for the StackPRE phis.  Then,
	// handle local variable occurences in successor block variable
	// phis.  We do this after the StackPRE phis since they will
	// hoist code above the variable phis.

	Iterator succs = cfg.succs(block).iterator();

	while (succs.hasNext()) {
	    Block succ = (Block) succs.next();

	    Phi succPhi = exprInfo.exprPhiAtBlock(succ);

	    if (succPhi != null) {
		succPhi.setOperandAt(block, top);
	    }
	}

	succs = cfg.succs(block).iterator();

	while (succs.hasNext()) {
	    Block succ = (Block) succs.next();

	    Iterator phis = varPhisAtBlock(succ).iterator();

	    while (phis.hasNext()) {
		PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

		Expr operand = stmt.operandAt(block);

		if (operand instanceof StackExpr) {
		    balance += operand.type().stackHeight();
		}

		if (stmt.target() instanceof StackExpr) {
		    balance -= stmt.target().type().stackHeight();

		    if (top != null) {
			top.setDownSafe(false);
			top = null;
		    }
		}

		if (operand != null && operand.def() == exprInfo.def()) {
		    // Phi operands aren't allowed to define any of the
		    // locals.  This should never happen since none of the
		    // locals should be dominated by the phi operand,
		    // but we'll play it safe and set top to null.
		    exprInfo.setDef((LocalExpr) operand, top);
		    top = null;
		}

		if (stmt.target() == exprInfo.def()) {
		    exprInfo.setDef((LocalExpr) stmt.target(), top);
		    top = new RealDef((LocalExpr) stmt.target());
		}

		totalBalance += balance;

		if (top != null && totalBalance < 0) {
		    top.setDownSafe(false);
		}
	    }
	}

	Iterator children = cfg.domChildren(block).iterator();

	while (children.hasNext()) {
	    Block child = (Block) children.next();
	    search(child, exprInfo, top, totalBalance, seenDef);
	}
    }

    private boolean onBottom(final LocalExpr var, final boolean really)
    {
	// InitStmts and PhiStmts are always on the bottom.
	if (var.stmt() instanceof InitStmt || var.stmt() instanceof PhiStmt) {
	    return true;
	}

	class Bool
	{
	    boolean value = true;
	};

	final Bool bottom = new Bool();

	var.stmt().visitChildren(new TreeVisitor()
	{
	    boolean seen = false;

	    public void visitExpr(Expr expr)
	    {
		if (DEBUG) {
		    System.out.println("Checking " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}

		if (! seen) {
		    expr.visitChildren(this);
		}

		if (! seen) {
		    bottom.value = false;
		    seen = true;
		}

		if (DEBUG) {
		    System.out.println("Done with " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}
	    }

	    public void visitLocalExpr(LocalExpr expr)
	    {
		if (DEBUG) {
		    System.out.println("Checking " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}

		if (! seen) {
		    if (expr == var) {
			seen = true;
		    }
		    else if (expr.def() != var.def()) {
			bottom.value = false;
			seen = true;
		    }
		}

		if (DEBUG) {
		    System.out.println("Done with " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}
	    }

	    public void visitStackExpr(StackExpr expr)
	    {
		if (DEBUG) {
		    System.out.println("Checking " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}

		if (really && ! seen) {
		    bottom.value = false;
		    seen = true;
		}

		if (DEBUG) {
		    System.out.println("Done with " + expr +
			" seen=" + seen + " bottom=" + bottom.value);
		}
	    }
	});

	return bottom.value;
    }

    /**
     * Mark each def as not down safe if there is a control flow path
     * from that Phi along which the expression is not evaluated before
     * exit or being altered by refinition of one of the variables of the
     * expression. This can happen if:
     *
     *     1) There is a path to exit along which the Phi target is not
     *        used.
     *     2) There is a path to exit along which the Phi target is
     *        used only as the operand of a non-down-safe Phi.
     */
    private void downSafety(ExprInfo exprInfo)
    {
	Iterator blocks = cfg.nodes().iterator();

	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi phi = exprInfo.exprPhiAtBlock(block);

	    if (phi == null) {
		continue;
	    }

	    if (DEBUG) {
		System.out.println("    down safety for " + phi +
		    " in " + block);
	    }

	    if (phi.downSafe()) {
		if (DEBUG) {
		    System.out.println("    already down safe");
		}

		continue;
	    }

	    // The phi is not down safe.  Make all its operands not
	    // down safe.

	    Iterator e = phi.operands().iterator();

	    while (e.hasNext()) {
		Def def = (Def) e.next();

		if (def != null) {
		    resetDownSafe(def);
		}
	    }
	}
    }

    private void resetDownSafe(Def def)
    {
	if (DEBUG) {
	    System.out.println("        reset down safe for " + def);
	}

	if (def instanceof Phi) {
	    Phi phi = (Phi) def;

	    if (phi.downSafe()) {
		phi.setDownSafe(false);

		Iterator e = phi.operands().iterator();

		while (e.hasNext()) {
		    Def operand = (Def) e.next();

		    if (operand != null) {
			resetDownSafe(operand);
		    }
		}
	    }
	}
	else {
	    def.setDownSafe(false);
	}
    }

    /**
     * Predict whether the expression will be available at each Phi
     * result following insertions for PRE.
     */
    private void willBeAvail(ExprInfo exprInfo)
    {
	computeCanBeAvail(exprInfo);
	computeLater(exprInfo);
    }

    private void computeCanBeAvail(ExprInfo exprInfo)
    {
	Iterator blocks = cfg.nodes().iterator();

	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi phi = exprInfo.exprPhiAtBlock(block);

	    if (phi == null) {
		continue;
	    }

	    if (! phi.downSafe() && phi.canBeAvail()) {
		resetCanBeAvail(exprInfo, phi);
	    }
	}
    }

    private void resetCanBeAvail(ExprInfo exprInfo, Phi phi)
    {
	phi.setCanBeAvail(false);

	Iterator blocks = cfg.nodes().iterator();

	// For each phi whose operand is at
	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi other = exprInfo.exprPhiAtBlock(block);

	    if (other == null) {
		continue;
	    }

	    Iterator e = cfg.preds(other.block()).iterator();

	    while (e.hasNext()) {
		Block pred = (Block) e.next();

		Def def = other.operandAt(pred);

		if (def == phi) {
		    other.setOperandAt(pred, null);

		    if (! other.downSafe() && other.canBeAvail()) {
			resetCanBeAvail(exprInfo, other);
		    }
		}
	    }
	}
    }

    private void computeLater(ExprInfo exprInfo)
    {
	Iterator blocks = cfg.nodes().iterator();

	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi phi = exprInfo.exprPhiAtBlock(block);

	    if (phi != null) {
		phi.setLater(phi.canBeAvail());
	    }
	}

	blocks = cfg.nodes().iterator();

	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi phi = exprInfo.exprPhiAtBlock(block);

	    if (phi != null && phi.later()) {
		Iterator e = phi.operands().iterator();

		while (e.hasNext()) {
		    Def def = (Def) e.next();

		    if (def instanceof RealDef) {
			resetLater(exprInfo, phi);
			break;
		    }
		}
	    }
	}
    }

    private void resetLater(ExprInfo exprInfo, Phi phi)
    {
	phi.setLater(false);

	Iterator blocks = cfg.nodes().iterator();

	while (blocks.hasNext()) {
	    Block block = (Block) blocks.next();

	    Phi other = exprInfo.exprPhiAtBlock(block);

	    if (other == null) {
		continue;
	    }

	    Iterator e = other.operands().iterator();

	    while (e.hasNext()) {
		Def def = (Def) e.next();

		if (def == phi && other.later()) {
		    resetLater(exprInfo, other);
		    break;
		}
	    }
	}
    }

    private void finalize(ExprInfo exprInfo)
    {
	Iterator uses = exprInfo.def().uses().iterator();

	while (uses.hasNext()) {
	    LocalExpr use = (LocalExpr) uses.next();

	    if (use.parent() instanceof PhiCatchStmt) {
		exprInfo.setSave(true);
		break;
	    }
	}

	finalizeVisit(exprInfo, cfg.source());
    }

    private void finalizeVisit(final ExprInfo exprInfo, Block block)
    {
	if (DEBUG) {
	    System.out.println("    finalizing " + block);
	}

	// First finalize normal occurences of the local.
	Iterator reals = exprInfo.varsAtBlock(block).iterator();

	while (reals.hasNext()) {
	    VarExpr node = (VarExpr) reals.next();

	    if (node instanceof LocalExpr) {
		LocalExpr real = (LocalExpr) node;

		if (DEBUG) {
		    System.out.println("        -----------");
		}

		Def def = exprInfo.def(real);

		if (def != null && def.downSafe()) {
		    // We can reload from a stack variable, unless the we
		    // can't safely push the phi operands.
		    if (def instanceof Phi) {
			if (((Phi) def).willBeAvail()) {
			    exprInfo.setPop(real, true);
			}
			else {
			    exprInfo.setSave(true);
			}
		    }
		    else {
			exprInfo.setPush(((RealDef) def).var, true);
			exprInfo.setPop(real, true);
		    }
		}
		else {
		    // The real is not on the bottom.  We must reload from a
		    // local variable.
		    if (real != exprInfo.def()) {
			exprInfo.setSave(true);
		    }
		}
	    }
	}

	// Next, handle code motion.
	Iterator succs = cfg.succs(block).iterator();

	while (succs.hasNext()) {
	    Block succ = (Block) succs.next();

	    Phi succPhi = exprInfo.exprPhiAtBlock(succ);

	    if (succPhi != null && succPhi.willBeAvail()) {
		if (succPhi.insert(block)) {
		    succPhi.setPushOperand(block, true);
		}
		else {
		    Def def = succPhi.operandAt(block);

		    if (def instanceof RealDef) {
			Assert.isTrue(def.downSafe(), succPhi +
			    " operand for " + block + " is not DS: " + def);
			exprInfo.setPush(((RealDef) def).var, true);
		    }
		    else {
			Assert.isTrue(def instanceof Phi, succPhi +
			    " operand for " + block + " is not a phi: " + def);
			Assert.isTrue(((Phi) def).willBeAvail(), succPhi +
			    " operand for " + block + " is not WBA: " + def);
		    }
		}
	    }
	}

	// Lastly, finalize occurences in variable phis.  We do this
	// after the StackPRE hoisting since the hoisted code will
	// occur before the phis.
	succs = cfg.succs(block).iterator();

	while (succs.hasNext()) {
	    Block succ = (Block) succs.next();

	    Iterator phis = varPhisAtBlock(succ).iterator();

	    while (phis.hasNext()) {
		PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

		Expr operand = stmt.operandAt(block);

		if (operand != null && operand.def() == exprInfo.def()) {
		    LocalExpr var = (LocalExpr) operand;
		    Def def = exprInfo.def(var);

		    if (def != null && def.downSafe()) {
			// We can reload from a stack variable, unless the we
			// can't safely push the phi operands.
			if (def instanceof Phi) {
			    if (((Phi) def).willBeAvail()) {
				exprInfo.setPop(var, true);
			    }
			    else {
				exprInfo.setSave(true);
			    }
			}
			else {
			    exprInfo.setPush(((RealDef) def).var, true);
			    exprInfo.setPop(var, true);
			}
		    }
		}
	    }
	}

	Iterator children = cfg.domChildren(block).iterator();

	while (children.hasNext()) {
	    Block child = (Block) children.next();
	    finalizeVisit(exprInfo, child);
	}
    }

    private void codeMotion(final ExprInfo exprInfo, StackExpr tmp,
	SSAConstructionInfo consInfo)
    {
	// Be sure to visit pre-order so at least one predecessor is visited
	// before each block.
	Iterator blocks = cfg.preOrder().iterator();

	while (blocks.hasNext()) {
	    final Block block = (Block) blocks.next();

	    if (block == cfg.source() || block == cfg.sink()) {
		continue;
	    }

	    boolean added = false;

	    Iterator reals = exprInfo.varsAtBlock(block).iterator();

	    while (reals.hasNext()) {
		VarExpr node = (VarExpr) reals.next();

		if (node instanceof LocalExpr) {
		    LocalExpr var = (LocalExpr) node;

		    // If marked push, save it to a stack variable.
		    // If marked pop, reload from a stack variable.

		    boolean push = exprInfo.push(var);
		    boolean pop  = exprInfo.pop(var);

		    if (var.isDef() && exprInfo.save()) {
			pop = false;
		    }

		    if (push && pop) {
			Assert.isTrue(var != exprInfo.def());

			StackExpr t1 = (StackExpr) tmp.clone();
			StackExpr t2 = (StackExpr) tmp.clone();

			StoreExpr store = new StoreExpr(t1, t2, t2.type());
			var.replaceWith(store);

			consInfo.addReal(t2);
			consInfo.addReal(t1);
			added = true;
		    }
		    else if (push) {
			StackExpr t1 = (StackExpr) tmp.clone();

			LocalExpr t2 = (LocalExpr) var.clone();
			t2.setDef(exprInfo.def());

			StoreExpr store = new StoreExpr(t1, t2, t2.type());

			if (var != exprInfo.def()) {
			    var.replaceWith(store);
			}
			else {
			    Node parent = var.parent();

			    if (parent instanceof Stmt) {
				// InitStmt or PhiStmt.
				Stmt stmt = new ExprStmt(store);
				block.tree().addStmtAfter(stmt, (Stmt) parent);
			    }
			    else {
				// a := E -> a := (S := E)
				Assert.isTrue(parent instanceof StoreExpr);
				Expr rhs = ((StoreExpr) parent).expr();
				parent.visit(new ReplaceVisitor(rhs, store));
				store.visit(new ReplaceVisitor(t2, rhs));
				t2.cleanup();
			    }
			}

			consInfo.addReal(t1);
			added = true;
		    }
		    else if (pop) {
			StackExpr t1 = (StackExpr) tmp.clone();
			var.replaceWith(t1);

			consInfo.addReal(t1);
			added = true;
		    }
		}
	    }

	    final List s = stackvars[cfg.preOrderIndex(block)];

	    if (added) {
		s.clear();

		block.tree().visitChildren(new TreeVisitor()
		{
		    public void visitStackExpr(StackExpr expr)
		    {
			s.add(expr);
		    }
		});
	    }

	    Iterator succs = cfg.succs(block).iterator();

	    while (succs.hasNext()) {
		Block succ = (Block) succs.next();

		Phi succPhi = exprInfo.exprPhiAtBlock(succ);

		if (succPhi != null && succPhi.pushOperand(block)) {
		    StackExpr t1 = (StackExpr) tmp.clone();
		    LocalExpr t2 = (LocalExpr) exprInfo.def().clone();
		    t2.setDef(exprInfo.def());

		    StoreExpr store = new StoreExpr(t1, t2, t1.type());

		    block.tree().addStmtBeforeJump(new ExprStmt(store));

		    s.add(t1);

		    consInfo.addReal(t1);

		    if (DEBUG) {
			System.out.println("insert at end of " + block +
			    ": " + store);
		    }
		}
	    }

	    succs = cfg.succs(block).iterator();

	    while (succs.hasNext()) {
		Block succ = (Block) succs.next();

		Iterator phis = varPhisAtBlock(succ).iterator();

		while (phis.hasNext()) {
		    PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

		    Expr operand = stmt.operandAt(block);

		    if (operand != null && operand.def() == exprInfo.def()) {
			LocalExpr var = (LocalExpr) operand;

			Assert.isFalse(exprInfo.push(var));

			if (exprInfo.pop(var)) {
			    StackExpr t1 = (StackExpr) tmp.clone();
			    var.replaceWith(t1);
			    consInfo.addReal(t1);
			}
		    }
		}
	    }
	}
    }

    abstract class Def
    {
	int version;
	boolean downSafe;

	public Def()
	{
	    this.version = next++;
	    this.downSafe = true;
	}

	public void setDownSafe(boolean flag)
	{
	    if (DEBUG) {
		System.out.println(this + " DS = " + flag);
	    }

	    downSafe = flag;
	}

	public boolean downSafe()
	{
	    return downSafe;
	}
    }

    class RealDef extends Def
    {
	LocalExpr var;

	public RealDef(LocalExpr var)
	{
	    this.var = var;

	    if (DEBUG) {
		System.out.println("new def for " + var +
		    " in " + var.parent());
	    }
	}

	public LocalExpr var()
	{
	    return var;
	}

	public String toString()
	{
	    return var.toString() + "{" + version + "," +
    	        (downSafe() ? "" : "!") + "DS}";
	}
    }

    class Phi extends Def
    {
	Block block;
	HashMap operands;
	HashMap saveOperand;
	boolean live;
	boolean downSafe;
	boolean canBeAvail;
	boolean later;

	public Phi(Block block)
	{
	    this.block = block;

	    operands = new HashMap(cfg.preds(block).size() * 2);
	    saveOperand = new HashMap(cfg.preds(block).size()* 2);

	    downSafe = true;
	    canBeAvail = true;
	    later = true;
	}

        public Block block()
        {
            return block;
        }

	public Collection operands()
	{
	    return new AbstractCollection()
	    {
		public int size()
		{
		    return cfg.preds(block).size();
		}

		public boolean contains(Object obj)
		{
		    if (obj == null) {
			return operands.size() != cfg.preds(block).size();
		    }

		    return operands.containsValue(obj);
		}

		public Iterator iterator()
		{
		    final Iterator iter = cfg.preds(block).iterator();

		    return new Iterator() {
			public boolean hasNext()
			{
			    return iter.hasNext();
			}

			public Object next()
			{
			    Block block = (Block) iter.next();
			    return operandAt(block);
			}

			public void remove()
			{
			    throw new UnsupportedOperationException();
			}
		    };
		}
	    };
	}

	public Def operandAt(Block block)
	{
	    return (Def) operands.get(block);
	}

	public void setOperandAt(Block block, Def def)
	{
	    if (def != null) {
		operands.put(block, def);
	    }
	    else {
		operands.remove(block);
	    }
	}

	public void setPushOperand(Block block, boolean flag)
	{
	    if (DEBUG) {
		System.out.println("    operand " + block + " save=" + flag);
	    }

	    saveOperand.put(block, new Boolean(flag));
	}

	public boolean pushOperand(Block block)
	{
	    Boolean flag = (Boolean) saveOperand.get(block);
	    return flag != null && flag.booleanValue();
	}

	public boolean insert(Block block)
	{
	    Def def = operandAt(block);

	    if (def == null) {
		return true;
	    }

	    if (! def.downSafe()) {
		return true;
	    }

	    if (def instanceof Phi && ! ((Phi) def).willBeAvail()) {
		return true;
	    }

	    return false;
	}

	public boolean willBeAvail()
	{
	    return canBeAvail && ! later;
	}

	public void setCanBeAvail(boolean flag)
	{
	    if (DEBUG) {
		System.out.println(this + " CBA = " + flag);
	    }

	    canBeAvail = flag;
	}

	public boolean canBeAvail()
	{
	    return canBeAvail;
	}

	public void setLater(boolean flag)
	{
	    if (DEBUG) {
		System.out.println(this + " Later = " + flag);
	    }

	    later = flag;
	}

	public boolean later()
	{
	    return later;
	}

	public String toString()
	{
	    String s = "";

	    Iterator iter = cfg.preds(block).iterator();

	    while (iter.hasNext()) {
		Block pred = (Block) iter.next();
		Def def = operandAt(pred);

		s += pred.label() + "=";

		if (def == null) {
		    s += "null";
	        }
		else {
		    s += def.version;
	        }

		if (iter.hasNext()) {
		    s += ", ";
		}
	    }

	    return "phi" + "{" + version + "," +
    	        (downSafe() ? "" : "!") + "DS," +
	        (canBeAvail() ? "" : "!") + "CBA," +
	        (later() ? "" : "!") + "Later}(" + s + ")";
	}
    }

    public List varPhisAtBlock(Block block)
    {
	return varphis[cfg.preOrderIndex(block)];
    }

    /**
     * Maintain the occurences so that they are visited in a preorder
     * traversal of the dominator tree.
     */
    private final class ExprInfo
    {
	ArrayList[] vars;
	Phi[] phis;
	boolean save;
	Map pushes;
	Map pops;
	Map defs;
	LocalExpr def;
	ArrayList cleanup;

	public ExprInfo(LocalExpr def)
	{
	    this.def = def;

	    vars = new ArrayList[cfg.size()];

	    for (int i = 0; i < vars.length; i++) {
		vars[i] = new ArrayList();
	    }

	    phis = new Phi[cfg.size()];

	    save = false;

	    pushes = new HashMap();
	    pops = new HashMap();

	    defs = new HashMap();

            cleanup = new ArrayList();
	}

        public void cleanup()
	{
            Iterator iter = cleanup.iterator();

            while (iter.hasNext()) {
                Node node = (Node) iter.next();
                node.cleanup();
            }

	    vars = null;
	    phis = null;
	    pushes = null;
	    pops = null;
	    defs = null;
	    def = null;
	    cleanup = null;
	}

        public void registerForCleanup(Node node)
	{
            cleanup.add(node);
	}

	public void setSave(boolean flag)
	{
	    save = flag;
	}

	public boolean save()
	{
	    return save;
	}

	public void setPush(LocalExpr expr, boolean flag)
	{
	    pushes.put(expr, new Boolean(flag));
	}

	public boolean push(LocalExpr expr)
	{
	    Boolean b = (Boolean) pushes.get(expr);
	    return b != null && b.booleanValue();
	}

	public void setPop(LocalExpr expr, boolean flag)
	{
	    pops.put(expr, new Boolean(flag));
	}

	public boolean pop(LocalExpr expr)
	{
	    Boolean b = (Boolean) pops.get(expr);
	    return b != null && b.booleanValue();
	}

	public void setDef(LocalExpr expr, Def def)
	{
	    if (DEBUG) {
		System.out.println("        setting def for " +
		    expr + " to " + def);
	    }

	    if (def != null) {
		defs.put(expr, def);
	    }
	    else {
		defs.remove(expr);
	    }
	}

	public Def def(LocalExpr expr)
	{
	    Def def = (Def) defs.get(expr);

	    if (DEBUG) {
		System.out.println("        def for " + expr + " is " + def);
	    }

	    return def;
	}

	public LocalExpr def()
	{
	    return def;
	}

	public void addPhi(final Block block)
	{
	    Phi phi = phis[cfg.preOrderIndex(block)];

	    if (phi == null) {
		if (DEBUG) {
		    System.out.println("    add phi for " + def +
			" at " + block);
		}

		phi = new Phi(block);
		phis[cfg.preOrderIndex(block)] = phi;
	    }
	}

	public List varsAtBlock(Block block)
	{
	    int blockIndex = cfg.preOrderIndex(block);

	    final List list = new ArrayList(vars[blockIndex].size() +
					    stackvars[blockIndex].size());

	    final Iterator viter = vars[blockIndex].iterator();
	    final Iterator siter = stackvars[blockIndex].iterator();

	    if (! viter.hasNext() && ! siter.hasNext()) {
		return new ArrayList(0);
	    }

	    block.tree().visitChildren(new TreeVisitor()
	    {
		VarExpr vnext = null;
		VarExpr snext = null;

		{
		    if (viter.hasNext()) {
			vnext = (VarExpr) viter.next();
		    }

		    if (siter.hasNext()) {
			snext = (VarExpr) siter.next();
		    }
		}

		public void visitStmt(Stmt stmt)
		{
		    if ((vnext != null && vnext.stmt() == stmt) ||
			(snext != null && snext.stmt() == stmt)) {
			super.visitStmt(stmt);
		    }
		}

		public void visitVarExpr(VarExpr expr)
		{
		    super.visitExpr(expr);

		    if (expr == vnext) {
			if (viter.hasNext()) {
			    vnext = (VarExpr) viter.next();
			}
			else {
			    vnext = null;
			}

			if (expr == snext) {
			    if (siter.hasNext()) {
				snext = (VarExpr) siter.next();
			    }
			    else {
				snext = null;
			    }
			}

			list.add(expr);
		    }
		    else if (expr == snext) {
			if (siter.hasNext()) {
			    snext = (VarExpr) siter.next();
			}
			else {
			    snext = null;
			}

			list.add(expr);
		    }
		}
	    });

	    return list;
	}

	public Phi exprPhiAtBlock(Block block)
	{
	    return phis[cfg.preOrderIndex(block)];
	}

	protected void print()
	{
	    System.out.println("Print for " + def + "------------------");

	    cfg.visit(new PrintVisitor()
	    {
		Phi phi = null;

		public void visitBlock(Block block)
		{
		    phi = exprPhiAtBlock(block);
		    super.visitBlock(block);
		}

		public void visitLabelStmt(LabelStmt stmt)
		{
		    super.visitLabelStmt(stmt);

		    if (stmt.label().startsBlock()) {
			if (phi != null) {
			    println(phi);
			}
		    }
		}

		public void visitLocalExpr(LocalExpr expr)
		{
		    super.visitLocalExpr(expr);

		    if (expr.def() == def) {
			super.print("{" + defs.get(expr) + "}");
		    }
		}
	    });

	    System.out.println("End Print ----------------------------");
	}
    }

    class Worklist
    {
	Map exprInfos;
	LinkedList exprs;

	public Worklist()
	{
	    exprInfos = new HashMap();
	    exprs = new LinkedList();
	}

	public boolean isEmpty()
	{
	    return exprs.isEmpty();
	}

	public ExprInfo removeFirst()
	{
	    ExprInfo exprInfo = (ExprInfo) exprs.removeFirst();
	    exprInfos.remove(exprInfo.def());
	    return exprInfo;
	}

	public void addLocalVar(LocalExpr var)
	{
	    int blockIndex = cfg.preOrderIndex(var.block());

	    if (DEBUG) {
		System.out.println("add var " + var);
	    }

	    ExprInfo exprInfo = (ExprInfo) exprInfos.get(var.def());
	    
	    if (exprInfo == null) {
		exprInfo = new ExprInfo((LocalExpr) var.def());
		exprs.add(exprInfo);
		exprInfos.put(var.def(), exprInfo);
		
		if (DEBUG) {
		    System.out.println("    add info for " + var);
		}
	    }

	    exprInfo.vars[blockIndex].add(var);
	}

	public void addStackVar(StackExpr var)
	{
	    int blockIndex = cfg.preOrderIndex(var.block());

	    if (DEBUG) {
		System.out.println("add var " + var);
	    }

	    stackvars[blockIndex].add(var);
	}

	public void addVarPhi(PhiJoinStmt stmt)
	{
	    varphis[cfg.preOrderIndex(stmt.block())].add(stmt);
	}

	public void prependVarPhi(PhiJoinStmt stmt)
	{
	    List v = varphis[cfg.preOrderIndex(stmt.block())];

	    if (! v.contains(stmt)) {
		v.add(0, stmt);
	    }
	}
    }
}
