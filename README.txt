[INSTALLAZIONE]
Il progetto è stato creato sull'ide Intellij.
Si consiglia di aprire per primo il file pom.xml per controllare che i plugin siano presenti (Javafx , maven-shade)
Se si usa questo ide, basta aprire la barra a lato (destra) relativa a maven, e cliccare due volte su "clean" e "install". Da qui viene generato il jar del client nella cartella "target" del progetto.
Per generare il jar del server, bisognerà andare su File -> project structure -> Artifacts -> premere sul "+", -> jar -> from module with dependencies -> e sull'attributo "main class" selezionare "Server"
Una volta creato l'artifact, si può chiudere la finestra, e basta andare in alto sulla barra delle opzioni, premere su Build -> Build Artifact e selezionare quello appena creato

Qualora si volesse generare il jar del progetto tramite comandi maven, basterà eseguire i seguenti comandi:

mvn clean
mvn install


Questi comandi genereranno il .jar del client, nella cartella "target" del progetto


Per generare il jar del server tramite comandi, bisogna creare un file "Manifest.txt" con all'interno la specifica della main class:

Main-Class : server.Server
Poi, eseguire il comando "jar cfm NomeJar.jar Manifest.txt server/*.class"




[COMPILAZIONE]

(CLIENT)
Per compilare il progetto ed eseguirlo tramite ide (in questo caso Intellij), basterà aprire dalla barra a lato la sezione "Maven", andare su Plugins -> Javafx e cliccare due volte su javafx:run.
(Server)
Per compilare il progetto ed eseguirlo tramite ide (in questo caso Intellij), basterà scegliere la classe "Server" ed eseguirla

(CLIENT CMD)
Per compilare ed eseguire il progetto , tramite comandi Maven, basta eseguire : "mvn javafx:run", in automatico procederà a compilare il lato client e ad eseguirlo. (Verificare che nel pom.xml ci sia il plugin "org.openjfx")

(Server CMD)
Per compilare ed eseguire il progetto, basterà eseguire i classici comandi per l'esecuzione di un file .java



[LIBRERIE]

Le librerie aggiunte si possono visualizzare sotto forma di "plugin" nel file pom.xml. Le librerie esterne utilizzate sono:
-JavaFX
-Maven-shade

La libreria JavaFX è quella che ci ha permesso di creare la parte grafica
La libreria Maven-shade è quella che ci ha permesso di creare il .jar con linkate le dipendenze della JavaFX cosi da poter eseguire il programma su qualsiasi computer.