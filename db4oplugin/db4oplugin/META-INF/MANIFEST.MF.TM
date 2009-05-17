Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: db4o Instrumentation Plugin
Bundle-SymbolicName: db4oplugin; singleton:=true
Bundle-Version: 1.0.0
Bundle-Activator: com.db4o.eclipse.Db4oPluginActivator
Bundle-Vendor: Versant
Require-Bundle: org.eclipse.ui,
 org.eclipse.core.runtime,
 org.eclipse.core.resources,
 org.eclipse.jdt.core;bundle-version="3.4.4",
 org.eclipse.jdt.ui;bundle-version="3.4.2",
 org.eclipse.text;bundle-version="3.4.0",
 org.eclipse.help;bundle-version="3.3.102"
Bundle-RequiredExecutionEnvironment: JavaSE-1.6
Bundle-ActivationPolicy: lazy
Bundle-ClassPath: lib/db4o-@db4o.version.iteration.revision@-all-java5.jar,
 .,
 lib/scala-library.jar,
 scala-bin/
