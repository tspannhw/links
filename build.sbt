name := "Links"

version := "1.0"

scalaVersion := "2.10.6"

jarName in assembly := "Links.jar"

libraryDependencies  += "org.apache.spark" % "spark-core_2.10" % "1.6.2" % "provided"
libraryDependencies  += "org.apache.spark" % "spark-sql_2.10" % "1.6.2" % "provided"
libraryDependencies  += "org.apache.spark" %% "spark-hive" % "1.6.2" % "provided"
libraryDependencies  += "com.databricks" %% "spark-avro" % "2.0.1"
libraryDependencies  += "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13"
libraryDependencies  += "com.google.code.gson" % "gson" % "2.3"

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                  => MergeStrategy.first
}
