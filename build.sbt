val osName: SettingKey[String] = SettingKey[String]("osName")

osName := (System.getProperty("os.name") match {
  case name if name.startsWith("Linux") => "linux"
  case name if name.startsWith("Mac") => "mac"
  case name if name.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
})

libraryDependencies += "org.openjfx" % "javafx-base" % "15-ea+3"
libraryDependencies += "org.openjfx" % "javafx-controls" % "15-ea+3"

libraryDependencies += "org.fxmisc.richtext" % "richtextfx" % "0.10.4"

mainClass in (Compile, run) := Some("syntaxfx.demo.Demo")

jflexOutputDirectory := baseDirectory.value / "src/main/scala/syntaxfx/lexers"
