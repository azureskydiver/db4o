using Mono.GetOptions;

namespace Db4oAdmin
{
	public class ProgramOptions : Options
	{
		private bool _prettyVerbose;
		
		[Option("optimize predicate subclasses", "optimize-predicates")]
		public bool OptimizePredicates;
		
		[Option("enable delegate style queries for CompactFramework 2", "cf2-delegates")]
		public bool EnableCF2DelegateQueries;

		[Option("Case sensitive queries", "case-sensitive")]
		public bool CaseSensitive;

		[Option("Verbose operation mode", 'v', "verbose")]
		public bool Verbose;

		[Option("Pretty verbose operation mode", "vv")]
		public bool PrettyVerbose
		{
			get
			{
				return _prettyVerbose;
			}
			
			set
			{
				_prettyVerbose = value;
				Verbose = value;
			}
		}

		public string Assembly
		{
			get
			{
				if (RemainingArguments.Length != 1) return null;
				return RemainingArguments[0];
			}
		}
		
		public bool IsValid
		{
			get
			{
				return Assembly != null
				       && (OptimizePredicates
				           || EnableCF2DelegateQueries);
			}
		}
		
		public ProgramOptions(string[] args)
		{
			ProcessArgs(args);
		}
		
		public ProgramOptions()
		{	
		}
	}
}