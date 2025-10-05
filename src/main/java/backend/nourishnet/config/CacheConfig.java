package backend.nourishnet.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        var mgr = new CaffeineCacheManager(
                "alerts_search", "alerts_count",
                "feed_open", "feed_count",
                "matches_by_ngo", "matches_count_ngo",
                "matches_by_donation", "matches_count_donation",
                "events_by_donation", "events_count_donation"
        );
        mgr.setCaffeine(caffeine);
        return mgr;
    }
}
