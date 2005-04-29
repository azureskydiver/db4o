/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

import com.yetac.doctor.applet.*;
import com.yetac.doctor.workers.*;

public class Doctor extends Task {
    
    public static void main(String[] args){
        Doctor doctor = new Doctor();
        String path = doctor.configurejtutorial();
        doctor.execute();
        try {
            BrowserLauncher.openURL(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String configurejtutorial(){
        setPdfBaseFont("/usr/share/fonts/msttcorefonts/verdana.ttf");
        String carlsWorkSpace = "C:/_db4o/HEAD";
        String davesWorkSpace = "/home/djo/workspaces/workspace.new";
        String workspace = davesWorkSpace;
        String tutorial = workspace + "/db4oj/tutorial"; 
        setName("f1");
        setHome(tutorial);
        setInteractive(true);
        setWorkspace(workspace);
        setInputSource(tutorial + "/src");
        setArchive("doctor-applets.jar, db4o-4.5-java1.4.jar, f1.jar");
        setVariable("java", true);
        return tutorial + "/out/index.html";
    }
    
    
    private String home;
    private String name;
    private String workspace;
    
    private String inputSource;
    
    private String sourceExtension;
    
    private Vector variables;
    private Hashtable variablesByName;
    
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
    
    public String author(){
        return "db4objects Inc.";
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
    
    public void setVariable(String name, Object value){
        if(variablesByName == null){
            variablesByName = new Hashtable();
        }
        variablesByName.put(name.toLowerCase(), value);
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
}
