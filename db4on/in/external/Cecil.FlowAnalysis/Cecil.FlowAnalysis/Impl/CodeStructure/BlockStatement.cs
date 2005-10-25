using Mono.Cecil;
using Mono.Cecil.Cil;
using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.Impl.CodeStructure
{
	internal class BlockStatement : IBlockStatement
	{
		IStatementCollection _statements;

		public BlockStatement(IStatementCollection statements)
		{
			_statements = statements;
		}

		public IStatementCollection Statements
		{
			get	{ return _statements; }
		}

		public CodeElementType CodeElementType
		{
			get { return CodeElementType.BlockStatement; } 
		}

		public void Accept(ICodeStructureVisitor visitor)
		{
			visitor.Visit(this);
		}
	}
}
