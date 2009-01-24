using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Xml;
using WixBuilder;
using System.Linq;

public class WixScriptBuilder
{
	public const string WixNamespace = "http://schemas.microsoft.com/wix/2003/01/wi";

	readonly IFolder _basePath;
	readonly XmlWriter _writer;
	string _currentDirectoryId;
	readonly ArrayList _components;
	readonly WixBuilderParameters _parameters;
	readonly Dictionary<string, string> _fileIdMapping = new Dictionary<string, string>();
	private Predicate<string> _currentFeatureFilePredicate;

	public WixScriptBuilder(TextWriter writer, string basePath, WixBuilderParameters parameters) : this(writer, NativeFileSystem.GetFolder(basePath), parameters)
	{
	}

	public WixScriptBuilder(TextWriter writer, IFolder basePath, WixBuilderParameters parameters)
		: this(XmlTextWriterFor(writer), basePath, parameters)
	{
	}

	public WixScriptBuilder(XmlWriter writer, IFolder basePath, WixBuilderParameters parameters)
	{
		parameters.Validate();
		_writer = writer;
		_basePath = basePath;
		_components = new ArrayList();
		_parameters = parameters;
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
		var text = new XmlTextWriter(writer);
		text.Formatting = Formatting.Indented;
		return text;
	}

	public void Build()
	{
		_writer.WriteStartDocument();
		_writer.WriteStartElement("Wix");
		_writer.WriteAttributeString("xmlns", WixNamespace);
		_writer.WriteStartElement("Fragment");
		_writer.WriteAttributeString("Id", "DirectoriesFilesAndComponents");

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
		foreach (var feature in _parameters.Features)
		{
			_currentFeatureFilePredicate = PredicateFor(feature);
			WriteRootDirectory();
			WriteFeature(feature, _components.Cast<string>());
		}
	}

	private void WriteFeature(Feature feature, IEnumerable<string> componentIds)
	{
		_writer.WriteStartElement("Feature");
		_writer.WriteAttributeString("Id", feature.Id);
		_writer.WriteAttributeString("Level", "1");
		_writer.WriteAttributeString("ConfigurableDirectory", "INSTALLDIR");
		
		_writer.WriteAttributeString("Description", feature.Description);
		_writer.WriteAttributeString("Title", feature.Title);
		
		_writer.WriteAttributeString("TypicalDefault", "install");
		_writer.WriteAttributeString("InstallDefault", "local");

		foreach (string componentId in componentIds)
		{
			WriteComponentRef(componentId);
		}

		_writer.WriteEndElement();
	}

	private Predicate<string> PredicateFor(Feature feature)
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

	void WriteRootDirectory()
	{
		_writer.WriteStartElement("Directory");
		_writer.WriteAttributeString("Id", "TARGETDIR");
		_writer.WriteAttributeString("Name", "SourceDir");

		WriteDirectoryComponent("c_" + GetIdFromPath(_basePath), _basePath);
		WriteSubDirectories(_basePath);

		_writer.WriteEndElement();
	}

	void WriteDirectory(IFolder path)
	{
		_writer.WriteStartElement("Directory");
		_currentDirectoryId = WriteIdNameAndLongName(path);

		WriteDirectoryComponent("c_" + _currentDirectoryId, path);
		WriteSubDirectories(path);

		_writer.WriteEndElement();
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
		_writer.WriteStartElement("Component");
		_writer.WriteAttributeString("Id", id);
		_writer.WriteAttributeString("Guid", NewGuid());
		foreach (var file in fileSet)
		{
			WriteFile(file);
		}
		_writer.WriteEndElement();
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

	void WriteFile(IFile file)
	{
		_writer.WriteStartElement("File");
		string id = WriteIdNameAndLongName(file);
		_writer.WriteAttributeString("src", file.FullPath);
		_writer.WriteAttributeString("DiskId", "1");
		_writer.WriteAttributeString("Vital", "yes");

		string shortcut = GetShortcutName(id);
		if (null != shortcut)
		{
			WriteShortcut(shortcut, file, _currentDirectoryId);
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

	void WriteShortcut(string displayName, IFileSystemItem fileSystemItem, string workingDirectory)
	{
		string fileId = GetIdFromPath(fileSystemItem);

		_writer.WriteStartElement("Shortcut");
		_writer.WriteAttributeString("Id", "s_" + fileId);

		string ext = Path.GetExtension(fileSystemItem.Name).Substring(1).ToLower();
		_writer.WriteAttributeString("Icon", ext + ".ico");
		_writer.WriteAttributeString("IconIndex", "0");
		_writer.WriteAttributeString("Directory", "TargetMenuFolder");
		_writer.WriteAttributeString("Name", fileSystemItem.ShortPathName);
		_writer.WriteAttributeString("LongName", displayName);
		_writer.WriteAttributeString("Description", displayName);
		_writer.WriteAttributeString("Show", "normal");
		_writer.WriteAttributeString("WorkingDirectory", workingDirectory);
		_writer.WriteEndElement();
	}

	string WriteIdNameAndLongName(IFileSystemItem path)
	{
		string id = GetIdFromPath(path);
		_writer.WriteAttributeString("Id", id);
		WriteNameAndLongName(path);
		return id;
	}

	void WriteNameAndLongName(IFileSystemItem path)
	{
		string name = path.Name;
		string shortName = path.ShortPathName;
		_writer.WriteAttributeString("Name", shortName);
		if (name != shortName)
		{
			_writer.WriteAttributeString("LongName", name);
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