package dpp.reporting;

public class Progress {

	public static void reportProgress(int approxCompletePercent, boolean complete) {
		if (complete) {
			System.out.println("\rCompleted : 100%");
		} else {
			System.out.print("\rCompleted : " + approxCompletePercent + "%");
		}
	}

}
