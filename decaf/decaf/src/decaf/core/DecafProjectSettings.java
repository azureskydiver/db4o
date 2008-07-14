package decaf.core;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import sharpen.core.framework.resources.*;
import decaf.*;

public class DecafProjectSettings {
	
	public static class OutputTarget {
		
		private final TargetPlatform _targetPlatform;
		private final IJavaProject _targetProject;

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
	}
	
	public static DecafProjectSettings forProject(IJavaProject project) throws CoreException {
		final DecafProjectSettings cached = (DecafProjectSettings) project.getProject().getSessionProperty(SESSION_KEY);
		if (null != cached) {
			return cached;
		}
		
		final DecafProjectSettings settings = load(project);
		project.getProject().setSessionProperty(SESSION_KEY, settings);
		return settings;
	}

	private static DecafProjectSettings load(IJavaProject project)
			throws CoreException {
		final List<OutputTarget> targets = new ArrayList<OutputTarget>();
		final String targetPlatforms = project.getProject().getPersistentProperty(TARGET_PLATFORMS);
		for (String platformId : targetPlatforms.split(",\\s+")) {
			final TargetPlatform platform = TargetPlatform.valueOf(platformId);
			targets.add(new OutputTarget(platform, decafProjectFor(project, platform)));
		}
		return new DecafProjectSettings(targets);
	}
	
	public static IJavaProject decafProjectFor(IJavaProject javaProject, TargetPlatform platform) throws CoreException {
		IWorkspaceRoot root = javaProject.getProject().getWorkspace().getRoot();
		String decafProjectName = platform.appendPlatformId(javaProject.getElementName() + ".decaf", '.');
		IProject decafProject = root.getProject(decafProjectName);
		WorkspaceUtilities.initializeProject(decafProject, null);
		WorkspaceUtilities.addProjectNature(decafProject, JavaCore.NATURE_ID);
		
		IJavaProject decafJavaProject = JavaCore.create(decafProject);
		decafJavaProject.setRawClasspath(mapClasspathEntries(javaProject, decafJavaProject), null);
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

	
	public static final QualifiedName SESSION_KEY = new QualifiedName(Activator.PLUGIN_ID, "decafProjectProperties");

	public static final QualifiedName TARGET_PLATFORMS = new QualifiedName(Activator.PLUGIN_ID, "targetPlatforms");

	private final List<OutputTarget> _platforms;
	
	public DecafProjectSettings(List<OutputTarget> platforms) {
		_platforms = Collections.unmodifiableList(platforms);
	}

	public List<OutputTarget> targets() {
		return _platforms;
	}
}
