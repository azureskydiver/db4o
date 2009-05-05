/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.yetac.doctor;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

import com.yetac.doctor.applet.*;
import com.yetac.doctor.cmd.*;
import com.yetac.doctor.workers.*;

public class Doctor extends Task {

	private static final PlatformConfiguration MAIN_PLATFORM = PlatformConfiguration.JAVA;
	
	private static final String MAIN_WORKSPACE = "e:/db4o/trunk/";
	
	private static final String MAIN_FONT = "C:/WINDOWS/Fonts/VERDANA.TTF";

    public static void main(String[] args){
        
        if(args == null || args.length == 0){
        	args = new String[] {
        	    	MAIN_WORKSPACE,
        	    	MAIN_FONT,
        	} ;
        }

		if(args.length<1||args.length>2) {
			System.out.println("Usage: Doctor <workspace path> [<pdf font path>]");
			return;
		}
        Doctor doctor = new Doctor();
        String path = doctor.configure(args);
        doctor.setPattern("version", "6.3");
        doctor.execute();
        try {
            BrowserLauncher.openURL(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String configure(String[] args){
        if(MAIN_PLATFORM == PlatformConfiguration.JAVA){
        	return configureJavaTutorial(args[0],(args.length>1 ? args[1] : null));
        }
        if (MAIN_PLATFORM == PlatformConfiguration.NET){
        	return configureNetTutorial(args[0],(args.length>1 ? args[1] : null));
        } 
       throw new IllegalStateException();
    }
    
    public String configureJavaTutorial(String workspace,String pdffontpath){
		if(pdffontpath!=null) {
			setPdfBaseFont(pdffontpath);
		}
        String tutorial = workspace + "/tutorial"; 
        setName("f1");
        setHome(tutorial);
        setInteractive(true);
        setWorkspace(workspace);
        setInputSource(tutorial + "/src");
        setArchive("doctor-applets.jar, db4o-5.0-java1.2.jar, f1.jar");
        setVariable("java", true);
        IgnoreInputFolder iif = createIgnoreInputFolder();
        iif.setName("net");
        iif = createIgnoreInputFolder();
        iif.setName("mono");
        return tutorial + "/out/index.html";
    }
    
    public String configureNetTutorial(String workspace,String pdffontpath){
		if(pdffontpath!=null) {
			setPdfBaseFont(pdffontpath);
		}
        String tutorial = workspace + "/tutorial"; 
        setName("f1");
        setHome(tutorial);
        setInteractive(false);
        setShowCodeExecutionResults(true);
        setWorkspace(workspace);
        
        setInputSource(workspace + "/db4o.net/Db4oTutorial/Db4odoc.Tutorial.Chapters");
        setSourcePathResolver("com.yetac.doctor.cmd.DotNetTutorialSourcePathResolver");
        setSourceExtension("cs");
        setArchive("doctor-applets.jar, db4o-4.5-java1.4.jar, f1.jar");
        setVariable("java", false);
        setVariable("net", true);
        setVariable("mono", false);
        IgnoreInputFolder iif = createIgnoreInputFolder();
        iif.setName("java");
        iif = createIgnoreInputFolder();
        iif.setName("mono");
        return tutorial + "/out/index.html";
    }
    
    private String home;
    private String name;
    private String workspace;
    
    private String inputSource;
    private String sourcePathResolverName;
    private boolean upperCaseDirectoryNames;
    
    private String sourceExtension;
    
    private Vector variables;
    private Hashtable variablesByName;
    
    private Vector patterns;
    private Hashtable patternsByName;
    
    private Vector ignoreInputFolders;
    private Hashtable ignoreInputFoldersByName;
    
    private Vector runnerClassPaths;

    private String outputPath;
    private String outputFile;
    
    private String pdfBaseFont;
    
    private boolean interactive = true;
    
    private String archive;
    
    private String outlineImage;
    private String linkHome;
    
    private boolean showCodeExecutionResults;
    
    

    
    public void setWorkspace(String workspace) {
        if(!workspace.endsWith("/")) {
            workspace+="/";
        }
        this.workspace=workspace;
    }
    
    public void setName(String name) {
        this.name=name;
    }
    
    public void setHome(String home){
        this.home = home;
    }
    
    public void execute() throws BuildException {
        
        checkConfiguration();
        
        ignoreInputFoldersByName = new Hashtable();
        if(ignoreInputFolders != null){
            Iterator i = ignoreInputFolders.iterator();
            while(i.hasNext()){
                IgnoreInputFolder ignoreFolder = (IgnoreInputFolder)i.next();
                ignoreInputFoldersByName.put(ignoreFolder.getName(), ignoreFolder);
            }
        }
        
        if(variablesByName == null){
            variablesByName = new Hashtable();
        }
        if(variables != null){
            Iterator i = variables.iterator();
            while(i.hasNext()){
                Variable variable = (Variable)i.next();
                variablesByName.put(variable.getName().toLowerCase(), variable.getValue());
            }
        }
        
        if(patternsByName == null){
            patternsByName = new Hashtable();
        }
        
        if(patterns != null){
        	System.out.println("Patterns not null");
            Iterator i = patterns.iterator();
            while(i.hasNext()){
                Pattern pattern = (Pattern)i.next();
                System.out.println(pattern.getName().toLowerCase()+ " " + pattern.getValue());
                patternsByName.put(pattern.getName().toLowerCase(), pattern.getValue());
            }
        }
        
        try {
            File file = new File(home());
            if(! file.exists()) {
                log("Directory " + home() + " does not exist.");
                log("Check your Doctor task setup.");
            }else{
                Files files = new Files();
                files.findFiles(this);
                files.parse();
                files.resolve();
                files.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("Error running doctor on workspace "+ workspace, e);
        }
    }
    
    public String home(){
        if(home != null){
            return home;
        }
        return getWorkspace() + name;
    }
    
    public String inputRoot(){
        return home() + "/in";
    }
    
    public String inputDocs(){
        return inputRoot() + "/docs";
    }
    
    public String inputImages(){
        return inputRoot() + "/images";
    }

    public String inputHTML(){
        return inputRoot() + "/html"; 
    }
    
    public String getInputSource(){
        if(inputSource != null){
            return inputSource;
        }
        return inputRoot() + "/src";
    }
    
    public void setInputSource(String source){
        inputSource = source;
    }
    
    public String getOutputPath(){
        if(outputPath != null){
            return outputPath;
        }
        return home() + "/out";
    }
    
    public void setOutputPath(String path){
        outputPath = path;
    }
    
    public String getOutputFile(){
        if(outputFile != null){
            return outputFile;
        }
        return "doctor";
    }
    
    public void setOutputFile(String name){
        outputFile = name;
    }
    
    public boolean outlineNumbers(){
        return true;
    }
    
    public String getSourceExtension(){
        if(sourceExtension != null){
            return sourceExtension;
        }
        return ".java";
    }
    
    public void setSourceExtension(String ext){
        if(ext.indexOf(".") != 0){
            ext = "." + ext;
        }
        sourceExtension = ext;
    }
    
    public void setPdfBaseFont(String path){
        pdfBaseFont = path;
    }
    
    public String getPdfBaseFont(){
        if(pdfBaseFont != null){
            return pdfBaseFont;
        }
        return "C:\\WINDOWS\\Fonts\\VERDANA.TTF";
    }

    public void setSourcePathResolver(String sourcePathResolverName){
    	this.sourcePathResolverName = sourcePathResolverName;
    }
    
    public SourcePathResolver getSourcePathResolver() throws Exception {
    	if(sourcePathResolverName!=null) {
    		return (SourcePathResolver) Class.forName(sourcePathResolverName).newInstance();
    	}
    	return SourcePathResolver.IDENTITY;
    }

    public void setUpperCaseDirectoryNames(boolean upperCaseDirectoryNames){
        this.upperCaseDirectoryNames=upperCaseDirectoryNames;
    }
    
    public boolean getUpperCaseDirectoryNames(){
        return upperCaseDirectoryNames;
    }

    public String author(){
        return "Versant Inc.";
    }
    
    public String title(){
        return "db4o tutorial";
    }
    
    public String creator(){
        return "Doctor";
    }
    
    public boolean isInputFolderIgnored(String folderName){
        return ignoreInputFoldersByName.get(folderName) != null;
    }
    
    public String getWorkspace(){
        return workspace;
    }
    
    private void checkConfiguration(){
        String path = getPdfBaseFont();
        File file = new File(path);
        if(! file.exists()){
            log("The PDF base font " + path + " does not exist on your system.");
        }
    }
    
    public void log(String msg) {
        System.out.println(msg);
    }
    
    public void writeRunnerClassPaths(StringBuffer sb){
        if(runnerClassPaths != null){
            int j = 0;
            Iterator i = runnerClassPaths.iterator();
            while(i.hasNext()){
                RunnerClassPath runnerClassPath = (RunnerClassPath)i.next();
                sb.append("<PARAM NAME=\"");
                sb.append(DoctorConsoleApplet.RUNNER_CLASS_PATH);
                sb.append(j++);
                sb.append("\" VALUE=\"");
                sb.append(runnerClassPath.getPath());
                sb.append("\">\r\n");
            }
        }
    }
    
    public RunnerClassPath createRunnerClassPath(){
        if(runnerClassPaths == null){
            runnerClassPaths = new Vector();
        }
        RunnerClassPath runnerClassPath = new RunnerClassPath();
        runnerClassPaths.add(runnerClassPath);
        return runnerClassPath;
    }
    
    public class RunnerClassPath{
        
        private String path;
        
        public void setPath(String path){
            this.path = path;
        }
        
        public String getPath(){
            return path;
        }
    }
    
    public Variable createVariable(){
        if(variables == null){
            variables = new Vector();
        }
        Variable variable = new Variable();
        variables.add(variable);
        return variable;
    }
    
    public Pattern createPattern(){
        if(patterns == null){
        	patterns = new Vector();
        }
        Pattern pattern = new Pattern();
        patterns.add(pattern);
        return pattern;
    }
    
    public class Variable{
        
        private String name;
        private Object value;
        
        public Variable(){
        }
        
        public void setName(String name){
            this.name = name.toLowerCase();
        }
        
        public void setBoolean(boolean value){
            this.value = new Boolean(value);
        }
        
        public void setString(String value){
            this.value = value;
        }
        
        public String getName(){
            return name;
        }
        
        public Object getValue(){
            return value;
        }
    }
    
    public class Pattern{
        
        private String name;
        private Object value;
        
        public Pattern(){
        }
        
        public void setName(String name){
            this.name = name.toLowerCase();
        }
        
        public void setValue(String value){
            this.value = value;
        }
        
        public String getName(){
            return name;
        }
        
        public Object getValue(){
            return value;
        }
    }
    
    public void setVariable(String name, Object value){
        if(variablesByName == null){
            variablesByName = new Hashtable();
        }
        variablesByName.put(name.toLowerCase(), value);
    }

    public void setPattern(String name, Object value){
        if(patternsByName == null){
        	patternsByName = new Hashtable();
        }
        patternsByName.put(name.toLowerCase(), value);
    }

    public Object getPattern(String name){
        if(patternsByName == null){
            return null;
        }
        return patternsByName.get(name);
    }

    public void setVariable(String name, boolean value){
		setVariable(name,Boolean.valueOf(value));
    }

    public boolean variableIsTrue(String variableName) {
        if(variablesByName != null){
            Object obj = variablesByName.get(variableName.toLowerCase());
            if(obj instanceof Boolean) {
                return ((Boolean)obj).booleanValue();
            }
        }
        return false;
    }
    
    public void setInteractive(boolean interactive){
        this.interactive = interactive;
    }

    public boolean isInteractive(){
        return interactive;
    }
    
    public void setArchive(String archive){
        this.archive = archive;
    }
    
    public String getArchive(){
        return archive;
    }
    
    public void setOutlineImage(String imageName){
        outlineImage = imageName;
    }
    
    public String getOutlineImage(){
        return outlineImage;
    }
    
    public void setLinkHome(String url){
        linkHome = url;
    }
    
    public String getLinkHome(){
        return linkHome;
    }
    
    public void setShowCodeExecutionResults(boolean flag){
        showCodeExecutionResults = flag;
    }
    
    public boolean doShowCodeExecutionResults(){
        return showCodeExecutionResults;
    }
    
    public IgnoreInputFolder createIgnoreInputFolder(){
        if(ignoreInputFolders == null){
            ignoreInputFolders = new Vector();
        }
        IgnoreInputFolder ignoreFolder = new IgnoreInputFolder();
        ignoreInputFolders.add(ignoreFolder);
        return ignoreFolder;
    }
    
    public class IgnoreInputFolder{
        
        private String name;
        
        public IgnoreInputFolder(){
        }
        
        public void setName(String name){
            this.name = name;
        }
        
        public String getName(){
            return name;
        }
    }
    
    public boolean isMonoOrDotNet(){
    	return (variableIsTrue("net") || variableIsTrue("mono"));
    }
    
	private static class PlatformConfiguration{
		private static final PlatformConfiguration JAVA = new PlatformConfiguration();
		private static final PlatformConfiguration NET = new PlatformConfiguration();
	}

}
