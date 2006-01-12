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
 * NodeComparator is a class used to differentiate nodes in an
 * expression tree.  
 */
public class NodeComparator {
  public static boolean DEBUG = false;

  /**
   * Determines whether or not two <tt>Node</tt>s are equal.
   */
  public static boolean equals(final Node v, final Node w) {
    class Bool {
      boolean value = false;
    };

    final Bool eq = new Bool();

    v.visit(new TreeVisitor() {
      public void visitExprStmt(ExprStmt stmt) {
	if (w instanceof ExprStmt) {
	  eq.value = true;
	}
      }

      public void visitIfCmpStmt(IfCmpStmt stmt) {
	if (w instanceof IfCmpStmt) {
	  IfCmpStmt s = (IfCmpStmt) w;
	  eq.value = stmt.trueTarget() == s.trueTarget() &&
	    stmt.falseTarget() == s.falseTarget() &&
	    stmt.comparison() == s.comparison();
	}
      }

      public void visitIfZeroStmt(IfZeroStmt stmt) {
	if (w instanceof IfZeroStmt) {
	  IfZeroStmt s = (IfZeroStmt) w;
	  eq.value = stmt.trueTarget() == s.trueTarget() &&
	    stmt.falseTarget() == s.falseTarget() &&
	    stmt.comparison() == s.comparison();
	}
      }


      public void visitSCStmt(SCStmt stmt) {
	if(w instanceof SCStmt) {
	  SCStmt s = (SCStmt)w;
	  eq.value = stmt.array() == s.array() &&
	    stmt.index() == s.index();
	}
      }

      public void visitSRStmt(SRStmt stmt) {
	if(w instanceof SRStmt) {
	  SRStmt s = (SRStmt)w;
	  eq.value = stmt.array() == s.array() &&
	    stmt.start() == s.start() &&
	    stmt.end() == s.end();
	}
      }

      public void visitInitStmt(InitStmt stmt) {
	if (w instanceof InitStmt) {
	  eq.value = true;
	}
      }

      public void visitGotoStmt(GotoStmt stmt) {
	if (w instanceof GotoStmt) {
	  GotoStmt s = (GotoStmt) w;
	  eq.value = stmt.target() == s.target();
	}
      }

      public void visitLabelStmt(LabelStmt stmt) {
	if (w instanceof LabelStmt) {
	  LabelStmt s = (LabelStmt) w;
	  eq.value = stmt.label().equals(s.label());
	}
      }

      public void visitMonitorStmt(MonitorStmt stmt) {
	if (w instanceof MonitorStmt) {
	  MonitorStmt s = (MonitorStmt) w;
	  eq.value = stmt.kind() == s.kind();
	}
      }

      public void visitPhiJoinStmt(PhiJoinStmt stmt) {
	if (w instanceof PhiJoinStmt) {
	  eq.value = true;
	}
      }

      public void visitPhiCatchStmt(PhiCatchStmt stmt) {
	if (w instanceof PhiCatchStmt) {
	  eq.value = true;
	}
      }

      public void visitCatchExpr(CatchExpr expr) {
	// Catches are not equivalent.
	eq.value = false;
      }

      public void visitStackManipStmt(StackManipStmt stmt) {
	if (w instanceof StackManipStmt) {
	  StackManipStmt s = (StackManipStmt) w;
	  eq.value = stmt.kind() == s.kind();
	}
      }

      public void visitRetStmt(RetStmt stmt) {
	if (w instanceof RetStmt) {
	  RetStmt s = (RetStmt) w;
	  eq.value = stmt.sub() == s.sub();
	}
      }

      public void visitReturnExprStmt(ReturnExprStmt stmt) {
	if (w instanceof ReturnExprStmt) {
	  eq.value = true;
	}
      }

      public void visitReturnStmt(ReturnStmt stmt) {
	if (w instanceof ReturnStmt) {
	  eq.value = true;
	}
      }

      public void visitAddressStoreStmt(AddressStoreStmt stmt) {
	if (w instanceof AddressStoreStmt) {
	  AddressStoreStmt s = (AddressStoreStmt) w;
	  eq.value = stmt.sub() == s.sub();
	}
      }

      public void visitStoreExpr(StoreExpr expr) {
	if (w instanceof StoreExpr) {
	  eq.value = true;
	}
      }

      public void visitJsrStmt(JsrStmt stmt) {
	if (w instanceof JsrStmt) {
	  JsrStmt s = (JsrStmt) w;
	  eq.value = stmt.sub() == s.sub();
	}
      }

      public void visitSwitchStmt(SwitchStmt stmt) {
	if (w instanceof SwitchStmt) {
	  eq.value = stmt == w;
	}
      }

      public void visitThrowStmt(ThrowStmt stmt) {
	if (w instanceof ThrowStmt) {
	  eq.value = true;
	}
      }

      public void visitArithExpr(ArithExpr expr) {
	if (w instanceof ArithExpr) {
	  ArithExpr e = (ArithExpr) w;
	  eq.value = e.operation() == expr.operation();
	}
      }

      public void visitArrayLengthExpr(ArrayLengthExpr expr) {
	if (w instanceof ArrayLengthExpr) {
	  eq.value = true;
	}
      }

      public void visitArrayRefExpr(ArrayRefExpr expr) {
	if (w instanceof ArrayRefExpr) {
	  eq.value = true;
	}
      }

      public void visitCallMethodExpr(CallMethodExpr expr) {
	// Calls are never equal.
	eq.value = false;
      }

      public void visitCallStaticExpr(CallStaticExpr expr) {
	// Calls are never equal.
	eq.value = false;
      }

      public void visitCastExpr(CastExpr expr) {
	if (w instanceof CastExpr) {
	  CastExpr e = (CastExpr) w;
	  eq.value = e.castType().equals(expr.castType());
	}
      }

      public void visitConstantExpr(ConstantExpr expr) {
	if (w instanceof ConstantExpr) {
	  ConstantExpr e = (ConstantExpr) w;
	  if (e.value() == null) {
	    eq.value = expr.value() == null;
	  } else {
	    eq.value = e.value().equals(expr.value());
	  }
	}
      }

      public void visitFieldExpr(FieldExpr expr) {
	if (w instanceof FieldExpr) {
	  FieldExpr e = (FieldExpr) w;
	  eq.value = e.field().equals(expr.field());
	}
      }

      public void visitInstanceOfExpr(InstanceOfExpr expr) {
	if (w instanceof InstanceOfExpr) {
	  InstanceOfExpr e = (InstanceOfExpr) w;
	  eq.value = e.checkType().equals(expr.checkType());
	}
      }

      public void visitLocalExpr(LocalExpr expr) {
	if (w instanceof LocalExpr) {
	  LocalExpr e = (LocalExpr) w;
	  eq.value = e.def() == expr.def();
	}
      }

      public void visitNegExpr(NegExpr expr) {
	if (w instanceof NegExpr) {
	  eq.value = true;
	}
      }

      public void visitNewArrayExpr(NewArrayExpr expr) {
	eq.value = false;
      }

      public void visitNewExpr(NewExpr expr) {
	eq.value = false;
      }

      public void visitNewMultiArrayExpr(NewMultiArrayExpr expr) {
	eq.value = false;
      }

      public void visitZeroCheckExpr(ZeroCheckExpr expr) {
	if (w instanceof ZeroCheckExpr) {
	  eq.value = true;
	}
      }

      public void visitRCExpr(RCExpr expr) {
	if (w instanceof RCExpr) {
	  eq.value = true;
	}
      }

      public void visitUCExpr(UCExpr expr) {
	if (w instanceof UCExpr) {
	  UCExpr e = (UCExpr) w;
	  eq.value = e.kind() == expr.kind();
	}
      }

      public void visitReturnAddressExpr(ReturnAddressExpr expr) {
	if (w instanceof ReturnAddressExpr) {
	  eq.value = true;
	}
      }

      public void visitShiftExpr(ShiftExpr expr) {
	if (w instanceof ShiftExpr) {
	  ShiftExpr e = (ShiftExpr) w;
	  eq.value = e.dir() == expr.dir();
	}
      }

      public void visitStackExpr(StackExpr expr) {
	if (w instanceof StackExpr) {
	  StackExpr e = (StackExpr) w;
	  eq.value = e.def() == expr.def();
	}
      }

      public void visitStaticFieldExpr(StaticFieldExpr expr) {
	if (w instanceof StaticFieldExpr) {
	  StaticFieldExpr e = (StaticFieldExpr) w;
	  eq.value = e.field().equals(expr.field());
	}
      }

      public void visitNode(Node node) {
	throw new RuntimeException("No method for " + node);
      }
    });

    return eq.value;
  }

