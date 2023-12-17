package com.bytemedrive.events.entity;

import java.time.ZonedDateTime;
import java.util.UUID;


/**
 * This is encrypted event data class. This is how event in encrypted form is stored in server.
 *
 * @param id - event id
 * @param keys - list of ids for encryption
 * @param createdAt - timestamp when event was stored on server
 * @param eventDataBase64 - event data encrypted and encoded base64
 */
public record EncryptedEvent(UUID id, UUID[] keys, ZonedDateTime createdAt, String eventDataBase64) {
}
