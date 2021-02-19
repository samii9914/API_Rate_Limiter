package Algorithm;

/**
 * Contains allow function which is called to check that whether a particular
 * request can be processed or not.
 */
public interface IRateLimiter {
	boolean allow();
}
