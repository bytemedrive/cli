package com.bytemedrive.events.entity;

import java.util.UUID;


/**
 * This is encrypted secret key. This is how secret key for events encryption and decryption is stored on server.
 * @param id - id of secret key
 * @param algorithm - algorithm of secret key
 * @param keyBase64 - secret key encrypted by user's password
 */
public record EncryptedSecretKey(UUID id, EncryptionAlgorithm algorithm, String keyBase64) {
}
