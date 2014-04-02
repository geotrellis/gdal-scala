import sbt._
import sbt.Keys._

object GdalBuild extends Build {
  val scalaOptions = Seq(
        "-deprecation",
        "-unchecked",
        "-Yclosure-elim",
        "-Yinline-warnings",
        "-optimize",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-language:existentials",
        "-feature"
  )

  lazy val gdal =
    Project("root", file("gdal")).settings(
      organization := "com.azavea.geotrellis",
      name := "gdal-scala",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.10.3",
     
      scalacOptions ++= scalaOptions,
      fork := true,
      javaOptions += "-Djava.library.path=/usr/local/lib",

      mainClass := Some("gdal.Main"),

      libraryDependencies ++= Seq(
        "org.gdal" % "gdal" % "1.9.2",
        "com.github.scopt" % "scopt_2.10" % "3.2.0"
      ),
      resolvers ++=
        Seq(
          "OpenGeo" at "http://repo.opengeo.org/"
        )
    )
}
