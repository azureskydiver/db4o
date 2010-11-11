package com.db4o.util.eclipse.parser;

import com.db4o.util.eclipse.parser.impl.*;
import com.db4o.util.file.*;

public class EclipsePlatform {

	public static Workspace openWorkspace(IFile root) {
		return new WorkspaceImp(root);
	}

}
