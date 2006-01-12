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

import java.util.*;
import EDU.purdue.cs.bloat.editor.*;


public class StackOpt extends InstructionAdapter implements Opcode {

    int stackHeight;
    int minStackHeight;
    UseMap uMap;
    static final boolean DEBUG = false;

    public void transform(MethodEditor method) {

	uMap = method.uMap();

	for (int i = method.codeLength() - 1; i > 0; i--) {

	    Instruction inst;
	    boolean isWide;
	    Object codeEl = method.codeElementAt(i);

	    if (codeEl instanceof Instruction
		&& ((Instruction) codeEl).isLoad())
		inst = (Instruction) codeEl;
	    else continue;

	    switch(inst.opcodeClass()) {

	    case opcx_iload:
	    case opcx_fload:
	    case opcx_aload:
	    case opcx_iaload:
	    case opcx_faload:
	    case opcx_aaload:
	    case opcx_baload:
	    case opcx_caload:
	    case opcx_saload:
		isWide = false;
		break;
	    case opcx_lload:
	    case opcx_dload:
	    case opcx_laload:
	    case opcx_daload:
	    default:
		isWide = true;
	    }
		
	    
	    stackHeight = 0;

	    for (int j = i - 1; ; j--) {


		// stop at the begining of the code or a basic block.
		if (j <= 0 ||     // this should be redundant, but to be safe
		    method.codeElementAt(j) instanceof Label)
		    break;
		
		if (stackHeight == -1
		    && ((uMap.hasSameDef(inst, 
					 ((Instruction) method
					  .codeElementAt(j)))
			 && ((Instruction) method.codeElementAt(j)).isLoad())
			|| dupRun(method, j, inst))) {


		    if (forwardCountCheck(method, j, i, -1)) {
		    // found a type 0 relation with a load
			if (DEBUG)
			    System.err.println(
	 "load type 0: " + ((Instruction) method.codeElementAt(j)).toString()
	 + " " + inst.toString());

			if (isWide)
			    method.insertCodeAt(new Instruction(opc_dup2),
						j+1);
			else
			    method.insertCodeAt(new Instruction(opc_dup), 
						j+1); //add dup
			i++; //code has changed; why don't method editors
     			     // have iterators?
			method.removeCodeAt(i);    // remove load
		    }
		    break;   //done, even if final check failed.
		}
		    
		else if (stackHeight == 0
			 && uMap.hasSameDef(inst, 
					    ((Instruction) method
					     .codeElementAt(j)))) {
		    if (((Instruction) method.codeElementAt(j)).isStore()) {

			if (forwardCountCheck(method, j, i, 0)) {
			    // found a type 0  with a store
			    if (DEBUG)
			    System.err.println(
	 "store type 0: " + ((Instruction) method.codeElementAt(j)).toString()
	 + " " + inst.toString());


			    if (isWide)
				method.insertCodeAt(new Instruction(opc_dup2), j);
			    else
				method.insertCodeAt(new Instruction(opc_dup), j);
			    i++;
			    method.removeCodeAt(i);
			}
			break;
		    }
		    else if (((Instruction) method.codeElementAt(j)).isLoad()
			     && !isWide) {  //can't do type 1s with wides.

			if (forwardCountCheck(method, j, i, -1)) {
			    // found a type 1 with a load
			if (DEBUG)
			    System.err.println(
	 "load type 1: " + ((Instruction) method.codeElementAt(j)).toString()
	 + " " + inst.toString());

			    method.insertCodeAt(new Instruction(opc_dup), j+1); 
			    i++;
			    method.replaceCodeAt(new Instruction(opc_swap), i);
			}
			break;
		    }
		}

		else if (stackHeight == 1
			 && uMap.hasSameDef(inst, 
					    ((Instruction) method
					     .codeElementAt(j)))) {
		    if (((Instruction) method.codeElementAt(j)).isStore()
			&& !isWide) {    //can't do type 1 with wides
			
			if (forwardCountCheck(method, j, i, 0)) {
			    // type 1 for stores
			if (DEBUG)
			    System.err.println(
	 "store type 1: " + ((Instruction) method.codeElementAt(j)).toString()
	 + " " + inst.toString());

			    method.insertCodeAt(new Instruction(opc_dup), j);
			    i++;
			    method.replaceCodeAt(new Instruction(opc_swap), i);
			}
			break;
		    }
		}

		heightChange(method.codeElementAt(j));
//System.err.print(stackHeight + ";");
	    }
	}

    }	

