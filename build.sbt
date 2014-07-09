android.Plugin.androidBuild

resolvers += "Paho" at "https://repo.eclipse.org/content/repositories/paho-releases/"

libraryDependencies += "org.eclipse.paho" % "mqtt-client" % "0.4.0"

libraryDependencies += "com.squareup.picasso" % "picasso" % "2.1.1"

libraryDependencies += "com.squareup.okhttp" % "okhttp" % "1.2.1"

libraryDependencies += "com.android.support" % "support-v4" % "19.+"

name := "xaHChat"

scalaSource in Compile <<= (sourceDirectory in Compile)(_ / "scala")

scalaVersion := "2.11.1"

scalacOptions in Compile += "-feature"
