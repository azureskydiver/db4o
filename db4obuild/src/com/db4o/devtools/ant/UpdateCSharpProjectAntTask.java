package com.db4o.devtools.ant;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class UpdateCSharpProjectAntTask extends Task {

	private List<FileSet> _sources = new ArrayList<FileSet>();
	private File _projectFile;
	private Document _document;
	private Element _files;
	private URI _baseDir;
	
	public UpdateCSharpProjectAntTask() {
	}
	
	public FileSet createSources() {
		FileSet set = new FileSet();
		_sources.add(set);
		return set;
	}
	
	public void setProjectFile(File srcFile) throws IOException {
		_projectFile = srcFile;
		_baseDir = srcFile.getParentFile().toURI();
	}
	
	@Override
	public void execute() throws BuildException {
		try {
			loadSourceFile();
			updateFilesNode();
			writeTargetFile();
		} catch (Exception x) {
			throw new BuildException(x, getLocation());
		}
	}

	private void writeTargetFile() {
		log("writing '" + _projectFile + "'");
		log("base source dir is '" + _baseDir + "'");
		LSSerializer serializer = ((DOMImplementationLS)_document.getImplementation()).createLSSerializer();
		serializer.writeToURI(_document, _projectFile.toURI().toString());
	}
	

	private void updateFilesNode() {
		for (FileSet fs : _sources) {
			DirectoryScanner scanner = fs.getDirectoryScanner(this.getProject());
			appendFileNodes(scanner.getIncludedFiles());
		}
	}

	private void appendFileNodes(String[] files) {
		for (int i=0; i<files.length; ++i) {
			String file = files[i];
			appendFileNode(file);
		}
	}

	private void appendFileNode(String file) {
		_files.appendChild(createFileNode(file));
	}

	private Node createFileNode(String file) {
		Element node = _document.createElement("File");
		String relativePath = file.replace('/', '\\');
		node.setAttribute("RelPath", relativePath);
		node.setAttribute("SubType", "Code");
		node.setAttribute("BuildAction", "Compile");
		return node;
	}
	
	private void loadSourceFile() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		log("loading '" + _projectFile + "'");
		_document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_projectFile);
		_files = (Element)XPathFactory.newInstance().newXPath().evaluate(getXPathExpression(), _document, XPathConstants.NODE);
		if (null == _files) {
			throw new RuntimeException("Invalid project file");
		}
		if (_files.hasChildNodes()) {
			Node old = _files;
			_files = _document.createElement("Include");
			old.getParentNode().replaceChild(_files, old);
		}
	}
	
	private String getXPathExpression() {
		return _projectFile.getName().endsWith(".csdproj") ?
					"VisualStudioProject/ECSHARP/Files/Include" :
					"VisualStudioProject/CSHARP/Files/Include";
	}
}
