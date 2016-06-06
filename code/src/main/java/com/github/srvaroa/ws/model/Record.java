package com.github.srvaroa.ws.model;

public class Record {

    public static final String SEPARATOR = "/";

    private final short version;
    private final long timestamp;
    private final String name;
    private final short clicks;

    public Record(String s) {
        String[] pieces = s.substring(1).split(SEPARATOR);
        if (pieces.length != 4) {
            throw new IllegalArgumentException(s);
        }
        version = Short.parseShort(pieces[0]);
        timestamp = Long.parseLong(pieces[1]);
        name = pieces[2];
        clicks = Short.parseShort(pieces[3]);
    }

    public short getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public short getClicks() {
        return clicks;
    }

}
