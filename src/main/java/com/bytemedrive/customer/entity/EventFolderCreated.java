package com.bytemedrive.customer.entity;

import com.bytemedrive.customer.control.EventApplication;

import java.util.ArrayList;
import java.util.UUID;


@EventName(EventFolderCreated.NAME)
public record EventFolderCreated(
        UUID id,
        String name,
        UUID parent) implements EventApplication {

    public static final String NAME = "folder-created";

    @Override
    public void applyEvent(CustomerAggregate customer) {
        var folder = new DataFolder(id, name, new ArrayList<>(), new ArrayList<>());
        findFolder(parent, customer.folderRoot).folders().add(folder);
    }

    private DataFolder findFolder(UUID folderIdSearched, DataFolder folder) {
        if (folderIdSearched == null && folder.id() == null) {
            return folder;
        }
        if (folderIdSearched.equals(folder.id())) {
            return folder;
        }
        for (var f : folder.folders()) {
            var found = findFolder(folderIdSearched, f);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
