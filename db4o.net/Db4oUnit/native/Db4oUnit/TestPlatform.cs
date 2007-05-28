﻿/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
namespace Db4oUnit
{
	using System;
	using System.IO;
	using System.Reflection;

	public class TestPlatform
	{
#if CF_1_0 || CF_2_0
        public static string NEWLINE = "\n";
#else
	    public static string NEWLINE = Environment.NewLine;
#endif

		// will be assigned from the outside on CF
		public static TextWriter Out;

        public static TextWriter Error;
        
#if !CF_1_0
		static TestPlatform()
		{
			Out = Console.Out;
            Error = Console.Error;
		}
#endif
		
		public static void PrintStackTrace(TextWriter writer, Exception e)
		{
			writer.Write(e);
		}

		public static void PrintStackTrace(Exception e)
		{
			PrintStackTrace (GetStdOut(), e);
		}

		public static TextWriter GetStdOut()
		{
			return Out;
		}
        
        public static TextWriter GetStdErr()
		{
			return Error;
		}
		
		public static void EmitWarning(string warning)
		{
			Out.WriteLine(warning);
		}		

		public static bool IsStatic(MethodInfo method)
		{
			return method.IsStatic;
		}

		public static bool IsPublic(MethodInfo method)
		{
			return method.IsPublic;
		}

		public static bool HasParameters(MethodInfo method)
		{
			return method.GetParameters().Length > 0;
		}

        public static TextWriter OpenTextFile(string fname)
        {
            return new StreamWriter(fname);
        }
	}
}
