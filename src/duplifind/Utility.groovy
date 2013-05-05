package duplifind

class Utility {

	static File outfile

	static void clearLog() {
		outfile?.write("")
	}

	static void log(String message) {
		if (message!=null) {
			println message
			outfile?.append("${message}\n")
		}
	}
}
