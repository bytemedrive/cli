package com.bytemedrive;

import com.bytemedrive.download.boundary.DownloadCommand;
import com.bytemedrive.events.boundary.EventsCommand;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;


@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {EventsCommand.class, DownloadCommand.class})
public class EntryCommand {
}
