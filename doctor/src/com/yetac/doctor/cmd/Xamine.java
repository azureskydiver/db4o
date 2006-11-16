/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;


public class Xamine extends Command {
	
    public void resolve() {
        detectParameters();
        if(text == null) {
        	if(isMonoOrDotNet()){
        		examineParameter();
        	}
            text = " " + parameter + " ";
        }
    }

	public void write(DocsWriter writer) throws Exception {
		writer.write(this);
	}
	
	private boolean isMonoOrDotNet(){
		return source.files.task.isMonoOrDotNet();
	}
	
	private void examineParameter(){
		for (int i = 0; i < java.length; i++) {
			if(parameter.equals(java[i])){
				parameter = net[i];
				return;
			}
		}
		if(parameter.substring(0, 1).equals("#")){
			parameter = "#" + parameter.substring(1,2).toUpperCase() + parameter.substring(2);
			return;
		}
		
		if(parameter.endsWith("()")){
			parameter = parameter.toUpperCase();
			return;
		}
		
	}
	
	private static final String[] java = new String[]{
		"ObjectContainer",
		"ObjectSet",
		"Db4o.openFile()",
		"com.db4o.config.Configuration",
		"ObjectClass",
		"ObjectField",
		"ExtObjectContainer",
		
	};
	
	private static final String[] net = new String[]{
		"IObjectContainer",
		"IObjectSet",
		"Db4oFactory.OpenFile()",
		"Db4objects.Db4o.Config.Configuration",
		"IObjectClass",
		"IObjectField",
		"IExtObjectContainer",
	};

}
