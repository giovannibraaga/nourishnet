package backend.nourishnet.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FeedJdbcRepository {
    private final JdbcTemplate jdbc;
    public FeedJdbcRepository(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    public long countOpen() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM BI_MV_DONATION_FEED_SUMMARY WHERE STATUS='OPEN'", Long.class);
    }

    public List<FeedRow> findOpen(Integer offset, Integer limit, FeedSort sort, Boolean asc) {
        String orderBy = orderByFor(sort, asc);

        String base = """
      SELECT DONATION_ID, STATUS, ADDRESS_TEXT, GEO_LAT, GEO_LNG, EXPIRES_AT,
             ITEMS_COUNT, TOTAL_QTY_KG, EARLIEST_BEST_BEFORE, LAST_UPDATE_AT
      FROM BI_MV_DONATION_FEED_SUMMARY
      WHERE STATUS = 'OPEN'
      """;

        boolean paginar = offset != null && limit != null;
        String sql = paginar
                ? base + " ORDER BY " + orderBy + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"
                : base + " ORDER BY " + orderBy;

        return paginar
                ? jdbc.query(sql, this::map, offset, limit)
                : jdbc.query(sql, this::map);
    }

    private String orderByFor(FeedSort sort, Boolean asc) {
        FeedSort s = (sort == null ? FeedSort.URGENCY : sort);
        boolean a = (asc == null ? true : asc);
        return switch (s) {
            case UPDATED -> "LAST_UPDATE_AT " + (a ? "ASC" : "DESC");
            case EXPIRES -> "EXPIRES_AT " + (a ? "ASC" : "DESC") + " NULLS LAST";
            case URGENCY -> "EARLIEST_BEST_BEFORE NULLS LAST, EXPIRES_AT NULLS LAST, LAST_UPDATE_AT DESC";
        };
    }

    private FeedRow map(ResultSet rs, int rowNum) throws SQLException {
        return new FeedRow(
                rs.getLong("DONATION_ID"),
                rs.getString("STATUS"),
                rs.getString("ADDRESS_TEXT"),
                rs.getObject("GEO_LAT", Double.class),
                rs.getObject("GEO_LNG", Double.class),
                rs.getObject("EXPIRES_AT", java.time.OffsetDateTime.class),
                rs.getLong("ITEMS_COUNT"),
                rs.getObject("TOTAL_QTY_KG", Double.class),
                rs.getObject("EARLIEST_BEST_BEFORE", java.time.OffsetDateTime.class),
                rs.getObject("LAST_UPDATE_AT", java.time.OffsetDateTime.class)
        );
    }
}
