package dpp.reporting;

import org.apache.log4j.Logger;

import dpp.util.FileUtils;

/**
 * Progress is reported in in a thread, for transwrapping we assume the transwrapped file will be approximately the same size as the original file and use this
 * to calculate the percentage complete.
 */
public class FileSizeProgress extends Progress implements Runnable {

	private final String fileName;
	private final long expectedSize;
	private static final Logger LOGGER = Logger.getLogger(FileSizeProgress.class);

	public FileSizeProgress(String fileName, long expectedSize) {
		super();
		this.fileName = fileName;
		this.expectedSize = expectedSize;
	}

	@Override
	public void run() {
		double lastApproxCompletePercent = -1.0;
		boolean complete = false;

		while (!complete) {
			long currentFileSize = FileUtils.getFileSize(fileName);
			double approxCompletePercent = Math.min(99.0, 100.0 * ((double) currentFileSize / (double) expectedSize));

			if ((int) approxCompletePercent != (int) lastApproxCompletePercent) {
				reportProgress((int) approxCompletePercent, false);
			}

			lastApproxCompletePercent = approxCompletePercent;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.debug("FileSizeProgress thread interrupted marking as complete.");
				complete = true;
			}
		}

		reportProgress(100, true);

	}

}
