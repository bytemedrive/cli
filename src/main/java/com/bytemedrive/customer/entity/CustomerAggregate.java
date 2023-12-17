package com.bytemedrive.customer.entity;

import java.util.ArrayList;
import java.util.List;


public class CustomerAggregate {

    public final List<DataFile> dataFiles = new ArrayList<>();

    public final DataFolder folderRoot = new DataFolder(null, null, new ArrayList<>(), new ArrayList<>());

}
