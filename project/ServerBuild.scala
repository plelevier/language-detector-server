import sbt._
import sbt.Keys._
import sbt.Process._
import sbtassembly.Plugin._
import AssemblyKeys._

object ServerBuild extends Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1.0",
    organization := "com.github.plelevier",
    scalaVersion := "2.10.2",

    scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8"),
    scalacOptions in Test ++= Seq("-Yrangepos"),

    parallelExecution in Test := false
  )

 lazy val sbtAssemblySettings = assemblySettings ++ Seq(

   mergeStrategy in assembly <<= (mergeStrategy in assembly) {
     (old) => {
       case _ => MergeStrategy.last // leiningen build files
     }
   },

   excludedFiles in assembly := { (bases: Seq[File]) =>
     bases flatMap { base =>
       //Exclude the license and manifest from the exploded jars
       ((base / "META-INF" * "*").get collect {
         case f if List("manifest.mf", "notice.txt", "notice", "license.txt", "license").contains(f.getName.toLowerCase) => f
       })
     }
   }
 )

  lazy val root = Project("language-detector-server", file("."), settings = buildSettings ++ sbtAssemblySettings) settings (

    mainClass := Some("com.github.plelevier.LangDetectServer"),

    resolvers ++= Seq(
      "Maven Repository" at "http://mvnrepository.com/artifact/",
      "Twitter Maven Repository" at "http://maven.twttr.com",
      "Sonatype Releases Repository" at "http://oss.sonatype.org/content/repositories/releases"
    ),

    libraryDependencies ++= Seq(
      "com.cybozu.labs" % "langdetect" % "1.1-20120112",
      "org.slf4j" % "slf4j-jdk14" % "1.7.5",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.2.2",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.2",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.2",
      "commons-io" % "commons-io" % "2.4",
      "org.scalaj" % "scalaj-time_2.10.2" % "0.7",

      "com.twitter" %% "twitter-server" % "1.5.1",

      "org.scalatest" %% "scalatest" % "1.9.1" % "test"
    )
  )
}
