package tecvest.workflow;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("camunda")
public class CamundaConfig {
    private String url;
    private String processKey;
    private String workerId;
    private String topicName;
    private int lockDuration;
    private int maxTasks;
    private boolean usePriority;
    private String fetchAndLockService;
    private String deployService;
    private String startProcessService;
    private String completeTaskService;

    public String getCompleteTaskService() {
        return completeTaskService;
    }

    public void setCompleteTaskService(String completeTaskService) {
        this.completeTaskService = completeTaskService;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(int lockDuration) {
        this.lockDuration = lockDuration;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public void setMaxTasks(int maxTasks) {
        this.maxTasks = maxTasks;
    }

    public boolean isUsePriority() {
        return usePriority;
    }

    public void setUsePriority(boolean usePriority) {
        this.usePriority = usePriority;
    }

    public String getFetchAndLockService() {
        return fetchAndLockService;
    }

    public void setFetchAndLockService(String fetchAndLockService) {
        this.fetchAndLockService = fetchAndLockService;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getStartProcessService() {
        return startProcessService;
    }

    public void setStartProcessService(String startProcessService) {
        this.startProcessService = startProcessService;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeployService() {
        return deployService;
    }

    public void setDeployService(String deployService) {
        this.deployService = deployService;
    }
}
