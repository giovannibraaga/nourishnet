package backend.nourishnet.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiryJob {
    private static final Logger log = LoggerFactory.getLogger(ExpiryJob.class);
    private final JdbcTemplate jdbc;
    private final boolean enabled;

    public ExpiryJob(JdbcTemplate jdbc, @Value("${nourish.jobs.expiry.enabled:true}") boolean enabled) {
        this.jdbc = jdbc;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${nourish.jobs.expiry.delay-ms:300000}")
    public void run() {
        if (!enabled) return;
        log.info("job.expiry.check.start");
        jdbc.execute("begin NOURISHNET_DEV.PR_CHECK_CRITICAL_EXPIRY; end;");
        log.info("job.expiry.check.end");
    }
}
