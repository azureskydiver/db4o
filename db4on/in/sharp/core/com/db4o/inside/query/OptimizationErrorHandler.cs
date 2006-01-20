namespace com.db4o.inside.query
{
	public class OptimizationErrorEventArgs : System.EventArgs
	{
		System.Exception _reason;

		public OptimizationErrorEventArgs(System.Exception e)
		{
			_reason = e;
		}

		public System.Exception Reason
		{
			get { return _reason; }
		}
	}

	public delegate void OptimizationErrorHandler(object sender, OptimizationErrorEventArgs args);
}