package io.jans.fido2.service.verifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jans.fido2.exception.Fido2RuntimeException;
import io.jans.fido2.model.assertion.Response;
import io.jans.fido2.service.processor.assertion.AssertionProcessorFactory;
import io.jans.fido2.service.processors.AssertionFormatProcessor;
import io.jans.orm.model.fido2.Fido2AuthenticationData;
import io.jans.orm.model.fido2.Fido2RegistrationData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssertionVerifierTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AssertionVerifier assertionVerifier;

    @Mock
    private Logger log;

    @Mock
    private AssertionProcessorFactory assertionProcessorFactory;

    @Test
    void verifyAuthenticatorAssertionResponse_authenticatorDataIsNull_fido2RuntimeException() {
        Response response = new Response();
        response.setClientDataJSON("TEST-clientDataJSON");
        response.setSignature("TEST-signature");
        Fido2RegistrationData registration = new Fido2RegistrationData();
        Fido2AuthenticationData authenticationEntity = new Fido2AuthenticationData();

        Fido2RuntimeException ex = assertThrows(Fido2RuntimeException.class, () -> assertionVerifier.verifyAuthenticatorAssertionResponse(response, registration, authenticationEntity));
        assertNotNull(ex);
        assertEquals(ex.getMessage(), "Authenticator data is invalid");
        verifyNoInteractions(log, assertionProcessorFactory);
    }

    @Test
    void verifyAuthenticatorAssertionResponse_clientDataJSONIsNull_fido2RuntimeException() {
        Response response = new Response();
        response.setAuthenticatorData("TEST-authenticatorData");
        response.setSignature("TEST-signature");

        Fido2RegistrationData registration = new Fido2RegistrationData();
        Fido2AuthenticationData authenticationEntity = new Fido2AuthenticationData();

        Fido2RuntimeException ex = assertThrows(Fido2RuntimeException.class, () -> assertionVerifier.verifyAuthenticatorAssertionResponse(response, registration, authenticationEntity));
        assertNotNull(ex);
        assertEquals(ex.getMessage(), "Authenticator data is invalid");
        verifyNoInteractions(log, assertionProcessorFactory);
    }

    @Test
    void verifyAuthenticatorAssertionResponse_signatureIsNull_fido2RuntimeException() {
        Response response = new Response();
        response.setAuthenticatorData("TEST-authenticatorData");
        response.setSignature("TEST-signature");
        Fido2RegistrationData registration = new Fido2RegistrationData();
        Fido2AuthenticationData authenticationEntity = new Fido2AuthenticationData();

        Fido2RuntimeException ex = assertThrows(Fido2RuntimeException.class, () -> assertionVerifier.verifyAuthenticatorAssertionResponse(response, registration, authenticationEntity));
        assertNotNull(ex);
        assertEquals(ex.getMessage(), "Authenticator data is invalid");
        verifyNoInteractions(log, assertionProcessorFactory);
    }

    @Test
    void verifyAuthenticatorAssertionResponse_validValues_valid() {
        String authenticatorDataValue = "TEST-authenticatorData";
        String clientDataJSONValue = "TEST-clientDataJSON";
        String signatureValue = "TEST-signature";
        Response response = new Response();
        response.setAuthenticatorData(authenticatorDataValue);
        response.setSignature(signatureValue);
        response.setClientDataJSON(clientDataJSONValue);

        Fido2RegistrationData registration = new Fido2RegistrationData();
        Fido2AuthenticationData authenticationEntity = new Fido2AuthenticationData();
        AssertionFormatProcessor assertionProcessor = mock(AssertionFormatProcessor.class);
        when(assertionProcessorFactory.getCommandProcessor(registration.getAttestationType())).thenReturn(assertionProcessor);

        assertionVerifier.verifyAuthenticatorAssertionResponse(response, registration, authenticationEntity);
        verify(log).debug("Authenticator data {}", authenticatorDataValue);
        verify(assertionProcessor).process(authenticatorDataValue, signatureValue, clientDataJSONValue, registration, authenticationEntity);
    }
}
