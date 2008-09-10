package com.db4o.container.tests;

import com.db4o.container.*;
import com.db4o.container.tests.internal.*;

import db4ounit.*;

public class ContainerTestCase implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(ContainerTestCase.class).run();
	}
	
	final Container container = ContainerFactory.newContainer();
	
	public void testConventionBasedInstantiation() {

		final SimpleService service = container.produce(SimpleService.class);
		Assert.areSame(SimpleServiceImpl.class, service.getClass());
		
		Assert.areNotSame(service, container.produce(SimpleService.class));
	}
	
	public void testSingleton() {
		Assert.isNotNull(container.produce(SingletonService.class));
		Assert.areSame(container.produce(SingletonService.class), container.produce(SingletonService.class));
	}
	
	public void testDependencyResolution() {
		
		final ComplexService service = container.produce(ComplexService.class);
		Assert.areSame(container.produce(SingletonService.class), service.dependency());
	}
	
	public void testCustomService() {
		final SingletonServiceImpl singleton = new SingletonServiceImpl();
		final Container container = ContainerFactory.newContainer(singleton);
		Assert.areSame(singleton, container.produce(SingletonService.class));
		Assert.areSame(singleton, container.produce(ComplexService.class).dependency());
	}

}
