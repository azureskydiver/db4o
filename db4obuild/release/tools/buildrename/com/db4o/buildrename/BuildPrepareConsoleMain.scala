package com.db4o.buildrename

import java.io._
import scala.collection._

object BuildPrepareConsoleMain {

	import BuildPrepareCore._
 
	def main(args: Array[String]) {
		args match {
		  	case BuildPrepareArgs(dryRun, folder, version2category) => 
		  		run(dryRun, folder, version2category)
		  	case _ =>
		  		println("Usage: BuildPrepareConsoleMain [-dry] <folder> [<version>  <category>]*")
		}
	}		
 
	private def run(dryRun: Boolean, folder: File, version2category: Map[String, String]) {
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
	def unapply(args: Array[String]): Option[(Boolean, File, Map[String, String])] = {
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
		val version2category = arr2map(remainingArgs.drop(1))
		Some((dryRun, folder, version2category))
	}
 
 	private def arr2map[T](arr: Array[T]) = {
		val map = mutable.Map[T, T]()
		for(idx <- 0 until arr.size by 2) {
			map += arr(idx) -> arr(idx + 1)
		}
		map
	}

}