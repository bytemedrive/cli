package com.bytemedrive.customer.entity;

import java.util.UUID;


public record DataFileLink(UUID id, String name, UUID dataFileId) {
}
