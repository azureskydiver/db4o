package com.db4o.buildrename

import java.io._
import scala.xml._
import scala.collection._

object FileExtensions {
	val extensions = Set("zip", "msi")
}

sealed case class Platform(id: String, generic: String, version: Option[String]) {
	override def toString = generic + version.map(" " + _).getOrElse("")
}

object Platform {
	private val PLATFORMS = Array(
		new Platform("net", ".NET", None),
		new Platform("net2", ".NET", Some("2.0")),
		new Platform("net35", ".NET", Some("3.5")),
		new Platform("net40", ".NET", Some("4.0")),
		new Platform("java", "Java", None)
	)

	private val ID2PLATFORM = PLATFORMS.foldLeft(immutable.Map[String, Platform]()) {(m, p) => m + (p.id -> p)}
 
	def apply(id: String) = ID2PLATFORM.get(id)
}

sealed case class Product(id: String, description: String) {
	override def toString = description
}

object Product {
	private val PRODUCTS = Array(
		new Product("db4o", "db4o"),
		new Product("dRS", "db4o Replication System")
	)

	private val ID2PRODUCT = PRODUCTS.foldLeft(immutable.Map[String, Product]()) {(m, p) => m + (p.id -> p)}

	def apply(id: String) = ID2PRODUCT.get(id)
}

case class VersionedFile(sourceFile: File, product: Product, major: String, minor: String, platform: Platform, extension: String) {
	def targetFile = new File(sourceFile.getParent, simpleName)
	def simpleName = product.id + "-" + major + "-" + platform.id + "." + extension
	def fullVersion = major + "." + minor
}

object VersionedFile {
  	private val NAME_PATTERN = ("""([^-]+)-((\d+\.\d+)\.(\d+\.\d+))-(.+)\.(""" + FileExtensions.extensions.mkString("|") + ")").r

   def apply(file: File): Option[VersionedFile] =
		file.getName match {
		  	case NAME_PATTERN(productID, full, major, minor, platformID, extension) =>
		  		for(product <- Product(productID); platform <- Platform(platformID))
		  			yield VersionedFile(file, product, major, minor, platform, extension)
		  	case _ => None
  		} 
}

class VersionedFileMetaRenderer(file: File, files: Iterable[VersionedFile], version2category: Map[String, Category]) {
	private val DATE = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date) + "T00:00:00"
 
   	def render() {
		val rendered = renderFiles()
		val writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8")
		try {
			writer.write(rendered)
		}
		finally {
			writer.close
		}
	}

	def renderFiles() =
		"""<?xml version="1.0" encoding="utf-8" ?>""" + "\n\n" + new PrettyPrinter(50, 3).format(metaData())
 
    def metaData(): Elem = {
      <Downloads>{files.map(metaData(_))}</Downloads>
	} 
	
 	private def metaData(file: VersionedFile): Elem =
 		<Download		
 			file={file.targetFile.getName}
 			title={file.product.description}
 			version={file.fullVersion}
 			releaseDate={DATE}
      		release={file.extension.toUpperCase}
 			platform={file.platform.toString}>
      		{categoryTag(file)}
      	</Download>

    private def categoryTag(file: VersionedFile): Iterable[Elem] = {
    	version2category.get(file.major).map(category => <Folders>{category.from.map(f => <clear />).toArray}<add folder={category.to} /></Folders>)
    }
}

case class Category(from: Option[String], to: String)


object BuildPrepareCore {
 	def filterFolder(folder: File) =
		folder.listFiles.flatMap(VersionedFile(_))

  	def writeXMLFile(file: File, files: Iterable[VersionedFile], version2category: Map[String, Category]) =
  		new VersionedFileMetaRenderer(file, files, version2category).render()

  	def renameFile(file: VersionedFile) =
 		file.sourceFile.renameTo(file.targetFile)
}