    boolean forwardCountCheck(MethodEditor m, int j, int i, int bound) {
	
	stackHeight = 0;
	minStackHeight = 0;

	for (int k = j + 1; k < i; k++) {
	    heightChange(m.codeElementAt(k));
	    if (minStackHeight < bound) return false;
	}
	
	return true;
    }

    boolean dupRun(MethodEditor m, int j, Instruction inst) {
	
	if (((Instruction) m.codeElementAt(j)).opcodeClass() == opcx_dup) 

	    for (int k = j - 1; ; k--) {
		if (m.codeElementAt(k) instanceof Instruction) {
		    if (((Instruction) m.codeElementAt(k)).opcodeClass()
			== opcx_dup)
			continue;
		    else if (((Instruction) m.codeElementAt(k)).isLoad()
			     && uMap.hasSameDef(inst, 
						((Instruction) m
						 .codeElementAt(k))))
			return true;
		}
		break;
	    }

	return false;
    }

		

    void heightChange(Object inst) {
	
	if (inst instanceof Instruction)
	    ((Instruction) inst).visit(this);
	
    }
    
    public void visit_nop(Instruction inst) {
	stackHeight += 0;
    }

    public void visit_ldc(Instruction inst) {
	Object operand = inst.operand();
	
	if(operand instanceof Long || operand instanceof Double) {
	    stackHeight += 2;
	    
	} else {
	    stackHeight += 1;
	}
    } 

  public void visit_iload(Instruction inst) {
    stackHeight += 1;
  }

  public void visit_lload(Instruction inst) {
    stackHeight += 2;
  }

  public void visit_fload(Instruction inst) {
    stackHeight += 1;
  }

  public void visit_dload(Instruction inst) {
    stackHeight += 2;
  }

  public void visit_aload(Instruction inst) {
    stackHeight += 1;
  }

  public void visit_iaload(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

    public void visit_laload(Instruction inst) {
	stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
    }

  public void visit_faload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_daload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight +=2;
  }

