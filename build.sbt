name := "GDAL-scala"

scalaVersion := "2.10.3"

fork in run := true

// resolvers ++= Seq(
//
// )

libraryDependencies ++= Seq(
  "com.github.scopt" % "scopt_2.10" % "3.2.0"
)

javaOptions += "-Djava.library.path=/usr/local/lib"

// assemblySettings

// mergeStrategy in assembly <<= (mergeStrategy in assembly) {
//   (old) => {
//     case "reference.conf" => MergeStrategy.concat
//     case "application.conf" => MergeStrategy.concat
//     case "META-INF/MANIFEST.MF" => MergeStrategy.discard
//     case "META-INF\\MANIFEST.MF" => MergeStrategy.discard
//     case _ => MergeStrategy.first
//   }
// }
