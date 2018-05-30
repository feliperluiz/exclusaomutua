package server;

import java.io.Serializable;

/**
 * This class represents a message to be exchanged between processes in distributed computing environment.
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the process.
	 */
	private final String processName;
	
	/**
	 * Logical clock of process.
	 */
	private final int logicalClock;

	/**
	 * Constructs an immutable packet with initialized values.
	 * @param processName
	 * @param logicalClock
	 */
	public Message(String processName, int logicalClock) {
		this.processName = processName;
		this.logicalClock = logicalClock;
	}

	/**
	 * This method gets the process name.
	 * @return
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * This method gets the logical clock value.
	 * @return
	 */
	public int getLogicalClock() {
		return logicalClock;
	}

	@Override
	public String toString() {
		return String.format("Message [processName=%s, logicalClock=%s]",
				processName, logicalClock);
	}
}