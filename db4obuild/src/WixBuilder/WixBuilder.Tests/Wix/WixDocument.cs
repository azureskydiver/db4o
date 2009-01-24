using System.Collections.Generic;
using System.Linq;
using System.Xml;

namespace WixBuilder.Tests.Wix
{
	public class WixDocument : WixElement
	{
		public WixDocument(XmlDocument document)
			: base(document.DocumentElement)
		{
		}

		public IEnumerable<WixFeature> Features
		{
			get { return SelectElements("//wix:Feature").Select(element => new WixFeature(element)); }
		}

		public IEnumerable<WixComponent> Components
		{
			get { return SelectElements("//wix:Component").Select(element => new WixComponent(element)); }
		}

		public IEnumerable<WixFile> Files
		{
			get { return Components.SelectMany(c => c.Files); }
		}

		public WixComponent ResolveComponentReference(string componentRef)
		{
			return new WixComponent(SelectElements("//wix:Component[@Id = '" + componentRef + "']").AssertSingle());
		}
	}


	public class WixElement
	{
		private readonly XmlElement _element;
		private readonly XmlNamespaceManager namespaces;

		public WixElement(XmlElement element)
		{
			_element = element;
			namespaces = new XmlNamespaceManager(element.OwnerDocument.NameTable);
			namespaces.AddNamespace("wix", WixScriptBuilder.WixNamespace);
		}

		public XmlElement ParentElement
		{
			get { return (XmlElement)_element.ParentNode; }
		}

		public IEnumerable<XmlElement> SelectElements(string xpath)
		{
			return SelectNodes(xpath).Cast<XmlElement>();
		}

		public XmlNodeList SelectNodes(string xpath)
		{
			return _element.SelectNodes(xpath, namespaces);
		}

		public string GetAttribute(string attributeName)
		{
			return _element.GetAttribute(attributeName);
		}
	}

	public class WixReferenceableElement : WixElement
	{
		public WixReferenceableElement(XmlElement element) : base(element)
		{
		}

		public string Id
		{
			get { return GetAttribute("Id"); }
		}
	}

	public class WixFile : WixReferenceableElement
	{
		public WixFile(XmlElement element) : base(element)
		{
		}
	}

	public class WixFeature : WixReferenceableElement
	{
		public WixFeature(XmlElement element)
			: base(element)
		{
		}

		public IEnumerable<string> ComponentReferences
		{
			get { return SelectNodes("wix:ComponentRef/@Id").Cast<XmlAttribute>().Select(attr => attr.Value);  }
		}

		public string Title
		{
			get { return GetAttribute("Title"); }
		}

		public string Description
		{
			get { return GetAttribute("Description"); }
		}
	}

	public class WixComponent : WixReferenceableElement
	{
		public WixComponent(XmlElement element)
			: base(element)
		{
		}

		public IEnumerable<WixFile> Files
		{
			get { return SelectElements("wix:File").Select(e => new WixFile(e)); }
		}

		public override string ToString()
		{
			return "WixComponent(" + Id + ", Files={ " + Files.MakeString(", ") + " }, ParentDirectory=" + ParentElement.GetAttribute("LongName")  + ")";
		}
	}
}