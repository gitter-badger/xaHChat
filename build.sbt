android.Plugin.androidBuild

resolvers += "Paho" at "https://repo.eclipse.org/content/repositories/paho-releases/"

libraryDependencies += "org.eclipse.paho" % "mqtt-client" % "0.4.0"

libraryDependencies += "com.android.support" % "support-v4" % "18.0.0"

name := "xaHChat"
 
scalaSource in Compile <<= (sourceDirectory in Compile)(_ / "scala")
 
scalaVersion := "2.10.2"
 
scalacOptions in Compile += "-feature"
