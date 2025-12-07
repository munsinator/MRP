# Development Protocol

Add a protocol document with the following content:
- Description of technical steps and architecture decisions
- Explanation of unit test coverage and why specific logic was tested
- Notes on problems encountered and how they were solved
- Estimated time tracking for each major part of the project
- consider that the git-history is part of the documentation (no need to copy it into the
protocol)
  
See the corresponding Checklist Excel sheets for the MUST-HAVES, Grading-Items and
  Grading-Points.
---

## Database Design + Docker
hand drawn, how done, docker compose and multiple redesigns -> join tables

## Models
..schreiben und @JsonProperty("username"); ich verwende dto=model, in prod eine katastrophe aber für ein proekt dieser größe ok
aufpassen dass jackson (mein serialization libray) das auch checkt welcher eintrag welche spalte ist
builder pattern implementiert bis auf genre; genre auf builder wäre mir zuviel und ergo nicht so leserlich
Initially, I modeled movies, games and series as separate tables with inheritance-like structure.
To reduce complexity and simplify persistence logic with plain JDBC, I refactored the design to a single media_entry table with a media_type column and an enum in code. This still fulfills the requirements while keeping the implementation maintainable


## Repositories


## Services


## Controller



## Authorization



---
## Problems

## Unit test coverage

## Estimated times
This is the time it roughly took for each part:
- Database modelling + Implementation: 2h
- Repository implementation:
- Service implementation:
- Controller implementation:
- Token-based authorization: 
- Unit test planning + implementation:
- Documentation:
- **Honorable mention** - Debugging: