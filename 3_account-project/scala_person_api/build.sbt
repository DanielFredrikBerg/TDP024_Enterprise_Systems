val Http4sVersion = "0.23.14"
val CirceVersion = "0.14.2"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.11"
val MunitCatsEffectVersion = "1.0.7"

lazy val root = (project in file("."))
  .settings(
    organization := "liu.se",
    name := "scala-person-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe" %% "circe-literal" % "0.14.3",
      "com.github.fd4s" %% "fs2-kafka" % "3.0.0-M8",
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-parser"       % CirceVersion,
      "io.circe"        %% "circe-literal"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion         % Runtime,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "org.http4s"      %% "http4s-blaze-server" % "0.23.12",
      "org.http4s"      %% "http4s-blaze-client" % "0.23.12",
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework"),
  )

