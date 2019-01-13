import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap that supports TTL-based entry expiry
 *
 * @author Mohammed Shaikh
 */
public class WeakConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private Map<K, TimeEntry> timeMap = new ConcurrentHashMap<>();
    private long defaultExpiry = 1000; // default value
    private long minExpiry = defaultExpiry;

    public WeakConcurrentHashMap() {
        init();
    }

    public WeakConcurrentHashMap(int defaultExpiry) {
        this.defaultExpiry = defaultExpiry;
        this.minExpiry = defaultExpiry;
        init();
    }

    void init() {
        new CleanerThread().start();
    }

    @Override
    public V get(Object key) {
        renewKey((K) key);
        return super.get(key);
    }

    public V put(K key, V value, long TTL) {
        timeMap.put(key, new TimeEntry(new Date().getTime(),TTL));
        this.minExpiry = Math.min(this.minExpiry, TTL);
        return super.put(key, value);
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, this.defaultExpiry);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet())
            put(key, m.get(key));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key))
            return put(key, value);
        else
            return get(key);
    }

    /**
     * Renews the timestamp for key
     */
    public boolean renewKey(K key) {
        if (!timeMap.containsKey(key))
            return false;
        timeMap.get(key).renew();
        return true;
    }


    class CleanerThread extends Thread {
        @Override
        public void run() {
            while(true) {
                cleanMap();
                try {
                    Thread.sleep(minExpiry / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void cleanMap() {
            long currentTime = new Date().getTime();
            for (K key: timeMap.keySet()) {
                if (currentTime > timeMap.get(key).getExpiry()) {
                    V value = remove(key);
                    timeMap.remove(key);
                    if (value != null)
                        System.out.println("Cleaning " + key + " : " + value);
                }
            }
        }
    }

    private class TimeEntry {
        private long timeStamp;
        private long TTL;

        public TimeEntry(long timeStamp, long TTL) {
            this.timeStamp = timeStamp; this.TTL = TTL;
        }

        public void renew() { this.timeStamp = new Date().getTime(); }

        public long getExpiry() {
            return this.timeStamp + this.TTL;
        }
    }
}
