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
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.util.*;
import java.io.*;
import java.util.*;

/**
 * PrintVistor traverses a Tree and prints some information about each visited
 * Node to a stream.
 */
public class PrintVisitor extends TreeVisitor {

  protected PrintWriter out;          // The stream to which we are printing

  /**
   * Constructor.  Prints to System.out.
   */  
  public PrintVisitor()
  {
    this(System.out);
  }
  
  public PrintVisitor(Writer out)
  {
    this.out = new PrintWriter(out);
  }
  
  public PrintVisitor(PrintStream out)
  {
    this.out = new PrintWriter(out);
  }
  
  protected void println()
  {
    out.println();
  }
  
  protected void println(Object s)
  {
    out.println(s);
  }
  
  protected void print(Object s)
  {
    out.print(s);
  }
  
  public void visitFlowGraph(FlowGraph cfg) {
    cfg.source().visit(this);
    
    Iterator e = cfg.trace().iterator();
    
    while (e.hasNext()) {
      Block block = (Block) e.next();
      block.visit(this);
    }
    
    cfg.sink().visit(this);
    
    this.out.flush();
  }
  
  public void visitBlock(Block block) {
    println();
    println(block);
    
    Handler handler = (Handler) block.graph().handlersMap().get(block);
    
    if (handler != null) {
      println("catches " + handler.catchType());
      println("protects " + handler.protectedBlocks());
    }
    
    block.visitChildren(this);
  }
  
  public void visitExprStmt(ExprStmt stmt) {
    print("eval ");
    stmt.expr().visit(this);
    println();
  }
  
