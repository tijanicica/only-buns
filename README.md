# OnlyBuns 🐰

A social network faculty project for rabbit owners — think Instagram/Twitter, but every post is about a bunny. Users register, post photos with locations, follow each other, chat in real time, and browse trending content and analytics. Built as three independent pieces: a Spring Boot backend, a Vue 3 frontend, and a small Python RabbitMQ producer for seeding vet/rabbit-care locations on the map.

> **Status:** This branch consolidates every feature branch built over the course of the project into `main`, which had fallen behind after the first few PRs. It's kept here as a **code showcase** — it reflects the final implementation rather than a guaranteed, ready-to-run deployment (secrets are hardcoded in `application.properties` from local dev and would need to be replaced).

## Concept

- **Posts** — users publish short posts ("buns") with a photo and a geolocation, viewable on a map and in feeds.
- **Social graph** — follow/unfollow other users, a "friends' posts" feed, a per-user profile and public profile pages.
- **Engagement** — likes and comments on posts, a trending page ranking popular posts/users.
- **Chat** — real-time 1:1 messaging over WebSocket/STOMP.
- **Admin** — an admin home page and user management view (ban/manage registered users).
- **Rabbit care map** — vet clinics / rabbit rescue centers plotted on the map, seeded via a separate RabbitMQ producer script.
- **Account lifecycle** — registration with email activation links, login, logout, JWT sessions, and scheduled deletion of accounts that never activated.
- **Resilience & performance extras** (originally built as course exercises, layered onto the app): a login rate limiter, per-IP brute-force tracking, a Bloom filter for fast username-existence checks, image compression + caching for uploaded photos, and a small custom round-robin load balancer in front of two backend instances.

## Architecture

```
┌────────────────────┐        HTTP / WebSocket        ┌──────────────────────────┐
│  only-buns-frontend │ ─────────────────────────────▶ │        onlybuns          │
│  Vue 3 SPA          │ ◀───────────────────────────── │  Spring Boot backend     │
└────────────────────┘                                 └───────────┬──────────────┘
                                                                    │
                                                       ┌────────────┼─────────────┐
                                                       │            │             │
                                                 PostgreSQL    RabbitMQ      Gmail SMTP
                                                 (JPA/Hibernate) (care-location  (activation
                                                                   messages)     emails)
                                                                    ▲
                                                                    │ publishes location messages
                                                          ┌──────────────────┐
                                                          │   rabbit_care     │
                                                          │  Python producer  │
                                                          └──────────────────┘
```

- **Frontend** (`only-buns-frontend/`) — Vue 3 SPA (Vue Router, Bootstrap 5, Leaflet for maps, Chart.js for analytics, STOMP over SockJS for chat).
- **Backend** (`onlybuns/`) — Spring Boot monolith exposing REST + WebSocket endpoints, backed by PostgreSQL.
- **RabbitCare producer** (`rabbit_care/`) — standalone Python script (`pika`) that publishes rabbit-care-center locations to a RabbitMQ direct exchange, consumed by the backend and rendered on the map.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | Vue 3, Vue Router, Bootstrap 5, Leaflet + leaflet-control-geocoder, Chart.js, Axios, @stomp/stompjs + SockJS, JWT decode |
| Backend | Java, Spring Boot (Web, Security, Data JPA, WebSocket, AMQP, Mail, Cache, AOP), Hibernate |
| Auth | JWT (jjwt), Spring Security |
| Database | PostgreSQL |
| Messaging | RabbitMQ (Spring AMQP) |
| Resilience | Resilience4j (rate limiting), a custom in-house round-robin load balancer |
| Other libs | Guava (Bloom filter), Thumbnailator (image compression), Lombok |
| Email | Spring Mail via Gmail SMTP (account activation) |
| Testing | JUnit, Spring Security Test, H2 (in-memory test DB) |
| Location seeding | Python 3, `pika` (RabbitMQ client) |

## Key features by module

