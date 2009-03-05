using System;
using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;
using System.Xml;
using WixBuilder;
using System.Linq;

public class WixScriptBuilder
{
	public const string WixNamespace = "http://schemas.microsoft.com/wix/2003/01/wi";

	private readonly IDictionary<string, IList<string>> _featureComponents = new Dictionary<string, IList<string>>();

	readonly IFolder _basePath;
	readonly XmlWriter _writer;
	string _currentDirectoryId;
	private readonly IList<string> _components = new List<string>();
	readonly WixBuilderParameters _parameters;
	readonly Dictionary<string, string> _fileIdMapping = new Dictionary<string, string>();
	private Predicate<string> _currentFeatureFilePredicate;
	private readonly string _db4oVersion;

	public WixScriptBuilder(TextWriter writer, string basePath, WixBuilderParameters parameters, string db4oVersion) : this(writer, NativeFileSystem.GetFolder(basePath), parameters, db4oVersion)
	{
	}

	public WixScriptBuilder(TextWriter writer, IFolder basePath, WixBuilderParameters parameters, string db4oVersion)
		: this(XmlTextWriterFor(writer), basePath, parameters, db4oVersion)
	{
	}

	public WixScriptBuilder(XmlWriter writer, IFolder basePath, WixBuilderParameters parameters, string db4oVersion)
	{
		parameters.Validate();
		_writer = writer;
		_basePath = basePath;
		_parameters = parameters;
		_db4oVersion = db4oVersion;
		InitializeFileIdMappings(parameters);
	}

	private void InitializeFileIdMappings(WixBuilderParameters parameters)
	{
		foreach (KnownId ki in parameters.KnownIds)
		{
			_fileIdMapping.Add(Rebase(ki.Path), ki.Id);
		}
	}

	private string Rebase(string path)
	{
		return Path.Combine(_basePath.FullPath, path).Replace('/', '\\');
	}

	private static XmlTextWriter XmlTextWriterFor(TextWriter writer)
	{
		var text = new XmlTextWriter(writer) {Formatting = Formatting.Indented};
		return text;
	}

	public void Build()
	{
		_writer.WriteStartDocument();
		StartElement("Wix");
		WriteAttribute("xmlns", WixNamespace);
		StartElement("Fragment");
		WriteAttribute("Id", "DirectoriesFilesAndComponents");

		WriteApplicationFilesFeature();

		EndElement();
		EndElement();
		_writer.WriteEndDocument();
	}

	void WriteComponentRef(string component)
	{
		StartElement("ComponentRef");
		WriteAttribute("Id", component);
		EndElement();
	}

	private void WriteAttribute(string attributeName, string attributeValue)
	{
		_writer.WriteAttributeString(attributeName, attributeValue);
	}

	private void EndElement()
	{
		_writer.WriteEndElement();
	}

	private void StartElement(string elementName)
	{
		_writer.WriteStartElement(elementName);
	}

	void WriteApplicationFilesFeature()
	{
		WriteDirectoryStructure();

		WriteShortcuts();

		WriteFeatures(_featureComponents);
	}

	private void WriteShortcuts()
	{
		StartElement("DirectoryRef");
		WriteAttribute("Id", "TargetMenuFolder");

		foreach (var feature in _parameters.Features)
		{
			var writtenComponentId = WriteNonEmptyFeatureShortcutList(feature);
			if (writtenComponentId == null)
				continue;

			_featureComponents[feature.Id].Add(writtenComponentId);
		}

		EndElement();
	}

	private string WriteNonEmptyFeatureShortcutList(Feature feature)
	{
		if (feature.Shortcuts.Length == 0)
			return null;
		
		var componentId = feature.Id + "_Shortcuts";
		StartComponent(componentId);
		foreach (var shortcut in feature.Shortcuts)
		{
			WriteShortcut(shortcut);
		}
		EndComponent();
		return componentId;
	}

	private void WriteFeatures(IDictionary<string, IList<string>> componentsByFeatureId)
	{
		foreach (var feature in _parameters.Features)
		{
			WriteFeature(feature, componentsByFeatureId[feature.Id]);
		}
	}

