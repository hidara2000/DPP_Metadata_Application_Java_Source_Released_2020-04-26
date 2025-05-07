package dpp.bmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dpp.App;

// Note that if you CTRL-C the main app then this will still be running on Windows, see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4717969

public class CommandLineExec {
	public static final long NO_TIMEOUT = 0L;
	public static final long DEFAULT_INFO_TIMEOUT = 5000L;
	public static final long DEFAULT_VALIDATE_TIMEOUT = 10000L;
	public static final long DEFAULT_TRANSWRAP_TIMEOUT = 60 * 60 * 1000;
	private static final String STOP_FILE = "stoptranswrap";

	private static final Logger LOGGER = Logger.getLogger(CommandLineExec.class);

	public StreamWrapper getStreamWrapper(InputStream is, String type) {
		return new StreamWrapper(is, type);
	}

	private class StreamWrapper extends Thread {
		private InputStream is = null;
		@SuppressWarnings("unused")
		private String type = null;
		private final List<String> message = new ArrayList<String>();

		StreamWrapper(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}

		public List<String> getMessage() {
			return message;
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, App.UTF_8_ENCODING_NAME));
				String line = null;
				while ((line = br.readLine()) != null) {
					message.add(line.trim());
				}
			} catch (IOException ioe) {
				LOGGER.error("caught exception " + ioe);
			}
		}
	}

	public static CommandLineResult runCommand(List<String> commands, long timeOutInMillis) {

		return runCommand(commands, null, timeOutInMillis);
	}

	public static CommandLineResult runCommand(List<String> commands, File stopFileDir, long timeOutInMillis) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Executing command : " + getCommandLine(commands));
		}

		boolean commandRanOK;
		CommandLineExec commandLineExec = new CommandLineExec();
		StreamWrapper error, output;
		CommandLineResult commandLineResult = new CommandLineResult();

		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			Process proc = pb.start();
			error = commandLineExec.getStreamWrapper(proc.getErrorStream(), "ERROR");
			output = commandLineExec.getStreamWrapper(proc.getInputStream(), "OUTPUT");
			int exitVal = 0;

			error.start();
			output.start();

			/*
			 * To allow front-end to kill off a transwrap process it writes a "stop file" to the working directory.
			 * 
			 * We periodically check for this file and if we find it we kill the current process.
			 */
			boolean completed = false;
			boolean stopFileExists = false;
			File stopFile = getStopFile(stopFileDir);

			while (!completed && !stopFileExists) {
				try {
					exitVal = proc.exitValue();
					completed = true;
				} catch (IllegalThreadStateException e) {
					// Process still running, ignore and continue looping
				}

				if (!completed) {
					Thread.sleep(1000);
					stopFileExists = stopFileExists(stopFile);
				}
			}

			if (stopFileExists) {
				LOGGER.info(String.format("Forcibly terminating process %s as stop file found", commands.get(0)));
				proc.destroy();
				stopFile.deleteOnExit();
				commandLineResult.setStopFileAborted(true);

				exitVal = -1;
			}

			// Wait for STDOUT/STDERR threads to terminate
			error.join(timeOutInMillis);
			output.join(timeOutInMillis);

			commandLineResult.setExitVal(exitVal);
			commandLineResult.setStdOutput(output.getMessage());
			commandLineResult.setStdError(error.getMessage());
			commandRanOK = exitVal == 0; // exit 0 means ran successfully.
		} catch (IOException ioe) {
			commandRanOK = false;
			LOGGER.error(ioe);
			ioe.printStackTrace();
		} catch (InterruptedException ie) {
			commandRanOK = false;
			LOGGER.error(ie);
			ie.printStackTrace();
		}
		commandLineResult.setSuccess(commandRanOK);
		LOGGER.debug(commandLineResult);
		return commandLineResult;
	}

	private static boolean stopFileExists(File stopFile) {

		boolean stopFileExists = false;
		if (stopFile != null) {
			stopFileExists = stopFile.exists();
		}

		return stopFileExists;
	}

	private static File getStopFile(File stopFileDir) {
		File stopFile = null;
		if (stopFileDir != null) {
			stopFile = new File(stopFileDir, STOP_FILE);
		}

		return stopFile;
	}

	private static String getCommandLine(List<String> commands) {

		StringBuilder commandLine = new StringBuilder();

		for (String command : commands) {

			commandLine.append(command);
			commandLine.append(" ");
		}

		return commandLine.toString();
	}

}
