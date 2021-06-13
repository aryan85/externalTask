package tecvest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tecvest.workflow.WorkflowComponent;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Autowired
    private WorkflowComponent workflowComponent;

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @PostConstruct
    private void init() {
        log.info("deploying existing bpmn file");
        workflowComponent.deploy();
    }
}
