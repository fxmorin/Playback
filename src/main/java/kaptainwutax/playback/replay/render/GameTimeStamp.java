package kaptainwutax.playback.replay.render;

public class GameTimeStamp implements Comparable<GameTimeStamp>{
    final long tick;
    final float tickDelta;
    public GameTimeStamp(long tick, float tickDelta) {
        this.tick = tick;
        this.tickDelta = tickDelta;
    }


    public int compareTo(GameTimeStamp other) {
        return compareTo(this.tick, this.tickDelta, other.tick, other.tickDelta);
    }

    /**
     * @return 1 if this is later, -1 if this is earlier, 0 if same time
     */
    public int compareTo(long tick, float tickDelta) {
        return compareTo(this.tick, this.tickDelta, tick, tickDelta);
    }

    /**
     * @param tick1 first timestamp
     * @param tickDelta1 first timestamp
     * @param tick2 second timestamp
     * @param tickDelta2 second timestamp
     * @return 1 if first time stamp is later, 0 if equal, -1 if first time stamp is earlier
     */
    public static int compareTo(long tick1, float tickDelta1, long tick2, float tickDelta2) {
        if (tick1 > tick2) return 1;
        if (tick1 < tick2) return -1;
        return Float.compare(tickDelta1, tickDelta2);
    }
}
