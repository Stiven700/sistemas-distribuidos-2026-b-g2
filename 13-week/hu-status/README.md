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
| #165 | Threat model: api-gateway -> property-workflow | done | https://github.com/code-corhuila/property-docs/pull/181 |
| #182 | [security] Only the owner or ADMIN can read a Saga | todo | https://github.com/code-corhuila/property-docs/issues/182 |
| #183 | [security] Limit repeated Saga starts per user | todo | https://github.com/code-corhuila/property-docs/issues/183 |
| #184 | [security] Reject forged tokens at the workflow even inside the network | todo | https://github.com/code-corhuila/property-docs/issues/184 |

## 2. My individual contribution
- Attack surface map of Property with its trust boundaries, and my boundary api-gateway -> property-workflow: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/13-week/workshop/01-attack-surface.md
- STRIDE analysis with risk ranking, residual risk and the library book [B], updated after the peer review: https://github.com/code-corhuila/property-docs/pull/181
- Three security stories in the team backlog: #182, #183, #184
- Security CI proposal (secret scan, dependency audit, negative test) with the real output of the test: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop/04-security-ci

## 3. Blockers and risks
- The CI of the code-corhuila organization does not start (billing lock), so the security workflow is a proposal in my fork, run by hand.

## 4. Plan for next week
- Implement the highest-priority story of my boundary in batch 2 of the code.
- Move the negative test of CP8 into the team repository with its pull request.

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
- STRIDE pull request and its commits: https://github.com/code-corhuila/property-docs/pull/181/commits
- Stories: https://github.com/code-corhuila/property-docs/issues/182, https://github.com/code-corhuila/property-docs/issues/183, https://github.com/code-corhuila/property-docs/issues/184
- Review comment I left: https://github.com/code-corhuila/property-docs/pull/173#issuecomment-6513866556
- Review received and my answer: https://github.com/code-corhuila/property-docs/pull/181
- Commits of the workshop in my fork: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commits/main/13-week
- Reflection document: https://docs.google.com/document/d/1UE2GjGTImCn9her6tznxUrbO30XjONgURysbWnxEA38/edit?usp=sharing
