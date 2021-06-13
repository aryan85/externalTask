package tecvest.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tecvest.http.HttpClient;
import tecvest.http.HttpConfig;

import java.util.*;

@Service
public class WorkflowComponent {

    private static final Logger log = LoggerFactory.getLogger(WorkflowComponent.class);

    @Value("classpath:helloWorld.bpmn")
    Resource workFlowFile;
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private HttpConfig httpConfig;
    @Autowired
    private CamundaConfig camundaConfig;

    public void deploy() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "multipart/form-data");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("deployment-name", "helloworldExample");
        parameters.put("enable-duplicate-filtering", "true");
        parameters.put("data", workFlowFile);
        httpClient.postFormData(camundaConfig.getUrl() + camundaConfig.getDeployService(), httpConfig, parameters, headers);
        log.info("deployment successfully done");
    }


    public void startProcess() {
        String processUrl = camundaConfig.getStartProcessService().replace("$key", camundaConfig.getProcessKey());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("businessKey", "helloworldBusinessKey");
        httpClient.postJason(camundaConfig.getUrl() + processUrl, httpConfig, parameters, null);
        log.info("process start response sent");
    }

    public List<Task> fetchAndLock() {
        JSONObject request = new JSONObject();
        request.put("workerId", camundaConfig.getWorkerId());
        request.put("maxTasks", camundaConfig.getMaxTasks());
        request.put("usePriority", camundaConfig.isUsePriority());
        List<Map<String, Object>> topics = Arrays.asList(Map.of("topicName", camundaConfig.getTopicName(), "lockDuration", camundaConfig.getLockDuration()));
        request.put("topics", topics);
        ResponseEntity<String> response = httpClient.postJason(camundaConfig.getUrl() + camundaConfig.getFetchAndLockService(), httpConfig, request.toString(), null);
        JsonNode jsonResponse = null;
        try {
            jsonResponse = new ObjectMapper().readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.info("error in parsing response json", e);
            throw new RuntimeException("error in parsing response json");
        }
        List<Task> tasks = new ArrayList<>();
        if (jsonResponse != null) {
            for (JsonNode jsonNode : jsonResponse) {
                tasks.add(new Task(jsonNode.get("id").asText()));
            }
        }
        return tasks;
    }

    public void complete(String taskId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("workerId", camundaConfig.getWorkerId());
        httpClient.postJason(camundaConfig.getUrl() + camundaConfig.getCompleteTaskService().replace("$taskId", taskId), httpConfig, parameters, null);
    }
}
