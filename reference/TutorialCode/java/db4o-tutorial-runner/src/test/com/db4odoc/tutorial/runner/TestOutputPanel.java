package com.db4odoc.tutorial.runner;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestOutputPanel {

    private TextViewModel model;
    private OutputPanel toTest;

    @BeforeMethod
    public void setup(){
        this.model = new TextViewModel();
        this.toTest = new OutputPanel(model);
    }

    @Test
    public void hasEmptyTestFromModel(){
        String text = toTest.getOutputTextArea().getText();
        Assert.assertEquals(text,"");
    }
    @Test
    public void testIsPickedUpFromModel(){
        model.getWriter().println("Hi");
        String text = toTest.getOutputTextArea().getText();
        Assert.assertEquals(text,"Hi"+SysInfo.NEW_LINE);
    }
}
