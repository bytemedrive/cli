package com.bytemedrive.customer.control;

import com.bytemedrive.customer.entity.CustomerAggregate;


public interface EventApplication {

    void applyEvent(CustomerAggregate customer);

}
