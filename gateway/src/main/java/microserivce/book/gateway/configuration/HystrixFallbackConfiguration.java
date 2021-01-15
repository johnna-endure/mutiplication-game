package microserivce.book.gateway.configuration;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class HystrixFallbackConfiguration {

    @Bean
    public FallbackProvider fallbackProvider() {
        return new FallbackProvider() {
            @Override
            public String getRoute() {
                return "multiplication"; // serviceId
            }

            @Override
            public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
                return new ClientHttpResponse() {

                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setAccessControlAllowCredentials(true);
                        headers.setAccessControlAllowOrigin("*");
                        return headers;
                    }

                    @Override
                    public InputStream getBody() throws IOException {
                        return new ByteArrayInputStream("서비스가 중단되었습니다.".getBytes(StandardCharsets.UTF_8));
                    }

                    @Override
                    public HttpStatus getStatusCode() throws IOException {
                        return HttpStatus.OK;
                    }

                    @Override
                    public int getRawStatusCode() throws IOException {
                        return HttpStatus.OK.value();
                    }

                    @Override
                    public String getStatusText() throws IOException {
                        return HttpStatus.OK.toString();
                    }

                    @Override
                    public void close() { }
                };
            }
        };
    }
}
