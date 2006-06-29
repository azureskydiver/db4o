namespace com.db4o.diagnostic
{
	/// <summary>provides methods to configure the behaviour of db4o diagnostics.</summary>
	/// <remarks>
	/// provides methods to configure the behaviour of db4o diagnostics.
	/// <br /><br />Diagnostic listeners can be be added and removed with calls
	/// to this interface.
	/// To install the most basic listener call:<br />
	/// <code>Db4o.configure().diagnostic().addListener(new DiagnosticToConsole());</code>
	/// </remarks>
	public interface DiagnosticConfiguration
	{
		/// <summary>adds a DiagnosticListener to listen to Diagnostic messages.</summary>
		/// <remarks>adds a DiagnosticListener to listen to Diagnostic messages.</remarks>
		void AddListener(com.db4o.diagnostic.DiagnosticListener listener);

		/// <summary>removes all DiagnosticListeners.</summary>
		/// <remarks>removes all DiagnosticListeners.</remarks>
		void RemoveAllListeners();
	}
}
