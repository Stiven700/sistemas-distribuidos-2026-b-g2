<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       13-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 13

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Daniel Stiven Poveda
- GITHUB_USER: Stiven700
- TEAM: Group 2 — Property
- SPRINT_GOAL: Threat model of the boundary api-gateway -> property-workflow and turn its top risks into security stories for the team backlog
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| #165 | Threat model: api-gateway -> property-workflow | doing | https://github.com/code-corhuila/property-docs/pull/181 |
| #182 | [security] Only the owner or ADMIN can read a Saga | todo | https://github.com/code-corhuila/property-docs/issues/182 |
| #183 | [security] Limit repeated Saga starts per user | todo | https://github.com/code-corhuila/property-docs/issues/183 |
| #184 | [security] Reject forged tokens at the workflow even inside the network | doing | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/342b921b02af90fd644f9087ee5210baadba93dc |

## 2. My individual contribution
- Attack surface map of Property with the trust boundaries TB1 to TB4 and my boundary api-gateway -> property-workflow: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/3d92a8005ab68ed91bde212d854f5a424b4106a3
- STRIDE analysis of POST /api/v1/sagas/reservar and GET /api/v1/sagas/{id}, with risk ranking and the library book: https://github.com/code-corhuila/property-docs/pull/181
- Three security stories in the team backlog: #182, #183, #184
- Security CI proposal (gitleaks, OWASP dependency-check, negative authorization test for property-workflow): https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/fe87c812dd084d0d0cb1a4f0b1e3aac31fd335c3
- Review of the STRIDE of booking-api -> PostgreSQL: https://github.com/code-corhuila/property-docs/pull/173#issuecomment-6513866556

## 3. Blockers and risks
- The CI of the code-corhuila organization does not start (billing lock), so the security workflow is only a proposal in my fork.
- The owner check of GET /api/v1/sagas/{id} does not exist yet: the endpoint comes in batch 2.

## 4. Plan for next week
- Answer the review on my STRIDE pull request and finish the residual risk and the references (CP6).
- Run the negative authorization test against property-workflow and record the output (CP8).
- Batch 2 of the code: business endpoints of the workflow, including the owner check of #182.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Workshop folder: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop
- Claim issue: https://github.com/code-corhuila/property-docs/issues/165
- STRIDE pull request: https://github.com/code-corhuila/property-docs/pull/181
- Stories: https://github.com/code-corhuila/property-docs/issues/182, https://github.com/code-corhuila/property-docs/issues/183, https://github.com/code-corhuila/property-docs/issues/184
- Review comment: https://github.com/code-corhuila/property-docs/pull/173#issuecomment-6513866556
- Commits: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/c509faf0da95136384bac6decf61c41ddd673db7, https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/3d92a8005ab68ed91bde212d854f5a424b4106a3, https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/0b39c9248932a3fb2c29070d5d9ea2bc9452f8d2, https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/fe87c812dd084d0d0cb1a4f0b1e3aac31fd335c3, https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commit/342b921b02af90fd644f9087ee5210baadba93dc
