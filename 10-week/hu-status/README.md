<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       10-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 10

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Daniel Stiven Poveda Cante
- GITHUB_USER: Stiven700
- TEAM: Pms_Property
- SPRINT_GOAL: Finish aligning the documentation (data models, API rules, requirements and user stories) and start the coding phase with the frontend — the Angular container, the Catalog and Booking portals — promoting each story from `develop` to `qa` and into the 2.0.0 release.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-DATA-004 | Rewrite the Notification data model and add the `workflow` schema; complete the Notification data dictionary and model (issues #80, #84) | done | https://github.com/code-corhuila/property-docs/pull/81 , https://github.com/code-corhuila/property-docs/pull/82 , https://github.com/code-corhuila/property-docs/pull/83 , https://github.com/code-corhuila/property-docs/pull/85 |
| DOC-API-006 | Align the API rules with the orchestrated Saga and Annex C (issue #86) | done | https://github.com/code-corhuila/property-docs/pull/87 |
| DOC-API-007 | Rewrite the Payment contract with charge and refund (issue #88) | done | https://github.com/code-corhuila/property-docs/pull/89 |
| DOC-REQ-001 | Functional requirements and product backlog (issue #105) | done | https://github.com/code-corhuila/property-docs/pull/106 |
| DOC-REQ-002 | Rewrite the Catalog and Booking user stories (issue #107) | done | https://github.com/code-corhuila/property-docs/pull/108 |
| DOC-REQ-003 | Rewrite the Payment user stories and add the Saga epic (issue #109) | done | https://github.com/code-corhuila/property-docs/pull/110 |
| HU-PLAT-002 | Story issue for the frontend container | done | https://github.com/code-corhuila/property-docs/issues/133 |
| HU-PLAT-002 | Frontend container (`property-front`): scaffold the Angular container | done | https://github.com/code-corhuila/property-front/pull/1 |
| HU-PLAT-002 | Frontend container: raise the contrast of the primary colour (accessibility) | done | https://github.com/code-corhuila/property-front/pull/6 |
| HU-PLAT-002 | Promote the container to `qa` with `cherry-pick -x` (PRs #1, #2, #3, #5, #6) — `hu-plat-002-qa` → `qa` | done | https://github.com/code-corhuila/property-front/pull/7 |
| HU-CAT-001 | Catalog portal: contract types, API calls and the mock API | done | https://github.com/code-corhuila/property-catalog-portal/pull/2 |
| — | Catalog portal CI: run only on PRs into `develop` and `qa`, 10 minutes at most (teacher's rule) | done | https://github.com/code-corhuila/property-catalog-portal/pull/8 |
| HU-PLAT-003 | Catalog portal: keep local files out of the Docker image (story issue #146) | done | https://github.com/code-corhuila/property-catalog-portal/pull/9 |
| HU-SAGA-001 | Booking portal: checkout summary and reservation result (story issue #147) | done | https://github.com/code-corhuila/property-booking-portal/pull/4 |
| HU-SAGA-001 | Promote the checkout to `qa` — `hu-saga-001-qa` → `qa` | done | https://github.com/code-corhuila/property-booking-portal/pull/6 |
| HU-SAGA-001 | Carry the checkout into the release, keeping the test payment out — `hu-saga-001-release` → `release/2.0.0` | done | https://github.com/code-corhuila/property-booking-portal/pull/8 |
| HU-BOOK-006 | Carry "My bookings" into the release — `hu-book-006-release` → `release/2.0.0` (story issue #148) | done | https://github.com/code-corhuila/property-booking-portal/pull/9 |
| — | Release 2.0.0 of `property-booking-portal` into `main` | done | https://github.com/code-corhuila/property-booking-portal/pull/10 |

## 2. My individual contribution

- **Documentation, closing the alignment with the norm.** Rewrote the Notification data
  model and added the `workflow` schema that stores the Saga state, then completed the
  Notification data dictionary that had entered `main` incomplete. Aligned
  `07-api/guidelines.md` with the orchestrated Saga and Annex C, and rewrote the Payment
  contract so it exposes a charge and a refund (the Saga calls Payment over REST).
- **Requirements.** Added the functional requirements and the product backlog, and
  rewrote the Catalog, Booking and Payment user stories, adding the Saga epic — the
  backlog the team now codes against.
- **Frontend container (HU-PLAT-002).** Scaffolded the Angular container in
  `property-front` and fixed the contrast of the primary colour so text on primary
  buttons meets accessibility contrast. Promoted the whole story to `qa` by
  cherry-picking the merged PRs (#1, #2, #3, #5, #6) with `cherry-pick -x`, so every
  commit in `qa` points back to the commit reviewed in `develop`.
- **Catalog portal (HU-CAT-001, HU-PLAT-003).** Added the contract types, the API
  calls and a mock API so the portal works before the backend exists, and kept local
  files out of the Docker image. Applied the teacher's CI rule: CI only on PRs into
  `develop` and `qa`, with a 10-minute limit.
- **Booking portal (HU-SAGA-001, HU-BOOK-006).** Built the checkout summary and the
  reservation result. Promoted the story to `qa` and then into the 2.0.0 release, with
  an extra commit that keeps the test payment out of the release build, and carried
  "My bookings" (HU-BOOK-006) into the release as well, then merged the 2.0.0 release of
  the Booking portal into `main`.
- **Tracking.** Opened the story issues for HU-PLAT-002, HU-PLAT-003, HU-SAGA-001 and
  HU-BOOK-006, and one issue per documentation PR (20 PRs and 11 issues this week).

## 3. Blockers and risks

- There is no backend yet: the portals run against mock APIs. The teacher said backend
  and database are not required for this cut, so the demo shows the frontend running in
  Docker.
- The norm asks for `qa/...` branch names, but git cannot have a branch `qa/x` while a
  branch `qa` exists; the team is using `hu-<id>-qa` per `git-conventions.md` (the
  teacher confirmed branch names follow that document). Still to be written there.

## 4. Plan for next week

- Delivery on 2026-10-13: 8-minute presentation (problem, architecture, Saga
  orchestration, frontend running locally in Docker with portals switched off/on and the
  app flow), the release on `main` with tags, a `.zip` of the repositories and a
  `release.md` with each repository's tag commit.
- Small docs PR with the fixes the container build uncovered (font-family syntax,
  primary button contrast, fallback texts for 400/422 and the 502/504 mapping, portal
  ports and route names).

## 5. Compliance self-check

- [x] Conventional Commits - type(scope): summary — *(e.g. `feat(booking): add the checkout summary and the reservation result`)*
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(`hu-plat-002-qa`, `hu-saga-001-qa`, `hu-saga-001-release`, `hu-book-006-release`, with `cherry-pick -x` from `develop`)*
- [x] Testable acceptance criteria — *(the stories rewritten this week carry Given/When/Then scenarios)*
- [x] Tests added/updated (unit / integration) — *(every frontend PR passed CI (1/1 check) before merging)*
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no backend code yet)*
- [x] No secrets; config via environment variables — *(the test payment is kept out of the release; no keys in the repositories)*

## 6. Evidence links

- property-docs: https://github.com/code-corhuila/property-docs
- property-front: https://github.com/code-corhuila/property-front
- property-catalog-portal: https://github.com/code-corhuila/property-catalog-portal
- property-booking-portal: https://github.com/code-corhuila/property-booking-portal
