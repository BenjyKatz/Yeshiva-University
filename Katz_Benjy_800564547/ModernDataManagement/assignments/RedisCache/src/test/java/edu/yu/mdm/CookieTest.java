package edu.yu.mdm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.junit.Test;

import edu.yu.mdm.RedisCacheBase.CleanerThreadBase;
import redis.clients.jedis.Jedis;

public class CookieTest {
	@Test
    public void test1()throws Exception{
		System.out.println("start test");
        final var j = new Jedis();
		j.flushAll();
		RedisCacheBase r = new RedisCache(j);

		r.updateCookie("cook1", "ben", "chair");

		System.out.println("in test");
		assertEquals("hey", "hey");
		assertEquals("ben", r.checkCookie("cook1"));
		r.updateCookie("cook1", "ben", "book");
		System.out.println("start of array");
		String[] its = r.getItems("cook1");
		for(String it: its){
        	System.out.println(it);
		}
		assertArrayEquals(new String[] { "chair", "book" }, r.getItems("cook1"));
		for(int i = 0; i<25; i++){
			r.updateCookie("cook1", "ben", "water"+i);
		}
		System.out.println("water test");
		its = r.getItems("cook1");
		for(String it: its){
        	System.out.println(it);
			//Should be 25 waters
		}
		r.updateCookie("cook2", "kobi", null);
		assertArrayEquals(new String[] { "cook1", "cook2"}, r.getCookies());
		System.out.println("null item test");
		its = r.getItems("cook2");
		for(String it: its){
        	System.out.println(it);
			//Should be nothing
		}

		CleanerThreadBase cleaner = new RedisCache.CleanerThread(50);
		try {
			assertEquals((Integer) 0, cleaner.call());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed");
			e.printStackTrace();
		}
		assertEquals("ben", r.checkCookie("cook1"));
		assertEquals("kobi", r.checkCookie("cook2"));
		System.out.println("nextCleaner");
		CleanerThreadBase nextCleaner = new RedisCache.CleanerThread(1);
		try {
			assertEquals((Integer) 1, nextCleaner.call());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed");
			e.printStackTrace();
		}
		assertNotEquals("ben", r.checkCookie("cook1"));
		assertEquals("kobi", r.checkCookie("cook2"));
		System.out.println("See no water");
		its = r.getItems("cook1");
		for(String it: its){
        	System.out.println(it);
			//Should be nothing
		}

		RedisCacheBase.CleanerThreadBase cleanerThread = new RedisCache.CleanerThread(4);
        Jedis jedis = new Jedis();
        RedisCache rd = new RedisCache(jedis);
        rd.updateCookie("cooks1", "ben1", "air");
        rd.updateCookie("cooks2", "ben2", "water");
        rd.updateCookie("cooks3", "ben3", "earth");
        rd.updateCookie("cooks4", "ben4", "fire");
        rd.updateCookie("cooks5", "ben5", "avatar");
        int removed = cleanerThread.call();
        assertEquals(removed, 1);
        assertArrayEquals(new String[]{"cooks2", "cooks3", "cooks4", "cooks5"}, (rd.getCookies()));
		
    }

}