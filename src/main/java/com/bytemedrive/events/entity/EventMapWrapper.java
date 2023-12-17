package com.bytemedrive.events.entity;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;


/**
 * This is generic event wrapper structure common to all events.
 * @param id
 * @param eventType
 * @param publishedAt
 * @param data
 */
public record EventMapWrapper(UUID id, String eventType, ZonedDateTime publishedAt, Map<String, Object> data) {
}
