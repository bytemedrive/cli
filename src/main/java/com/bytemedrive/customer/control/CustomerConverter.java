package com.bytemedrive.customer.control;

import com.bytemedrive.customer.entity.CustomerAggregate;
import com.bytemedrive.customer.entity.EventFileThumbnailUploaded;
import com.bytemedrive.customer.entity.EventFileUploaded;
import com.bytemedrive.customer.entity.EventFolderCreated;
import com.bytemedrive.events.entity.EventMapWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ApplicationScoped
public class CustomerConverter {

    private final Map<String, Class<? extends EventApplication>> eventTypes = new HashMap<>();

    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        eventTypes.put(EventFolderCreated.NAME, EventFolderCreated.class);
        eventTypes.put(EventFileUploaded.NAME, EventFileUploaded.class);
        eventTypes.put(EventFileThumbnailUploaded.NAME, EventFileThumbnailUploaded.class);
    }


    public CustomerAggregate createCustomer(List<EventMapWrapper> events) {
        var customer = new CustomerAggregate();
        var supportedEvents = eventTypes.keySet();
        for (var event : events) {
            if (supportedEvents.contains(event.eventType())) {
                try {
                    var json = objectMapper.writeValueAsString(event.data());
                    objectMapper.readValue(json, eventTypes.get(event.eventType())).applyEvent(customer);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return customer;
    }

}
