/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */
using System;
using Mono.GetOptions;

namespace Db4oAdmin
{
	public class Program
	{
		static int Main(string[] args)
		{
			ProgramOptions options = new ProgramOptions(args);
			if (!options.IsValid)
			{
				options.DoHelp();
				return -1;
			}

			return DoRun(options);
		}

		public static int Run(ProgramOptions options)
		{
			if (options == null) throw new ArgumentNullException("options");
			if (!options.IsValid) throw new ArgumentException("options");

			return DoRun(options);
		}

		private static int DoRun(ProgramOptions options)
		{
			try
			{
				Configuration configuration = new Configuration(options.Assembly);
				configuration.CaseSensitive = options.CaseSensitive;
					
				InstrumentationPipeline pipeline = new InstrumentationPipeline(configuration);
				if (options.OptimizePredicates)
				{
					pipeline.Add(new PredicateOptimizer());
				}
				if (options.EnableCF2DelegateQueries)
				{
					pipeline.Add(new CFNQEnabler());
				}
				pipeline.Run();
			}
			catch (Exception x)
			{
				Console.WriteLine(x);
				return -2;
			}
			return 0;
		}
	}
}