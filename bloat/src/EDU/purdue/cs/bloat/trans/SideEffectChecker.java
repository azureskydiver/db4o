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

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;
import java.util.*;

/** 
 * <tt>SideEffectChecker</tt> traverses a tree and determines is a
 * node has any side effects such as changing the stack, calling a
 * function, or performing a residency check.  The side effects are
 * represented by an integer whose bits represent a certain kind of
 * side effect.
 *
 * <p>
 *
 * <Tt>SideEffectChecker</tt> is a <tt>TreeVisitor</tt>.  The way it
 * works is that after a <tt>SideEffectChecker</tt> is reset, an
 * expression tree <tt>Node</tt> is visited to determine whether or
 * not it has side effects.  Neat.  
 */
public class SideEffectChecker extends TreeVisitor {
  private int sideEffects = 0;

  public static final int STACK = (1<<0);
  public static final int THROW = (1<<1);
  public static final int CALL  = (1<<2);
  public static final int SYNC  = (1<<3);
  public static final int ALLOC = (1<<4);  // Allocates memory
  public static final int RC    = (1<<5);
  public static final int UC    = (1<<6);
  public static final int STORE = (1<<7);
  public static final int ALIAS = (1<<8);
  public static final int VOLATILE = (1<<9);

  private EditorContext context;

  /** 
   * Constructor.  The <tt>Context</tt> is needed to determine whether
   * or not a field is VOLATILE, etc.
   */
  public SideEffectChecker(EditorContext context) {
    this.context = context;
  }

  public int sideEffects() {
    return sideEffects;
  }

  public boolean hasSideEffects()
  {
    return sideEffects != 0;
  }

  public void reset()
  {
    sideEffects = 0;
  }

  public void visitStoreExpr(StoreExpr expr)
  {
    sideEffects |= STORE;
    expr.visitChildren(this);
  }

  public void visitLocalExpr(LocalExpr expr)
  {
    if (expr.isDef()) {
      sideEffects |= STORE;
    }
    expr.visitChildren(this);
  }

  public void visitZeroCheckExpr(ZeroCheckExpr expr)
  {
    sideEffects |= THROW;
    expr.visitChildren(this);
  }

  public void visitRCExpr(RCExpr expr)
  {
    sideEffects |= RC;
    expr.visitChildren(this);
  }

  public void visitUCExpr(UCExpr expr)
  {
    sideEffects |= UC;
    expr.visitChildren(this);
  }

  public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)
  {
    // Memory allocation
    // NegativeArraySizeException
    sideEffects |= THROW | ALLOC;
    expr.visitChildren(this);
  }

  public void visitNewArrayExpr(NewArrayExpr expr)
  {
    // Memory allocation
    // NegativeArraySizeException
    sideEffects |= THROW | ALLOC;
    expr.visitChildren(this);
  }

  public void visitCatchExpr(CatchExpr expr)
  {
    // Stack change
    sideEffects |= STACK;
    expr.visitChildren(this);
  }

  public void visitNewExpr(NewExpr expr)
  {
    // Memory allocation
    sideEffects |= ALLOC;
    expr.visitChildren(this);
  }

  public void visitStackExpr(StackExpr expr)
  {
    // Stack change
    sideEffects |= STACK;

    if (expr.isDef()) {
      sideEffects |= STORE;
    }

    expr.visitChildren(this);
  }

  public void visitCastExpr(CastExpr expr)
  {
    // ClassCastException
    if (expr.castType().isReference()) {
      sideEffects |= THROW;
    }
    expr.visitChildren(this);
  }

  public void visitArithExpr(ArithExpr expr)
  {
    // DivideByZeroException -- handled by ZeroCheckExpr
    /*
      if (expr.operation() == ArithExpr.DIV ||
      expr.operation() == ArithExpr.REM) {

      if (expr.type().isIntegral() || expr.type().equals(Type.LONG)) {
      sideEffects |= THROW;
      }
      }
      */

    expr.visitChildren(this);
  }

  public void visitArrayLengthExpr(ArrayLengthExpr expr)
  {
    // NullPointerException
    sideEffects |= THROW;
    expr.visitChildren(this);
  }

  public void visitArrayRefExpr(ArrayRefExpr expr)
  {
    // NullPointerException, ArrayIndexOutOfBoundsException,
    // ArrayStoreException
    sideEffects |= THROW;

    if (expr.isDef()) {
      sideEffects |= STORE;
    }

    sideEffects |= ALIAS;

    expr.visitChildren(this);
  }

  public void visitFieldExpr(FieldExpr expr)
  {
    // NullPointerException -- handled by ZeroCheckExpr
    /*
      sideEffects |= THROW;
      */

    if (expr.isDef()) {
      sideEffects |= STORE;
    }

    MemberRef field = expr.field();

    try {
      FieldEditor e = context.editField(field);

      if (! e.isFinal()) {
	sideEffects |= ALIAS;
      }

      if (e.isVolatile()) {
	sideEffects |= VOLATILE;
      }

      context.release(e.fieldInfo());
    }
    catch (NoSuchFieldException e) {
      // A field wasn't found.  Silently assume it's not final and
      // is volatile.
      sideEffects |= ALIAS;
      sideEffects |= VOLATILE;
    }

    expr.visitChildren(this);
  }

  public void visitStaticFieldExpr(StaticFieldExpr expr)
  {
    if (expr.isDef()) {
      sideEffects |= STORE;
    }

    MemberRef field = expr.field();

    try {
      FieldEditor e = context.editField(field);

      if (e.isVolatile()) {
	sideEffects |= VOLATILE;
      }

      context.release(e.fieldInfo());
    }
    catch (NoSuchFieldException e) {
      // A field wasn't found.  Silently assume it's volatile.
      sideEffects |= VOLATILE;
    }

    expr.visitChildren(this);
  }

  public void visitCallStaticExpr(CallStaticExpr expr)
  {
    // Call
    sideEffects |= THROW | CALL;
    expr.visitChildren(this);
  }

  public void visitCallMethodExpr(CallMethodExpr expr)
  {
    // Call
    sideEffects |= THROW | CALL;
    expr.visitChildren(this);
  }

  public void visitMonitorStmt(MonitorStmt stmt)
  {
    // Synchronization
    sideEffects |= THROW | SYNC;
    stmt.visitChildren(this);
  }

  public void visitStackManipStmt(StackManipStmt stmt)
  {
    // Stack change
    sideEffects |= STACK;
    stmt.visitChildren(this);
  }
}
