/* RemovePopAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.ListIterator;

import jode.AssertError;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.obfuscator.CodeTransformer;

public class RemovePopAnalyzer implements CodeTransformer, Opcodes
{
    public void transformCode(BytecodeInfo bytecodeinfo) {
	int[] is = new int[2];
	ListIterator listiterator
	    = bytecodeinfo.getInstructions().listIterator();
	while (listiterator.hasNext()) {
	    Instruction instruction = (Instruction) listiterator.next();
	    boolean bool = false;
	switch_2_:
	    switch (instruction.getOpcode()) {
	    case 0:
		listiterator.remove();
		break;
	    case 88:
		bool = true;
		/* fall through */
	    case 87:
		if (instruction.getPreds() == null) {
		    Handler[] handlers = bytecodeinfo.getExceptionHandlers();
		    for (int i = 0; i < handlers.length; i++) {
			if (handlers[i].catcher == instruction)
			    break switch_2_;
		    }
		    listiterator.remove();
		    Instruction instruction_0_
			= (Instruction) listiterator.previous();
		    Instruction instruction_1_ = instruction_0_;
		    int i = 0;
		    for (;;) {
			if (instruction_1_.getSuccs() != null
			    || instruction_1_.doesAlwaysJump()) {
			    instruction_1_ = null;
			    break;
			}
			instruction_1_.getStackPopPush(is);
			if (i < is[1]) {
			    if (i == 0)
				break;
			    int i_2_ = instruction_1_.getOpcode();
			    if (i <= 3 && i_2_ == 89 + i - 1) {
				listiterator.remove();
				if (!bool)
				    break switch_2_;
				instruction = new Instruction(87);
				bool = false;
				instruction_1_
				    = (Instruction) listiterator.previous();
			    } else {
				if (bool && i > 1 && i <= 4
				    && i_2_ == 92 + i - 2)
				    listiterator.remove();
				else {
				    instruction_1_ = null;
				    break;
				}
				break switch_2_;
			    }
			} else {
			    i += is[0] - is[1];
			    instruction_1_
				= (Instruction) listiterator.previous();
			}
		    }
		    if (instruction_1_ == null) {
			while (listiterator.next() != instruction_0_) {
			    /* empty */
			}
			if (!bool && instruction_0_.getOpcode() == 87)
			    listiterator.set(new Instruction(88));
			else
			    listiterator.add(instruction);
		    } else {
			int i_3_ = instruction_1_.getOpcode();
			switch (i_3_) {
			case 20:
			case 22:
			case 24:
			    if (!bool)
				throw new AssertError("pop on long");
			    listiterator.remove();
			    break switch_2_;
			case 18:
			case 21:
			case 23:
			case 25:
			case 89:
			case 187:
			    if (bool)
				listiterator.set(new Instruction(87));
			    else
				listiterator.remove();
			    break switch_2_;
			case 46:
			case 48:
			case 50:
			case 51:
			case 52:
			case 53:
			case 96:
			case 98:
			case 100:
			case 102:
			case 104:
			case 106:
			case 108:
			case 110:
			case 112:
			case 114:
			case 120:
			case 122:
			case 124:
			case 126:
			case 128:
			case 130:
			case 149:
			case 150:
			    listiterator.next();
			    listiterator.add(instruction);
			    listiterator.previous();
			    listiterator.previous();
			    listiterator.set(new Instruction(87));
			    break switch_2_;
			case 90:
			    listiterator.set(new Instruction(95));
			    listiterator.next();
			    if (bool)
				listiterator.add(new Instruction(87));
			    break switch_2_;
			case 92:
			    if (bool)
				listiterator.remove();
			    else
				break;
			    break switch_2_;
			case 95:
			    if (bool)
				listiterator.set(instruction);
			    else
				break;
			    break switch_2_;
			case 47:
			case 49:
			case 117:
			case 119:
			case 138:
			case 143:
			    if (!bool)
				throw new AssertError("pop on long");
			    /* fall through */
			case 116:
			case 118:
			case 134:
			case 139:
			case 145:
			case 146:
			case 147:
			case 188:
			case 189:
			case 190:
			case 193:
			    listiterator.set(instruction);
			    break switch_2_;
			case 136:
			case 137:
			case 142:
			case 144:
			    if (bool) {
				listiterator.next();
				listiterator.add(new Instruction(87));
				listiterator.previous();
				listiterator.previous();
			    }
			    listiterator.set(new Instruction(88));
			    break switch_2_;
			case 97:
			case 99:
			case 101:
			case 103:
			case 105:
			case 107:
			case 109:
			case 111:
			case 113:
			case 115:
			case 127:
			case 129:
			case 131:
			    if (!bool)
				throw new AssertError("pop on long");
			    listiterator.next();
			    listiterator.add(instruction);
			    listiterator.previous();
			    listiterator.previous();
			    listiterator.set(new Instruction(88));
			    break switch_2_;
			case 121:
			case 123:
			case 125:
			    if (!bool)
				throw new AssertError("pop on long");
			    listiterator.next();
			    listiterator.add(instruction);
			    listiterator.previous();
			    listiterator.previous();
			    listiterator.set(new Instruction(87));
			    break switch_2_;
			case 133:
			case 135:
			case 140:
			case 141:
			    if (!bool)
				throw new AssertError("pop on long");
			    listiterator.set(new Instruction(87));
			    break switch_2_;
			case 148:
			case 151:
			case 152:
			    listiterator.next();
			    listiterator.add(new Instruction(88));
			    if (bool) {
				listiterator.add(new Instruction(87));
				listiterator.previous();
			    }
			    listiterator.previous();
			    listiterator.previous();
			    listiterator.set(new Instruction(88));
			    break switch_2_;
			case 178:
			case 180: {
			    Reference reference
				= instruction_1_.getReference();
			    int i_4_ = TypeSignature
					   .getTypeSize(reference.getType());
			    if (i_4_ == 2 && !bool)
				throw new AssertError("pop on long");
			    if (i_3_ == 180)
				i_4_--;
			    switch (i_4_) {
			    case 0:
				listiterator.set(instruction);
				break;
			    case 1:
				if (bool) {
				    listiterator.set(new Instruction(87));
				    break;
				}
				/* fall through */
			    case 2:
				listiterator.remove();
				break;
			    }
			    break switch_2_;
			}
			case 197: {
			    int i_5_ = instruction_1_.getDimensions();
			    if (--i_5_ > 0) {
				listiterator.next();
				while (i_5_-- > 0) {
				    listiterator.add(new Instruction(87));
				    listiterator.previous();
				}
				listiterator.previous();
			    }
			    listiterator.set(instruction);
			    break switch_2_;
			}
			case 182:
			case 183:
			case 184:
			case 185:
			    if (TypeSignature.getReturnSize(instruction_1_
								.getReference
								().getType())
				!= 1)
				break;
			    /* fall through */
			case 192:
			    if (bool) {
				listiterator.next();
				listiterator.add(new Instruction(87));
				listiterator.add(new Instruction(87));
				listiterator.previous();
			    } else
				break;
			    break switch_2_;
			}
			listiterator.next();
			listiterator.add(instruction);
		    }
		}
		break;
	    }
	}
    }
}
