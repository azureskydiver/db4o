/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.Diagnostics;
using Db4objects.Db4o.Diagnostic;

namespace Db4objects.Db4odoc.Diagnostics
{
	public class TranslatorDiagListener: DiagnosticToConsole
	{
		override public void OnDiagnostic(Db4objects.Db4o.Diagnostic.IDiagnostic d) 
		{
            if (d.GetType().Equals(typeof(Db4objects.Db4o.Diagnostic.DescendIntoTranslator)))
			{
				System.Console.WriteLine(d.ToString());
			}
		}
	}
}