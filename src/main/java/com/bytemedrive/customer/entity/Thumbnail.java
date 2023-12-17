package com.bytemedrive.customer.entity;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;


public record Thumbnail(
        List<UUID> chunksIds,
        List<UUID> chunksViewIds,
        Long sizeBytes,
        String contentType,
        Integer resolution,
        SecretKey secretKey
) {
}
