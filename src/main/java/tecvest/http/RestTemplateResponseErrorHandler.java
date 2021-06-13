package tecvest.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (
                response.getStatusCode().series() == CLIENT_ERROR
                        || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        InputStream bodyinputStream = response.getBody();
        String body = new BufferedReader(
                new InputStreamReader(bodyinputStream, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
        String errorMessage = extractEtemadError(body);
        log.error("error in http service call response code: '{}' and response code text : '{}' and response body : '{}'" + response.getStatusCode().value(), response.getStatusText(), body);
        if (response.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            throw new RuntimeException("");
        } else if (response.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            throw new RuntimeException(errorMessage);
        }
    }

    private String extractEtemadError(String body) {
        try {
            JsonNode json = new ObjectMapper().readTree(body);
            JsonNode errors = json.get("errors");
            JsonNode message = json.get("message");
            if (errors != null && errors.get(0) != null && !errors.get(0).asText().equals("null")) {
                return errors.get(0).asText();
            }
            if (message != null)
                return message.asText();
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
