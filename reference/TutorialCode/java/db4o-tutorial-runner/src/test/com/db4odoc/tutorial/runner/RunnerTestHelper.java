package com.db4odoc.tutorial.runner;


import com.db4o.ObjectContainer;
import org.testng.Assert;

public class RunnerTestHelper {

    public void exampleToRun(ObjectContainer container){
        Assert.assertNotNull(container);
    }

    public void printSomething(ObjectContainer container){
        System.out.println("Hi");
    }


}
