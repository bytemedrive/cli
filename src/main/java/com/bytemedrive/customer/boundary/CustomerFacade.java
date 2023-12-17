package com.bytemedrive.customer.boundary;

import com.bytemedrive.customer.control.CustomerConverter;
import com.bytemedrive.customer.entity.CustomerAggregate;
import com.bytemedrive.events.boundary.EventsFacade;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class CustomerFacade {

    @Inject
    EventsFacade facadeEvents;

    @Inject
    CustomerConverter converter;

    public CustomerAggregate getCustomer(String username, char[] password) {
        var events = facadeEvents.getEvents(username, password);
        return converter.createCustomer(events);
    }

}
