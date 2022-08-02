package f1digitaltwin;

public class Time {
    private int milliseconds;
    private int minutes;
    private int seconds;

    /**
     * Constructor starting the time at 0
     */
    public Time() {
        minutes = 0;
        seconds = 0;
        milliseconds = 0;
    }

    /**
     * Constructor for a specified time
     *
     * @param minutes      Minutes
     * @param seconds      Seconds
     * @param milliseconds Milliseconds
     */
    public Time(int minutes, int seconds, int milliseconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
        timeMath();
    }

    /**
     * Constructor for a time using a String
     *
     * @param time Minutes:Seconds,Milliseconds
     */
    public Time(String time) {
        String[] minutesAndRest = time.split(":");
        String[] secondsAndRest = minutesAndRest[1].split(",");
        minutes = Integer.parseInt(minutesAndRest[0]);
        seconds = Integer.parseInt(secondsAndRest[0]);
        milliseconds = Integer.parseInt(secondsAndRest[1]);
        timeMath();
    }

    /**
     * @param millisecs Amount of milliseconds to add
     */
    public void addMilliseconds(int millisecs) {
        milliseconds += millisecs;
        timeMath();
    }

    /**
     * @param min Amount of minutes to add
     */
    public void addMinutes(int min) {
        minutes += min;
    }

    /**
     * @param secs Amount of seconds to add
     */
    public void addSeconds(int secs) {
        seconds += secs;
        timeMath();
    }

    /**
     * @param time Time object to add
     */
    public void addTime(Time time) {
        minutes += time.getMinutes();
        seconds += time.getSeconds();
        milliseconds += time.getMilliseconds();
        timeMath();
    }

    /**
     * @param minutes      to add
     * @param seconds      to add
     * @param milliseconds to add
     */
    public void addTime(int minutes, int seconds, int milliseconds) {
        this.minutes += minutes;
        this.seconds += seconds;
        this.milliseconds += milliseconds;
        timeMath();
    }

    /**
     * @return Milliseconds
     */
    public int getMilliseconds() {
        return milliseconds;
    }

    /**
     * @return Minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @return Seconds
     */
    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return minutes + ":" + String.format("%02d", seconds) + "," + String.format("%03d", milliseconds);
    }

    /**
     * Method to correctly apply the carry over
     */
    private void timeMath() {
        if (milliseconds < 0 || milliseconds >= 1000) {
            int temp = milliseconds / 1000;
            seconds += temp;
            milliseconds = Math.floorMod(milliseconds, 1000);
        }
        if (seconds < 0 || seconds >= 60) {
            int temp = seconds / 60;
            minutes += temp;
            seconds = Math.floorMod(seconds, 60);
        }

    }
}
