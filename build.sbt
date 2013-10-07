android.Plugin.androidBuild

libraryDependencies += "com.android.support" % "support-v4" % "18.0.0"

name := "xaHChat"
 
scalaSource in Compile <<= (sourceDirectory in Compile)(_ / "scala")
 
scalaVersion := "2.10.2"
 
scalacOptions in Compile += "-feature"
