package com.scodash.android.services.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class CustomDateTimeSerializer extends StdSerializer<DateTime> {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'Z");

    public CustomDateTimeSerializer() {
        this(null);
    }

    public CustomDateTimeSerializer(Class<DateTime> t) {
        super(t);
    }

    @Override
    public void serialize
            (DateTime value, JsonGenerator gen, SerializerProvider serializerProvider)
            throws IOException {
        gen.writeString(formatter.print(value));
    }
}

