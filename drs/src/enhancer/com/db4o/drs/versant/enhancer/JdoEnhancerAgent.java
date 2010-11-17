package com.db4o.drs.versant.enhancer;


import java.lang.instrument.*;

public class JdoEnhancerAgent {
	
    public static void premain(String agentArguments, Instrumentation instrumentation)
    {
    	new EnhancerStarter(agentArguments).enhance();
    }

}
