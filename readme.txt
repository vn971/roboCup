RoboCup is an automatic tournament holder for the game Points.
It's written in Scala language, using LiftWeb as a web framework, 
SBT as a build tool, git+github as a version control system.

How to run roboCup:
* Download and install Simple Build Tool https://github.com/harrah/xsbt/
* Install the SBT web plugin. Try google/yandex/ddg to find an dated way. It worked for me to create a file "~/.sbt/plugins.sbt" with contents:
	libraryDependencies <+= sbtVersion(v => v match {
	case "0.11.0" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.0-0.2.8"
	case "0.11.1" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.1-0.2.10"
	case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
	})
* install git (on debian/ubuntu it's "aptitude install git", in suse/redhat it's "yum install git")
* download RoboCup from the internet: "git clone https://github.com/vn971/robocup"
* launch it! In command line, "sbt" > Enter > "container:start"
* you should have RoboCup running now. Check it out on http://localhost:8989
* In case of questions, suggestions or any comments, please, contact me n1m5-tournament@yaaaandex.com or write an issue here on github

After running roboCup you may be interested in an IDE support.
RoboCup is written in eclipse-scala as an IDE. 
You can use any other IDE, but eclipse is already set up and ready to use.
To use eclipse:
* Download and install eclipse-scala IDE http://scala-ide.org/
* In menu, "File" > Import > Existing Projects into workspace > ender roboCup directory path

