package org.polepos.circuits.hungaroring;

public interface HungaroringDriver {

    void write();
	
    void queryWithTwoWorkers() throws Exception;
    
    void queryWithFourWorkers() throws Exception;
    
}
