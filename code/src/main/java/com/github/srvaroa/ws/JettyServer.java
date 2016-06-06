package com.github.srvaroa.ws;

import com.github.srvaroa.ws.model.Record;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class JettyServer {

    private final LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();

    private final Runnable runner = () -> {
        while (true) {
            try {
                String s = q.take();
                if (s == null)
                    continue;
                new Record(s);
            } catch (InterruptedException e) {
                System.err.println("Interrupted on queue.take");
                Thread.interrupted();
                break;
            }
        }
    };

    private final Handler h = new AbstractHandler() {
        @Override
        public void handle(String target,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response)
                throws IOException, ServletException {
            try {
                q.put(request.getRequestURI());
                response.setContentType("text/html; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_CREATED);
                baseRequest.setHandled(true);
            } catch (InterruptedException e) {
                System.err.println("Interrupted on queue.put");
                Thread.interrupted();
            }
        }
    };

    public static void main(String[] args) throws Exception {
        JettyServer parser = new JettyServer();
        Thread t = new Thread(parser.runner);
        t.setName("runner");
        t.setDaemon(true);
        t.start();

        Server server = new Server(8080);
        server.setHandler(parser.h);
        server.start();
        server.dumpStdErr();
        server.join();
    }
}
