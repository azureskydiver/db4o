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
	return Assembly.ReflectionOnlyLoadFrom(path).GetExportedTypes()

def namespacesFromAssembly(path as string):
	return namespacesFromTypes(getExportedTypes(path))
	
def namespacesFromTypes(types as (Type)):
	return uniqueList(type.Namespace for type in types)

def uniqueList(items):
	seen = {}
	list = []
	for item in items:
		if item in seen: continue
		list.Add(item)
		seen[item] = item
	return list
	
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
	
def splitTypeName(typeName as string):
	lastDot = typeName.LastIndexOf('.')
	if lastDot < 0: return "", typeName
	return typeName[:lastDot], typeName[lastDot+1:]		
	
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
	return [member.GetAttribute("name")[2:]
			for member as XmlElement
			in queryXmlDoc(xmldocPath, "//member/exclude/..")]

basePath, = argv
buildPath = { path as string | Path.Combine(basePath, path) }

configPath = buildPath("dist/ndoc/Output/MRefBuilder.config")
assemblyPath = buildPath("dist/dll/Db4objects.Db4o.dll")
namespaceSummaryPath = buildPath("config/db4o-namespace-summaries.xml")
xmldocPath = Path.ChangeExtension(assemblyPath, ".xml")

try: 
	File.Copy(buildPath("config/sandcastle/MRefBuilder.config"), configPath, true)
	
	documentedNamespaces = namespacesFromXmlSummary(namespaceSummaryPath)
	excludedTypes = getExcludedTypes(xmldocPath)
	
	filters = resetApiFilters(configPath)
	types = getExportedTypes(assemblyPath)
		
	for type in types:
		if type.Namespace not in documentedNamespaces: continue
		if type.FullName in excludedTypes: continue
		appendFilter(filters, { "namespace": type.Namespace,
								"type": type.Name,
								"expose": "true" })
								
	for item in excludedTypes:
		ns, type = splitTypeName(item)
		appendFilter(filters, { "namespace": ns, "type": type, "expose": "false" })

	for item in namespacesFromTypes(types):
		appendFilter(filters, { "namespace": item, "expose": "false" })			
	
	filters.OwnerDocument.Save(configPath)
	
			
	print "MRefBuilder.config successfully saved to '${configPath}'"
except x:
	print x
