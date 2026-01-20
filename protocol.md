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
1.1:Best DB design for your current schema
1) email → column on users

Email is a 1:1 attribute of a user. Add it to users.

Good practice:

allow NULL (because it’s not required by the schema)

optionally enforce uniqueness if your app wants “one email per account”

optionally basic format check (optional)

UND: ALTER TABLE users
ADD COLUMN favorite_genre_id UUID REFERENCES genre(id);

ALTER TABLE users
ADD COLUMN email VARCHAR(255); //unique nicht nötig, weil wir es für den login nicht brauchen

bei email wird einfach username@mrp.at genommen der einfachkeithalber

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
- Database modelling + Implementation: 4h
- Repository implementation: ~1h
- Service implementation: ~6h
- Controller implementation: ~10h
- Token-based authorization: ~2h
- Unit test planning + implementation:
- Documentation: ~3h
- **Honorable mention** - Debugging: 20h

### Was jetzt noch fehlt generell an Features:
- Create at least 20 unit tests to validate core business logic

A user:
- can view and edit a profile with personal statistics 
- can rate media entries from 1–5 stars and optionally write a comment 
- can edit or delete their own ratings 
- can like other users' ratings (1 like per rating)
- can mark media entries as favorites 
- can view their own rating history and list of favorites 
- receives recommendations based on previous rating behavior and content similarity
  
A media entry:
- includes a list of ratings and a calculated average score 
- can be marked as favorite by other users

A rating:
- is tied to a specific media entry and a specific user
- contains: star value (1–5), optional comment, timestamp
- can be liked by other users
- can be edited or deleted by the user who created it
- requires confirmation by the creator before the comment becomes publicly visible (moderation feature)
- comments are not publicly visible until confirmed by the author