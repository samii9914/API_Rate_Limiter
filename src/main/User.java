package main;

import java.util.*;

/**
 * Creates a user and contains the name and the Api which the user can call as
 * its property.
 */
public class User {
	private final String name;
	private final String api;

	User(String api, String name) {
		this.name = name;
		this.api = api;
	}

	public boolean equals(Object other) {
		User otherUser = (User) other;
		return this.api.equals(otherUser.api) && this.name.equals(otherUser.name);
	}

	public int hashCode() {
		return Objects.hash(this.api, this.name);
	}

	public String getName() {
		return this.name;
	}

	public String getApi() {
		return this.api;
	}
}
