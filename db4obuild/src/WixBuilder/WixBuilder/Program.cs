using System;
using System.IO;

/// <summary>
/// WixBuilder generates a Wix [1] Fragment with all the files below a given path.
/// 
/// WixBuilder will create one component per directory and will also generate
/// a Feature definition including all generated components.
/// 
/// run with:
/// 	
/// 	WixBuilder <srcdir> <targetfile>
/// 
/// [1] http://wix.sf.net/
/// </summary>
class Program 
{
	static int Main(string[] argv)
	{
		if (3 != argv.Length)
		{
			Console.WriteLine("WixBuilder <src dir> <wix parameters file> <target file>");
			return -1;
		}

		string srcDir = argv[0];
		string parametersFile = argv[1];
		string targetFile = argv[2];

		using (StreamWriter stream = new StreamWriter(targetFile))
		{
			WixScriptBuilder builder = new WixScriptBuilder(stream,
											srcDir,
											WixBuilderParameters.FromFile(parametersFile));
			builder.Build();
		}
		return 0;
	}
}

