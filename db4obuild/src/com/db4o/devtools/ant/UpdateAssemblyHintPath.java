package com.db4o.devtools.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class UpdateAssemblyHintPath extends Task {

	private List<FileSet> _projectFiles = new ArrayList<FileSet>();

	private String _to;

	public UpdateAssemblyHintPath() {
	}

	public FileSet createProjectFiles() {
		FileSet set = new FileSet();
		_projectFiles.add(set);
		return set;
	}

	public void setTo(String to) {
		_to = to;
	}

	@Override
	public void execute() throws BuildException {
		try {
			for (FileSet fs : _projectFiles) {
				DirectoryScanner scanner = fs.getDirectoryScanner(this.getProject());
				for (String fname : scanner.getIncludedFiles()) {
					updateProjectFile(new File(scanner.getBasedir(), fname));
				}
			}
		} catch (Exception x) {
			throw new BuildException(x, getLocation());
		}
	}

	private void updateProjectFile(File file) throws Exception {
		log("Looking in '" + file + "' for references to '" + assemblyName() + "'");
		CSharpProject project = CSharpProject.load(file);
		String hintPath = project.getHintPathFor(assemblyName());
		if (hintPath == null) return;
		
		project.setHintPathFor(assemblyName(), _to);
		project.writeToURI(file.toURI().toString());
	}

	private String assemblyName() {
		return new File(_to).getName();
	}

}
