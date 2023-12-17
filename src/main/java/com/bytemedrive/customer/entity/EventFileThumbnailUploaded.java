package com.bytemedrive.customer.entity;

import com.bytemedrive.customer.control.EventApplication;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@EventName(EventFileThumbnailUploaded.NAME)
public record EventFileThumbnailUploaded(
        UUID sourceDataFileId,
        List<UUID> chunksIds,
        List<UUID> chunksViewIds,
        Long sizeBytes,
        String checksum,
        String contentType,
        String secretKeyBase64,
        Integer resolution
) implements EventApplication {
    public static final String NAME = "file-thumbnail-uploaded";

    @Override
    public void applyEvent(CustomerAggregate customer) {
        for (var dataFile : customer.dataFiles) {
            if (dataFile.id().equals(sourceDataFileId)) {
                var secretKeyAsBytes = Base64.getDecoder().decode(secretKeyBase64);
                var secretKey = new SecretKeySpec(secretKeyAsBytes, 0, secretKeyAsBytes.length, "AES");
                dataFile.thumbnails().add(new Thumbnail(chunksIds, chunksViewIds, sizeBytes, contentType, resolution, secretKey));
            }
        }
    }
}
