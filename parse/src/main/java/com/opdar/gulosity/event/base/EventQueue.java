package com.opdar.gulosity.event.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shey on 2016/8/21.
 */
public class EventQueue implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
    private Handler handler = new Handler();
    private AtomicBoolean running = new AtomicBoolean();
    private static EventQueue eventQueue;

    public static EventQueue getInstance() {
        if(eventQueue == null){
            eventQueue = new EventQueue();
            new Thread(eventQueue).start();
        }
        return eventQueue;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        logger.info("start event manager.");
        if (running.compareAndSet(false, true)) {
            while (true) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (running.get()) {
                        new Thread(new EventDo(handler)).start();
                    }
                }
            }
        }
    }

    public void stop() {
        running.getAndSet(false);
    }

    public static class Handler {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        public void error(Event event, Exception e) {
            logger.debug(event + " error");
        }

        public void success(Event event) {
            logger.debug(event + " success");
        }

        public void complate(Event event) {
            logger.debug(event + " complate");
        }
    }

    class EventDo implements Runnable {
        private Handler handler;

        public EventDo(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            Event event = queue.poll();
            try {
                event.doing();
                handler.success(event);
            } catch (Exception e) {
                handler.error(event, e);
            } finally {
                handler.complate(event);
            }
        }
    }

    public void addEvent(Event event) {
        while (!running.get()){}
        queue.add(event);
        synchronized (this) {
            this.notify();
        }
    }
}
