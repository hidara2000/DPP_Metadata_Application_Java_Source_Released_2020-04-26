package dpp.bmx;

import java.util.List;

public class CommandLineResult {
	private boolean success;
	private boolean stopFileAborted;
	private int exitVal;
	private List<String> stdOutput;
	private List<String> stdError;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public List<String> getStdOutput() {
		return stdOutput;
	}

	public void setStdOutput(final List<String> stdOutput) {
		this.stdOutput = stdOutput;
	}

	public List<String> getStdError() {
		return stdError;
	}

	public void setStdError(final List<String> stdError) {
		this.stdError = stdError;
	}

	public int getExitVal() {
		return exitVal;
	}

	public void setExitVal(final int exitVal) {
		this.exitVal = exitVal;
	}

	@Override
	public String toString() {
		return "CommandLineResult [success=" + success + ", exitVal=" + exitVal + "\nstdOutput=" + stdOutput + "\nstdError=" + stdError + "]";
	}

	public boolean isStopFileAborted() {
		return stopFileAborted;
	}

	public void setStopFileAborted(boolean stopFileAborted) {
		this.stopFileAborted = stopFileAborted;
	}

}
