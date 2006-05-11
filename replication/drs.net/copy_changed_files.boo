#!/usr/bin/env booi
"""
Detects all changed files in the 'core/src' directory tree that should be
copied back to the 'in' directory tree.
"""
import System.IO
import Useful.IO from Boo.Lang.Useful

def EnumerateChanges(srcPath as string, targetPath as string, ignoreFileRegex as regex):
	srcPath = Path.GetFullPath(srcPath)
	for file as string in listFiles(srcPath):
		continue if not IsExistingFile(file)
		
		relativeFName = file[srcPath.Length+1:].Replace("\\", "/")
		continue if null != ignoreFileRegex and relativeFName =~ ignoreFileRegex
		
		targetFile = Path.Combine(targetPath, relativeFName)		
		continue if not IsExistingFile(targetFile)
		
		if AreDifferent(file, targetFile):
			yield relativeFName
			
def AreDifferent(fname1 as string, fname2 as string):
	using file1=File.OpenRead(fname1), file2=File.OpenRead(fname2):
		return true if file1.Length != file2.Length
		while file1.Position < file1.Length:
			return true if file1.ReadByte() != file2.ReadByte()
			
def IsExistingFile(file as string):
	return false if not File.Exists(file)
	return not (FileAttributes.Directory & File.GetAttributes(file))
	
directories = (
	("in", "core/src"),
)

for srcDir, targetDir in directories:
	for change in EnumerateChanges(srcDir, targetDir, /\b(\.svn)|(CVS)\b/):
		answer = prompt("do you want the file '${change}' to be copied to '${srcDir}'? (y/n) ")
		continue unless answer.StartsWith("y")
		File.Copy(
			Path.Combine(targetDir, change),
			Path.Combine(srcDir, change),
			true)
		
