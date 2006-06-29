namespace com.db4o.diagnostic
{
	/// <summary>listens to Diagnostic messages.</summary>
	/// <remarks>
	/// listens to Diagnostic messages.
	/// <br /><br />Create a class that implements this listener interface and add
	/// the listener by calling Db4o.configure().diagnostic().addListener().
	/// </remarks>
	/// <seealso cref="com.db4o.diagnostic.DiagnosticConfiguration">com.db4o.diagnostic.DiagnosticConfiguration
	/// 	</seealso>
	public interface DiagnosticListener
	{
		/// <summary>this method will be called with Diagnostic messages.</summary>
		/// <remarks>this method will be called with Diagnostic messages.</remarks>
		void OnDiagnostic(com.db4o.diagnostic.Diagnostic d);
	}
}
