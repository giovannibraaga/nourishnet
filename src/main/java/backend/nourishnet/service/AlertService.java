package backend.nourishnet.service;

import backend.nourishnet.domain.Alert;
import backend.nourishnet.repository.AlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository repo;
    private final JdbcTemplate jdbc;

    public List<Alert> search(String status, String severity, Long donationId,
                              Integer page, Integer size, String sort, String order) {
        var sortObj = AlertRepository.orderBy(sort, order);
        if (page == null || size == null) {
            var pageable = PageRequest.of(0, Integer.MAX_VALUE, sortObj);
            return repo.search(status, severity, donationId, pageable).getContent();
        }
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        Pageable pageable = PageRequest.of(p, s, sortObj);
        return repo.search(status, severity, donationId, pageable).getContent();
    }

    public long count(String status, String severity, Long donationId) {
        return repo.count(status, severity, donationId);
    }

    @Transactional
    public int closeOpenForDonation(Long donationId) {
        log.info("alert.close.for.donation donation={}", donationId);
        return jdbc.update(
                "update ALERT set STATUS='CLOSED', CLOSED_AT=SYSTIMESTAMP where DONATION_ID=? and STATUS='OPEN'",
                donationId
        );
    }
}