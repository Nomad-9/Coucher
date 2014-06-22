package com.ts.coucher.util;


public class Keys {

	public static final String DB_NAME = "testdb";
	public static final String KEY_MAIL = "email";
	public static final String KEY_REG = "registered";
	public static final String KEY_SCORES = "scores";
	
	public enum Replica {PUSH, PULL};
	public enum Span {CONTINUOUS, ONESHOT};
}
