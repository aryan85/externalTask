package tecvest.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tecvest.workflow.Task;
import tecvest.workflow.WorkflowComponent;

import java.util.List;

@Service
public class WorkerComponent {

    private static final Logger log = LoggerFactory.getLogger(WorkerComponent.class);

    @Autowired
    private WorkflowComponent workflowComponent;

    @Scheduled(fixedDelayString = "${pull.task.interval}")
    public void pullAndDoTask() {
        List<Task> tasks = workflowComponent.fetchAndLock();
        log.info("fetching {} tasks", tasks.size());
        for (Task task : tasks) {
            log.info("hello task : {}", task.getId());
            workflowComponent.complete(task.getId());
            log.info("task : {} completed successfully", task.getId());
        }
    }
}
