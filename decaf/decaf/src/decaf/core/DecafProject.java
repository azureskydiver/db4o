package decaf.core;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import sharpen.core.framework.resources.*;
import decaf.*;
import decaf.config.*;

public class DecafProject {
	
	public static class OutputTarget {
		
		private final TargetPlatform _targetPlatform;
		private final IJavaProject _targetProject;
		private DecafConfiguration _config;

		public OutputTarget(TargetPlatform targetPlatform, IJavaProject targetProject) {
			_targetPlatform = targetPlatform;
			_targetProject = targetProject;
		}

		public IJavaProject targetProject() {
			return _targetProject;
		}
		
		public TargetPlatform targetPlatform() {
			return _targetPlatform;
		}

		public DecafConfiguration config() {
			if (null == _config) {
				_config = _targetPlatform.defaultConfig();
			}
			return _config;
		}
	}
	
	public static DecafProject create(IJavaProject javaProject) throws CoreException {
		final DecafProject cached = (DecafProject) javaProject.getProject().getSessionProperty(SESSION_KEY);
		if (null != cached) {
			return cached;
		}
		
		final DecafProject decafProject = new DecafProject(javaProject);
		javaProject.getProject().setSessionProperty(SESSION_KEY, decafProject);
		return decafProject;
	}
	
	private static final QualifiedName SESSION_KEY = new QualifiedName(Activator.PLUGIN_ID, "decafSession");

	private static final QualifiedName TARGET_PLATFORMS = new QualifiedName(Activator.PLUGIN_ID, "targetPlatforms");

	private final IJavaProject _project;

	private List<OutputTarget> _targets;
	
	private DecafProject(IJavaProject project) {
		_project = project;
	}
	
	public IJavaProject project() {
		return _project;
	}

	public List<OutputTarget> targets() throws CoreException {
		if (null == _targets) {
			_targets = readTargets();
		}
		return _targets;
	}
	
	public void setTargetPlatforms(TargetPlatform... targetPlatforms) throws CoreException {
		
		_project.getProject().setPersistentProperty(TARGET_PLATFORMS, commaSeparatedPlatformIds(targetPlatforms));
		_targets = null;
	}

	private List<OutputTarget> readTargets() throws CoreException {
		return targetsForPlatforms(readTargetPlatforms());
	}

	private List<OutputTarget> targetsForPlatforms(
			final Iterable<TargetPlatform> targetPlatforms) throws CoreException {
		final List<OutputTarget> targets = new ArrayList<OutputTarget>();
		for (TargetPlatform platform : targetPlatforms) {
			targets.add(new OutputTarget(platform, decafProjectFor(platform)));
		}
		
		return targets;
	}
	
	private String commaSeparatedPlatformIds(TargetPlatform[] targetPlatforms) {
		final StringBuilder value = new StringBuilder();
		for (TargetPlatform targetPlatform : targetPlatforms) {
			if (value.length() > 0) {
				value.append(",");
			}
			value.append(targetPlatform.toString());
		}
		return value.toString();
	}

	private List<TargetPlatform> readTargetPlatforms()
			throws CoreException {
		final String targetPlatforms = _project.getProject().getPersistentProperty(TARGET_PLATFORMS);
		if (null == targetPlatforms) {
			return Collections.singletonList(TargetPlatform.JDK11);
		}
		
		final ArrayList<TargetPlatform> result = new ArrayList<TargetPlatform>();
		for (String platformId : targetPlatforms.split(",\\s*")) {
			final TargetPlatform platform = TargetPlatform.valueOf(platformId);
			result.add(platform);
		}
		return result;
	}
	
	private IJavaProject decafProjectFor(TargetPlatform platform) throws CoreException {
		IWorkspaceRoot root = _project.getProject().getWorkspace().getRoot();
		String decafProjectName = platform.appendPlatformId(_project.getElementName() + ".decaf", ".");
		IProject decafProject = root.getProject(decafProjectName);
		WorkspaceUtilities.initializeProject(decafProject, null);
		WorkspaceUtilities.addProjectNature(decafProject, JavaCore.NATURE_ID);
		
		IJavaProject decafJavaProject = JavaCore.create(decafProject);
		decafJavaProject.setRawClasspath(mapClasspathEntries(_project, decafJavaProject), null);
		return decafJavaProject;
	}
	
	private static IClasspathEntry[] mapClasspathEntries(IJavaProject javaProject,
			IJavaProject decafJavaProject) throws JavaModelException,
			CoreException {
		IClasspathEntry[] srcClasspath = javaProject.getRawClasspath();
		IClasspathEntry[] targetClasspath = new IClasspathEntry[srcClasspath.length];
		for (int i=0; i<srcClasspath.length; ++i) {
			IClasspathEntry entry = srcClasspath[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				targetClasspath[i] = createSourceFolder(decafJavaProject, entry.getPath());
			} else {
				targetClasspath[i] = entry;
			}
		}
		return targetClasspath;
	}
	
	private static IClasspathEntry createSourceFolder(IJavaProject decafJavaProject,
			IPath path) throws CoreException {
		
		IFolder folder = decafJavaProject.getProject().getFolder(path.removeFirstSegments(1));
		WorkspaceUtilities.initializeTree(folder, null);
		return JavaCore.newSourceEntry(folder.getFullPath(), new IPath[] {});
	}
}
