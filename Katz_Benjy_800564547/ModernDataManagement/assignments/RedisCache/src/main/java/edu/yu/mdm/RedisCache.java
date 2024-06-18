package edu.yu.mdm;

import redis.clients.jedis.Jedis;
import java.util.*;
import edu.yu.mdm.RedisCacheBase;
public class RedisCache extends RedisCacheBase{
    public RedisCache(Jedis conn) {
        super(conn);
    }
    //conn.set
    //conn.rpush
    public String checkCookie(String cookie){
        String user = conn.hget(cookie, "user");
        if (user == null) {
            return null;
        }
        // update the timestamp of the cookie
        //conn.hset(cookie, "timeAccessed", System.currentTimeMillis());
        //conn.zadd("keySet", System.currentTimeMillis(), cookie);
        return user;
    }

    public void updateCookie(String cookie, String user, String item) {
        if(cookie == null||cookie.equals("")){throw new IllegalArgumentException();}
        if(user ==null || user.equals("")){throw new IllegalArgumentException();}
        // update the cookie and item to the hash table
        conn.zadd("keySet", System.nanoTime(), cookie);
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("user", user);
        hash.put("timeAccessed", ""+System.nanoTime());
        String currentItems = conn.hgetAll(cookie).get("items");
        if (currentItems == null){
            currentItems = "";
        }
        
        
        if(item == null||currentItems.contains(item)){
            hash.put("items", currentItems);
        }
        if(getItems(cookie).length >= 25){
            currentItems = currentItems.substring(currentItems.indexOf(":::")+3);
        }
        if(item!=null && !currentItems.equals("")&&!currentItems.contains(item)){
            hash.put("items", currentItems+":::"+item);
        }
        //in the first item
        else if(item!=null){
            hash.put("items", item);
        }
        

        conn.hset(cookie, hash);
    }

    public String[] getItems(String cookie) {
        // get the list of viewed items and sort by time
        //conn.hset(cookie, "timeAccessed", System.currentTimeMillis());
        //conn.zadd("keySet", System.currentTimeMillis(), cookie);
        if(conn.hget(cookie, "items") == null||conn.hget(cookie, "items").equals("")){
            return new String[0];
        }
        
        return conn.hget(cookie, "items").split(":::");
    }

    public String[] getCookies() {
        // get the list of cookies sorted by time
        List<String> cooks = conn.zrange("keySet", 0, -1);
        int index = 0;
        String cookieArray[] = new String[cooks.size()];
        for(String cook:cooks){
            cookieArray[index] = cook;
            index++;
        }
        return cookieArray;
    }

    public static class CleanerThread extends CleanerThreadBase {
        public CleanerThread(final int limit) {
            super(limit);
        }

        public Integer call() {
            // get the size of recent cookies list
            final Jedis jed = new Jedis();
            jed.select(DB_INDEX);
            Long size = jed.zcard("keySet");
            if (size <= limit) {
                jed.close();
                return 0;
            }
            // remove the oldest cookies and associated items
            long endIndex = size - limit;
            String[] cookies = jed.zrange("keySet", 0, endIndex - 1).toArray(new String[0]);
            for(String cookie: cookies){
                jed.del(cookie);
            }
            
            jed.zrem("keySet", cookies);
            jed.close();
            return cookies.length;
        }
    }

}