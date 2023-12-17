package com.bytemedrive.customer.entity;

import java.util.List;
import java.util.UUID;


public record DataFolder(UUID id, String name, List<DataFolder> folders, List<DataFileLink> files) {
}
