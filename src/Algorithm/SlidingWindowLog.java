package Algorithm;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements the allow function to check whether a particular request can be
 * processed or not using Sliding Window Log Algorithm.
 */
public class SlidingWindowLog implements IRateLimiter {

	private final Queue<Long> logs;
	private final int maxRequestPerSecond;

	public SlidingWindowLog(int maxRequestPerSecond) {
		this.logs = new LinkedList<>();
		this.maxRequestPerSecond = maxRequestPerSecond;
	}

	@Override
	public boolean allow() {
		long currTime = System.currentTimeMillis();
		long boundary = currTime - 1000;
		synchronized (logs) {
			while (!logs.isEmpty() && logs.element() <= boundary) {
				logs.poll();
			}
			logs.add(currTime);
			return logs.size() <= maxRequestPerSecond;
		}
	}
}
