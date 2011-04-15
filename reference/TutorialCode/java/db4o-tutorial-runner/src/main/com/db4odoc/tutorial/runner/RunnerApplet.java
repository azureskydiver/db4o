package com.db4odoc.tutorial.runner;


import java.applet.Applet;
import java.awt.*;

public class RunnerApplet  extends Applet {

    private static final String CLASS_TO_RUN = "classToRun";
    private static final String METHOD_TO_RUN = "methodToRun";
    private ExampleRunner runner;

    public RunnerApplet() throws HeadlessException {
        super();
    }

    @Override
    public void init() {
        super.init();
        TextViewModel model = new TextViewModel();
        this.runner = ExampleRunner.create(model.getWriter());
        this.setLayout(new FlowLayout());
        this.add(OutputPanel.create(model));
    }

    @Override
    public void start() {
        super.start();
        String classToRun = getParameter(CLASS_TO_RUN);
        String method = getParameter(METHOD_TO_RUN);
        runner.run(classToRun, method);
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
