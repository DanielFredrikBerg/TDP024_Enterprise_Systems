# TDP024 Account Projekt lab 3 och 4

THE SCREENCAST FOR LAB 3 OF XFINAL TESTS COMPLETION IS BOTH IN main AND IN THE BRANCH xfinal_done. THE .mkv VIDEO IS IN 3_account-project

Labben består av två SOA tjänster skrivna i valfritt språk och en account-del skriven i Java 18. Person-SOAn är skriven i Python3 med Flask (https://flask.palletsprojects.com/en/2.2.x/) och Bank-SOAn är skriven i Scala med HTTP4S (https://http4s.org/). Alla REST-förfrågningar och transaktioner loggas mha Kafka i respektive topic: "rest-requests" och "transactions".

I enlighet med kraven för lab 4 kastas det custom Exception:s vid fel. REST:lagret skickar passande felmeddelanden och status-koder för användaren och fel loggas också i kafka i respektive topic beroende på vart det gått fel.

#### Code coverage
* account-datalayer  85%
* account-logic     100%
* account-rest       73%
Det är inte 100% code coverage för att vid konstigare fel vi inte kan testa kastas det exceptions, men dessa exceptions är mycket svåra att täcka av tester. Internt fångas de dock och läcker inte ut till användaren. Således uppfylls kravet om "Fel som uppstår som man inte har uttryckligen tagit hand om får inte läcka ut till anroparen".


#### Setup
Katalogen 3_account-project innehåller det mesta som behövs för att starta servicen, Börja därför med att klona ned hela repot och gå in i root-mappen för labbarna. Totalt behövs ca 6 instanser av shell. Varje kommando förutsätter att du står i root-mappen /tdp024-enterprise-systems/3_account-project:\
    $ git clone git@gitlab.liu.se:andpl509/tdp024-enterprise-systems.git\
    $ cd tdp024-enterprise-systems/3_account-project\
Starta kafka zookeeper och server:\
    $ kafka_2.13-3.2.1/bin/zookeeper-server-start.sh kafka_2.13-3.2.1/config/zookeeper.properties\
    $ kafka_2.13-3.2.1/bin/kafka-server-start.sh kafka_2.13-3.2.1/config/server.properties\
Starta varsin cli kafka consumer i shell på vardera topic:\
    $ cd kafka_2.13-3.2.1/ && bin/kafka-console-consumer.sh --topic rest-requests --from-beginning --bootstrap-server localhost:9092\
    $ cd kafka_2.13-3.2.1/ && bin/kafka-console-consumer.sh --topic transactions --from-beginning --bootstrap-server localhost:9092 \
Starta virtuell miljö:\
    $ .  env/bin/activate\
Installera de pip paket som behövs från requirements.txt i root-mappen:\
    $ pip install -r requirements.txt\
Starta flaskservern i ny shellinstans:\
    $ cd flask_app && flask --debug run --host=localhost --port=8070\
Öppna ny shell och installera sdk-man för installation av Scala:\
    $ curl -s "https://get.sdkman.io" | bash\
Installera sbt för att kunna bygga Bank-SOAn skriven i Scala:\
    $ sdk install sbt\
Gå in i mappen och starta scala servern:\
    $ cd scala_person_api/ && sbt run\
Öppna root-mappen i idea editor (om annan editor används läggs ansvaret för korrekt uppstart på användaren):\
    $ idea .\
Se till att kafka zookeeper och server är på och flytta till /tdp024-enterprise-systems/3_account-project/account-rest Starta applikationen:\
    $ mvn spring-boot:run\
Öppna en ny terminal i root-katalogen och kör testerna:\
    $ mvn clean install\
Efter att testerna körts har det skapats en Jacoco index.html code coverage fil i varje katalog för respektive lager under target/site\
    $ firefox ./target/site/index.html
Under testernas gång loggas multipla händelser till repsektive kafka topic. Dessa syns i respektive kafka consumer för respektive topic.

För övrigt går det också att starta applikationen och köra testerna mha Intellij GUI.