  public void visit_aaload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_baload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_caload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_saload(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_istore(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_lstore(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_fstore(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_dstore(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_astore(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_iastore(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_lastore(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_fastore(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_dastore(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_aastore(Instruction inst) { 
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_bastore(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_castore(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_sastore(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_pop(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_pop2(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_dup(Instruction inst) {
    stackHeight += 1;
  }

  public void visit_dup_x1(Instruction inst) {
      stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 3;
  }

  public void visit_dup_x2(Instruction inst) {
      stackHeight -= 3;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 4;
  }

  public void visit_dup2(Instruction inst) {
    stackHeight += 2;
  }

  public void visit_dup2_x1(Instruction inst) {
      stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 5;
  }

  public void visit_dup2_x2(Instruction inst) {
      stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 6;
  }

    public void visit_swap(Instruction inst) {
	stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
    }

  public void visit_iadd(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;

  }

  public void visit_ladd(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_fadd(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dadd(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_isub(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lsub(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_fsub(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dsub(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_imul(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lmul(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_fmul(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dmul(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_idiv(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_ldiv(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_fdiv(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_ddiv(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_irem(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lrem(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_frem(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_drem(Instruction inst) {
    stackHeight -= 4;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }
  public void visit_ineg(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }


  public void visit_lneg(Instruction inst) {
      stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_fneg(Instruction inst) {
      stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dneg(Instruction inst) {
      stackHeight -= 2;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
      
}

  public void visit_ishl(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lshl(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_ishr(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lshr(Instruction inst) {
    stackHeight -= 3;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_iushr(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lushr(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_iand(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_land(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_ior(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lor(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_ixor(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lxor(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

    public void visit_iinc(Instruction inst) {

    }

  public void visit_i2l(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 2;
  }

  public void visit_i2f(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_i2d(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

	stackHeight += 2;
  }

  public void visit_l2i(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_l2f(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }
  public void visit_l2d(Instruction inst) {
      stackHeight -= 2;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_f2i(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_f2l(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 2;
  }

  public void visit_f2d(Instruction inst) {
      stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += 2;
  }

  public void visit_d2i(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_d2l(Instruction inst) {
      stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 2;
  }

  public void visit_d2f(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_i2b(Instruction inst) {
      stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }


  public void visit_i2c(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }


  public void visit_i2s(Instruction inst) {
      stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_lcmp(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_fcmpl(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_fcmpg(Instruction inst) {
    stackHeight -= 2;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dcmpl(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_dcmpg(Instruction inst) {
    stackHeight -= 4;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
  }

  public void visit_ifeq(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_ifne(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_iflt(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_ifge(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_ifgt(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_ifle(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmpeq(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmpne(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmplt(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmpge(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmpgt(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_icmple(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_if_acmpeq(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_if_acmpne(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_goto(Instruction inst) {
      
  }

  public void visit_jsr(Instruction inst) {
    stackHeight += 1;
  }

  public void visit_ret(Instruction inst) {}

  public void visit_switch(Instruction inst) {
    stackHeight -= 1;
  }

  public void visit_ireturn(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_lreturn(Instruction inst) {
      stackHeight -= 2;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_freturn(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_dreturn(Instruction inst) {
      stackHeight -=2;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_areturn(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }
  public void visit_return(Instruction inst) {}

  public void visit_getstatic(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight += type.stackHeight();
  }

  public void visit_putstatic(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight();
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_putstatic_nowb(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight();
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_getfield(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();

    stackHeight -= 1;
    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += type.stackHeight();
  }

  public void visit_putfield(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight() + 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_putfield_nowb(Instruction inst) {
    Type type = ((MemberRef) inst.operand()).nameAndType().type();
    stackHeight -= type.stackHeight() + 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_invokevirtual(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight -= type.stackHeight() + 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

    stackHeight += type.returnType().stackHeight();
  }

  public void visit_invokespecial(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight -= type.stackHeight() + 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;


    stackHeight += type.returnType().stackHeight();


  }

  public void visit_invokestatic(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight -= type.stackHeight();
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;


    stackHeight += type.returnType().stackHeight();
  }

  public void visit_invokeinterface(Instruction inst) {
    MemberRef method = (MemberRef) inst.operand();
    Type type = method.nameAndType().type();

    stackHeight -= type.stackHeight() + 1;

    	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

	stackHeight += type.returnType().stackHeight();
  }

  public void visit_new(Instruction inst) {
    stackHeight += 1;
  }
  public void visit_newarray(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
      
  }

  public void visit_arraylength(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight +=1;
  }

  public void visit_athrow(Instruction inst) {
      stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }


  public void visit_checkcast(Instruction inst) {
      
  }

  public void visit_instanceof(Instruction inst) {
      stackHeight -= 1;
      	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
	stackHeight += 1;
      
  }
 


  public void visit_monitorenter(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_monitorexit(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;

  }

  public void visit_multianewarray(Instruction inst) {
    MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
    int dim = operand.dimensions();

    stackHeight -= dim;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;


    stackHeight += 1;
  }

  public void visit_ifnull(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_ifnonnull(Instruction inst) {
    stackHeight -= 1;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_rc(Instruction inst) {}
  public void visit_aupdate(Instruction inst) {}
  public void visit_supdate(Instruction inst) {}

  public void visit_aswizzle(Instruction inst) {
    stackHeight -= 2;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }

  public void visit_aswrange(Instruction inst) {
    stackHeight -= 3;
	if (stackHeight < minStackHeight)
	    minStackHeight = stackHeight;
  }
	

}	      
		

