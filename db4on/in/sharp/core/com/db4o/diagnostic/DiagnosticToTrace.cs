namespace com.db4o.diagnostic {
    /// <summary>prints Diagnostic messsages to the Console.</summary>
    /// <remarks>
    /// prints Diagnostic messsages to System.Diagnostics.Trace.
    /// Install this
    /// <see cref="com.db4o.diagnostic.DiagnosticListener">com.db4o.diagnostic.DiagnosticListener
    /// 	</see>
    /// with: <br />
    /// <code>Db4o.Configure().Diagnostic().AddListener(new DiagnosticToTrace());</code><br />
    /// </remarks>
    /// <seealso cref="com.db4o.diagnostic.DiagnosticConfiguration">com.db4o.diagnostic.DiagnosticConfiguration
    /// 	</seealso>
    public class DiagnosticToTrace : com.db4o.diagnostic.DiagnosticListener {
        /// <summary>redirects Diagnostic messages to System.Diagnostics.Trace</summary>
        /// <remarks>redirects Diagnostic messages to the Console.</remarks>
        public virtual void OnDiagnostic(com.db4o.diagnostic.Diagnostic d) {
            System.Diagnostics.Trace.WriteLine(d.ToString());
        }
    }
}
