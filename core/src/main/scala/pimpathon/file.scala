package pimpathon

import java.io.File


object file extends FileUtils

case class FileUtils(tempPrefix: String = "temp", tempSuffix: String = ".tmp") {
  def withTemp[A](f: File => A): A = {
    val file = File.createTempFile(tempPrefix, tempSuffix)

    try f(file) finally file.delete
  }

  def withTempDirectory[A](f: File => A): A = {
    withTemp[A](file => {
      file.delete()
      file.mkdir()

      f(file)
    })
  }
}
