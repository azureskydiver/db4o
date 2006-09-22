namespace com.db4o.diagnostic
{
	/// <summary>prints Diagnostic messsages to the Console.</summary>
	/// <remarks>
	/// prints Diagnostic messsages to the Console.
	/// Install this
	/// <see cref="com.db4o.diagnostic.DiagnosticListener">com.db4o.diagnostic.DiagnosticListener
	/// 	</see>
	/// with: <br />
	/// <code>Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());</code><br />
	/// </remarks>
	/// <seealso cref="com.db4o.diagnostic.DiagnosticConfiguration">com.db4o.diagnostic.DiagnosticConfiguration
	/// 	</seealso>
	public class DiagnosticToConsole : com.db4o.diagnostic.DiagnosticListener
	{
		/// <summary>redirects Diagnostic messages to the Console.</summary>
		/// <remarks>redirects Diagnostic messages to the Console.</remarks>
		public virtual void OnDiagnostic(com.db4o.diagnostic.Diagnostic d)
		{
			System.Console.Out.WriteLine(d.ToString());
		}
	}
}
