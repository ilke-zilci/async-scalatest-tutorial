lazy val root = project
  .in(file("."))
  .settings(
    name := "async-scalatest-tutorial",
    version := "0.1",
    scalaVersion := "2.12.4",
    libraryDependencies ++= {
      val monix = "io.monix"
      val monixV = "2.3.3"

      Seq(
        monix             %% "monix"                    % monixV,
        monix             %% "monix-cats"               % monixV,
        "org.scalatest" %% "scalatest" % "3.0.4" % Test
      )
    }
  )
