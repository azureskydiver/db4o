namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface ICodeElement
	{
		CodeElementType CodeElementType { get; }
		void Accept(ICodeStructureVisitor visitor);
	}
}