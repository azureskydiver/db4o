namespace com.db4o.diagnostic
{
	/// <summary>Diagnostic, if query was required to load candidate set from class index.
	/// 	</summary>
	/// <remarks>Diagnostic, if query was required to load candidate set from class index.
	/// 	</remarks>
	public class LoadedFromClassIndex : com.db4o.diagnostic.DiagnosticBase
	{
		private readonly string _className;

		public LoadedFromClassIndex(string className)
		{
			_className = className;
		}

		public override object Reason()
		{
			return _className;
		}

		public override string Problem()
		{
			return "Query candidate set could not be loaded from a field index";
		}

		public override string Solution()
		{
			return "Consider indexing fields that you query for: " + "Db4o.configure().objectClass([class]).objectField([fieldName]).indexed(true)";
		}
	}
}
