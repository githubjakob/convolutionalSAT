# Automatisches Finden von Faltungskodierern mit Hilfe von SAT-Solvern
__Bachelorarbeitsprojekt__

## Umfang der Java Applikation

Die Main Methode generiert den Schaltplan eines Faltungskodierers auf Grundlage der Usereingabe:
* Verfügbare Gates des Kodierers und Dekodierers
* Blocklänge, d.h. Länge der Bitströme, die durch den Faltungskodierer geschickt werden
* Länge des Delay, d.h. Anzahl der Ticks, die gewartet werden, bevor das Ergebnis am Dekodierer abgegriffen wird
* Noise, Wahrscheinlichkeit, dass im Übertragunskanal ein Bit geflippt wird

Intentiert ist ein iteratives Vorgehen: 

Es werden zufällig einige Bitströme der gewünschten Länge erzeugt und zusammen mit den weiteren Requirements in eine Formel übersetzt. Die Formel wird mit einem Sat Solver gelöst, und die Lösung der Formel dann in einen Schaltplan übersetzt. Auf diesem Schaltplan werden so lange weitere zufällige Bitströme getestet, bis ein Fehler auftritt, d.h. der Faltungskodierer diesen Bitstrom nicht fehlerfrei übertragen kann. Dieser Bitstrom wird dann zur ursprünglichen Formel hinzugefügt usw.


## Build & Run

mvn install
java -jar ./target/convolutionalSat-0.0.1-uber.jar 

gcloud compute --project "blackalert-207710" ssh --zone "europe-west3-b" "instance-2"

gcloud auth login

gcloud compute ssh instance-2

gcloud compute scp ~/Projects/ConvolutionalSAT/target/convolutionalSat-0.0.1-uber.jar instance-2:~/


## Einführung


In der digitalen Datenübertragung und bei der Speicherung von Daten werden Bits in einem analogen Signal kodiert. Durch Störungen in dem Medium können Bitfehler auftreten. Deshalb werden bei der Speicherung und Übertragung von Daten auf Bitebene Fehlerkorrekturverfahren eingesetzt. Diese dienen dazu, Fehler automatisch zu finden und wenn möglich auch zu korrigieren. Dabei werden den Nutzdaten zusätzliche Redudanz hinzufügt: dem ursprünglichen Bitsttrom werden auf der Senderseite zusätzliche Bits hinzugefügt. Das zu übertragende Codewort ist also länger als die ursprünglichen Nutzungsdaten. Treten Bitfehler im Codewort auf, können redudanten Informationen genutzt werden um die Fehler zu erkennen und automatisch zu korrigieren (Vorwärtsfehlerkorrektur). Eine Form von Vorwärtsfehlerkorrektur sind neben Blockcodes sogennante Faltungscodes. Namensgebend hierfür ist das mathematische Verfahren der Faltungen, wobei die Informationen, die in den Nutzdaten enthalten sind, über mehrere Stellen des Codeworts verteilt werden. Faltungscodes können durch digitalen Schaltungen mit Hilfe von Schieberegistern realisiert werden. 

Für die Konstruktion von Faltungscodes gibt es kein bekanntes systematisches Verfahren. Geeignete Schaltpläne für Faltungscodierer wurden in den 80er Jahren durch exhaustive Suche vor allem von der NASA gefunden. Da digitale Schaltungen und boolsche Formeln sich ohne Informationsverlust ineinander übersetzen lassen, liegt es nahe, Schaltplänte für Faltungscodierer als Lösungen einer boolsche Formeln zu finden. Dabei werden die Eigenschaften der benutzen Schaltteile (ANDs, NOTs, Register usw.), die Eigenschaften der Schaltung (Verbindungen zwiwschen den Schaltteilen, Nicht-Zirkulärität usw.) und die erwünschten fehlerkorrigierenden Eigenschaften in der Formel (Bitfehler im übertragenden Codewort) kodiert. Eine Belegung, die die Formel erfüllt, ist dann ein konkreter Schaltplan, der diese Eigenschaften erfüllt, d.h. ein Faltungskodierer mit entsprechender Güte. Diese Verfahren kann sowohl zum Finden von En- als auch Decodern genutzt werden. 

In der Entwicklung von SAT Solvern hat es in den letzten Jahren große Fortschritte gegeben. SAT Solver können oft mit einer sehr großen Anzahl von Klauseln und Variablen noch Ergebnisse finden. Trotz der schlechten worst-case Laufzeit werden sie in vielen Anwendungsbereichen zum Finden von Lösungen eingesetzt, in denen sich die Problemstellung in ein Erfüllbarkeitsproblem übersetzten lässt. 

Die Bachelorarbeit will versuchen Schaltpläne für Faltungskodierer mit Hilfe eines SAT Solver zu finden. Die erwartete Herausforderung liegt zum einen in der Aufgabe, alle notwendigen Eigenschaften der Schaltung und die fehlekorrigierenden Eigenschaften des dadurch repräsentierten Faltungskodierers in eine Formel zu übersetzen. Zum anderen wird sich ganz praktisch zeigen, ob die Leistung eines SAT Solver ausreicht, um eine Lösung der Formel in vertretbarer Zeit zu finden. Es ist zu erwarten, dass die Anzahl der Klauseln vor allem mit der Länge der zu testenden Codewörter massiv zunimmt. 

Idealerweies lassen sich durch dieses Verfahren automatisiert die Ergebnisse bereits bekannter Faltungskodierer reproduzieren. Im besten Fall können sogar neue Faltungskodierer gefunden werden. Im schlechtesten Fall, stellt sich heraus, dass eine Formel, die alle benötigten Eigenschaften eines Faltungskodierers enthält, zu groß ist, um in vertretbarer Zeit von einem SAT Solver gelöst zu werden.

