namespace com.db4o.inside.diagnostic
{
	/// <exclude>
	/// This is just a very basic first implementation that allows
	/// passing String messages.
	/// Possible future content of classes that implement Diagnostic
	/// could be:
	/// - time something takes
	/// - severity
	/// - the "cause" object itself
	/// - individual classes for individual cases
	/// </exclude>
	public class DiagnosticMessage : com.db4o.diagnostic.Diagnostic
	{
		private readonly string _message;

		public DiagnosticMessage(string message)
		{
			_message = message;
		}

		public override string ToString()
		{
			return _message;
		}
	}
}
