using System;
using System.Collections;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CecilUtilities
{
	public class InstructionDispatcher
	{
		public static void Dispatch(IInstruction instruction, IInstructionVisitor visitor)
		{
			InstructionVisitorDelegate handler = (InstructionVisitorDelegate)_handlers[instruction.OpCode.Value];
			if (null == handler) throw new ArgumentException(CecilFormatter.FormatInstruction(instruction), "instruction");
			handler(visitor, instruction);
		}

		delegate void InstructionVisitorDelegate(IInstructionVisitor visitor, IInstruction instruction);

		static IDictionary _handlers = new Hashtable();

		static InstructionDispatcher()
		{
			Bind(new InstructionVisitorDelegate(DispatchNop), OpCodes.Nop);
			Bind(new InstructionVisitorDelegate(DispatchBreak), OpCodes.Break);
			Bind(new InstructionVisitorDelegate(DispatchLdarg_0), OpCodes.Ldarg_0);
			Bind(new InstructionVisitorDelegate(DispatchLdarg_1), OpCodes.Ldarg_1);
			Bind(new InstructionVisitorDelegate(DispatchLdarg_2), OpCodes.Ldarg_2);
			Bind(new InstructionVisitorDelegate(DispatchLdarg_3), OpCodes.Ldarg_3);
			Bind(new InstructionVisitorDelegate(DispatchLdloc_0), OpCodes.Ldloc_0);
			Bind(new InstructionVisitorDelegate(DispatchLdloc_1), OpCodes.Ldloc_1);
			Bind(new InstructionVisitorDelegate(DispatchLdloc_2), OpCodes.Ldloc_2);
			Bind(new InstructionVisitorDelegate(DispatchLdloc_3), OpCodes.Ldloc_3);
			Bind(new InstructionVisitorDelegate(DispatchStloc_0), OpCodes.Stloc_0);
			Bind(new InstructionVisitorDelegate(DispatchStloc_1), OpCodes.Stloc_1);
			Bind(new InstructionVisitorDelegate(DispatchStloc_2), OpCodes.Stloc_2);
			Bind(new InstructionVisitorDelegate(DispatchStloc_3), OpCodes.Stloc_3);
			Bind(new InstructionVisitorDelegate(DispatchLdarg), OpCodes.Ldarg, OpCodes.Ldarg_S);
			Bind(new InstructionVisitorDelegate(DispatchLdarga), OpCodes.Ldarga, OpCodes.Ldarga_S);
			Bind(new InstructionVisitorDelegate(DispatchStarg), OpCodes.Starg, OpCodes.Starg_S);
			Bind(new InstructionVisitorDelegate(DispatchLdloc), OpCodes.Ldloc, OpCodes.Ldloc_S);
			Bind(new InstructionVisitorDelegate(DispatchLdloca), OpCodes.Ldloca, OpCodes.Ldloca_S);
			Bind(new InstructionVisitorDelegate(DispatchStloc), OpCodes.Stloc, OpCodes.Stloc_S);
			Bind(new InstructionVisitorDelegate(DispatchLdnull), OpCodes.Ldnull);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_M1), OpCodes.Ldc_I4_M1);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_0), OpCodes.Ldc_I4_0);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_1), OpCodes.Ldc_I4_1);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_2), OpCodes.Ldc_I4_2);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_3), OpCodes.Ldc_I4_3);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_4), OpCodes.Ldc_I4_4);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_5), OpCodes.Ldc_I4_5);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_6), OpCodes.Ldc_I4_6);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_7), OpCodes.Ldc_I4_7);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4_8), OpCodes.Ldc_I4_8);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I4), OpCodes.Ldc_I4, OpCodes.Ldc_I4_S);
			Bind(new InstructionVisitorDelegate(DispatchLdc_I8), OpCodes.Ldc_I8);
			Bind(new InstructionVisitorDelegate(DispatchLdc_R4), OpCodes.Ldc_R4);
			Bind(new InstructionVisitorDelegate(DispatchLdc_R8), OpCodes.Ldc_R8);
			Bind(new InstructionVisitorDelegate(DispatchDup), OpCodes.Dup);
			Bind(new InstructionVisitorDelegate(DispatchPop), OpCodes.Pop);
			Bind(new InstructionVisitorDelegate(DispatchJmp), OpCodes.Jmp);
			Bind(new InstructionVisitorDelegate(DispatchCall), OpCodes.Call);
			Bind(new InstructionVisitorDelegate(DispatchCalli), OpCodes.Calli);
			Bind(new InstructionVisitorDelegate(DispatchRet), OpCodes.Ret);
			Bind(new InstructionVisitorDelegate(DispatchBr), OpCodes.Br, OpCodes.Br_S);
			Bind(new InstructionVisitorDelegate(DispatchBrfalse), OpCodes.Brfalse, OpCodes.Brfalse_S);
			Bind(new InstructionVisitorDelegate(DispatchBrtrue), OpCodes.Brtrue, OpCodes.Brtrue_S);
			Bind(new InstructionVisitorDelegate(DispatchBeq), OpCodes.Beq, OpCodes.Beq_S);
			Bind(new InstructionVisitorDelegate(DispatchBge), OpCodes.Bge, OpCodes.Bge_S);
			Bind(new InstructionVisitorDelegate(DispatchBgt), OpCodes.Bgt, OpCodes.Bgt_S);
			Bind(new InstructionVisitorDelegate(DispatchBle), OpCodes.Ble, OpCodes.Ble_S);
			Bind(new InstructionVisitorDelegate(DispatchBlt), OpCodes.Blt, OpCodes.Blt_S);
			Bind(new InstructionVisitorDelegate(DispatchBne_Un), OpCodes.Bne_Un, OpCodes.Bne_Un_S);
			Bind(new InstructionVisitorDelegate(DispatchBge_Un), OpCodes.Bge_Un, OpCodes.Bge_Un_S);
			Bind(new InstructionVisitorDelegate(DispatchBgt_Un), OpCodes.Bgt_Un, OpCodes.Bgt_Un_S);
			Bind(new InstructionVisitorDelegate(DispatchBle_Un), OpCodes.Ble_Un, OpCodes.Ble_Un_S);
			Bind(new InstructionVisitorDelegate(DispatchBlt_Un), OpCodes.Blt_Un, OpCodes.Blt_Un_S);
			Bind(new InstructionVisitorDelegate(DispatchSwitch), OpCodes.Switch);
			Bind(new InstructionVisitorDelegate(DispatchLdind_I1), OpCodes.Ldind_I1);
			Bind(new InstructionVisitorDelegate(DispatchLdind_U1), OpCodes.Ldind_U1);
			Bind(new InstructionVisitorDelegate(DispatchLdind_I2), OpCodes.Ldind_I2);
			Bind(new InstructionVisitorDelegate(DispatchLdind_U2), OpCodes.Ldind_U2);
			Bind(new InstructionVisitorDelegate(DispatchLdind_I4), OpCodes.Ldind_I4);
			Bind(new InstructionVisitorDelegate(DispatchLdind_U4), OpCodes.Ldind_U4);
			Bind(new InstructionVisitorDelegate(DispatchLdind_I8), OpCodes.Ldind_I8);
			Bind(new InstructionVisitorDelegate(DispatchLdind_I), OpCodes.Ldind_I);
			Bind(new InstructionVisitorDelegate(DispatchLdind_R4), OpCodes.Ldind_R4);
			Bind(new InstructionVisitorDelegate(DispatchLdind_R8), OpCodes.Ldind_R8);
			Bind(new InstructionVisitorDelegate(DispatchLdind_Ref), OpCodes.Ldind_Ref);
			Bind(new InstructionVisitorDelegate(DispatchStind_Ref), OpCodes.Stind_Ref);
			Bind(new InstructionVisitorDelegate(DispatchStind_I1), OpCodes.Stind_I1);
			Bind(new InstructionVisitorDelegate(DispatchStind_I2), OpCodes.Stind_I2);
			Bind(new InstructionVisitorDelegate(DispatchStind_I4), OpCodes.Stind_I4);
			Bind(new InstructionVisitorDelegate(DispatchStind_I8), OpCodes.Stind_I8);
			Bind(new InstructionVisitorDelegate(DispatchStind_R4), OpCodes.Stind_R4);
			Bind(new InstructionVisitorDelegate(DispatchStind_R8), OpCodes.Stind_R8);
			Bind(new InstructionVisitorDelegate(DispatchAdd), OpCodes.Add);
			Bind(new InstructionVisitorDelegate(DispatchSub), OpCodes.Sub);
			Bind(new InstructionVisitorDelegate(DispatchMul), OpCodes.Mul);
			Bind(new InstructionVisitorDelegate(DispatchDiv), OpCodes.Div);
			Bind(new InstructionVisitorDelegate(DispatchDiv_Un), OpCodes.Div_Un);
			Bind(new InstructionVisitorDelegate(DispatchRem), OpCodes.Rem);
			Bind(new InstructionVisitorDelegate(DispatchRem_Un), OpCodes.Rem_Un);
			Bind(new InstructionVisitorDelegate(DispatchAnd), OpCodes.And);
			Bind(new InstructionVisitorDelegate(DispatchOr), OpCodes.Or);
			Bind(new InstructionVisitorDelegate(DispatchXor), OpCodes.Xor);
			Bind(new InstructionVisitorDelegate(DispatchShl), OpCodes.Shl);
			Bind(new InstructionVisitorDelegate(DispatchShr), OpCodes.Shr);
			Bind(new InstructionVisitorDelegate(DispatchShr_Un), OpCodes.Shr_Un);
			Bind(new InstructionVisitorDelegate(DispatchNeg), OpCodes.Neg);
			Bind(new InstructionVisitorDelegate(DispatchNot), OpCodes.Not);
			Bind(new InstructionVisitorDelegate(DispatchConv_I1), OpCodes.Conv_I1);
			Bind(new InstructionVisitorDelegate(DispatchConv_I2), OpCodes.Conv_I2);
			Bind(new InstructionVisitorDelegate(DispatchConv_I4), OpCodes.Conv_I4);
			Bind(new InstructionVisitorDelegate(DispatchConv_I8), OpCodes.Conv_I8);
			Bind(new InstructionVisitorDelegate(DispatchConv_R4), OpCodes.Conv_R4);
			Bind(new InstructionVisitorDelegate(DispatchConv_R8), OpCodes.Conv_R8);
			Bind(new InstructionVisitorDelegate(DispatchConv_U4), OpCodes.Conv_U4);
			Bind(new InstructionVisitorDelegate(DispatchConv_U8), OpCodes.Conv_U8);
			Bind(new InstructionVisitorDelegate(DispatchCallvirt), OpCodes.Callvirt);
			Bind(new InstructionVisitorDelegate(DispatchCpobj), OpCodes.Cpobj);
			Bind(new InstructionVisitorDelegate(DispatchLdobj), OpCodes.Ldobj);
			Bind(new InstructionVisitorDelegate(DispatchLdstr), OpCodes.Ldstr);
			Bind(new InstructionVisitorDelegate(DispatchNewobj), OpCodes.Newobj);
			Bind(new InstructionVisitorDelegate(DispatchCastclass), OpCodes.Castclass);
			Bind(new InstructionVisitorDelegate(DispatchIsinst), OpCodes.Isinst);
			Bind(new InstructionVisitorDelegate(DispatchConv_R_Un), OpCodes.Conv_R_Un);
			Bind(new InstructionVisitorDelegate(DispatchUnbox), OpCodes.Unbox);
			Bind(new InstructionVisitorDelegate(DispatchThrow), OpCodes.Throw);
			Bind(new InstructionVisitorDelegate(DispatchLdfld), OpCodes.Ldfld);
			Bind(new InstructionVisitorDelegate(DispatchLdflda), OpCodes.Ldflda);
			Bind(new InstructionVisitorDelegate(DispatchStfld), OpCodes.Stfld);
			Bind(new InstructionVisitorDelegate(DispatchLdsfld), OpCodes.Ldsfld);
			Bind(new InstructionVisitorDelegate(DispatchLdsflda), OpCodes.Ldsflda);
			Bind(new InstructionVisitorDelegate(DispatchStsfld), OpCodes.Stsfld);
			Bind(new InstructionVisitorDelegate(DispatchStobj), OpCodes.Stobj);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I1_Un), OpCodes.Conv_Ovf_I1_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I2_Un), OpCodes.Conv_Ovf_I2_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I4_Un), OpCodes.Conv_Ovf_I4_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I8_Un), OpCodes.Conv_Ovf_I8_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U1_Un), OpCodes.Conv_Ovf_U1_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U2_Un), OpCodes.Conv_Ovf_U2_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U4_Un), OpCodes.Conv_Ovf_U4_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U8_Un), OpCodes.Conv_Ovf_U8_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I_Un), OpCodes.Conv_Ovf_I_Un);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U_Un), OpCodes.Conv_Ovf_U_Un);
			Bind(new InstructionVisitorDelegate(DispatchBox), OpCodes.Box);
			Bind(new InstructionVisitorDelegate(DispatchNewarr), OpCodes.Newarr);
			Bind(new InstructionVisitorDelegate(DispatchLdlen), OpCodes.Ldlen);
			Bind(new InstructionVisitorDelegate(DispatchLdelema), OpCodes.Ldelema);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_I1), OpCodes.Ldelem_I1);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_U1), OpCodes.Ldelem_U1);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_I2), OpCodes.Ldelem_I2);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_U2), OpCodes.Ldelem_U2);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_I4), OpCodes.Ldelem_I4);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_U4), OpCodes.Ldelem_U4);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_I8), OpCodes.Ldelem_I8);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_I), OpCodes.Ldelem_I);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_R4), OpCodes.Ldelem_R4);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_R8), OpCodes.Ldelem_R8);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_Ref), OpCodes.Ldelem_Ref);
			Bind(new InstructionVisitorDelegate(DispatchStelem_I), OpCodes.Stelem_I);
			Bind(new InstructionVisitorDelegate(DispatchStelem_I1), OpCodes.Stelem_I1);
			Bind(new InstructionVisitorDelegate(DispatchStelem_I2), OpCodes.Stelem_I2);
			Bind(new InstructionVisitorDelegate(DispatchStelem_I4), OpCodes.Stelem_I4);
			Bind(new InstructionVisitorDelegate(DispatchStelem_I8), OpCodes.Stelem_I8);
			Bind(new InstructionVisitorDelegate(DispatchStelem_R4), OpCodes.Stelem_R4);
			Bind(new InstructionVisitorDelegate(DispatchStelem_R8), OpCodes.Stelem_R8);
			Bind(new InstructionVisitorDelegate(DispatchStelem_Ref), OpCodes.Stelem_Ref);
			Bind(new InstructionVisitorDelegate(DispatchLdelem_Any), OpCodes.Ldelem_Any);
			Bind(new InstructionVisitorDelegate(DispatchStelem_Any), OpCodes.Stelem_Any);
			Bind(new InstructionVisitorDelegate(DispatchUnbox_Any), OpCodes.Unbox_Any);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I1), OpCodes.Conv_Ovf_I1);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U1), OpCodes.Conv_Ovf_U1);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I2), OpCodes.Conv_Ovf_I2);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U2), OpCodes.Conv_Ovf_U2);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I4), OpCodes.Conv_Ovf_I4);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U4), OpCodes.Conv_Ovf_U4);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I8), OpCodes.Conv_Ovf_I8);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U8), OpCodes.Conv_Ovf_U8);
			Bind(new InstructionVisitorDelegate(DispatchRefanyval), OpCodes.Refanyval);
			Bind(new InstructionVisitorDelegate(DispatchCkfinite), OpCodes.Ckfinite);
			Bind(new InstructionVisitorDelegate(DispatchMkrefany), OpCodes.Mkrefany);
			Bind(new InstructionVisitorDelegate(DispatchLdtoken), OpCodes.Ldtoken);
			Bind(new InstructionVisitorDelegate(DispatchConv_U2), OpCodes.Conv_U2);
			Bind(new InstructionVisitorDelegate(DispatchConv_U1), OpCodes.Conv_U1);
			Bind(new InstructionVisitorDelegate(DispatchConv_I), OpCodes.Conv_I);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_I), OpCodes.Conv_Ovf_I);
			Bind(new InstructionVisitorDelegate(DispatchConv_Ovf_U), OpCodes.Conv_Ovf_U);
			Bind(new InstructionVisitorDelegate(DispatchAdd_Ovf), OpCodes.Add_Ovf);
			Bind(new InstructionVisitorDelegate(DispatchAdd_Ovf_Un), OpCodes.Add_Ovf_Un);
			Bind(new InstructionVisitorDelegate(DispatchMul_Ovf), OpCodes.Mul_Ovf);
			Bind(new InstructionVisitorDelegate(DispatchMul_Ovf_Un), OpCodes.Mul_Ovf_Un);
			Bind(new InstructionVisitorDelegate(DispatchSub_Ovf), OpCodes.Sub_Ovf);
			Bind(new InstructionVisitorDelegate(DispatchSub_Ovf_Un), OpCodes.Sub_Ovf_Un);
			Bind(new InstructionVisitorDelegate(DispatchEndfinally), OpCodes.Endfinally);
			Bind(new InstructionVisitorDelegate(DispatchLeave), OpCodes.Leave, OpCodes.Leave_S);
			Bind(new InstructionVisitorDelegate(DispatchStind_I), OpCodes.Stind_I);
			Bind(new InstructionVisitorDelegate(DispatchConv_U), OpCodes.Conv_U);
			Bind(new InstructionVisitorDelegate(DispatchArglist), OpCodes.Arglist);
			Bind(new InstructionVisitorDelegate(DispatchCeq), OpCodes.Ceq);
			Bind(new InstructionVisitorDelegate(DispatchCgt), OpCodes.Cgt);
			Bind(new InstructionVisitorDelegate(DispatchCgt_Un), OpCodes.Cgt_Un);
			Bind(new InstructionVisitorDelegate(DispatchClt), OpCodes.Clt);
			Bind(new InstructionVisitorDelegate(DispatchClt_Un), OpCodes.Clt_Un);
			Bind(new InstructionVisitorDelegate(DispatchLdftn), OpCodes.Ldftn);
			Bind(new InstructionVisitorDelegate(DispatchLdvirtftn), OpCodes.Ldvirtftn);
			Bind(new InstructionVisitorDelegate(DispatchLocalloc), OpCodes.Localloc);
			Bind(new InstructionVisitorDelegate(DispatchEndfilter), OpCodes.Endfilter);
			Bind(new InstructionVisitorDelegate(DispatchUnaligned), OpCodes.Unaligned);
			Bind(new InstructionVisitorDelegate(DispatchVolatile), OpCodes.Volatile);
			Bind(new InstructionVisitorDelegate(DispatchTail), OpCodes.Tail);
			Bind(new InstructionVisitorDelegate(DispatchInitobj), OpCodes.Initobj);
			Bind(new InstructionVisitorDelegate(DispatchCpblk), OpCodes.Cpblk);
			Bind(new InstructionVisitorDelegate(DispatchInitblk), OpCodes.Initblk);
			Bind(new InstructionVisitorDelegate(DispatchRethrow), OpCodes.Rethrow);
			Bind(new InstructionVisitorDelegate(DispatchSizeof), OpCodes.Sizeof);
			Bind(new InstructionVisitorDelegate(DispatchRefanytype), OpCodes.Refanytype);
		}

		static void Bind(InstructionVisitorDelegate handler, params OpCode[] opcodes)
		{
			foreach (OpCode op in opcodes)
			{
				_handlers.Add(op.Value, handler);
			}
		}

		static void DispatchNop(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnNop(instruction);
		}

		static void DispatchBreak(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBreak(instruction);
		}

		static void DispatchLdarg_0(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarg_0(instruction);
		}

		static void DispatchLdarg_1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarg_1(instruction);
		}

		static void DispatchLdarg_2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarg_2(instruction);
		}

		static void DispatchLdarg_3(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarg_3(instruction);
		}

		static void DispatchLdloc_0(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloc_0(instruction);
		}

		static void DispatchLdloc_1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloc_1(instruction);
		}

		static void DispatchLdloc_2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloc_2(instruction);
		}

		static void DispatchLdloc_3(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloc_3(instruction);
		}

		static void DispatchStloc_0(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStloc_0(instruction);
		}

		static void DispatchStloc_1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStloc_1(instruction);
		}

		static void DispatchStloc_2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStloc_2(instruction);
		}

		static void DispatchStloc_3(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStloc_3(instruction);
		}

		static void DispatchLdarg(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarg(instruction);
		}

		static void DispatchLdarga(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdarga(instruction);
		}

		static void DispatchStarg(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStarg(instruction);
		}

		static void DispatchLdloc(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloc(instruction);
		}

		static void DispatchLdloca(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdloca(instruction);
		}

		static void DispatchStloc(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStloc(instruction);
		}

		static void DispatchLdnull(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdnull(instruction);
		}

		static void DispatchLdc_I4_M1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_M1(instruction);
		}

		static void DispatchLdc_I4_0(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_0(instruction);
		}

		static void DispatchLdc_I4_1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_1(instruction);
		}

		static void DispatchLdc_I4_2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_2(instruction);
		}

		static void DispatchLdc_I4_3(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_3(instruction);
		}

		static void DispatchLdc_I4_4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_4(instruction);
		}

		static void DispatchLdc_I4_5(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_5(instruction);
		}

		static void DispatchLdc_I4_6(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_6(instruction);
		}

		static void DispatchLdc_I4_7(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_7(instruction);
		}

		static void DispatchLdc_I4_8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4_8(instruction);
		}

		static void DispatchLdc_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I4(instruction);
		}

		static void DispatchLdc_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_I8(instruction);
		}

		static void DispatchLdc_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_R4(instruction);
		}

		static void DispatchLdc_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdc_R8(instruction);
		}

		static void DispatchDup(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnDup(instruction);
		}

		static void DispatchPop(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnPop(instruction);
		}

		static void DispatchJmp(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnJmp(instruction);
		}

		static void DispatchCall(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCall(instruction);
		}

		static void DispatchCalli(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCalli(instruction);
		}

		static void DispatchRet(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRet(instruction);
		}

		static void DispatchBr(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBr(instruction);
		}

		static void DispatchBrfalse(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBrfalse(instruction);
		}

		static void DispatchBrtrue(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBrtrue(instruction);
		}

		static void DispatchBeq(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBeq(instruction);
		}

		static void DispatchBge(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBge(instruction);
		}

		static void DispatchBgt(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBgt(instruction);
		}

		static void DispatchBle(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBle(instruction);
		}

		static void DispatchBlt(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBlt(instruction);
		}

		static void DispatchBne_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBne_Un(instruction);
		}

		static void DispatchBge_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBge_Un(instruction);
		}

		static void DispatchBgt_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBgt_Un(instruction);
		}

		static void DispatchBle_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBle_Un(instruction);
		}

		static void DispatchBlt_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBlt_Un(instruction);
		}

		static void DispatchSwitch(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnSwitch(instruction);
		}

		static void DispatchLdind_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_I1(instruction);
		}

		static void DispatchLdind_U1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_U1(instruction);
		}

		static void DispatchLdind_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_I2(instruction);
		}

		static void DispatchLdind_U2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_U2(instruction);
		}

		static void DispatchLdind_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_I4(instruction);
		}

		static void DispatchLdind_U4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_U4(instruction);
		}

		static void DispatchLdind_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_I8(instruction);
		}

		static void DispatchLdind_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_I(instruction);
		}

		static void DispatchLdind_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_R4(instruction);
		}

		static void DispatchLdind_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_R8(instruction);
		}

		static void DispatchLdind_Ref(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdind_Ref(instruction);
		}

		static void DispatchStind_Ref(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_Ref(instruction);
		}

		static void DispatchStind_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_I1(instruction);
		}

		static void DispatchStind_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_I2(instruction);
		}

		static void DispatchStind_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_I4(instruction);
		}

		static void DispatchStind_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_I8(instruction);
		}

		static void DispatchStind_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_R4(instruction);
		}

		static void DispatchStind_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_R8(instruction);
		}

		static void DispatchAdd(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnAdd(instruction);
		}

		static void DispatchSub(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnSub(instruction);
		}

		static void DispatchMul(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnMul(instruction);
		}

		static void DispatchDiv(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnDiv(instruction);
		}

		static void DispatchDiv_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnDiv_Un(instruction);
		}

		static void DispatchRem(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRem(instruction);
		}

		static void DispatchRem_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRem_Un(instruction);
		}

		static void DispatchAnd(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnAnd(instruction);
		}

		static void DispatchOr(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnOr(instruction);
		}

		static void DispatchXor(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnXor(instruction);
		}

		static void DispatchShl(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnShl(instruction);
		}

		static void DispatchShr(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnShr(instruction);
		}

		static void DispatchShr_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnShr_Un(instruction);
		}

		static void DispatchNeg(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnNeg(instruction);
		}

		static void DispatchNot(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnNot(instruction);
		}

		static void DispatchConv_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_I1(instruction);
		}

		static void DispatchConv_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_I2(instruction);
		}

		static void DispatchConv_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_I4(instruction);
		}

		static void DispatchConv_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_I8(instruction);
		}

		static void DispatchConv_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_R4(instruction);
		}

		static void DispatchConv_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_R8(instruction);
		}

		static void DispatchConv_U4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_U4(instruction);
		}

		static void DispatchConv_U8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_U8(instruction);
		}

		static void DispatchCallvirt(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCallvirt(instruction);
		}

		static void DispatchCpobj(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCpobj(instruction);
		}

		static void DispatchLdobj(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdobj(instruction);
		}

		static void DispatchLdstr(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdstr(instruction);
		}

		static void DispatchNewobj(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnNewobj(instruction);
		}

		static void DispatchCastclass(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCastclass(instruction);
		}

		static void DispatchIsinst(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnIsinst(instruction);
		}

		static void DispatchConv_R_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_R_Un(instruction);
		}

		static void DispatchUnbox(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnUnbox(instruction);
		}

		static void DispatchThrow(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnThrow(instruction);
		}

		static void DispatchLdfld(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdfld(instruction);
		}

		static void DispatchLdflda(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdflda(instruction);
		}

		static void DispatchStfld(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStfld(instruction);
		}

		static void DispatchLdsfld(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdsfld(instruction);
		}

		static void DispatchLdsflda(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdsflda(instruction);
		}

		static void DispatchStsfld(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStsfld(instruction);
		}

		static void DispatchStobj(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStobj(instruction);
		}

		static void DispatchConv_Ovf_I1_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I1_Un(instruction);
		}

		static void DispatchConv_Ovf_I2_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I2_Un(instruction);
		}

		static void DispatchConv_Ovf_I4_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I4_Un(instruction);
		}

		static void DispatchConv_Ovf_I8_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I8_Un(instruction);
		}

		static void DispatchConv_Ovf_U1_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U1_Un(instruction);
		}

		static void DispatchConv_Ovf_U2_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U2_Un(instruction);
		}

		static void DispatchConv_Ovf_U4_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U4_Un(instruction);
		}

		static void DispatchConv_Ovf_U8_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U8_Un(instruction);
		}

		static void DispatchConv_Ovf_I_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I_Un(instruction);
		}

		static void DispatchConv_Ovf_U_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U_Un(instruction);
		}

		static void DispatchBox(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnBox(instruction);
		}

		static void DispatchNewarr(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnNewarr(instruction);
		}

		static void DispatchLdlen(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdlen(instruction);
		}

		static void DispatchLdelema(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelema(instruction);
		}

		static void DispatchLdelem_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_I1(instruction);
		}

		static void DispatchLdelem_U1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_U1(instruction);
		}

		static void DispatchLdelem_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_I2(instruction);
		}

		static void DispatchLdelem_U2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_U2(instruction);
		}

		static void DispatchLdelem_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_I4(instruction);
		}

		static void DispatchLdelem_U4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_U4(instruction);
		}

		static void DispatchLdelem_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_I8(instruction);
		}

		static void DispatchLdelem_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_I(instruction);
		}

		static void DispatchLdelem_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_R4(instruction);
		}

		static void DispatchLdelem_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_R8(instruction);
		}

		static void DispatchLdelem_Ref(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_Ref(instruction);
		}

		static void DispatchStelem_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_I(instruction);
		}

		static void DispatchStelem_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_I1(instruction);
		}

		static void DispatchStelem_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_I2(instruction);
		}

		static void DispatchStelem_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_I4(instruction);
		}

		static void DispatchStelem_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_I8(instruction);
		}

		static void DispatchStelem_R4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_R4(instruction);
		}

		static void DispatchStelem_R8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_R8(instruction);
		}

		static void DispatchStelem_Ref(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_Ref(instruction);
		}

		static void DispatchLdelem_Any(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdelem_Any(instruction);
		}

		static void DispatchStelem_Any(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStelem_Any(instruction);
		}

		static void DispatchUnbox_Any(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnUnbox_Any(instruction);
		}

		static void DispatchConv_Ovf_I1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I1(instruction);
		}

		static void DispatchConv_Ovf_U1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U1(instruction);
		}

		static void DispatchConv_Ovf_I2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I2(instruction);
		}

		static void DispatchConv_Ovf_U2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U2(instruction);
		}

		static void DispatchConv_Ovf_I4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I4(instruction);
		}

		static void DispatchConv_Ovf_U4(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U4(instruction);
		}

		static void DispatchConv_Ovf_I8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I8(instruction);
		}

		static void DispatchConv_Ovf_U8(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U8(instruction);
		}

		static void DispatchRefanyval(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRefanyval(instruction);
		}

		static void DispatchCkfinite(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCkfinite(instruction);
		}

		static void DispatchMkrefany(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnMkrefany(instruction);
		}

		static void DispatchLdtoken(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdtoken(instruction);
		}

		static void DispatchConv_U2(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_U2(instruction);
		}

		static void DispatchConv_U1(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_U1(instruction);
		}

		static void DispatchConv_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_I(instruction);
		}

		static void DispatchConv_Ovf_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_I(instruction);
		}

		static void DispatchConv_Ovf_U(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_Ovf_U(instruction);
		}

		static void DispatchAdd_Ovf(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnAdd_Ovf(instruction);
		}

		static void DispatchAdd_Ovf_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnAdd_Ovf_Un(instruction);
		}

		static void DispatchMul_Ovf(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnMul_Ovf(instruction);
		}

		static void DispatchMul_Ovf_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnMul_Ovf_Un(instruction);
		}

		static void DispatchSub_Ovf(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnSub_Ovf(instruction);
		}

		static void DispatchSub_Ovf_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnSub_Ovf_Un(instruction);
		}

		static void DispatchEndfinally(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnEndfinally(instruction);
		}

		static void DispatchLeave(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLeave(instruction);
		}

		static void DispatchStind_I(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnStind_I(instruction);
		}

		static void DispatchConv_U(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnConv_U(instruction);
		}

		static void DispatchArglist(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnArglist(instruction);
		}

		static void DispatchCeq(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCeq(instruction);
		}

		static void DispatchCgt(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCgt(instruction);
		}

		static void DispatchCgt_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCgt_Un(instruction);
		}

		static void DispatchClt(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnClt(instruction);
		}

		static void DispatchClt_Un(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnClt_Un(instruction);
		}

		static void DispatchLdftn(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdftn(instruction);
		}

		static void DispatchLdvirtftn(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLdvirtftn(instruction);
		}

		static void DispatchLocalloc(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnLocalloc(instruction);
		}

		static void DispatchEndfilter(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnEndfilter(instruction);
		}

		static void DispatchUnaligned(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnUnaligned(instruction);
		}

		static void DispatchVolatile(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnVolatile(instruction);
		}

		static void DispatchTail(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnTail(instruction);
		}

		static void DispatchInitobj(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnInitobj(instruction);
		}

		static void DispatchCpblk(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnCpblk(instruction);
		}

		static void DispatchInitblk(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnInitblk(instruction);
		}

		static void DispatchRethrow(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRethrow(instruction);
		}

		static void DispatchSizeof(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnSizeof(instruction);
		}

		static void DispatchRefanytype(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.OnRefanytype(instruction);
		}
	}
}

