package com.db4odoc.tutorial.runner;


import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.PagingMemoryStorage;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4odoc.tutorial.utils.NoArgAction;

import java.io.PrintStream;
import java.lang.reflect.Method;

import static com.db4odoc.tutorial.utils.ExceptionUtils.reThrow;

public class ExampleRunner {
    private final PrintStream writer;
    private final ClassLoader loader;
    private final ObjectContainer container;

    private ExampleRunner(PrintStream writer, ObjectContainer container) {
        this.writer = writer;
        this.loader = ExampleRunner.class.getClassLoader();
        this.container = container;

    }

    private static ObjectContainer openContainer(ClassLoader loader) {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().reflectWith(new JdkReflector(loader));
        config.file().storage(new PagingMemoryStorage());
        return Db4oEmbedded.openFile(config,"!In:Memory!");
    }

    public static ExampleRunner create(PrintStream writer){
        return create(writer,openContainer(ExampleRunner.class.getClassLoader()));
    }
    public static ExampleRunner create(PrintStream writer, ObjectContainer container){
        return new ExampleRunner(writer,container);
    }

    public void run(String name, String exampleToRun) {
        try {
            final Class classToRun = loader.loadClass(name);
            final Method methodToRun = classToRun.getMethod(exampleToRun,
                    new Class[]{ObjectContainer.class});

            withRedirectedOut(new NoArgAction() {
                @Override
                public void invoke() {
                    ExampleRunner.this.invoke(methodToRun, classToRun);
                }
            });
        } catch (Exception e) {
            throw reThrow(e);
        }
    }

    private void invoke(Method methodToRun,
                        Class classToRun) {
        try {
            methodToRun.invoke(classToRun.newInstance(), new Object[]{container});
        } catch (Exception e) {
            throw reThrow(e);
        }
    }

    private void withRedirectedOut(NoArgAction toRun) {
        PrintStream oldout = System.out;
        try {
            System.setOut(writer);
            toRun.invoke();
        } finally {
            System.setOut(oldout);
        }
    }

    public void dispose() {
        container.close();
    }
}
