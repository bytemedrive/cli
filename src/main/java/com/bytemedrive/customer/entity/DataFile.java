package com.bytemedrive.customer.entity;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;


public record DataFile(
        UUID id,
        List<UUID> chunksIds,
        List<UUID> chunksViewIds,
        String name,
        Long sizeBytes,
        String contentType,
        SecretKey secretKey,
        List<Thumbnail> thumbnails) {
}
