//package utils;
//
//import mdp.ctp.State;
//import org.ehcache.CacheManager;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.CacheManagerBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.EntryUnit;
//import org.ehcache.config.units.MemoryUnit;
//import serilizers.StateSerilizer;
//
//public class LocalCacheManager {
//
//    static CacheManager cache;
//    static org.ehcache.Cache myCache;
//    //init cache
//    static {
//        ResourcePoolsBuilder rpb =  ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.MB)
//                .offheap(100, MemoryUnit.MB)
//                .disk(50000, MemoryUnit.MB);
//         cache = CacheManagerBuilder.newCacheManagerBuilder()
//                .withCache("preConfigured",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,rpb)
//                ).withSerializer(State.class,
//                         StateSerilizer.class).build(true); // .withSerializer(Person.class, PersonSerializer.class)
//        org.ehcache.Cache preConfigured
//                = cache.getCache("preConfigured", Long.class, String.class);
//
//        myCache = cache.createCache("myCache",
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
//                        rpb).build());
//    }
//    public static void storeStateInCache(mdp.generic.State stt){
//        //todo: put in method & run
//        myCache.put(stt.getId(),stt);
//
//        //cache.close();
//    }
//
//    public static mdp.generic.State getFromCache(String sttId) {
//        return (State)myCache.get(sttId);
//    }
//}
