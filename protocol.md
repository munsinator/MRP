# Development Protocol
## Database Design + Docker
Ich habe ein simples ER-esque Diagramm zur Orientierung gezeichnet und habe es von meinem Prof prüfen lassen. Es sah ungefähr so aus:

 ![ ](./erd.drawio.png)

Ich hatte Filme, Spiele und Serien als separate Tabellen mit Vererbung modelliert.
Um die Komplexität zu reduzieren und die Persistenzlogik zu vereinfachen, habe ich das Design
zu einer einzigen media_entry-Tabelle mit einer media_type-Spalte und einer Enumeration im Code geändert.
Dies erfüllt weiterhin die Anforderungen und hält gleichzeitig die Implementierung wartbar. Danach sah meine Datenbank so aus:

![ ](./mrp.drawio.png)

Note: JOIN-Tabellen habe ich aktuell nicht grafisch dargestellt.


## Models
Ich habe für jede Entity das Bilder Pattern implementiert, da durch das Pattern eine strikte Vorgabe beim Kreieren befolgt werden muss.
Die Chancen sind so geringer, dass "Müll Objekte" in der Datenbank landen. Beim Erstellen, der Entities musste ich aufpassen, dass die 
Attributnamen auch mit Spaltennamen kompatibel sind. Ich will somit Probleme bei der Serialisierung mit Jackson vermeiden.


## Repositories
Für die Intermediate Abgabe, habe ich mich dazu entschieden zumindest die Grundfunktionen (CRUD) für MediaEntry zu erstellen:
- save() (CREATE)
- findId() (READ)
- findAll() (READ)
- update() (UPDATE)
- delete() (DELETE)
Zusätzlich entschied ich mich dazu, die Datenbank Connection über den Konstruktor zu injizieren, 
damit vermeide ich mit jedem CRUD Aufruf eine neue Datenbankverbindung aufzubauen.

## Controller + Services
Beide Layer sind noch in der Rohphase. Zuerst habe ich mich an die Services gesetzt. Beim UserController wars mir
zuerst wichtig nur register() und login() und beim MediaController CRUD Methoden zu implementieren.
DTOs habe ich als Records erstellt, weil die schwieriger zu manipulieren sind aufgrund ihrer privat & finalen Feldern.
Setter gibt es auch nicht. Das HTTP-Routing habe ich der Spezifikation übernommen. Die Responses und Header 
sind leer gemäß Spezifikation, aber das wird für die nächste Abgabe abgeändert. Business logic Implementierung ist für den 
Service Layer geplant und muss noch fertig geschrieben werden.

Getestet wurde die Applikation mit Postman Requests, wie zb. : POST http://localhost:8080/api/users/login/ 
```json
{
    "username":"system_user",
    "passwordHash":"password"
}
```

---

## Estimated times
This is the time it roughly took for each part:
- Database modelling + Implementation: 2h
- Repository implementation: ~1h
- Service implementation: ~3h
- Controller implementation: ~5h
- Token-based authorization: ~2h
- Unit test planning + implementation:
- Documentation: ~30min
- **Honorable mention** - Debugging: