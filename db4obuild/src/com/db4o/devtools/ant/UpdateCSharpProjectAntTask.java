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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class UpdateCSharpProjectAntTask extends Task {

	private List<FileSet> _sources = new ArrayList<FileSet>();
	private File _projectFile;
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
			log("loading '" + _projectFile + "'");
			CSharpProject project = CSharpProject.load(_projectFile);
			
			for (FileSet fs : _sources) {
				DirectoryScanner scanner = fs.getDirectoryScanner(this.getProject());
				project.addFiles(scanner.getIncludedFiles());
			}
			
			log("writing '" + _projectFile + "'");
			log("base source dir is '" + _baseDir + "'");
			project.writeToURI(_projectFile.toURI().toString());
			
		} catch (Exception x) {
			throw new BuildException(x, getLocation());
		}
	}
}
