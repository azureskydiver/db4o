package com.db4o.util;
/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */


/**
 * This file was taken from the db4oj.tests project from
 * the com.db4o.db4ounit.util package. 
 * TODO: move to own project and supply as separate Jar.
 */


import java.io.*;

public class IOServices {
    
	public static String buildTempPath(String fname) {
		return Path4.combine(Path4.getTempPath(), fname);
	}

	public static String safeCanonicalPath(String path) {
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return path;
		}
	}
	
	public static boolean killProcess(String processName) throws IOException, InterruptedException{
		ProcessInfo processInfo = findProcess(processName);
		if(processInfo == null){
			return false;
		}
		ProcessResult processResult = exec("taskkill.exe", new String[]{"/PID", Long.toString(processInfo.processId), "/F"});
		return processResult.returnValue == 0;
	}
	
	public static ProcessInfo findProcess(String processName) throws IOException, InterruptedException{
		ProcessInfo[] runningProcesses = runningProcesses();
		for (ProcessInfo processInfo : runningProcesses) {
			if(processName.equals(processInfo.name)){
				return processInfo;
			}
		}
		return null;
	}
	
	public static ProcessInfo[] runningProcesses() throws IOException, InterruptedException{
		ProcessResult processResult = exec("tasklist.exe", new String[]{"/fo", "csv", "/nh"});
		String[] lines = processResult.out.split("\n");
		ProcessInfo[] result = new ProcessInfo[lines.length];
		for (int i = 0; i < lines.length; i++) {
			String[] infos = lines[i].split(",");
			String processName = unquote(infos[0]);
			long processId = Long.parseLong(unquote(infos[1]));
			result[i] = new ProcessInfo(processName, processId);
		}
		return result;
	}
	
	private static String unquote(String str){
		return str.replaceAll("\"", "");
	}
	
	public static ProcessResult exec(String program) throws IOException, InterruptedException{
	    return exec(program, null);
	}
	
	public static ProcessResult exec(String program, String[] arguments) throws IOException, InterruptedException{
	    ProcessRunner runner = new ProcessRunner(program, arguments);
	    runner.waitFor();
	    return runner.processResult();  
	}

	public static ProcessRunner start(String program, String[] arguments) throws IOException {
	    return new ProcessRunner(program, arguments);
	}

	public static ProcessResult execAndDestroy(String program, String[] arguments, String expectedOutput, long timeout) throws IOException{
        ProcessRunner runner = new ProcessRunner(program, arguments);
        runner.destroy(expectedOutput, timeout);
        return runner.processResult();
    }
	
	public static class DestroyTimeoutException extends RuntimeException{
	}
	
	public static class ProcessTerminatedBeforeDestroyException extends RuntimeException{
	}
	
	public static class ProcessRunner{
	    
	    final long _startTime;
	    
	    private final String _command;
	    
        private final StreamReader _inputReader;
        
        private final StreamReader _errorReader;
        
        private final Process _process;
        
        private int _result;
        
	    public ProcessRunner(String program, String[] arguments) throws IOException{
    		_command = generateCommand(program, arguments);
    		_process = Runtime.getRuntime().exec(_command);
    		_inputReader = new StreamReader(_process.getInputStream());
    		_errorReader = new StreamReader(_process.getErrorStream());
    		_startTime = System.currentTimeMillis();
	    }
	    
	    private String generateCommand (String program, String[] arguments){
            String command = program;
            if(arguments != null){
                for (int i = 0; i < arguments.length; i++) {
                    command += " " + arguments[i];
                }
            }
            return command;
	    }
	    
	    public int waitFor() throws InterruptedException{
	        _result = _process.waitFor();
	        stopReaders();
	        return _result;
	    }
	    
	    private boolean outputHasStarted(){
	        return _inputReader.outputHasStarted() || _errorReader.outputHasStarted();
	    }

	    public boolean outputContains(String str){
	        return _inputReader.outputContains(str) || _errorReader.outputContains(str);
	    }
	    
	    private void checkTimeOut(long time){
	    	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        if(System.currentTimeMillis() - _startTime > time){
	            throw new DestroyTimeoutException();
	        }
	    }
	    
	    public void destroy(String expectedOutput, long timeout){
	        try{
    	        checkIfStarted(expectedOutput, timeout);
	        } 
	        finally {
	        	destroy();
	        }
	    }

	    public void destroy(){
	        try{
    	        checkIfTerminated();
    	        
    	        // Race condition: If the process is terminated right here , it may
    	        // terminate successfully before being destroyed.
    	        
	        } finally {
	            _process.destroy();
	            stopReaders();
	        }
	    }

	    public void write(String msg) throws IOException {
	    	OutputStreamWriter out = new OutputStreamWriter(_process.getOutputStream());
			out.write(msg + "\n");
			out.flush();
	    }
	    
        public void checkIfStarted(String expectedOutput, long timeout) {
            while(! outputHasStarted()){
	            checkTimeOut(timeout);
	        }
	        while(! outputContains(expectedOutput)){
	            checkTimeOut(timeout);
	        }
        }

        private void checkIfTerminated() {
            boolean ok = false;
	        try{
	            _process.exitValue();
	        }catch (IllegalThreadStateException ex){
	            ok = true;
	        }
	        if(! ok){
	            throw new ProcessTerminatedBeforeDestroyException();
	        }
        }
	    
	    private void stopReaders(){
	        _inputReader.stop();
	        _errorReader.stop();
	    }
	    
	    public ProcessResult processResult(){
	    	return new ProcessResult(_command, _inputReader.result(), _errorReader.result(), _result);
	    }
	    
	    
	}
	

    static class StreamReader implements Runnable {
        
        private final Object _lock = new Object();
        
        private final InputStream _stream;
        
        private final Thread _thread;
        
        private final StringBuffer _stringBuffer = new StringBuffer();
        
        private boolean _stopped;
        
        private String _result;
        
        StreamReader(InputStream stream){
            _stream = stream;
            _thread = new Thread(this);
            _thread.start();
        }
        
        public void run() {
            final InputStream bufferedStream = new BufferedInputStream(_stream);
            try {
                while(! _stopped){
                    int i = bufferedStream.read();
                    if(i >= 0){
                        synchronized(_lock){
                            _stringBuffer.append((char)i);
                            // System.out.print((char)i);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            _result = _stringBuffer.toString();
        }
        
        public boolean outputHasStarted(){
            synchronized(_lock){
                return _stringBuffer.length() > 0;
            }
        }
        
        public boolean outputContains(String str){
            synchronized(_lock){
                return _stringBuffer.toString().indexOf(str) >= 0;
            }
        }
        
        public void stop(){
            _stopped = true;
            try {
                _thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        public boolean hasResult(){
            return _result != null && _result.length() > 0;
        }
        
        public String result(){
            return _result;
        }
    }
    
    public static String joinArgs(String separator, String[] args, boolean doQuote)
    {
        StringBuffer buffer = new StringBuffer();
        for (String arg : args)
        {
            if (buffer.length() > 0) buffer.append(separator);
            buffer.append((doQuote ? quote(arg) : arg));
        }
        return buffer.toString();
    }
    
    public static String quote(String s)
    {
        if (s.startsWith("\"")) return s;
        return "\"" + s + "\"";
    }

}
