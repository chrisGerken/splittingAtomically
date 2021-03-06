package org.atomsg.util;

import java.util.Map;

public class TimerUtil {

	/*
	 * Sleep for the given number of miliseconds
	 */
	public static void sleep(long msec) {
		try { Thread.sleep(msec); } catch (Throwable t) {  }
	}
	
	public long getLong(Map config, String prop, long def) {
		Object value = config.get(prop);
		if (value==null) {
			return def;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof String) {
			try { return Long.parseLong((String)value); } catch (Throwable t) { }
		}
		return def;
	}
	
	public class Generator {
		private long start;
		private long last;
		private long every;
		private long count;
		public Generator(Map config, String component) {
			start = System.currentTimeMillis();
			last = start;
			every = getLong(config,"timer."+component+".every",10L);
			count = getLong(config,"timer."+component+".count",1L);
		}

		/* 
		 * Return the number of pending ticks since the last call
		 */
		public long pending() {
			long now = System.currentTimeMillis();
			long total = (now-start) / every;
			long prev = (last-start) / every;
			last = now;
			return (total - prev) * count;
		}
	}
		
	public class Sleeper {
		private long time;
		public Sleeper(Map config, String component) {
			time = getLong(config,"timer."+component+".time",10L);
		}

		/* 
		 * Return the number of pending ticks since the last call
		 */
		public void sleep() {
			TimerUtil.sleep(time);
		}
	}
}
