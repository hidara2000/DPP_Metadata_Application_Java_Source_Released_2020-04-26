package dpp.util;

public final class Timecode {

	private static final String FRAME_SEPARATOR_NO_DROP_FRAMES = ":";

	private final Long frames;
	private final Long seconds;
	private final Long minutes;
	private final Long hours;
	private final static Double UK_DPP_FRAME_RATE = 25.0; // Always for UK DPP

	private Timecode(Long hours, Long minutes, Long seconds, Long frames) {
		this.frames = frames;
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
	}

	private Timecode(String timeCode) {
		if (timeCode == null) {
			this.frames = 0L;
			this.seconds = 0L;
			this.minutes = 0L;
			this.hours = 0L;
			return;
		}
		String strHours = timeCode.substring(0, 2);
		String strMinutes = timeCode.substring(3, 5);
		String strSeconds = timeCode.substring(6, 8);
		String strFrames = timeCode.substring(9, 11);

		this.frames = Long.parseLong(strFrames);
		this.seconds = Long.parseLong(strSeconds);
		this.minutes = Long.parseLong(strMinutes);
		this.hours = Long.parseLong(strHours);
	}

	public static final Timecode getInstance(Long frameNumber) {
		Long frames = Math.round(Math.floor((frameNumber % UK_DPP_FRAME_RATE)));
		Long seconds = Math.round(Math.floor((frameNumber / UK_DPP_FRAME_RATE))) % 60;
		Long minutes = (Math.round(Math.floor((frameNumber / UK_DPP_FRAME_RATE))) / 60) % 60;
		Long hours = ((Math.round(Math.floor((frameNumber / UK_DPP_FRAME_RATE))) / 60) / 60) % 24;

		return new Timecode(hours, minutes, seconds, frames);
	}

	public static final Timecode getInstance(String formattedTimecode) {
		return new Timecode(formattedTimecode);
	}

	/**
	 * Returns the timecode as a String. The format of the timecode is HH:MM:SS:FF
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String frameIndicator = FRAME_SEPARATOR_NO_DROP_FRAMES;
		String result = String.format("%02d:%02d:%02d%s%02d", hours, minutes, seconds, frameIndicator, frames);
		return result;
	}

	public Long getNumberOfFrames() {
		Double totalFrames = (double) frames;
		totalFrames += seconds * UK_DPP_FRAME_RATE;
		totalFrames += (minutes * 60) * UK_DPP_FRAME_RATE;
		totalFrames += (hours * 60 * 60) * UK_DPP_FRAME_RATE;

		return Math.round(totalFrames);
	}

	public Long getDurationInSeconds() {
		return (hours * 60L * 60L) + (minutes * 60L) + (seconds);
	}

	public Float getDurationInSecondsIncludingFrames() {
		return (hours * 60L * 60L) + (minutes * 60L) + (seconds) + (frames / 25.0f);
	}

	public Long getFrames() {
		return frames;
	}

	public Long getSeconds() {
		return seconds;
	}

	public Long getMinutes() {
		return minutes;
	}

	public Long getHours() {
		return hours;
	}

	/**
	 * Returns the difference in seconds between timeCode1 and timeCode2. Returns a float as also considers the frames - for an exact number of seconds the
	 * return value will be an integer
	 * 
	 * @return Will return a positive value if the second timecode is greater or equal to the first otherwise negative.
	 */
	public static float getDifferenceInSeconds(Timecode timecode1, Timecode timecode2) {
		return timecode2.getDurationInSecondsIncludingFrames() - timecode1.getDurationInSecondsIncludingFrames();

	}

	/**
	 * Returns the sum of two timecodes - typically this will be used for adding an initial start timecode and a duration timecode.
	 * 
	 */
	public static Timecode sum(Timecode timecode1, Timecode timecode2) {
		Long totalSumOfFrams = timecode1.getNumberOfFrames() + timecode2.getNumberOfFrames();
		return Timecode.getInstance(totalSumOfFrams);

	}

	public boolean after(Timecode timeCodeEndOfLastTrack) {
		return this.getNumberOfFrames() > (timeCodeEndOfLastTrack != null ? timeCodeEndOfLastTrack.getNumberOfFrames() : 0);
	}

}