  public void visitIfZeroStmt(IfZeroStmt stmt)
  {
    print("if0 (");
    stmt.expr().visit(this);
    print(" ");
    
    switch (stmt.comparison()) 
      {
      case IfStmt.EQ:
	print("==");
	break;
      case IfStmt.NE:
	print("!=");
	break;
      case IfStmt.GT:
	print(">");
	break;
      case IfStmt.GE:
	print(">=");
	break;
      case IfStmt.LT:
	print("<");
	break;
      case IfStmt.LE:
	print("<=");
	break;
      }
    
    if (stmt.expr().type().isReference()) {
      print(" null");
    }
    else {
      print(" 0");
    }
    
    print(") then " + stmt.trueTarget() + " else " + stmt.falseTarget());
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitIfCmpStmt(IfCmpStmt stmt)
  {
    print("if (");
    stmt.left().visit(this);
    print(" ");
    
    switch (stmt.comparison()) 
      {
      case IfStmt.EQ:
	print("==");
	break;
      case IfStmt.NE:
	print("!=");
	break;
      case IfStmt.GT:
	print(">");
	break;
      case IfStmt.GE:
	print(">=");
	break;
      case IfStmt.LT:
	print("<");
	break;
      case IfStmt.LE:
	print("<=");
	break;
      }
    
    print(" ");
    
    if (stmt.right() != null) {
      stmt.right().visit(this);
    }
    
    print(") then " + stmt.trueTarget() + " else " + stmt.falseTarget());
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitInitStmt(InitStmt stmt)
  {
    print("INIT");
    
    LocalExpr[] t = stmt.targets();
    
    if (t != null) {
      for (int i = 0; i < t.length; i++) {
	if (t[i] != null) {
	  print(" ");
	  t[i].visit(this);
	}
      }
    }
    
    println();
  }
  
  public void visitGotoStmt(GotoStmt stmt)
  {
    print("goto " + stmt.target().label());
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitLabelStmt(LabelStmt stmt)
  {
    if (stmt.label() != null) {
      println(stmt.label());
    }
  }
  
  public void visitMonitorStmt(MonitorStmt stmt)
  {
    if (stmt.kind() == MonitorStmt.ENTER) {
      print("enter ");
    }
    else {
      print("exit ");
    }
    
    print("monitor (");
    
    if (stmt.object() != null)
      stmt.object().visit(this);
    
    println(")");
  }
  
  public void visitCatchExpr(CatchExpr expr)
  {
    print("Catch(" + expr.catchType() + ")");
  }
  
  public void visitStackManipStmt(StackManipStmt stmt)
  {
    print("(");
    
    StackExpr[] target = stmt.target();
    
    if (target != null) {
      for (int i = 0; i < target.length; i++) {
	target[i].visit(this);
	if (i != target.length-1) {
	  print(", ");
	}
      }
    }
    
    String[] str = new String[] { "swap",
				    "dup", "dup_x1", "dup_x2",
				    "dup2", "dup2_x1", "dup2_x2" };
    
    print(") := " + str[stmt.kind()] + "(");
    
    StackExpr[] source = stmt.source();
    
    if (source != null) {
      for (int i = 0; i < source.length; i++) {
	source[i].visit(this);
	if (i != source.length-1) {
	  print(", ");
	}
      }
    }
    
    println(")");
  }
  
  public void visitPhiJoinStmt(PhiJoinStmt stmt)
  {
    if (stmt.target() != null) {
      stmt.target().visit(this);
    }
    
    print(" := Phi(");
    
    if (stmt.hasParent()) {
      Tree tree = (Tree) stmt.parent();
      Block block = tree.block();
      
      Iterator e = block.graph().preds(block).iterator();
      
      while (e.hasNext()) {
	Block pred = (Block) e.next();
	
	Expr operand = stmt.operandAt(pred);
	print(pred.label() + "=");
	operand.visit(this);
	
	if (e.hasNext()) {
	  print(", ");
	}
      }
    }
    else {
      Iterator e = stmt.operands().iterator();
      
      while (e.hasNext()) {
	Expr operand = (Expr) e.next();
	operand.visit(this);
	
	if (e.hasNext()) {
	  print(", ");
	}
      }
    }
    
    println(")");
  }
  
  public void visitPhiCatchStmt(PhiCatchStmt stmt)
  {
    if (stmt.target() != null) {
      stmt.target().visit(this);
    }
    
    print(" := Phi-Catch(");
    
    Iterator e = stmt.operands().iterator();
    
    while (e.hasNext()) {
      Expr operand = (Expr) e.next();
      operand.visit(this);
      
      if (e.hasNext()) {
	print(", ");
      }
    }
    
    println(")");
  }
  
  public void visitRetStmt(RetStmt stmt)
  {
    print("ret from " + stmt.sub());
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitReturnExprStmt(ReturnExprStmt stmt)
  {
    print("return ");
    
    if (stmt.expr() != null) {
      stmt.expr().visit(this);
    }
    
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitReturnStmt(ReturnStmt stmt)
  {
    print("return");
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitStoreExpr(StoreExpr expr)
  {
    print("(");
    
    if (expr.target() != null)
      expr.target().visit(this);
    
    print(" := ");
    
    if (expr.expr() != null)
      expr.expr().visit(this);
    
    print(")");
  }
  
  public void visitAddressStoreStmt(AddressStoreStmt stmt)
  {
    print("La");
    
    if (stmt.sub() != null)
      print(new Integer(stmt.sub().returnAddress().index()));
    else
      print("???");
    
    println(" := returnAddress");
  }
  
  public void visitJsrStmt(JsrStmt stmt)
  {
    print("jsr ");
    
    if (stmt.sub() != null)
      print(stmt.sub().entry());
    
    if (stmt.follow() != null)
      print(" ret to " + stmt.follow());
    
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitSwitchStmt(SwitchStmt stmt)
  {
    print("switch (");
    
    if (stmt.index() != null)
      stmt.index().visit(this);
    
    print(")");
    println(" caught by " + stmt.catchTargets());
    
    if (stmt.values() != null && stmt.targets() != null) {
      for (int i = 0; i < stmt.values().length; i++) {
	println("    case " + stmt.values()[i] +
		": " + stmt.targets()[i]);
      }
    }
    
    Block t = stmt.defaultTarget();
    
    println("    default: " + stmt.defaultTarget());
  }
  
  public void visitThrowStmt(ThrowStmt stmt)
  {
    print("throw ");
    
    if (stmt.expr() != null)
      stmt.expr().visit(this);
    
    println(" caught by " + stmt.catchTargets());
  }
  
  public void visitSCStmt(SCStmt stmt)
  {
    print("aswizzle ");
    if (stmt.array() != null)
      stmt.array().visit(this);
    if (stmt.index() != null)
      stmt.index().visit(this);        
  }
  
  public void visitSRStmt(SRStmt stmt)
  {
    print("aswrange array: ");
    if (stmt.array() != null)
      stmt.array().visit(this);
    print(" start: ");
    if (stmt.start() != null)
      stmt.start().visit(this);        
    print(" end: ");
    if (stmt.end() != null)
      stmt.end().visit(this);  	
    println("");
  }
  
  public void visitArithExpr(ArithExpr expr)
  {
    print("(");
    
    if (expr.left() != null)
      expr.left().visit(this);
    
    print(" ");
    
    switch (expr.operation()) 
      {
      case ArithExpr.ADD:
	print("+");
	break;
      case ArithExpr.SUB:
	print("-");
	break;
      case ArithExpr.DIV:
	print("/");
	break;
      case ArithExpr.MUL:
	print("*");
	break;
      case ArithExpr.REM:
	print("%");
	break;
      case ArithExpr.AND:
	print("&");
	break;
      case ArithExpr.IOR:
	print("|");
	break;
      case ArithExpr.XOR:
	print("^");
	break;
      case ArithExpr.CMP:
	print("<=>");
	break;
      case ArithExpr.CMPL:
	print("<l=>");
	break;
      case ArithExpr.CMPG:
	print("<g=>");
	break;
      }
    
    print(" ");
    if (expr.right() != null)
      expr.right().visit(this);
    print(")");
  }
  
  public void visitArrayLengthExpr(ArrayLengthExpr expr)
  {
    if (expr.array() != null)
      expr.array().visit(this);
    print(".length");
  }
  
  public void visitArrayRefExpr(ArrayRefExpr expr)
  {
    if (expr.array() != null)
      expr.array().visit(this);
    print("[");
    if (expr.index() != null)
      expr.index().visit(this);
    print("]");
  }
  
  public void visitCallMethodExpr(CallMethodExpr expr)
  {
    if (expr.receiver() != null)
      expr.receiver().visit(this);
    
    print(".");
    if (expr.method() != null)
      print(expr.method().nameAndType().name());
    print("(");
    
    if (expr.params() != null) {
      for (int i = 0; i < expr.params().length; i++) {
	expr.params()[i].visit(this);
	if (i != expr.params().length-1) {
	  print(", ");
	}
      }
    }
    
    print(")");
  }
  
  public void visitCallStaticExpr(CallStaticExpr expr)
  {
    if (expr.method() != null)
      print(expr.method().declaringClass());
    
    print(".");
    if (expr.method() != null)
      print(expr.method().nameAndType().name());
    print("(");
    
    if (expr.params() != null) {
      for (int i = 0; i < expr.params().length; i++) {
	expr.params()[i].visit(this);
	if (i != expr.params().length-1) {
	  print(", ");
	}
      }
    }
    
    print(")");
  }
  
  public void visitCastExpr(CastExpr expr)
  {
    print("((" + expr.castType() + ") ");
    if (expr.expr() != null)
      expr.expr().visit(this);
    print(")");
  }
  
  public void visitConstantExpr(ConstantExpr expr)
  {
    if (expr.value() instanceof String) {
      StringBuffer sb = new StringBuffer();
      
      String s = (String) expr.value();
      
      for (int i = 0; i < s.length(); i++) {
	char c = s.charAt(i);
	if (Character.isWhitespace(c) || (0x20 <= c && c <= 0x7e)) {
	  sb.append(c);
	}
	else {
	  sb.append("\\u");
	  sb.append(Integer.toHexString(c));
	}
	
	if (sb.length() > 50) {
	  sb.append("...");
	  break;
	}
      }
      
      print("'" + sb.toString() + "'");
    }
    else if (expr.value() instanceof Float) {
      print(expr.value() + "F");
    }
    else if (expr.value() instanceof Long) {
      print(expr.value() + "L");
    }
    else {
      print(expr.value());
    }
  }
  
  public void visitFieldExpr(FieldExpr expr)
  {
    if (expr.object() != null)
      expr.object().visit(this);
    print(".");
    if (expr.field() != null)
      print(expr.field().nameAndType().name());
  }
  
  public void visitInstanceOfExpr(InstanceOfExpr expr)
  {
    if (expr.expr() != null)
      expr.expr().visit(this);
    print(" instanceof " + expr.checkType());
  }
  
  public void visitLocalExpr(LocalExpr expr)
  {
    if (expr.fromStack()) {
      print("T");
    }
    else {
      print("L");
    }
    
    print(expr.type().shortName().toLowerCase());
    print(Integer.toString(expr.index()));
    
    DefExpr def = expr.def();
    
    if (def == null || def.version() == -1) {
      print("_undef");

    } else {
      print("_" + def.version());
    }
  }
  
  public void visitNegExpr(NegExpr expr)
  {
    print("-");
    if (expr.expr() != null)
      expr.expr().visit(this);
  }
  
  public void visitNewArrayExpr(NewArrayExpr expr)
  {
    print("new " + expr.elementType() + "[");
    if (expr.size() != null)
      expr.size().visit(this);
    print("]");
  }
  
  public void visitNewExpr(NewExpr expr)
  {
    print("new " + expr.objectType());
  }
  
  public void visitNewMultiArrayExpr(NewMultiArrayExpr expr)
  {
    print("new " + expr.elementType());
    
    if (expr.dimensions() != null) {
      for (int i = 0; i < expr.dimensions().length; i++) {
	print("[" + expr.dimensions()[i] + "]");
      }
    }
  }
  
  public void visitZeroCheckExpr(ZeroCheckExpr expr)
  {
    if (expr.expr().type().isReference()) {
      print("notNull(");
    }
    else {
      print("notZero(");
    }
    
    if (expr.expr() != null)
      expr.expr().visit(this);
    
    print(")");
  }
  
  public void visitRCExpr(RCExpr expr)
  {
    print("rc(");
    if (expr.expr() != null)
      expr.expr().visit(this);
    print(")");
  }
  
  public void visitUCExpr(UCExpr expr)
  {
    if (expr.kind() == UCExpr.POINTER) {
      print("aupdate(");
    }
    else {
      print("supdate(");
    }
    
    if (expr.expr() != null)
      expr.expr().visit(this);
    print(")");
  }
  
  public void visitReturnAddressExpr(ReturnAddressExpr expr)
  {
    print("returnAddress");
  }
  
  public void visitShiftExpr(ShiftExpr expr)
  {
    print("(");
    if (expr.expr() != null)
      expr.expr().visit(this);
    
    if (expr.dir() == ShiftExpr.LEFT) {
      print("<<");
    }
    else if (expr.dir() == ShiftExpr.RIGHT) {
      print(">>");
    }
    else if (expr.dir() == ShiftExpr.UNSIGNED_RIGHT) {
      print(">>>");
    }
    
    if (expr.bits() != null)
      expr.bits().visit(this);
    print(")");
  }
  
  public void visitStackExpr(StackExpr expr)
  {
    print("S" + expr.type().shortName().toLowerCase() + expr.index());
    
    DefExpr def = expr.def();
    
    if (def == null || def.version() == -1) {
      print("_undef");
    }
    else {
      print("_" + def.version());
    }
  }
  
  public void visitStaticFieldExpr(StaticFieldExpr expr)
  {
    if (expr.field() != null) {
      print(expr.field().declaringClass() + "." +
	    expr.field().nameAndType().name());
    }
  }
  
  public void visitExpr(Expr expr)
  {
    print("EXPR");
  }
  
  public void visitStmt(Stmt stmt)
  {
    print("STMT");
  }
}
