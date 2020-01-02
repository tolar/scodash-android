package com.scodash.android.services.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class CustomDateTimeDeserializer extends StdDeserializer<DateTime> {

    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();

    public CustomDateTimeDeserializer() {
        this(null);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String dateTime = jsonParser.getText();
        try {
            return formatter.parseDateTime(dateTime);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public CustomDateTimeDeserializer(Class<DateTime> t) {
        super(t);
    }




}

