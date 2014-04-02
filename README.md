# xaHChat [![Build Status](https://travis-ci.org/lemonxah/xaHChat.png?branch=master)](https://travis-ci.org/lemonxah/xaHChat)

=======

xaHChat, chat app framework written in scala using mqtt server

using Travis-ci.org to build my project
***

### To run in Intellij Idea
* Install [Scala](http://www.jetbrains.net/confluence/display/SCA/Scala+Plugin+for+IntelliJ+IDEA) plugin
* Install [SBT](http://github.com/orfjackal/idea-sbt-plugi) plugin

* From command line run `sbt gen-idea`
* Edit Run Config of Intellij Idea:
   * if you go to build configs in intellij, take out the `make`
   * SBT action in the dropdown and type `android:package` (or `android:packageDebug`)
* If you get `[error] set ANDROID_HOME or run 'android update project -p...`,
create `local.properties` file in the root of the project with following content:

    sdk.dir=\<Your ANDROID_HOME\>

or better approach to setup ANDROID_HOME in your environment, for example on Mac:

    launchctl setenv ANDROID_HOME \<Your ANDROID_HOME\>



