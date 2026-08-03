# CampusVerse — Backend Scaffold

This is the initial Spring Boot scaffold for CampusVerse, matching the architecture in the
project documentation: layered MVC backend (`controller/ service/ repository/ model/ dto/ config/ exception/`),
exposed as a REST API, consumed by a decoupled HTML/CSS/JS frontend.

At this stage the app has **no features yet** — just a working, connected skeleton with:
- Maven project + dependencies (Web, JPA, PostgreSQL driver, Security, JWT, Swagger, Lombok)
- `application.yml` wired to PostgreSQL
- CORS config for a separate frontend origin
- Global exception handler (consistent JSON error responses)
- A `/api/health` endpoint to confirm everything boots
- Swagger UI wired up with a JWT bearer auth scheme (ready for when auth exists)

---

## 1. Prerequisites

- **Java 17+** — you said you already have this. Check with:
  ```bash
  java -version
  ```
- **Maven** — you don't need to install this separately. Every modern IDE (IntelliJ, VS Code)
  can generate a Maven wrapper (`mvnw`) for you, or you can install Maven directly:
  - **IntelliJ**: right-click `pom.xml` → *Maven* → *Reload project*. IntelliJ manages Maven internally, no install needed.
  - **Manual install** (if you want the `mvn` CLI): https://maven.apache.org/install.html, or via a package manager:
    ```bash
    # macOS
    brew install maven
    # Ubuntu/Debian
    sudo apt install maven
    # Windows (with Chocolatey)
    choco install maven
    ```
  Verify with `mvn -version`.
- **PostgreSQL** — you already have this.

---

## 2. Create the database

Open `psql` or a GUI tool (pgAdmin, DBeaver, TablePlus) and run:

```sql
CREATE DATABASE campusverse_db;
```

That's it — no tables to create manually. `spring.jpa.hibernate.ddl-auto=update` in
`application.yml` means Hibernate will create tables automatically from `@Entity` classes as
we build each feature. (We'll revisit this setting once the schema stabilizes — see the note
in `application.yml`.)

---

## 3. Configure credentials

`application.yml` reads DB credentials from environment variables, with local defaults:

```yaml
username: ${DB_USERNAME:postgres}
password: ${DB_PASSWORD:postgres}
```

Either:
- Set env vars `DB_USERNAME` / `DB_PASSWORD` to match your local Postgres setup, **or**
- Just edit the defaults directly in `src/main/resources/application.yml` for local dev.

Do the same for `COLLEGE_EMAIL_DOMAIN` when we get to the `users` table (Section 5.1 restricts
registration to a college email domain).

---

## 4. Import into IntelliJ

1. Open IntelliJ → **Open** → select the `campusverse` folder (the one containing `pom.xml`).
2. IntelliJ will detect it as a Maven project and download all dependencies automatically —
   this is the point where Maven itself gets used, even without a separate install.
3. Wait for indexing + dependency download to finish (bottom-right progress bar).

---

## 5. Run it

From IntelliJ: right-click `CampusverseApplication.java` → **Run**.

From terminal (if you installed Maven):
```bash
mvn spring-boot:run
```

Then check:
```bash
curl http://localhost:8080/api/health
# {"status":"UP","service":"CampusVerse"}
```

Swagger UI (empty for now, will fill up as we add endpoints):
```
http://localhost:8080/swagger-ui.html
```

---

## 6. What's next

Per our plan, next up is:
1. `users` table — the `User` entity, repository, DTOs
2. Spring Security + JWT — register/login endpoints, password hashing, token filter
3. Then one feature at a time: Lost & Found → Confession Wall → Business Discovery → Mentorship Matching

---

## Project structure

```
src/main/java/com/campusverse/
├── controller/    REST endpoints
├── service/       business logic (matching, moderation, scoring)
├── repository/    Spring Data JPA interfaces
├── model/         JPA entity classes
├── dto/           request/response objects
├── config/        security, CORS, Swagger config
└── exception/     custom exceptions + global error handling
```
