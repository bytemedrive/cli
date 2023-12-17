package com.bytemedrive.events.boundary;

import com.bytemedrive.events.control.EventsRestClient;
import com.bytemedrive.events.entity.EncryptedEvent;
import com.bytemedrive.events.entity.EventMapWrapper;
import com.bytemedrive.privacy.boundary.PrivacyFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@ApplicationScoped
public class EventsFacade {
    @Inject
    PrivacyFacade facadePrivacy;

    @RestClient
    EventsRestClient restClient;

    @Inject
    ObjectMapper objectMapper;

    public List<EventMapWrapper> getEvents(String username, char[] password) {
        var usernameSha3 = facadePrivacy.hashSha3(username);
        var authorizationHeader = "Hash " + facadePrivacy.hashSha3("%s:%s".formatted(username, new String(password)));

        var keysEncrypted = restClient.getPrivateKeys(usernameSha3, authorizationHeader);
        var eventKeys = new HashMap<UUID, SecretKey>();
        for (var key : keysEncrypted) {
            var secretKeyAsBytes = facadePrivacy.decryptWithPassword(Base64.getDecoder().decode(key.keyBase64()), password, usernameSha3.getBytes(StandardCharsets.UTF_8));
            eventKeys.put(key.id(), new SecretKeySpec(secretKeyAsBytes, 0, secretKeyAsBytes.length, "AES"));
        }

        return restClient.allEvents(usernameSha3, authorizationHeader, 0)
                .stream()
                .map(encryptedEvent -> decryptEvent(encryptedEvent, eventKeys))
                .toList();
    }

    private EventMapWrapper decryptEvent(EncryptedEvent encryptedEvent, Map<UUID, SecretKey> eventKeys) {
        var secretKey = eventKeys.get(encryptedEvent.keys()[0]);
        var eventBytes = facadePrivacy.decryptWithKey(Base64.getDecoder().decode(encryptedEvent.eventDataBase64()), secretKey);
        try {
            return objectMapper.readValue(eventBytes, EventMapWrapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
