package main;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import Algorithm.IRateLimiter;
import Algorithm.SlidingWindowLog;

/**
 * Creates a table which keeps a count that for each Api and a particular user
 * what is the maximum requests rate.
 */
public class Driver {

	static ArrayList<Integer> requestsRejected;

	public static void main(String[] args) throws Exception {
		try (Scanner scn = new Scanner(System.in)) {
			HashMap<User, Integer> maxRequestsMap = new HashMap<>();
			maxRequestsMap.put(new User("/api/v1/developers", "user1"), 100);
			maxRequestsMap.put(new User("/api/v1/developers", "user2"), 50);
			maxRequestsMap.put(new User("/api/v1/organizations", "user1"), 250);
			maxRequestsMap.put(new User("/api/v1/organizations", "user2"), 500);
			
			String api = scn.next();
			String userName = scn.next();

			User userToBeFind = new User(api, userName);

			if (maxRequestsMap.containsKey(userToBeFind)) {
				System.out.println("Your requests are being processed");
				IRateLimiter rateLimiter = new SlidingWindowLog(maxRequestsMap.get(userToBeFind));
				sendRequest(rateLimiter, 10, 1);
				sendRequest(rateLimiter, 200, 20);
				sendRequest(rateLimiter, 2000, 200);

			} else {
				throw new Exception("Invalid Api or user");
			}
		}
	}

	/**
	 * Processes the requests which are sent to particular Api and keeps a count
	 * that which requests are rejected and which are not rejected.
	 * 
	 * @param rateLimiter
	 * @param requestsTotalCount
	 * @param requestPerSec
	 * @throws Exception
	 */
	private static void sendRequest(IRateLimiter rateLimiter, int requestsTotalCount, int requestPerSec)
			throws Exception {
		requestsRejected = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		CountDownLatch doneSignal = new CountDownLatch(requestsTotalCount);
		for (int i = 0; i < requestsTotalCount; i++) {
			int j = i + 1;
			try {
				new Thread(() -> {
					if (!rateLimiter.allow()) {
						requestsRejected.add(j);
						try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					doneSignal.countDown();
				}).start();

				// To process the requests, the thread is caused to sleep
				// for a specific number of milliseconds.
				TimeUnit.MILLISECONDS.sleep(1000 / requestPerSec);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		double duration = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.println(requestsTotalCount + " requests processed in " + duration + " seconds. " + "Rate: "
				+ (double) requestsTotalCount / duration + " per second");

		if (requestsRejected.size() == 0) {
			System.out.println("There are no rejected requests");
		} else {
			System.out.println("Requests " + requestsRejected);
			System.out.println("are rejected.");
		}
	}
}
