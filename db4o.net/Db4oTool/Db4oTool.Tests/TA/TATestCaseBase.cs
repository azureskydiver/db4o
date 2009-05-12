﻿/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

This file is part of the sharpen open source java to c# translator.

sharpen is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to Versant, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

sharpen is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */

using System.Reflection;
using Db4oTool.Core;
using Db4oTool.Tests.Core;
using Db4oUnit;
using Mono.Cecil;

namespace Db4oTool.Tests.TA
{
	internal abstract class TATestCaseBase : ITestCase
	{
		protected AssemblyDefinition GenerateAssembly(string resourceName, params Assembly[] references)
		{
			return AssemblyFactory.GetAssembly(
						CompilationServices.EmitAssemblyFromResource(
							ResourceServices.CompleteResourceName(
													GetType(),
													resourceName), references));
		}

		protected static void InstrumentAssembly(AssemblyDefinition testAssembly)
		{
			InstrumentationContext context = new InstrumentationContext(Configuration(testAssembly.MainModule.Image.FileInformation.FullName), testAssembly);
			new Db4oTool.TA.TAInstrumentation().Run(context);
		}

		protected static Configuration Configuration(string assemblyLocation)
		{
			return new Configuration(assemblyLocation);
		}
	}
}
