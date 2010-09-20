package com.db4odoc.concurrency;


public class VersionConflictException extends RuntimeException{

    public VersionConflictException(int committerVersion,
                                    int currentVersion, Object object) {
        super("Committer-Version: "+committerVersion+
                " Current Version: "+currentVersion +
                " Object '"+object+"'"
        );
    }
}
