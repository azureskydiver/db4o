/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o.Diagnostic;

namespace Db4objects.Db4odoc.Diagnostics
{
	public class IndexDiagListener: DiagnosticToConsole
	{
		override public void OnDiagnostic(Db4objects.Db4o.Diagnostic.IDiagnostic d) 
		{
            if (d.GetType().Equals(typeof(Db4objects.Db4o.Diagnostic.LoadedFromClassIndex)))
            {
				System.Console.WriteLine(d.ToString());
			}
		}
	}
}