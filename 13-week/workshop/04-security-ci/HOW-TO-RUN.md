# How to run the negative authorization test — property-workflow

Story: **[security] Reject forged tokens at the workflow even inside the network** (STRIDE S).
Acceptance criterion covered: *a request to workflow:3006 with a token signed by another key
answers 401 UNAUTHORIZED, and an automated HTTP test runs on every pull request.*

Endpoint: `POST /api/v1/sagas/reservar` (E-23).

1. Clone `code-corhuila/property-workflow` (branch `develop`).
2. Copy `SagaAuthorizationNegativeTest.java` to
   `workflow-app/src/test/java/co/edu/corhuila/property/workflow/`.
3. Run (Maven 3.9 and JDK 21, or Docker):
   ```bash
   mvn -B -pl workflow-app -am -Dtest=SagaAuthorizationNegativeTest -Dsurefire.failIfNoSpecifiedTests=false verify
   ```
4. Expected: `Tests run: 2, Failures: 0` — both requests are rejected with 401 and the error
   envelope `{"error":"UNAUTHORIZED",...}`.

`security.yml` runs the same test plus gitleaks and OWASP dependency-check on every pull
request into `develop` and `qa`. The real console output is added in CP8.
