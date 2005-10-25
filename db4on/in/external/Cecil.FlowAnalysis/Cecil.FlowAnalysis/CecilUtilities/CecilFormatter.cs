using System;
using System.IO;
using System.Text;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CecilUtilities
{
	/// <summary>
	/// </summary>
	public class CecilFormatter
	{
		public static string FormatInstruction(IInstruction instruction)
		{
			StringWriter writer = new StringWriter();
			WriteInstruction(writer, instruction);
			return writer.ToString();
		}

		public static string FormatMethodBody(IMethodDefinition method)
		{
			StringWriter writer = new StringWriter();
			WriteMethodBody(writer, method);
			return writer.ToString();
		}

		public static void WriteMethodBody(TextWriter writer, IMethodDefinition method)
		{
			writer.WriteLine(method.ToString());
			foreach (IInstruction instruction in method.Body.Instructions)
			{
				writer.Write('\t');
				WriteInstruction(writer, instruction);
				writer.WriteLine();
			}
		}

		public static void WriteInstruction(TextWriter writer, IInstruction instruction)
		{
			writer.Write(FormatLabel(instruction.Offset));
			writer.Write(": ");
			writer.Write(instruction.OpCode.Name);
			if (null != instruction.Operand)
			{
				writer.Write(' ');
				WriteOperand(writer, instruction.Operand);
			}
		}

		private static string FormatLabel(int offset)
		{
			string label = "000" + offset.ToString("x");
			return "IL_" + label.Substring(label.Length-4);
		}

		private static void WriteOperand(TextWriter writer, object operand)
		{
			if (null == operand) throw new ArgumentNullException("operand");

			IInstruction targetInstruction = operand as IInstruction;
			if (null != targetInstruction)
			{
				writer.Write(FormatLabel(targetInstruction.Offset));
				return;
			}

			IVariableReference variableRef = operand as IVariableReference;
			if (null != variableRef)
			{
				writer.Write(variableRef.Index.ToString());
				return;
			}

			IMethodReference methodRef = operand as IMethodReference;
			if (null != methodRef)
			{
				WriteMethodReference(writer, methodRef);
				return;
			}

			string s = operand as string;
			if (null != s)
			{
				writer.Write("\"" + s + "\"");
				return;
			}
			
			writer.Write(operand.ToString());
		}

		private static void WriteMethodReference(TextWriter writer, IMethodReference method)
		{	
			writer.Write(FormatTypeReference(method.ReturnType.ReturnType));
			writer.Write(' ');
			writer.Write(FormatTypeReference(method.DeclaringType));
			writer.Write("::");
			writer.Write(method.Name);
			writer.Write("(");
			IParameterDefinitionCollection parameters = method.Parameters;
			for (int i=0; i<parameters.Count; ++i)
			{
				if (i > 0) writer.Write(", ");
				writer.Write(FormatTypeReference(parameters[i].ParameterType));
			}
			writer.Write(")");
		}

		public static string FormatTypeReference(ITypeReference type)
		{
			string typeName = type.FullName;
			switch (typeName)
			{
				case "System.Void": return "void";
				case "System.String": return "string";
				case "System.Int32": return "int32";
				case "System.Boolean": return "bool";
			}
			return typeName;
		}

		private CecilFormatter()
		{
		}
	}
}
