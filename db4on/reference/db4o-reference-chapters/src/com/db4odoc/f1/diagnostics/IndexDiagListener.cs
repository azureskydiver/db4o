/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.Diagnostics;
using com.db4o.diagnostic;

namespace com.db4odoc.f1.diagnostics
{
	public class IndexDiagListener: DiagnosticToConsole
	{
		override public void OnDiagnostic(com.db4o.diagnostic.Diagnostic d) 
		{
			if (d.GetType().Equals(typeof(com.db4o.diagnostic.LoadedFromClassIndex)))
			{
				System.Diagnostics.Trace.WriteLine(d.ToString());
			}
		}
	}
}