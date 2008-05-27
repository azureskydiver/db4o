/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the sharpen open source java to c# translator.

sharpen is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

sharpen is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */

package sharpen.ui.tests;

import java.io.IOException;

import sharpen.core.SharpenConversionBatch;
import sharpen.core.resources.SimpleProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;

public class BatchConverterTestCase extends AbstractConversionTestCase {

	public void testSingleClassEmptyPackage() throws Throwable {
		runBatchConverterTestCase("EmptyClass");
	}
	
	public void testMultipleClassesEmptyPackage() throws Throwable {
		runBatchConverterTestCase("EmptyClass", "AnotherEmptyClass");
	}
	
	public void testKeywordNamespaces() throws Throwable {
		runBatchConverterTestCase("namespaceMapping/out/event/Foo");
	}
	
	public void testEventInterfaceAndClassInDifferentCompilationUnits() throws Throwable, IOException, Throwable {
		runBatchConverterPascalCaseTestCase("events/EventInterface", "events/EventInterfaceImpl");
	}
	
	private void runBatchConverterPascalCaseTestCase(String... resourceNames) throws CoreException, IOException, Throwable {
		runBatchConverterTestCase(newPascalCaseIdentifiersConfiguration(), resourceNames);
	}
	
	private void runBatchConverterTestCase(String... resourceNames) throws CoreException, IOException, Throwable {
		runBatchConverterTestCase(getConfiguration(), resourceNames);
	}
	
	public void testSingleClassSimplePackageAndTargetFolder() throws Throwable {

		runResourceTestCaseWithTargetFolder("mp/Albatross");
	}
	
	public void testSingleClassNestedPackageAndTargetFolder() throws Throwable {
		
		runResourceTestCaseWithTargetFolder("mp/nested/Parrot");
		
	}

	private void runResourceTestCaseWithTargetFolder(String path)
			throws Throwable {

		TestCaseResource resource = new TestCaseResource(path);
		ICompilationUnit cu = createCompilationUnit(resource);

		SimpleProject targetProject = new SimpleProject("TargetProject");
		IFolder targetFolder = targetProject.createFolder("src");

		try {

			SharpenConversionBatch converter = new SharpenConversionBatch(getConfiguration());
			converter.setSource(new ICompilationUnit[] {cu});
			converter.setTargetFolder(targetFolder);
			converter.run();

			assertFile(resource, targetFolder.getFile(path + ".cs"));

		} finally {
			targetProject.dispose();
		}

	}

}