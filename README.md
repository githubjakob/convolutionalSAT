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

## Bekannte Probleme 
Das Programm startet mit einem java.awt.AWTError.
In der Datei /etc/java-<VERSION 8/9/10>-openjdk/accessibility.properties muss die Zeile 
"assistive_technologies=org.GNOME.Accessibility.AtkWrapper" auskommentiert werden.
