package com.db4odoc.f1.diagnostics;

import com.db4o.diagnostic.*;

public class IndexDiagListener  implements DiagnosticListener
{
	   public void onDiagnostic(Diagnostic d) {
		   if (d.getClass().equals(LoadedFromClassIndex.class)){
	        System.out.println(d.toString());
		   }
		   /*if (d.getClass().getName().equals("com.db4o.diagnostic.LoadedFromClassIndex")){
			   System.out.println("Class name match "+ d.getClass().getName());
		       System.out.println(d.toString());
		   } else {
			   System.out.println("Class name = "+ d.getClass().getName());
			   
		   }*/
	    }
}
