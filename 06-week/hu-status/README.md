<!-- Your weekly grade is read AUTOMATICALLY from this file:
   06-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 06

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Define the environment and configuration strategy (develop/qa/prod) for
the 5 microservices, and close a gap between the Definition of Done's expected branch
model and the project's actual Git conventions.

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-OPS-001 | Write `13-operations/environments.md` — 3-environment strategy (develop/qa/prod) for the future deployed system, 12-factor config, variable naming convention per service | done | *(pending commit — see note below)* |
| DOC-OPS-002 | Draft base `docker-compose.yml` for the 5 services + their databases + message broker, with health checks and `depends_on: condition: service_healthy` | done | *(pending commit — see note below)* |
| DOC-GOV-002 | Reconcile `00-governance/git-conventions.md` with the fact that this repository is documentation-only — revised the branch model to a single `main` branch instead of a multi-environment `dev`/`qa`/`main` flow | done | *(pending commit — see note below)* |
| MVP2-OPS-001..003 | Slice 3 MVP 2 orchestration stories with acceptance criteria (see `environments.md` §4) | done | *(pending commit — see note below)* |

> **Evidence note:** as with previous weeks, this documentation has not yet been
> committed to the repository — see the Week 5 report for the standing risk this
> creates. Recommend prioritizing the backlog of uncommitted work (Weeks 2–6) before
> Week 7.

## 2. My individual contribution

- Found that this week's session topic (Docker Compose, environments, 12-factor
  config, orchestration) mapped directly onto a gap already flagged back in Week 4's
  `overview.md`: the project never actually defined *how* the `develop`/`staging`/
  `production` environments listed there would be built or promoted between. Closed
  that gap with `13-operations/environments.md`.
- Initially believed this also meant `00-governance/git-conventions.md` was missing a
  `qa` branch stage (since the Definition of Done template references a
  `hu-xxx-dev`/`hu-xxx-qa`/`hu-xxx-main` branch flow) and added one. On review, realized
  this repository is **documentation-only** — there is no deployed application in it —
  so a multi-environment branch model belongs to a *future* service-code repository,
  not to this one. Reverted to a single `main` branch with short-lived work branches
  merged directly into it, and updated `environments.md` to make explicit that its
  `develop`/`qa`/`prod` runtime-environment plan is for the eventual deployed system,
  decoupled from how this repo is branched.
- Drafted a base `docker-compose.yml` covering all 5 real services
  (`booking-service`, `catalog-service`, `payment-service`, `identity-service`,
  `notification-service`), each with its own datastore per ADR-002 (PostgreSQL ×3,
  MongoDB, Redis), a message broker, and health-check gating — applying this week's
  "started ≠ ready" lesson directly (`depends_on: condition: service_healthy`
  everywhere a service depends on its DB or the broker).
- Sliced 3 MVP 2 orchestration user stories with Given/When/Then-style acceptance
  criteria, following the DoR format (`00-governance/definition-of-ready.md`), so this
  work is ready to enter a sprint once the team starts implementation.
- Produced two infographics: `week6_sesion1` (Docker Compose and orchestration basics)
  and `week6_sesion2` (Planning — environments, config strategy and orchestration).

## 3. Blockers and risks

- Same standing risk as previous weeks: documentation exists only as delivered files,
  not as repository history — now 5 weeks deep.
- `docker-compose.yml` currently references `./booking-service`, `./catalog-service`,
  etc. as build contexts that **do not exist yet** — no service code has been written.
  The compose file is a correct target/draft, not yet runnable; this is expected and
  consistent with the project still being in the documentation/design phase, but
  should not be mistaken for a working local environment.
- The message broker choice (Kafka vs. RabbitMQ) is still marked TBD in
  `01-context/scope.md` — RabbitMQ was used in the compose draft only as the simpler
  single-container option for now, not as a final decision.

## 4. Plan for next week

- Start implementing `booking-service`'s walking skeleton against the hexagonal
  structure already defined in `hexagonal-architecture.md`, so the `docker-compose.yml`
  drafted this week has something real to build and run.
- Commit the Week 2–6 documentation backlog to the repository.
- Set up the formal HU backlog in GitHub Projects, replacing the temporary
  `DOC-`/`MVP2-` scheme used across these weekly reports.

## 5. Compliance self-check

> Documentation/config-design week, pre-implementation — the checklist below is
> code-oriented and partially not yet applicable. Left unchecked rather than falsely
> marked, except where the artifact produced this week genuinely satisfies the item.

- [ ] Conventional Commits - type(scope): summary
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(not applicable to this repo per the Week 6 revision: documentation-only repos use a single `main` branch — see `00-governance/git-conventions.md`; this item applies once service code has its own repository)*
- [x] Testable acceptance criteria — *(the 3 MVP2-OPS stories sliced this week have explicit Given/When/Then criteria)*
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables — *(the `docker-compose.yml` draft uses placeholder dev-only values and reads real config from env vars per the 12-factor table in `environments.md`; no secret is committed)*

## 6. Evidence links

- Infographic: `week6_sesion1` — Docker Compose and orchestration basics
- Infographic: `week6_sesion2` — Planning: environments, config strategy and orchestration
- `13-operations/environments.md`
- `docker-compose.yml` (repository root)
- `00-governance/git-conventions.md` — revised to a single `main` branch, correctly scoped to a documentation-only repo
