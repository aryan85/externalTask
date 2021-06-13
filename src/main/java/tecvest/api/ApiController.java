package tecvest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tecvest.workflow.WorkflowComponent;

@RestController
public class ApiController {

    @Autowired
    private WorkflowComponent workflowComponent;

    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    @ResponseBody
    public String helloworld() {
        workflowComponent.startProcess();
        return "ok";
    }
}
