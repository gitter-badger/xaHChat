# xaHChat 
=======

xaHChat, chat app framework written in scala using mqtt server

***

### To run in Android Studio
* Install [Scala](http://www.jetbrains.net/confluence/display/SCA/Scala+Plugin+for+IntelliJ+IDEA) plugin
* Install [SBT](http://github.com/orfjackal/idea-sbt-plugin) plugin

using gradle to build it and then you have to add the scala facet

    <facet type="scala" name="Scala">
      <configuration>
        <option name="compilerLibraryLevel" value="Project" />
        <option name="compilerLibraryName" value="scala-library-2.11.2" />
        <option name="fsc" value="true" />
        <option name="languageLevel" value="Scala 2.11" />
      </configuration>
    </facet>

* If you get `[error] set ANDROID_HOME or run 'android update project -p...`,
create `local.properties` file in the root of the project with following content:

    sdk.dir=\<Your ANDROID_HOME\>

or better approach to setup ANDROID_HOME in your environment, for example on Mac:

    launchctl setenv ANDROID_HOME \<Your ANDROID_HOME\>



