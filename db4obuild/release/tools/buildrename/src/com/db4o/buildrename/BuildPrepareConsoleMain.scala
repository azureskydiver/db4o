package com.db4o.buildrename

import java.io._
import scala.collection._

object BuildPrepareConsoleMain {

	import BuildPrepareCore._
 
	def main(args: Array[String]) {
		args match {
		  	case BuildPrepareArgs(dryRun, folder, version2category) => 
		  		run(dryRun, folder, version2category)
		  	case _ => {
		  		println("Usage: BuildPrepareConsoleMain [-dry] <folder> [<version>  <category>['/''c'|'a']]*")
		  		println("Ex.: BuildPrepareConsoleMain ./data 7.12 Stable/a 8.0 Production/c 8.1 Development/c")
		  	}
		}
	}		
 
	private def run(dryRun: Boolean, folder: File, version2category: Map[String, Category]) {
		val files = filterFolder(folder)
		files.foreach(logFile(_, "? "))
		println()
		println("Path: " + folder.getAbsolutePath)
		println(files.size + " files contain a long version name with 3 dots and can be renamed.")
		if(files.isEmpty) {
			return
		}
		println()
		println("Hit y and Return to proceed.")
		val readByte = System.in.read()
		if(readByte != 'y') {
			println("Rename operation cancelled.")
			return
		}
		files.foreach(f => {
			logFile(f, "")
			if(!dryRun) {
				renameFile(f)
			}
		    else {
		    	println("DRY")
		    }
		})
		writeXMLFile(new File(folder, "downloads.xml"), files, version2category)
		println()
		println("Path: " + folder.getAbsolutePath)
		println(files.size + " files renamed.")
	}
  
	private def logFile(file: VersionedFile, prepend: String) {
		println(prepend + "Rename " + file.sourceFile)
		println(prepend + "To     " + file.targetFile)
		println
	}
 
}

object BuildPrepareArgs {
	val CategoryPattern = """([^/]+)(\/([ca]))?""".r
	
	def unapply(args: Array[String]): Option[(Boolean, File, Map[String, Category])] = {
		if(args == null || args.size == 0) {
			return None
		}
		var remainingArgs = args
		var dryRun = false
		if("-dry".equals(remainingArgs(0))) {
			dryRun = true
			remainingArgs = remainingArgs.drop(1)
		}
		if((remainingArgs.size % 2) == 0) {
			return None
		}
		val folder = new File(remainingArgs(0))
		val version2category = arr2map(remainingArgs.drop(1), str2Category)
		Some((dryRun, folder, version2category))
	}
 
	private def str2Category(s: String) = {
		def op(id: String) = id match {
			case "c" => ClearOp
			case "a" => ArchiveOp
			case _ => NoOp
		}
		s match {
			case CategoryPattern(name, _, opId) => Category(name, op(opId))
			case _ => throw new IllegalArgumentException("Not a category: " + s)
		}
	}
	
 	private def arr2map[T, V](arr: Array[T], mapper: T => V) = {
		val map = mutable.Map[T, V]()
		for(idx <- 0 until arr.size by 2) {
			map += arr(idx) -> mapper(arr(idx + 1))
		}
		map
	}

}