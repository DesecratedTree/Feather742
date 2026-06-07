package com.feather.engine.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ActionQueue {

    private final LinkedList<QueueScript> scripts = new LinkedList<>();

    public void queue(QueueScript script) {
        scripts.add(script);
    }

    public void queue(QueueType type, Runnable action) {
        queue(type, action, 0);
    }

    public void queue(QueueType type, Runnable action, int delayTicks) {
        scripts.add(new QueueScript(type, action, delayTicks));
    }

    public void queueFirst(QueueType type, Runnable action) {
        scripts.addFirst(new QueueScript(type, action, 0));
    }

    public boolean hasType(QueueType type) {
        for (QueueScript script : scripts) {
            if (script.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public void process(boolean playerDelayed, boolean hasModalOpen, Runnable closeModalInterfaces) {
        if (scripts.isEmpty()) {
            return;
        }

        boolean hasStrong = hasType(QueueType.STRONG);
        if (hasStrong) {
            closeModalInterfaces.run();
            removeWeak();
        }

        for (QueueScript script : scripts) {
            script.tick();
        }

        while (!scripts.isEmpty()) {
            int processedCount = processOnce(playerDelayed, hasModalOpen, closeModalInterfaces);
            if (processedCount == 0) {
                break;
            }
        }
    }

    private int processOnce(boolean playerDelayed, boolean hasModalOpen, Runnable closeModalInterfaces) {
        int processedCount = 0;
        Iterator<QueueScript> it = scripts.iterator();
        while (it.hasNext()) {
            QueueScript script = it.next();

            if (script.getType().closesModalInterface()) {
                closeModalInterfaces.run();
            }

            if (script.canProcess(playerDelayed, hasModalOpen)) {
                script.execute();
                it.remove();
                processedCount++;
            }
        }
        return processedCount;
    }

    public void removeWeak() {
        scripts.removeIf(s -> s.getType() == QueueType.WEAK);
    }

    public void clear() {
        scripts.clear();
    }

    public void clearExcept(QueueType type) {
        scripts.removeIf(s -> s.getType() != type);
    }

    public boolean isEmpty() {
        return scripts.isEmpty();
    }

    public int size() {
        return scripts.size();
    }

    public boolean containsWeak() {
        return hasType(QueueType.WEAK);
    }
}
