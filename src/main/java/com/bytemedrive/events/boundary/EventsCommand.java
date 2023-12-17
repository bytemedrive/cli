package com.bytemedrive.events.boundary;

import com.bytemedrive.ByteMeCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


@CommandLine.Command(name = "events", description = "Get all user events")
public class EventsCommand extends ByteMeCommand {

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output file")
    String outputPath;

    @Inject
    Logger log;

    @Inject
    EventsFacade facade;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void run() {
        try {
            var events = facade.getEvents(username, password);
            log.infof("Events count: %s", events.size());
            if (outputPath != null) {
                Files.writeString(Path.of(outputPath), objectMapper.writeValueAsString(events), StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                System.out.println(objectMapper.writeValueAsString(events));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
