#!/usr/bin/env booi
"""
Creates a MRefBuilder.config file configured to filter out:
	1) all the namespaces that do not appear in config/db4o-namespace-summaries.xml
	2) all the types with a <exclude /> documentation tag
"""
namespace MRefConfigGenerator

import System
import System.IO
import System.Xml
import System.Reflection

def getExportedTypes(path as string):
	types = List(Assembly.ReflectionOnlyLoadFrom(path).GetExportedTypes())
	types.Sort(compareTypes)
	return types
	
def compareTypes(t1 as Type, t2 as Type):
	result = t1.Namespace.CompareTo(t2.Namespace)
	if result != 0: return result
	return t1.Name.CompareTo(t2.Name)

def loadXmlDoc(path as string):
	doc = XmlDocument()
	doc.Load(path)
	return doc
	
def queryXmlDoc(path as string, xpath as string):
	return loadXmlDoc(path).SelectNodes(xpath)

def namespacesFromXmlSummary(path as string):
	return [nameAttr.Value
			for nameAttr as XmlAttribute
			in queryXmlDoc(path, "//namespace/@name")]
	
def appendFilter(filters as XmlElement, attributes as Hash):
	f = filters.OwnerDocument.CreateElement("filter")
	for item in attributes:
		f.SetAttribute(item.Key, item.Value)
	filters.AppendChild(f)	
	
def resetApiFilters(path as string):
	doc = loadXmlDoc(path)
	filters as XmlElement = doc.SelectSingleNode("//apiFilters")
	filters.RemoveAll()
	return filters
	
def getExcludedTypes(xmldocPath as string):
	return [name.Value[2:]
			for name as XmlAttribute
			in queryXmlDoc(xmldocPath, "//member[exclude]/@name")]		

if len(argv) == 2:
	 baseConfigPath, baseDistPath = argv
else:
	basePath, = argv
	baseConfigPath = Path.Combine(basePath, "config")
	baseDistPath = Path.Combine(basePath, "dist")

buildDistPath = { path  | Path.Combine(baseDistPath, path) }
buildConfigPath = { path | Path.Combine(baseConfigPath, path) }

configTemplatePath = buildConfigPath("sandcastle/MRefBuilder.config")
configPath = buildDistPath("ndoc/Output/MRefBuilder.config")
assemblyPath = buildDistPath("dll/Db4objects.Db4o.dll")
namespaceSummaryPath = buildConfigPath("db4o-namespace-summaries.xml")
xmldocPath = Path.ChangeExtension(assemblyPath, ".xml")

try: 
	File.Copy(configTemplatePath, configPath, true)
	
	documentedNamespaces = namespacesFromXmlSummary(namespaceSummaryPath)
	excludedTypes = getExcludedTypes(xmldocPath)
	
	filters = resetApiFilters(configPath)	
	for type as Type in getExportedTypes(assemblyPath):
		
		expose = type.Namespace in documentedNamespaces and type.FullName not in excludedTypes
		if expose: continue
		appendFilter(filters, { "namespace": type.Namespace,
								"type": type.Name,
								"expose": expose.ToString().ToLower() })								
	
	filters.OwnerDocument.Save(configPath)
	
			
	print "MRefBuilder.config successfully saved to '${configPath}'"
except x:
	print x
