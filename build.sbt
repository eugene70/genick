val scala3Version = "3.0.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "genick",
    version := "0.1.0",
    scalaVersion := scala3Version,
    resourceDirectory in Compile := file(".") / "./src/main/resources",
    resourceDirectory in Runtime := file(".") / "./src/main/resources",
    libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.4.38.v20210224",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
  )
