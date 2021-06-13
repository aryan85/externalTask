package tecvest.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Component
public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);


    private RestTemplate getRestTemplate(HttpConfig httpConfig) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(httpConfig.getConnectionTimeout());
        clientHttpRequestFactory.setReadTimeout(httpConfig.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    public ResponseEntity<String> postFormData(String url, HttpConfig httpConfig, Map<String, Object> parameters, Map<String, String> headers) throws RuntimeException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (headers != null) {
            for (String s : headers.keySet()) {
                httpHeaders.set(s, headers.get(s));
            }
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (String s : parameters.keySet()) {
            map.put(s, Collections.singletonList(parameters.get(s)));
        }
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, httpHeaders);
        return postForEntity(url, httpConfig, request);
    }

    public ResponseEntity<String> postJason(String url, HttpConfig httpConfig, Map<String, Object> inputJason, Map<String, String> headerParameters) {
        HttpHeaders headers = getJsonHeader(headerParameters);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(inputJason, headers);
        return postForEntity(url, httpConfig, request);
    }

    public ResponseEntity<String> postJason(String url, HttpConfig httpConfig, String inputJason, Map<String, String> headerParameters) {
        HttpHeaders headers = getJsonHeader(headerParameters);
        HttpEntity<String> request = new HttpEntity<>(inputJason, headers);
        return postForEntity(url, httpConfig, request);
    }

    private HttpHeaders getJsonHeader(Map<String, String> headerParameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (headerParameters != null && !headerParameters.isEmpty()) {
            for (String s : headerParameters.keySet()) {
                headers.set(s, headerParameters.get(s));
            }
        }
        return headers;
    }

    private <T> ResponseEntity<String> postForEntity(String url, HttpConfig httpConfig, HttpEntity<T> request) {
        log.info("post request :'{}' for url : '{}'", request, url);
        try {
            RestTemplate restTemplate = getRestTemplate(httpConfig);
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, request, String.class);
            log.info("post service call response : '{}' for service : '{}'", stringResponseEntity, url);
            return stringResponseEntity;
        } catch (ResourceAccessException e) {
            log.error("timeout in post url data", e);
            throw new RuntimeException("timeout in post url data");
        } catch (RestClientException e) {
            log.error("error in post url data", e);
            throw new RuntimeException("error in post url data");
        }
    }

    public <T> ResponseEntity<T> getRequest(String url, HttpConfig httpConfig, Map<String, String> queryParameters, Map<String, String> headerParameters, Class<T> responseType) throws RestClientException {
        try {
            RestTemplate restTemplate = getRestTemplate(httpConfig);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
            if (queryParameters != null) {
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                for (String s : queryParameters.keySet()) {
                    map.put(s, Collections.singletonList(queryParameters.get(s)));
                }
                uriBuilder.queryParams(map);
            }
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            if (headerParameters != null && !headerParameters.isEmpty()) {
                for (String s : headerParameters.keySet()) {
                    map.put(s, Collections.singletonList(headerParameters.get(s)));
                }
            }
            HttpEntity<String> request = new HttpEntity<>(null, map);
            log.info("get url call : {}", uriBuilder.build().toUriString());
            ResponseEntity<T> forEntity = restTemplate.exchange(uriBuilder.build().toUriString(), HttpMethod.GET, request, responseType);
            log.info("get call response : {}", forEntity.getBody());
            return forEntity;
        } catch (ResourceAccessException e) {
            log.error("timeout in post url data", e);
            throw new RuntimeException("timeout in post url data");
        } catch (RestClientException e) {
            log.error("error in get url data", e);
            throw new RuntimeException("error in get url data");
        }
    }
}
