package com.db4o.devtools.ant;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MonoDevelopProject extends CSharpProject {

	protected MonoDevelopProject(Document document) throws Exception {
		super(document);
	}

	@Override
	protected Element getFilesContainerElement() throws Exception {
		Element contents = selectElement("/Project/Contents");
		if (null == contents) invalidProjectFile();
		return contents;
	}

	@Override
	protected Node createFileNode(String file) {		
		//<File name="./MyClass.cs" subtype="Code" buildaction="Compile" />
		Element element = createElement("File");
		element.setAttribute("name", file);
		element.setAttribute("subtype", "Code");
		element.setAttribute("buildaction", "Compile");
		return element;
	}
	
	@Override
	protected String prepareFileNameForNode(String file) {
		return file.replace('\\', '/');
	}
}
