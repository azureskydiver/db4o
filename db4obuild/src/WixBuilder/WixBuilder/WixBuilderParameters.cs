using System.IO;
using System.Xml.Serialization;

public class WixShortcut
{
	public string Path;
	public string Name;
}

public class KnownId
{
	public string Id;
	public string Path;
}

public class Feature
{
	[XmlAttribute]
	public string Title;
	public string Description;
	public Content Content;
}

public class Content
{
	[XmlAttribute] public string Include;
	[XmlAttribute] public string Exclude;
}

public class WixBuilderParameters
{
	public WixShortcut[] Shortcuts;

	public KnownId[] KnownIds;

	public Feature[] Features;

	public static WixBuilderParameters Load(string fname)
	{
		using (TextReader reader = File.OpenText(fname))
		{
			XmlSerializer serializer = new XmlSerializer(typeof(WixBuilderParameters));
			return (WixBuilderParameters)serializer.Deserialize(reader);
		}
	}
}