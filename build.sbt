android.Plugin.androidBuild

resolvers += "My Github repo" at "https://github.com/lemonxah/lemaven/raw/master/releases/"
 
libraryDependencies += "com.google.android.gms" % "play-services" % "3.2.25"

libraryDependencies += "com.android.support" % "support-v4" % "18.0.0"

libraryDependencies += "de.freakempire" % "asmack" % "android-17-0.8.3"
 
name := "xaHChat"
 
scalaSource in Compile <<= (sourceDirectory in Compile)(_ / "scala")
 
scalaVersion := "2.10.2"
 
scalacOptions in Compile += "-feature"
