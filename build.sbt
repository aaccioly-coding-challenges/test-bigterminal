import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.bigterminal",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    
    name := "test-bigterminal",
    
    libraryDependencies ++= Seq(
      stm,
      scalaTest % Test
    )
  )