	private void WriteDirectoryStructure()
	{
		StartElement("Directory");
		WriteAttribute("Id", "TARGETDIR");
		WriteAttribute("Name", "SourceDir");

		var dirEnd = StartDirectory(@"PFiles[ProgramFilesFolder]\Db4objects[PFiles.Db4objects]\db4o-{0}[INSTALLDIR]", _db4oVersion);

		foreach (var feature in _parameters.Features)
		{
			_components.Clear();
			_currentFeatureFilePredicate = PredicateFor(feature);
			WriteFeatureFiles();
			_featureComponents[feature.Id] = _components.ToList();
		}

		dirEnd();
		EndElement();
	}

	private void StartDirectoryItem(string id, string name)
	{
		StartElement("Directory");
		WriteAttribute("Id", id);

		if (name.Length > 8)
		{
			WriteAttribute("LongName", name);
			name = name.Substring(0, 8);
		}

		WriteAttribute("Name", name);
	}

	private Func<int> StartDirectory(string dirStruct, params object[] args)
	{
		if (args != null)
		{
			dirStruct = String.Format(dirStruct, args);
		}

		MatchCollection directoryMatches = Regex.Matches(dirStruct, @"(?:\\?(?<folderName>[^\[]*)\[(?<id>[^\]]*)\])");
		int n = directoryMatches.Count;
		foreach (Match directoryMatch in directoryMatches)
		{
			StartDirectoryItem(directoryMatch.Groups["id"].Value, directoryMatch.Groups["folderName"].Value);
		}

		return delegate
		       	{
		       		for(int i =0; i < n; i++)
		       		{
		       			_writer.WriteEndElement();
		       		}
					return n;
		       	};
	}

	private void WriteFeature(Feature feature, IEnumerable<string> componentIds)
	{
		StartElement("Feature");
		WriteAttribute("Id", feature.Id);
		WriteAttribute("Level", "1");
		WriteAttribute("ConfigurableDirectory", "INSTALLDIR");
		
		WriteAttribute("Description", feature.Description);
		WriteAttribute("Title", feature.Title);
		
		WriteAttribute("TypicalDefault", "install");
		WriteAttribute("InstallDefault", "local");

		foreach (string componentId in componentIds)
		{
			WriteComponentRef(componentId);
		}

		EndElement();
	}

	private static Predicate<string> PredicateFor(Feature feature)
	{
		var content = feature.Content;
		if (content.Include != null)
		{
			var include = Patterns.Include(content.Include);
			return null == content.Exclude
			       	? include
			       	: Patterns.And(include, Patterns.Exclude(content.Exclude));
		}
		return content.Exclude == null
			? fileName => true
			: Patterns.Exclude(content.Exclude);
	}

	void WriteFeatureFiles()
	{
		WriteDirectoryComponent("c_" + GetIdFromPath(_basePath), _basePath);
		WriteSubDirectories(_basePath);
	}

	void WriteDirectoryComponent(string id, IFolder path)
	{
		if (WriteComponentIfNonEmpty(id, path))
			_components.Add(id);
	}

	private bool WriteComponentIfNonEmpty(string id, IFolder path)
	{
		var fileSet = FilesForCurrentFeatureFrom(path.Children.OfType<IFile>()).ToList();
		if (fileSet.Count == 0)
			return false;

		WriteComponent(fileSet, id);
		return true;
	}

	private void WriteComponent(IEnumerable<IFile> fileSet, string id)
	{
		StartComponent(id);
		foreach (var file in fileSet)
		{
			WriteFile(file);
		}
		EndComponent();
	}

	private void EndComponent()
	{
		EndElement();
	}

	private void StartComponent(string id)
	{
		StartElement("Component");
		WriteAttribute("Id", id);
		WriteAttribute("Guid", NewGuid());
	}

	private IEnumerable<IFile> FilesForCurrentFeatureFrom(IEnumerable<IFile> fileSet)
	{
		return new FileFinder(_basePath).FindAllIn(fileSet, _currentFeatureFilePredicate);
	}