| Area | What's implemented |
|---|---|
| Auth | Registration with email activation link, login/logout, JWT-based sessions, role-based routes (user/admin) |
| Posts | Create/update/delete posts with images and geolocation, post detail view, comments, likes |
| Social | Follow/unfollow, friends'-posts feed, public user profile pages |
| Discovery | Trending posts/users page, analytics dashboard (charts) |
| Map | Post locations and rabbit-care-center locations plotted with Leaflet |
| Chat | Real-time messaging (WebSocket/STOMP + SockJS), chat rooms |
| Admin | Admin home page, manage/ban registered users |
| Hardening | Login rate limiting (Resilience4j), per-IP attempt tracking/temporary blocking, Bloom filter for username lookups, periodic cleanup of unactivated accounts |
| Performance | Image compression on upload, image byte caching, simple round-robin load balancer across two backend instances |

## Repository structure

```
.
├── onlybuns/                # Spring Boot backend
│   ├── src/main/java/com/project/onlybuns/
│   │   ├── controller/       # Post, Chat, Analytics, Trends, LoadBalancer, RabbitCareLocation, RegisteredUser...
│   │   ├── service/           # Business logic (Post, Follow, Like, Comment, Email, Jwt, ImageCompression, BloomFilter...)
│   │   ├── model/ repository/ mapper/ dto/ config/
│   │   └── loadbalancer/     # Custom round-robin load balancer
│   ├── src/main/resources/application.properties
│   └── pom.xml
├── only-buns-frontend/       # Vue 3 SPA
│   └── src/views/            # HomePage, Login/Register, UserHomePage, Chat, Map, Trends, Analytics, Admin...
└── rabbit_care/               # Python RabbitMQ producer for rabbit-care-center map locations
    └── main.py
```

## Startup guide

There's no Docker Compose here — the three pieces run as separate local processes.

### 1. Prerequisites

- Java 17+ and Maven (or use the bundled `./mvnw`)
- Node.js 16+ and npm
- PostgreSQL running locally
- RabbitMQ running locally (only needed for the rabbit-care map locations feature)
- Python 3 + `pip install pika` (only needed to run the location seeder)

### 2. Database

Create a PostgreSQL database matching `onlybuns/src/main/resources/application.properties`:

```sql
CREATE DATABASE onlybuns;
```

Default configured credentials are `postgres` / `tijana` — update `application.properties` to match your local setup. `spring.jpa.hibernate.ddl-auto=update` means the schema is created/updated automatically on boot; `data.sql` seeds initial data.

### 3. Backend

```bash
cd onlybuns
./mvnw spring-boot:run
```

Runs on `http://localhost:8080` by default. Notes:

- Email activation links require a working SMTP config (`spring.mail.*` in `application.properties` — currently pointed at a Gmail account with an app password). Without valid mail credentials, registration will still create the account but the activation email will fail to send.
- The custom load balancer (`LoadBalancerService`) expects two backend instances at `localhost:8081` and `localhost:8082` — this only matters if you exercise that code path (`LoadBalancerController`), not for normal single-instance use.

### 4. Frontend

```bash
cd only-buns-frontend
npm install
npm run serve
```

Runs on `http://localhost:8081` (Vue CLI dev server default) and talks to the backend via Axios.

### 5. (Optional) Rabbit-care location seeder

With RabbitMQ running locally:

```bash
cd rabbit_care
pip install pika
python main.py
```

Publishes a set of hardcoded rabbit-care-center locations onto the `direct_exchange` / `rabbit-care-queue`, which the backend consumes and the frontend renders on the map page.

## History note

`main` only ever tracked the first PR (home page). All subsequent feature work — auth, posts, social graph, chat, admin tooling, analytics/trends, rate limiting, load balancing, image compression/caching — landed on `develop` through 26 further pull requests but was never merged back into `main`. This branch merges `develop` (confirmed to be a superset of every other feature branch) into `main` to bring the full implementation together in one place.
