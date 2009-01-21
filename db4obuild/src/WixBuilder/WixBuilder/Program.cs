using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Runtime.InteropServices;

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
public class WixScriptBuilder
{
	readonly string _basePath;
	readonly XmlTextWriter _writer;
	string _currentDirectoryId;
	readonly ArrayList _components;
	readonly WixBuilderParameters _parameters;
	readonly Dictionary<string, string> _fileIdMapping = new Dictionary<string, string>();

	public WixScriptBuilder(TextWriter writer, string basePath, WixBuilderParameters parameters)
	{
		_writer = new XmlTextWriter(writer);
		_writer.Formatting = Formatting.Indented;
		_basePath = Path.GetFullPath(basePath);
		_components = new ArrayList();
		_parameters = parameters;
		foreach (KnownId ki in parameters.KnownIds)
		{
			_fileIdMapping.Add(Path.GetFullPath(Path.Combine(_basePath, ki.Path)), ki.Id);
		}
	}

	public void Run()
	{
		_writer.WriteStartDocument();
		_writer.WriteStartElement("Wix");
		_writer.WriteAttributeString("xmlns", "http://schemas.microsoft.com/wix/2003/01/wi");
		_writer.WriteStartElement("Fragment");
		_writer.WriteAttributeString("Id", "DirectoriesFilesAndComponents");

		WriteRootDirectory();

		WriteApplicationFilesFeature();

		_writer.WriteEndElement();
		_writer.WriteEndElement();
		_writer.WriteEndDocument();
	}

	void WriteComponentRef(string component)
	{
		_writer.WriteStartElement("ComponentRef");
		_writer.WriteAttributeString("Id", component);
		_writer.WriteEndElement();
	}

	void WriteApplicationFilesFeature()
	{
		_writer.WriteStartElement("Feature");
		_writer.WriteAttributeString("Id", "ApplicationFiles");
		_writer.WriteAttributeString("Level", "1");
		_writer.WriteAttributeString("ConfigurableDirectory", "INSTALLDIR");
		
		_writer.WriteAttributeString("Description", "Install Db4objects core assemblies and debug information.");
		_writer.WriteAttributeString("Title", "Db4objects Core");
		
		_writer.WriteAttributeString("TypicalDefault", "install");
		_writer.WriteAttributeString("InstallDefault", "local");	

		foreach (string component in _components)
		{
			WriteComponentRef(component);
		}

		_writer.WriteEndElement();
	}

	void WriteRootDirectory()
	{
		_writer.WriteStartElement("Directory");
		_writer.WriteAttributeString("Id", "TARGETDIR");
		_writer.WriteAttributeString("Name", "SourceDir");

		WriteDirectoryComponent("c_" + GetIdFromPath(_basePath), _basePath);
		WriteSubDirectories(_basePath);

		_writer.WriteEndElement();
	}

	void WriteDirectory(string path)
	{
		_writer.WriteStartElement("Directory");
		_currentDirectoryId = WriteIdNameAndLongName(path);

		WriteDirectoryComponent("c_" + _currentDirectoryId, path);
		WriteSubDirectories(path);

		_writer.WriteEndElement();
	}

	void WriteDirectoryComponent(string id, string path)
	{
		_writer.WriteStartElement("Component");
		_writer.WriteAttributeString("Id", id);
		_writer.WriteAttributeString("Guid", NewGuid());

		_components.Add(id);

		foreach (string fname in Directory.GetFiles(path))
		{
			WriteFile(Path.GetFullPath(fname));
		}

		_writer.WriteEndElement();
	}

	void WriteSubDirectories(string path)
	{
		foreach (string dir in Directory.GetDirectories(path))
		{
			WriteDirectory(dir);
		}
	}

	void WriteFile(string fname)
	{
		_writer.WriteStartElement("File");
		string id = WriteIdNameAndLongName(fname);
		_writer.WriteAttributeString("src", fname);
		_writer.WriteAttributeString("DiskId", "1");
		_writer.WriteAttributeString("Vital", "yes");

		string shortcut = GetShortcutName(id);
		if (null != shortcut)
		{
			WriteShortcut(shortcut, fname, _currentDirectoryId);
		}
		_writer.WriteEndElement();
	}

	string GetShortcutName(string id)
	{
		if (null != _parameters)
		{
			foreach (WixShortcut item in _parameters.Shortcuts)
			{
				if (id == GetIdFromRelativePath(item.Path))
				{
					return item.Name;
				}
			}
		}
		return null;
	}

	void WriteShortcut(string displayName, string fname, string workingDirectory)
	{
		string fileId = GetIdFromPath(fname);

		_writer.WriteStartElement("Shortcut");
		_writer.WriteAttributeString("Id", "s_" + fileId);

		string ext = Path.GetExtension(fname).Substring(1).ToLower();
		_writer.WriteAttributeString("Icon", ext + ".ico");
		_writer.WriteAttributeString("IconIndex", "0");
		_writer.WriteAttributeString("Directory", "TargetMenuFolder");
		_writer.WriteAttributeString("Name", Path.GetFileName(GetShortPathName(fname)));
		_writer.WriteAttributeString("LongName", displayName);
		_writer.WriteAttributeString("Description", displayName);
		_writer.WriteAttributeString("Show", "normal");
		_writer.WriteAttributeString("WorkingDirectory", workingDirectory);
		_writer.WriteEndElement();
	}

	string WriteIdNameAndLongName(string path)
	{
		string id = GetIdFromPath(path);
		_writer.WriteAttributeString("Id", id);
		WriteNameAndLongName(path);
		return id;
	}

	void WriteNameAndLongName(string path)
	{
        string name = Path.GetFileName(path);
        string shortName = Path.GetFileName(GetShortPathName(path));
        _writer.WriteAttributeString("Name", shortName);
        if (name != shortName)
        {
            _writer.WriteAttributeString("LongName", name);
        }
	}

	string GetIdFromPath(string path)
	{
		string existing;
		if (_fileIdMapping.TryGetValue(path, out existing))
		{
			return existing;
		}

		string newId = "_" + NewGuid().Replace('-', '_');
		_fileIdMapping.Add(path, newId);
		return newId;
	}

    static string GetIdFromRelativePath(string path)
	{
		return path.Replace("\\", ".").Replace("/", ".").Replace("-", "_").Replace("$", "_").Replace(" ", "_");
	}

	static string NewGuid()
	{
		return Guid.NewGuid().ToString().ToUpper();
	}

	[DllImport("kernel32.dll", SetLastError = true, CharSet = CharSet.Auto)]
	static extern int GetShortPathName(
							string lpszLongPath,
							[Out] StringBuilder lpszShortPath,
							int cchBuffer);

	static string GetShortPathName(string name)
	{
        StringBuilder builder = new StringBuilder(new string(' ', name.Length));
		int cch = GetShortPathName(name, builder, builder.Capacity);
		if (cch > builder.Capacity)
		{
			builder.Capacity = cch;
			cch = GetShortPathName(name, builder, builder.Capacity);
		}
	    builder.Length = cch;
        return builder.ToString();
	}

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
											WixBuilderParameters.Load(parametersFile));
			builder.Run();
		}
		return 0;
	}
}

