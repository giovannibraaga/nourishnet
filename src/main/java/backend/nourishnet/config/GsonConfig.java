package backend.nourishnet.config;


import backend.nourishnet.adapter.InstantAdapter;
import backend.nourishnet.adapter.LocalDateAdapter;
import backend.nourishnet.adapter.LocalDateTimeAdapter;
import backend.nourishnet.adapter.OffsetDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        var conv = new GsonHttpMessageConverter();
        conv.setGson(gson);
        conv.setSupportedMediaTypes(List.of(org.springframework.http.MediaType.APPLICATION_JSON));
        return conv;
    }
}
