package com.db4odoc.tutorial.runner;


import com.db4o.ObjectContainer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestExampleRunner {

    private ExampleRunner toTest;
    private TextViewModel viewModel;

    @BeforeMethod
    public void setup(){
        this.viewModel = new TextViewModel();
        this.toTest = ExampleRunner.create(viewModel.getWriter());
    }

    @Test
    public void runsCode(){
        toTest.run(RunnerTestHelper.class.getName(),"exampleToRun");
    }
    @Test
    public void printsIntoModel(){
        toTest.run(RunnerTestHelper.class.getName(),"printSomething");
        Assert.assertEquals(viewModel.getText(),"Hi"+SysInfo.NEW_LINE);
    }
    @Test
    public void disposesContainer(){
        ObjectContainer container = mock(ObjectContainer.class);
        ExampleRunner runner = ExampleRunner.create(viewModel.getWriter(), container);
        runner.dispose();
        verify(container).close();
    }
}
