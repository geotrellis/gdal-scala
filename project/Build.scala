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
      organization := "org.gdal.gdal",
      name := "gdal-scala",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.10.3",
     
      scalacOptions ++= scalaOptions,
      fork in run := true,
      fork in test := true,
      javaOptions += "-Djava.library.path=/usr/local/lib",

      mainClass := Some("gdal.Main"),

      libraryDependencies ++= Seq(
        "com.github.scopt" % "scopt_2.10" % "3.2.0"
      )
    )

  lazy val geotrellis =
    Project("geotrellis", file("geotrellis")).settings(
      organization := "com.azavea.geotrellis",
      name := "geotrellis-gdal",
      version := "0.9.0",
      scalaVersion := "2.10.3",

      scalacOptions ++= scalaOptions,
      fork in run := true,
      fork in test := true,
      javaOptions += "-Djava.library.path=/usr/local/lib",

      libraryDependencies ++= Seq(
        "com.azavea.geotrellis" %% "geotrellis" % "0.9.0",
        "com.azavea.geotrellis" %% "geotrellis-geotools" % "0.9.0" % "test",
        "org.scalatest"         %%  "scalatest"  % "2.0.M5b" % "test"
      ),
      resolvers ++= 
        Seq(
          "Geotools" at "http://download.osgeo.org/webdav/geotools/"
        )
    ).dependsOn(gdal)

  // Project: benchmark

  val key = AttributeKey[Boolean]("javaOptionsPatched")
  lazy val benchmark: Project =
    Project("benchmark", file("benchmark"))
      .settings(
      organization := "org.gdal.gdal",
        name := "gdal-benchmark",

      scalaVersion := "2.10.3",
        // raise memory limits here if necessary
        javaOptions += "-Xmx2G",
        javaOptions += "-Djava.library.path=/usr/local/lib",

      libraryDependencies ++= Seq(
        "com.azavea.geotrellis" %% "geotrellis-geotools" % "0.9.0"
      ),
        resolvers ++=
          Seq(
            "Geotools" at "http://download.osgeo.org/webdav/geotools/"
          ),

      // enable forking in both run and test
      fork := true
    ).dependsOn(geotrellis)
}
