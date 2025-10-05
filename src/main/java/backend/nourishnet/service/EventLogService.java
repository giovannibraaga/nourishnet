package backend.nourishnet.service;

import backend.nourishnet.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository repo;

    public List<EventLogRepository.EventRow> pageByDonation(Long donationId, Integer page, Integer size) {
        var pageable = (page == null || size == null)
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return repo.pageByDonation(donationId, pageable).getContent();
    }

    public long countByDonation(Long donationId) {
        return repo.countByDonation(donationId);
    }
}
