using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Xml;
using NUnit.Framework;
using WixBuilder.Tests.Wix;

namespace WixBuilder.Tests
{
	[TestFixture]
	public class WixScriptBuilderTestCase
	{
		private readonly IFolder root = new FolderMock("root")
			.EnterFolder("Bin")
				.AddFiles("foo.exe", "bar.dll")
			.LeaveFolder()
			.EnterFolder("Doc")
				.AddFiles("foo.chm", "README.TXT")
			.LeaveFolder()
			.GetFolder();

		private WixDocument WixDocumentFor(WixBuilderParameters parameters)
		{
			return new WixDocument(RunScriptBuilderWith(parameters));
		}

		private XmlDocument RunScriptBuilderWith(WixBuilderParameters parameters)
		{
			var resultingDocument = new StringWriter();
			new WixScriptBuilder(resultingDocument, root, parameters).Build();
			return LoadXml(resultingDocument.ToString());
		}

		private XmlDocument LoadXml(string xmlString)
		{
			var document = new XmlDocument();
			document.LoadXml(xmlString);
			return document;
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void TestEmptyFeatureId()
		{
			RunScriptBuilderWith(new WixBuilderParameters { Features = new[] {new Feature { Id = "", Content = new Content() }}});
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void TestNullFeatureId()
		{
			RunScriptBuilderWith(new WixBuilderParameters { Features = new[] { new Feature { Id = null, Content = new Content() }}});
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void TestNullFeatureContent()
		{
			RunScriptBuilderWith(new WixBuilderParameters { Features = new[] { new Feature { Id = "id", Content = null }}});
		}

		[Test]
		public void TestKnownId()
		{
			var parameters = new WixBuilderParameters
			{
				Features = new[]
				{
					new Feature
					{
						Id = "ApplicationFiles",
						Title = "All Files",
						Description = "All Files",
						Content = new Content
						{
							Include = @"**/*"
						}
					}
				},
				KnownIds = new[]
				{
					new KnownId { Id = "foo", Path = "Bin/foo.exe" }
				}
			};

			var document = WixDocumentFor(parameters);
			var fileWithKnownId = (from file in document.Files where file.Id == "foo" select file).AssertSingle();
			WixAssert.AssertFile((root["Bin"] as IFolder)["foo.exe"], fileWithKnownId);
		}

		[Test]
		public void TestSingleFeatureComponents()
		{
			var parameters = new WixBuilderParameters
			                 {
			                 	Features = new[]
			                 	           {
			                 	           	new Feature
			                 	           	{
			                 	           		Id = "ApplicationFiles",
			                 	           		Title = "Documentation",
			                 	           		Description = "all the docs",
			                 	           		Content = new Content
			                 	           		          {
			                 	           		          	Include = @"Doc\*.*"
			                 	           		          }
			                 	           	}
			                 	           }
			                 };

			WixDocument wix = WixDocumentFor(parameters);

			WixFeature featureElement = wix.Features.AssertSingle();
			Feature expectedFeature = parameters.Features[0];
			WixAssert.AssertFeature(expectedFeature, featureElement);

			string componentRef = featureElement.ComponentReferences.AssertSingle();
			WixComponent docComponent = wix.ResolveComponentReference(componentRef);
			WixAssert.AssertDirectoryComponent((IFolder) root["Doc"], docComponent);
		}

		//[Test]
		public void TestMultipleFeaturesReferencesFilesInCommonFolder()
		{
			var parameters = new WixBuilderParameters
			{
				Features = new[]
				{
					new Feature
					{
						Id = "CHM_FILES",
						Title = "Documentation",
						Description = "Windows Help Files",
						Content = new Content
						{
							Include = @"Doc\*.chm"
						}
					},

					new Feature
					{
						Id = "TXT_Files",
						Title = "Text Files",
						Description = "Text Files",
						Content = new Content
						{
							Include= @"Doc\*.TXT"
						}
					}
				}
			};
			
			WixDocument wix = WixDocumentFor(parameters);
			wix.ResolveDirectoryByName("Doc").AssertSingle();
		}

		[Test]
		public void TestMultipleFeaturesSingleTARGETDIR()
		{
			WixDocument wix = WixDocumentFor(ParametersForMultipleFeaturesTest());
			Assert.AreEqual(2, wix.Features.Count());
			wix.ResolveDirectoryById("TARGETDIR").AssertSingle();
		}
		
		[Test]
		public void TestMultipleFeaturesFileAddedOnlyOnce()
		{
			WixDocument wix = WixDocumentFor(ParametersForMultipleFeaturesTest());
			Assert.AreEqual(2, wix.Features.Count());

			Assert.AreEqual(1, wix.Files.Where(file => file.Id == "foo_exe").Count());
		}

		[Test]
		public void TestMultipleFeaturesDirectoriesAppearOnlyOnce()
		{
			WixDocument wix = WixDocumentFor(ParametersForMultipleFeaturesTest());
			Assert.AreEqual(2, wix.Features.Count());

			wix.ResolveDirectoryByName("Doc").AssertSingle();
		}

		[Test]
		public void TestFeaturesReferencesCorrectComponents()
		{
			WixDocument wix = WixDocumentFor(ParametersForMultipleFeaturesTest());
			Assert.AreEqual(2, wix.Features.Count());

			WixFeature wixFeature = wix.Features.Where(feature => feature.Id == "Documentation").AssertSingle();
			string componentRef = wixFeature.ComponentReferences.AssertSingle();
			WixComponent component = wix.ResolveComponentReference(componentRef);

			WixAssert.AssertDirectoryComponent((IFolder) root["Doc"], component);
		}

		private static WixBuilderParameters ParametersForMultipleFeaturesTest()
		{
			return new WixBuilderParameters
			       	{
			       		Features = new[]
			       		           	{
			       		           		new Feature
			       		           			{
			       		           				Id = "Documentation",
			       		           				Title = "Documentation",
			       		           				Description = "all the docs",
			       		           				Content = new Content
			       		           				          	{
			       		           				          		Include = @"Doc\*.*"
			       		           				          	}
			       		           			},

			       		           		new Feature
			       		           			{
			       		           				Id="ApplicationFiles",
			       		           				Title = "Visual Studio 2005 Plugin",
			       		           				Description = "Visual Studio 2005 Plugin",
			       		           				Content = new Content
			       		           				          	{
			       		           				          		Include=@"Bin\*.*"
			       		           				          	}
			       		           			}
			       		           	},

			       		KnownIds = new []
			       		           	{
			       		           		new KnownId
			       		           			{
			       		           				Id = "foo_exe",
			       		           				Path = @"Bin\foo.exe"
			       		           			}
			       		           	}
			       	};
		}

		[Test]
		public void TestWixNamespace()
		{
			XmlDocument script = RunScriptBuilderWith(new WixBuilderParameters());
			Assert.AreEqual(WixScriptBuilder.WixNamespace, script.DocumentElement.NamespaceURI);
		}
	}
}