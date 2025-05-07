package dpp.reporting;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import dpp.bmx.CommandLineConstructor;
import dpp.bmx.CommandLineExec;
import dpp.util.FileUtils;

/**
 * Progress is reported in a thread by benchmarking the time taken to run an md5 calculation on a fixed size file and then working out the approximate time to
 * do the whole mxf file.
 */
public class Md5Progress extends Progress implements Runnable {

	private final static long BENCHMARK_FILE_SIZE = 10000000; // 10MB - big enough to give a reasonable idea of md5 calculation time
	private final static String BENCHMARK_FILE_NAME = "BENCHMARK_FILE_NAME.TMP";
	private final long sizeOfFileToHaveMd5Calculated;
	private final long expectedNumberOfMilliSeconds;
	private static final Logger LOGGER = Logger.getLogger(Md5Progress.class);

	private static long benchMarkMd5NumberOfMilliSeconds = -1;

	public Md5Progress(long expectedSize, String binPath) {
		super();
		this.sizeOfFileToHaveMd5Calculated = expectedSize;

		synchronized (Md5Progress.class) {
			if (benchMarkMd5NumberOfMilliSeconds == -1) {
				LOGGER.debug("Initializing Md5Progress");
				benchMarkMd5NumberOfMilliSeconds = benchMarkMd5Generation(binPath);
				LOGGER.debug("Initializing Md5Progress benchMarkMd5NumberOfMilliSeconds = " + benchMarkMd5NumberOfMilliSeconds);
			}
		}

		expectedNumberOfMilliSeconds = benchMarkMd5NumberOfMilliSeconds * sizeOfFileToHaveMd5Calculated / BENCHMARK_FILE_SIZE;
	}

	@Override
	public void run() {
		long startTimeMilliSeconds = Calendar.getInstance().getTimeInMillis();

		double lastApproxCompletePercent = -1.0;
		boolean complete = false;

		while (!complete) {
			long currTimeMilliSeconds = Calendar.getInstance().getTimeInMillis();
			long elapsedMilliSeconds = currTimeMilliSeconds - startTimeMilliSeconds;

			double approxCompletePercent = Math.min(99.0, 100.0 * ((double) elapsedMilliSeconds / (double) expectedNumberOfMilliSeconds));

			if ((int) approxCompletePercent != (int) lastApproxCompletePercent) {
				reportProgress((int) approxCompletePercent, false);
			}

			lastApproxCompletePercent = approxCompletePercent;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				complete = true;
				LOGGER.debug("Caught interrupt - marking md5 progress as complete");
			}
		}

		reportProgress(100, true);

	}

	/**
	 * Creates a small test file and runs md5 on it to get a benchmark for how long it takes.
	 * 
	 * @return
	 */
	private static long benchMarkMd5Generation(String binPath) {
		CommandLineConstructor commandLineConstructor = new CommandLineConstructor(binPath);

		FileUtils.createTempFile(BENCHMARK_FILE_NAME, BENCHMARK_FILE_SIZE);

		long startTimeMilliSeconds = Calendar.getInstance().getTimeInMillis();

		List<String> commands = commandLineConstructor.createMd5InfoCommandLine(BENCHMARK_FILE_NAME);
		CommandLineExec.runCommand(commands, CommandLineExec.DEFAULT_INFO_TIMEOUT);

		long finishTimeMilliSeconds = Calendar.getInstance().getTimeInMillis();

		FileUtils.deleteFile(BENCHMARK_FILE_NAME);

		return finishTimeMilliSeconds - startTimeMilliSeconds;

	}

	protected long getExpectedNumberOfMilliSeconds() {
		return expectedNumberOfMilliSeconds;
	}
}
