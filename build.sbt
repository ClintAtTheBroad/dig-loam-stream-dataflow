import sbt.project

lazy val Versions = new {
  val App = "0.1"
  val Scala = "2.11.7"
  val ScalaTest = "2.2.6"
}

lazy val testDeps = Seq(
  "org.scalatest" %% "scalatest" % Versions.ScalaTest % Test
)

lazy val mainDeps = Seq(
  "org.scala-lang" % "scala-library" % Versions.Scala,
  "org.scala-lang" % "scala-compiler" % Versions.Scala,
  "org.scala-lang" % "scala-reflect" % Versions.Scala
)

lazy val commonSettings = Seq(
  version := Versions.App,
  scalaVersion := Versions.Scala,
  scalacOptions ++= Seq("-feature"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  libraryDependencies ++= (testDeps ++ mainDeps)
)

lazy val vcf = (project in file("vcf")).
  settings(commonSettings: _*).
  settings(
    name := "LoamStream VCF Support"
  )

lazy val dataflow = (project in file("dataflow")).
  dependsOn(vcf).
  settings(commonSettings: _*).
  settings(
    name := "LoamStream Google Dataflow Support"
  )

lazy val root = (project in file(".")).
  dependsOn(vcf, dataflow).
  settings(commonSettings: _*).
  settings(
    name := "LoamStream Dataflow"
  )
