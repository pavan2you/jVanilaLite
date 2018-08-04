/*
 * Copyright (C) 2015 - 2018 The jVanila Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * author - pavan.jvanila@gmail.com
 */
package com.jvanila.droid.eventbus;

import android.os.Handler;
import android.os.Message;

import com.jvanila.PlatformLocator;
import com.jvanila.core.eventbus.IEvent;
import com.jvanila.core.eventbus.IEventBus;
import com.jvanila.core.eventbus.IEventSubscriber;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventBus extends Handler implements IEventBus {

    private WeakHashMap<String, EventSubscriberNotifier> mEventMap;

    public EventBus() {
        mEventMap = new WeakHashMap<>();
    }

    @Override
    public synchronized void subscribe(String eventClassName, IEventSubscriber subscriber) {
        EventSubscriberNotifier eventSubscriberNotifier;
        if ((eventSubscriberNotifier = mEventMap.get(eventClassName)) == null) {
            eventSubscriberNotifier = new EventSubscriberNotifier(eventClassName);
            mEventMap.put(eventClassName, eventSubscriberNotifier);
        }
        eventSubscriberNotifier.subscribe(subscriber);
    }

    public synchronized void unsubscribe(String eventClassName, IEventSubscriber eventSubscriber) {
        if (mEventMap.size() == 0) {
            return;
        }

        EventSubscriberNotifier eventSubscriberNotifier;
        if ((eventSubscriberNotifier = mEventMap.get(eventClassName)) != null) {
            eventSubscriberNotifier.unsubscribe(eventSubscriber);
            if (eventSubscriberNotifier.isEmpty()) {
                mEventMap.remove(eventClassName);
            }
        }
    }

    @Override
    public synchronized void unsubscribeAllEventsOf(IEventSubscriber subscriber) {
        for (Iterator<String> it = mEventMap.keySet().iterator(); it.hasNext();) {
            String eventClassName = it.next();
            EventSubscriberNotifier eventSubscriberNotifier = mEventMap.get(eventClassName);
            if (eventSubscriberNotifier == null) {
                continue;
            }
            eventSubscriberNotifier.unsubscribe(subscriber);
            if (eventSubscriberNotifier.isEmpty()) {
                mEventMap.remove(eventClassName);
            }
        }
    }

    public void publish(IEvent event) {
        if (event == null) {
            return;
        }

        Message msg = obtainMessage();
        msg.obj = event;
        sendMessage(msg);
    }

    public void handleMessage(Message msg) {
        publishInternal((IEvent) msg.obj);
    }

    private void publishInternal(IEvent event) {
        String eventClass = event.getClassName();
        if (PlatformLocator.getPlatform().getLogger().canLog()) {
            System.out.println("publishInternal : " + eventClass);
        }
        if (mEventMap.containsKey(eventClass)) {
            EventSubscriberNotifier eventSubscriberNotifier = mEventMap.get(eventClass);
            if (eventSubscriberNotifier != null) {
                eventSubscriberNotifier.notifyEvent(event);
            }
        }
    }

    public void flushBus() {
        mEventMap.clear();
    }

    private static class EventSubscriberNotifier {

        String eventClass;
        CopyOnWriteArrayList<WeakReference<IEventSubscriber>> subscribers;
        CopyOnWriteArrayList<WeakReference<IEventSubscriber>> unsubscribers;

        EventSubscriberNotifier(String eventClass) {
            this.eventClass = eventClass;
            subscribers = new CopyOnWriteArrayList<>();
            unsubscribers = new CopyOnWriteArrayList<>();
        }

        void subscribe(IEventSubscriber subscriber) {
            boolean found = false;
            for (WeakReference<IEventSubscriber> wr : subscribers) {
                IEventSubscriber es = wr.get();
                if (subscriber.equals(es)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                subscribers.add(new WeakReference<>(subscriber));
            }
        }

        void unsubscribe(IEventSubscriber subscriber) {
            for (WeakReference<IEventSubscriber> wr : subscribers) {
                IEventSubscriber es = wr.get();
                if (subscriber.equals(es)) {
                    if (!unsubscribers.contains(wr)) {
                        unsubscribers.add(wr);
                    }
                    break;
                }
            }

            removeUnsubscribers();
        }

        void notifyEvent(IEvent event) {
            removeUnsubscribers();
            for (WeakReference<IEventSubscriber> wr : subscribers) {
                IEventSubscriber subscriber = wr.get();
                if (subscriber != null) {
                    subscriber.onEvent(event);
                }
            }
        }

        private void removeUnsubscribers() {
            if (unsubscribers.size() > 0) {
                for (WeakReference<IEventSubscriber> wr : unsubscribers) {
                    if (subscribers.contains(wr)) {
                        subscribers.remove(wr);
                    }
                }

                unsubscribers.clear();
            }
        }

        boolean isEmpty() {
            return subscribers.size() == 0;
        }
    }

}