	void WriteSubDirectories(IFolder path)
	{
		foreach (var dir in path.Children.OfType<IFolder>())
		{
			WriteDirectory(dir);
		}
	}

	void WriteDirectory(IFolder path)
	{
		if (FeatureContainsFilesIn(path))
		{
			StartElement("Directory");

			_currentDirectoryId = WriteIdNameAndLongName(path);

			WriteDirectoryComponent("c_" + _currentDirectoryId, path);
			WriteSubDirectories(path);

			EndElement();
		}
	}

	private bool FeatureContainsFilesIn(IFolder path)
	{
		IEnumerable<IFileSystemItem> many = path.Children.SelectMany(item =>
		                                            	{
		                                            		IFolder folder = item as IFolder;
		                                            		return folder != null
		                                            		       	? folder.Children
		                                            		       	: new[] {item};
		                                            	});

		return FilesForCurrentFeatureFrom(many.OfType<IFile>()).Count() > 0;
	}

	void WriteFile(IFileSystemItem file)
	{
		StartElement("File");
		string id = WriteIdNameAndLongName(file);
		WriteAttribute("src", file.FullPath);
		WriteAttribute("DiskId", "1");
		WriteAttribute("Vital", "yes");

		string shortcut = GetShortcutName(id);
		if (null != shortcut)
		{
			WriteShortcut(shortcut, file, _currentDirectoryId);
		}
		EndElement();
	}

	string GetShortcutName(string id)
	{
		//if (null != _parameters)
		//{
		//    foreach (Shortcut item in _parameters.Shortcuts)
		//    {
		//        if (id == GetIdFromRelativePath(item.Path))
		//        {
		//            return item.Name;
		//        }
		//    }
		//}
		return null;
	}

	private void WriteShortcut(Shortcut shortcut)
	{
		IFileSystemItem shortcutTarget = ResolveFile(shortcut.Path);
		if (shortcutTarget == null)
			throw new ArgumentException(string.Format("Could not find file '{0}' required by shortcut '{1}'", shortcut.Path, shortcut.Name));
		WriteShortcut(shortcut.Name, shortcutTarget, GetIdFromPath(shortcutTarget.Parent));
	}

	private IFileSystemItem ResolveFile(string filePath)
	{
		IFolder path = _basePath;
		string[] pathComponents = filePath.Split(new [] {'/','\\'});
		foreach(var part in pathComponents.Take(pathComponents.Length - 1))
		{
			path = (IFolder) path[part];
			if (path == null)
				return null;
		}

		return path[pathComponents[pathComponents.Length - 1]];
	}

	void WriteShortcut(string displayName, IFileSystemItem fileSystemItem, string workingDirectory)
	{
		string fileId = GetIdFromPath(fileSystemItem);

		StartElement("Shortcut");
		WriteAttribute("Id", "s_" + fileId);

		string ext = Path.GetExtension(fileSystemItem.Name).Substring(1).ToLower();
		WriteAttribute("Icon", ext + ".ico");
		WriteAttribute("IconIndex", "0");
		WriteAttribute("Directory", "TargetMenuFolder");
		WriteAttribute("Name", fileSystemItem.ShortPathName);
		WriteAttribute("LongName", displayName);
		WriteAttribute("Description", displayName);
		WriteAttribute("Show", "normal");
		WriteAttribute("WorkingDirectory", workingDirectory);
		WriteAttribute("Target", "[#" + fileId + "]");
		EndElement();
	}

	string WriteIdNameAndLongName(IFileSystemItem path)
	{
		string id = GetIdFromPath(path);
		WriteAttribute("Id", id);
		WriteNameAndLongName(path);
		return id;
	}

	void WriteNameAndLongName(IFileSystemItem path)
	{
		string name = path.Name;
		string shortName = path.ShortPathName;
		WriteAttribute("Name", shortName);
		if (name != shortName)
		{
			WriteAttribute("LongName", name);
		}
	}

	string GetIdFromPath(IFileSystemItem fileSystemItem)
	{
		var path = fileSystemItem.FullPath;
		string existing;
		if (_fileIdMapping.TryGetValue(path, out existing))
			return existing;

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
}