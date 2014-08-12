package pimpathon

import _root_.java.io.File

import pimpathon.any._


object file extends FileUtils(".tmp", "temp")

case class FileUtils(suffix: String, prefix: String) {
  implicit class FileOps(file: File) {
    def named(name: String = file.getName): File = new NamedFile(file, name)

    def tree: Stream[File]     = if (!file.exists) Stream.empty[File] else file #:: children.flatMap(_.tree)
    def children: Stream[File] = if (file.isFile) Stream.empty[File] else file.listFiles.toStream

    def changeToDirectory(): File = file.tapIf(_.isFile)(_.delete(), _.mkdir())
    def create(): File            = file.tap(_.createNewFile())
  }


  def withTempFile[A](f: File => A): A = withTempFile(suffix)(f)

  def withTempFile[A](suffix: String, prefix: String = prefix)(f: File => A): A = {
    val file = File.createTempFile(prefix, suffix)

    try f(file) finally file.delete
  }


  def withTempDirectory[A](f: File => A): A = withTempDirectory(suffix)(f)

  def withTempDirectory[A](suffix: String, prefix: String = prefix)(f: File => A): A =
    withTempFile[A](suffix, prefix)(tmp => f(tmp.changeToDirectory()))


  class NamedFile(file: File, name: String) extends File(file.getPath) {
    override def toString = name
  }
}