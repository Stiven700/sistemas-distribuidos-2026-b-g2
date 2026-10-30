package co.edu.corhuila.property.workflow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

// Story: [security] Reject forged tokens at the workflow even inside the network (STRIDE S).
@SpringBootTest(classes = WorkflowApplication.class)
@AutoConfigureMockMvc
class SagaAuthorizationNegativeTest {

    private static final KeyPair SERVICE_KEY = newKey();
    private static final KeyPair OTHER_KEY = newKey();

    @DynamicPropertySource
    static void publicKey(DynamicPropertyRegistry registry) {
        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(SERVICE_KEY.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n";
        registry.add("JWT_PUBLIC_KEY", () -> pem);
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void withoutTokenAnswers401() throws Exception {
        mvc.perform(post("/api/v1/sagas/reservar").contentType(MediaType.APPLICATION_JSON).header("Idempotency-Key", "negative-test-0001").content("{\"propiedadId\":\"3f2b7c1e-0000-4000-8000-000000000001\",\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-03\",\"huespedes\":2,\"metodoPago\":\"TARJETA_CREDITO\",\"tokenPago\":\"tok_test\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void tokenSignedByAnotherKeyAnswers401() throws Exception {
        String forged = token(OTHER_KEY);
        mvc.perform(post("/api/v1/sagas/reservar").contentType(MediaType.APPLICATION_JSON).header("Idempotency-Key", "negative-test-0001").content("{\"propiedadId\":\"3f2b7c1e-0000-4000-8000-000000000001\",\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-03\",\"huespedes\":2,\"metodoPago\":\"TARJETA_CREDITO\",\"tokenPago\":\"tok_test\"}").header("Authorization", "Bearer " + forged))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    private static KeyPair newKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String token(KeyPair key) throws Exception {
        Base64.Encoder url = Base64.getUrlEncoder().withoutPadding();
        String header = url.encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        long exp = Instant.now().plusSeconds(600).getEpochSecond();
        String payload = url.encodeToString(("{\"sub\":\"00000000-0000-4000-8000-000000000001\",\"roles\":[\"GUEST\"],\"exp\":" + exp + "}")
                .getBytes(StandardCharsets.UTF_8));
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(key.getPrivate());
        signer.update((header + "." + payload).getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + url.encodeToString(signer.sign());
    }
}
