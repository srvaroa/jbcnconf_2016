package com.github.srvaroa.log;

import org.slf4j.LoggerFactory;

import java.util.logging.*;

public class LoggingFacade {

    public interface ILogger {
        void log(String msg);
    }

    final static Logger jul = Logger.getLogger("jbcnconf-jul");
    static {
        // TODO change pattern
        ConsoleHandler h = new ConsoleHandler();
        h.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLoggerName() + " " + record.getMessage();
            }
        });
        jul.addHandler(h);
    }

    final static org.slf4j.Logger logback =
            LoggerFactory.getLogger("jbcnconf-logback");

    private static final ILogger _jul = msg -> jul.info(msg);
    private static final ILogger _logback = msg -> logback.info(msg);
    private static final ILogger _stdout = msg -> System.out.println(msg);
    private static final ILogger _devnull = msg -> {};

    public static ILogger get() {
        return _stdout;
    }

}
