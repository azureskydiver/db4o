package com.db4odoc.concurrency;

/**
* @author roman.stoffel@gamlor.info
* @since 16.09.2010
*/
public class VersionedObject {
    private int version = 0;
    private transient boolean alreadyIncremented;

    public void increment(){
        if(!alreadyIncremented){
            version++;
            alreadyIncremented = true;
        }
    }

    public int getVersion(){
        return version;
    }

    @Override
    public String toString() {
        return "VersionedObject{" +
                "version=" + version +
                '}';
    }
}
