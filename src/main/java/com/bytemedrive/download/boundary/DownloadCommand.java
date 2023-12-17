package com.bytemedrive.download.boundary;

import com.bytemedrive.ByteMeCommand;
import com.bytemedrive.customer.boundary.CustomerFacade;
import com.bytemedrive.customer.entity.DataFile;
import com.bytemedrive.customer.entity.DataFolder;
import com.bytemedrive.download.control.FileRestClient;
import com.bytemedrive.privacy.boundary.PrivacyFacade;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@CommandLine.Command(name = "download", description = "Download all source files")
public class DownloadCommand extends ByteMeCommand {

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output folder")
    String outputFolder;

    @CommandLine.Option(names = {"-t", "--thumbnails"}, description = "Download also thumbnails")
    Boolean thumbnails;

    @Inject
    Logger log;

    @Inject
    CustomerFacade facadeCustomer;

    @RestClient
    FileRestClient fileRestClient;

    @Inject
    PrivacyFacade facadePrivacy;

    @Override
    public void run() {
        try {
            var customer = facadeCustomer.getCustomer(username, password);
            downloadFolder(Path.of(outputFolder), customer.folderRoot, customer.dataFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadFolder(Path targetFolder, DataFolder dataFolder, List<DataFile> dataFiles) throws IOException {
        log.infof("Creating folder %s", targetFolder.toAbsolutePath());
        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }
        for (var dataFileLink : dataFolder.files()) {
            var dataFile = findDataFile(dataFileLink.dataFileId(), dataFiles);
            downloadDataFile(dataFile, targetFolder);
        }
        for (var folder : dataFolder.folders()) {
            downloadFolder(targetFolder.resolve(folder.name()), folder, dataFiles);
        }
    }

    private void downloadDataFile(DataFile dataFile, Path pathFolder) throws IOException {
        downloadFile(dataFile.chunksViewIds(), dataFile.name(), dataFile.secretKey(), pathFolder);
        if (Boolean.TRUE.equals(thumbnails)) {
            for (var thumbnail : dataFile.thumbnails()) {
                var name = dataFile.name().substring(0, dataFile.name().lastIndexOf('.'));
                var extension = dataFile.name().substring(dataFile.name().lastIndexOf('.') + 1);
                log.infof("Downloading file %s", dataFile.name());
                downloadFile(thumbnail.chunksViewIds(), "%s-%s.%s".formatted(name, thumbnail.resolution(), extension), thumbnail.secretKey(), pathFolder);
            }
        }
    }

    private void downloadFile(List<UUID> chunksViewIds, String name, SecretKey secretKey, Path pathFolder) throws IOException {
        log.infof("Downloading file %s", name);
        List<byte[]> chunks = new ArrayList<>();
        var pathFile = pathFolder.resolve(name);
        if (Files.exists(pathFile)) {
            Files.delete(pathFile);
        }
        var pathTmp = Files.createTempFile("byteme", "drive").toAbsolutePath();
        for (var chunkId : chunksViewIds) {
            log.infof("Downloading chunk id: %s", chunkId);
            var bytes = fileRestClient.getFileChunk(chunkId);
            chunks.add(bytes);
            Files.write(pathTmp, bytes, StandardOpenOption.APPEND);
        }
        facadePrivacy.decryptFile(Files.newInputStream(pathTmp), Files.newOutputStream(pathFile), secretKey);
        Files.delete(pathTmp);
    }

    private DataFile findDataFile(UUID dataFileId, List<DataFile> dataFiles) {
        for (var dataFile : dataFiles) {
            if (dataFile.id().equals(dataFileId)) {
                return dataFile;
            }
        }
        return null;
    }
}
