lazy val root = project
  .in(file("."))
  .settings(
    name := "async-scalatest-tutorial",
    version := "0.1",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.0.1",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )
