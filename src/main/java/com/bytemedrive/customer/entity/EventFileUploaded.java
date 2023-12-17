package com.bytemedrive.customer.entity;

import com.bytemedrive.customer.control.EventApplication;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@EventName(EventFileUploaded.NAME)
public record EventFileUploaded(
        UUID dataFileId,
        List<UUID> chunksIds,
        List<UUID> chunksViewIds,
        String name,
        Long sizeBytes,
        String checksum,
        String contentType,
        String secretKeyBase64,
        UUID dataFileLinkId,
        UUID folderId,
        Integer exifOrientation) implements EventApplication {
    public static final String NAME = "file-uploaded";

    @Override
    public void applyEvent(CustomerAggregate customer) {
        var secretKeyAsBytes = Base64.getDecoder().decode(secretKeyBase64);
        var secretKey = new SecretKeySpec(secretKeyAsBytes, 0, secretKeyAsBytes.length, "AES");
        var dataFile = new DataFile(dataFileId, chunksIds, chunksViewIds, name, sizeBytes, contentType, secretKey, new ArrayList<>());
        customer.dataFiles.add(dataFile);
        var folder = findFolder(folderId, customer.folderRoot);
        folder.files().add(new DataFileLink(dataFileLinkId, name, dataFileId));
    }

    private DataFolder findFolder(UUID folderId, DataFolder folder) {
        if (folderId == null && folder.id() == null) {
            return folder;
        }
        if (folderId.equals(folder.id())) {
            return folder;
        }
        if (folder.folders() == null) {
            return null;
        }
        for (var childFolders : folder.folders()) {
            var found = findFolder(folderId, childFolders);
            if (found != null) {
                return found;
            }
        }
        return null;

    }
}
