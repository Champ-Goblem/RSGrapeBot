package stumpy3toes.api.util;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class RuntimeTracker {
    private final CopyOnWriteArrayList<Long> runtimes = new CopyOnWriteArrayList<Long>();
    private final AtomicLong startTime = new AtomicLong(-1);

    public void start() {
        if (!isRunning()) {
            startTime.set(System.currentTimeMillis());
        }
    }

    public void stop() {
        if (isRunning()) {
            runtimes.add(currentRuntime());
            startTime.set(-1);
        }
    }

    public long currentRuntime() {
        if (isRunning()) {
            return System.currentTimeMillis() - startTime.get();
        }
        return 0;
    }

    public String currentRuntimeStr(boolean ms) {
        return Utils.formatTime(currentRuntime(), ms);
    }

    public boolean isRunning() {
        return startTime.get() != -1;
    }

    public int iterations() {
        return runtimes.size();
    }

    public String iterationsStr() {
        return Utils.formatNumber(iterations());
    }

    public long lowest() {
        if (runtimes.size() == 0) {
            return 0;
        }
        return Collections.min(runtimes);
    }

    public String lowestStr(boolean ms) {
        return Utils.formatTime(lowest(), ms);
    }

    public long highest() {
        if (runtimes.size() == 0) {
            return 0;
        }
        return Collections.max(runtimes);
    }

    public String highestStr(boolean ms) {
        return Utils.formatTime(highest(), ms);
    }

    public long total() {
        long total = 0;
        for (long time : runtimes) {
            total += time;
        }
        return total;
    }

    public String totalStr(boolean ms) {
        return Utils.formatTime(total(), ms);
    }

    public long average() {
        if (runtimes.size() == 0) {
            return 0;
        }
        return total() / runtimes.size();
    }

    public String averageStr(boolean ms) {
        return Utils.formatTime(average(), ms);
    }

    public String toString(boolean ms) {
        return "[" + iterationsStr() + "] " + lowestStr(ms) + " - " + highestStr(ms)
                + " (" + averageStr(ms) + " | " + totalStr(ms) + ")";
    }

    @Override
    public String toString() {
        return toString(true);
    }
}
