package com.db4o.devtools.ant;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

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
			project.writeToFile(_projectFile);
			
		} catch (Exception x) {
			throw new BuildException(x, getLocation());
		}
	}
}
