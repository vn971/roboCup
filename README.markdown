### RoboCup

RoboCup is an automatic tournament holder for the game Points.

It's written in Scala language, using Liftweb as a web framework,
akka.io as an actor framework, SBT as a build tool,
git+github as a version control system.

### How to run:

* install git
* download RoboCup from the internet:  `git clone git@github.com:vn971/roboCup.git`
* launch it! In command line type  `./bin/sbt ~reStart`
* you should have RoboCup running now. Check it out on [http://localhost:8989](http://localhost:8989)

After running roboCup you may be interested in IDE support.

### To open in eclipse IDE:

* Download and install eclipse-scala IDE [http://scala-ide.org/](http://scala-ide.org/)
* In command-line run  `./bin/sbt "eclipse with-source=true"`
* In menu, "File" > "Import" > "Existing Projects into workspace" > find your project > Ok

### To open in intellij-idea IDE:

* download the community edition (apache2-licensed): https://www.jetbrains.com/idea/download/
* find and install the Scala plugin
* In menu, "File" > "Open" > "build.sbt" file > download all sources and docs

Feel free to contact me.:)


-----

### Authors:

Vasya Novikov:

* invention of RoboCups
* 100% of code written here yet
* adding and promoting 4cross rules
* creation and promotion fo the Swiss tournament system amongst Points game

Bartek Duda:

* all Polish translations
* proposing Swiss tournament system for the first time
* providing API on zagram for RoboCups
* advertising the tournament on zagram.

Ivan Geyko:

* proposing and proving that tournament rules should be very different each time
* choosing the default common rules to the very first tournaments
* organising many of the tournaments in 2012
* help in organising and advertising the tournament A LOT
* proposal of the name "RoboCup"

Oleg Anokhin:

* keeping the history of tournaments up to date on our site
* choosing the default common rules to the very first tournaments
* organising many of the tournaments in 2012
* help in organising and advertising the tournament A LOT


Copyright: Vasya Novikov 2012-present
License: AGPL v3 or (at your option) any later version
