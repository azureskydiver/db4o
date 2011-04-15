package com.db4odoc.tutorial.runner;

/**
 * @author roman.stoffel@gamlor.info
 * @since 25.03.11
 */
public class DynamicCodeProofOfConcept {
    public static void main(String[] args) throws Exception {
        new DynamicCodeProofOfConcept().run();
    }

    private void run() throws Exception{
        ClassLoader cl = new ClassLoader(Thread.currentThread().getContextClassLoader()){

        };
    }
}
