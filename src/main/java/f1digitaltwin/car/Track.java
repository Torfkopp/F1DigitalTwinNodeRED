package f1digitaltwin.car;

import f1digitaltwin.Time;

/**
 * Class representing a track
 */
public final class Track {

    /**
     * Names of the available tracks
     */
    public enum Name {
        MONZA,
        MONACO
    }

    /**
     * @param track The track
     * @return Time the base time based on the 2022 qualifying average
     */
    public static Time getBaseTime(Name track) {
        switch (track) {
            case MONZA:
                return Monza.baseTime;
            case MONACO:
                return Monaco.baseTime;
        }
        return Monza.baseTime;
    }

    /**
     * @param track The track
     * @return int of how much of the lap is full throttle
     */
    public static int getFullThrottle(Name track) {
        switch (track) {
            case MONZA:
                return Monza.percentFullThrottle;
            case MONACO:
                return Monaco.percentFullThrottle;
        }
        return Monza.percentFullThrottle;
    }

    /**
     * @param track The track
     * @return Amount of laps to go
     */
    public static int getLaps(Name track) {
        switch (track) {
            case MONZA:
                return Monza.laps;
            case MONACO:
                return Monaco.laps;
        }
        return Monza.laps;
    }

    /**
     * @param track The track
     * @return Length of one lap
     */
    public static int getLength(Name track) {
        switch (track) {
            case MONZA:
                return Monza.length;
            case MONACO:
                return Monaco.length;
        }
        return Monza.length;
    }

    /**
     * @param track The track
     * @return If track is clockwise
     */
    public static boolean isClockwise(Name track) {
        switch (track) {
            case MONZA:
                return Monza.clockwise;
            case MONACO:
                return Monaco.clockwise;
        }
        return Monza.clockwise;
    }

    /**
     * Class representing Monza
     */
    private static final class Monza {

        private static final Time baseTime = new Time("1:22,000");
        private static final boolean clockwise = true;
        private static final int laps = 53;
        private static final int length = 5793;
        private static final int percentFullThrottle = 80;
    }

    /**
     * Class representing Monaco
     */
    private static final class Monaco {

        private static final Time baseTime = new Time("1:13,000");
        private static final boolean clockwise = true;
        private static final int laps = 78;
        private static final int length = 3337;
        private static final int percentFullThrottle = 59;
    }

    /* TEMPLATE FOR A TRACK CLASS
    private static final class  {

        private static final int laps = ;
        private static final int length = ;
        private static final int percentFullThrottle = ;
        private static final boolean clockwise = ;

        private static final Time baseTime = new Time("");
    }
     */
}