  /** 
   * Computes a hash code for a given <tt>Node</tt> based upon its
   * type.  The hash code of nodes that are composed of other nodes
   * are based upon their composits.
   */
  public static int hashCode(final Node node) {
    class Int {
      int value = 0;
    };

    final Int hash = new Int();

    node.visit(new TreeVisitor() {
      public void visitExprStmt(ExprStmt stmt) {
	hash.value = 1;
      }

      public void visitIfCmpStmt(IfCmpStmt stmt) {
	hash.value = stmt.comparison() +
	  stmt.trueTarget().hashCode() +
	  stmt.falseTarget().hashCode();
      }

      public void visitIfZeroStmt(IfZeroStmt stmt) {
	hash.value = stmt.comparison() +
	  stmt.trueTarget().hashCode() +
	  stmt.falseTarget().hashCode();
      }

      public void visitInitStmt(InitStmt stmt) {
	hash.value = 2;
      }

      public void visitGotoStmt(GotoStmt stmt) {
	hash.value = stmt.target().hashCode();
      }

      public void visitLabelStmt(LabelStmt stmt) {
	hash.value = stmt.label().hashCode();
      }

      public void visitMonitorStmt(MonitorStmt stmt) {
	hash.value = stmt.kind();
      }

      public void visitPhiJoinStmt(PhiJoinStmt stmt) {
	hash.value = 3;
      }

      public void visitPhiCatchStmt(PhiCatchStmt stmt) {
	hash.value = 4;
      }

      public void visitCatchExpr(CatchExpr expr) {
	// Catches are not equivalent.
	hash.value = expr.hashCode();
      }

      public void visitStackManipStmt(StackManipStmt stmt) {
	hash.value = stmt.kind();
      }

      public void visitRetStmt(RetStmt stmt) {
	hash.value = 5;
      }

      public void visitReturnExprStmt(ReturnExprStmt stmt) {
	hash.value = 6;
      }

      public void visitReturnStmt(ReturnStmt stmt) {
	hash.value = 7;
      }

      public void visitAddressStoreStmt(AddressStoreStmt stmt) {
	hash.value = 8;
      }

      public void visitStoreExpr(StoreExpr expr) {
	hash.value = 9;
      }

      public void visitJsrStmt(JsrStmt stmt) {
	hash.value = 10;
      }

      public void visitSwitchStmt(SwitchStmt stmt) {
	hash.value = 11;
      }

      public void visitThrowStmt(ThrowStmt stmt) {
	hash.value = 12;
      }

      public void visitArithExpr(ArithExpr expr) {
	hash.value = expr.operation();
      }

      public void visitArrayLengthExpr(ArrayLengthExpr expr) {
	hash.value = 13;
      }

      public void visitArrayRefExpr(ArrayRefExpr expr) {
	hash.value = 14;
      }

      public void visitCallMethodExpr(CallMethodExpr expr) {
	// Calls are never equal.
	hash.value = expr.hashCode();
      }

      public void visitCallStaticExpr(CallStaticExpr expr) {
	// Calls are never equal.
	hash.value = expr.hashCode();
      }

      public void visitCastExpr(CastExpr expr) {
	hash.value = expr.castType().hashCode();
      }

      public void visitConstantExpr(ConstantExpr expr) {
	if (expr.value() == null) {
	  hash.value = 0;
	} else {
	  hash.value = expr.value().hashCode();
	}
      }

      public void visitFieldExpr(FieldExpr expr) {
	hash.value = expr.field().hashCode();
      }

      public void visitInstanceOfExpr(InstanceOfExpr expr) {
	hash.value = expr.checkType().hashCode();
      }

      public void visitLocalExpr(LocalExpr expr) {
	if (expr.def() != null) {
	  hash.value = expr.def().hashCode();
	} else {
	  hash.value = 0;
	}
      }

      public void visitNegExpr(NegExpr expr) {
	hash.value = 16;
      }

      public void visitNewArrayExpr(NewArrayExpr expr) {
	hash.value = expr.hashCode();
      }

      public void visitNewExpr(NewExpr expr) {
	hash.value = expr.hashCode();
      }

      public void visitNewMultiArrayExpr(NewMultiArrayExpr expr) {
	hash.value = expr.hashCode();
      }

      public void visitZeroCheckExpr(ZeroCheckExpr expr) {
	hash.value = 15;
      }

      public void visitRCExpr(RCExpr expr) {
	hash.value = 17;
      }

      public void visitUCExpr(UCExpr expr) {
	hash.value = 18 + expr.kind();
      }

      public void visitReturnAddressExpr(ReturnAddressExpr expr) {
	hash.value = 21;
      }

      public void visitSCStmt(SCStmt stmt) {
	hash.value = 23;
      }

      public void visitSRStmt(SRStmt stmt) {
	hash.value = 22;
      }

      public void visitShiftExpr(ShiftExpr expr) {
	hash.value = expr.dir();
      }

      public void visitStackExpr(StackExpr expr) {
	if (expr.def() != null) {
	  hash.value = expr.def().hashCode();
	} else {
	  hash.value = 0;
	}
      }

      public void visitStaticFieldExpr(StaticFieldExpr expr) {
	hash.value = expr.field().hashCode();
      }

      public void visitNode(Node node) {
	throw new RuntimeException("No method for " + node);
      }
    });

    return hash.value;
  }
}


