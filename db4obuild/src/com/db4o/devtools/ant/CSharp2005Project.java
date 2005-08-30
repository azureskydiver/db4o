package com.db4o.devtools.ant;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CSharp2005Project extends CSharpProject {

	protected CSharp2005Project(Document document) throws Exception {
		super(document);
	}

	@Override
	protected Node createFileNode(String file) {
		Element node = createElement("Compile");
		node.setAttribute("Include", file);
		return node;
	}

	@Override
	protected Element getFilesContainerElement() throws Exception {
		Element compile = selectElement("//*[local-name()='ItemGroup']/*[local-name()='Compile']");
		if (null == compile) invalidProjectFile();
		
		Element container = (Element)compile.getParentNode();
		if (container.hasChildNodes()) {
			Element old = container;
			container = createElement("ItemGroup");
			old.getParentNode().replaceChild(container, old);
		}
		return container;
	}

}
