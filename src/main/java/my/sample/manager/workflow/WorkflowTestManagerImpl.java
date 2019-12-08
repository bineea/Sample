package my.sample.manager.workflow;

import my.sample.common.tools.JsonTools;
import my.sample.manager.AbstractManager;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class WorkflowTestManagerImpl extends AbstractManager implements WorkflowTestManager {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private DynamicBpmnService dynamicBpmnService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private IdentityService identityService;

    @Override
    public void simpleProcessTest() {
//        try {
//            handleIdentity();
//            testDeploy();
//            queryProcessDefinitionTest();
//            startProcessInstanceTest();
//            claimAndCheckTask();
//            queryAndCompleteTask();
//            queryTask();
//
//            deleteDeployment();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 部署流程
     * <p>
     * <p>
     * StartEvent负责开始节点
     * UserTask负责任务节点
     * SequenceFlow负责连线
     * EndEvent负责结束节点
     * ExclusiveGateway负责条件判断
     */
    private void testDeploy() throws IOException {

        // Build up the model from scratch
        BpmnModel model = new BpmnModel();
        Process process = new Process();

        // 连接线：开始节点->节点0
        SequenceFlow flow0 = new SequenceFlow();
        flow0.setId("flow0");
        flow0.setName("开始节点->节点0");
        flow0.setSourceRef("start1");
        flow0.setTargetRef("serviceTask0");
        // 连接线：节点0->条件节点
        SequenceFlow flow11 = new SequenceFlow();
        flow11.setId("flow11");
        flow11.setName("节点0->条件节点");
        flow11.setSourceRef("serviceTask0");
        flow11.setTargetRef("check1");
        // 连接线：条件节点->节点1
        SequenceFlow flow1 = new SequenceFlow();
        flow1.setId("flow1");
        flow1.setName("条件节点->节点1");
        flow1.setConditionExpression("${MALE_RESULT=='NEUTER'}");
        flow1.setSourceRef("check1");
        flow1.setTargetRef("userTask1");
        // 连接线：节点1->并行节点1
        SequenceFlow flow2 = new SequenceFlow();
        flow2.setId("flow2");
        flow2.setName("节点1->并行节点1");
        flow2.setSourceRef("userTask1");
        flow2.setTargetRef("parallel1");
        // 连接线：节点5->结束节点
        SequenceFlow flow3 = new SequenceFlow();
        flow3.setId("flow3");
        flow3.setName("节点5->结束节点");
        flow3.setSourceRef("serviceTask1");
        flow3.setTargetRef("endEvent");
        // 连接线：条件节点->结束节点
        SequenceFlow flow4 = new SequenceFlow();
        flow4.setId("flow4");
        flow4.setName("条件节点->结束节点");
        flow4.setConditionExpression("${MALE_RESULT=='111NEUTER'}");
        flow4.setSourceRef("check1");
        flow4.setTargetRef("endEvent");
        // 连接线：并行节点1->节点2
        SequenceFlow flow5 = new SequenceFlow();
        flow5.setId("flow5");
        flow5.setName("并行节点1->节点2");
        flow5.setSourceRef("parallel1");
        flow5.setTargetRef("userTask2");
        // 连接线：并行节点1->节点3
        SequenceFlow flow6 = new SequenceFlow();
        flow6.setId("flow6");
        flow6.setName("并行节点1->节点3");
        flow6.setSourceRef("parallel1");
        flow6.setTargetRef("userTask3");
        // 连接线：节点2->并行节点2
        SequenceFlow flow7 = new SequenceFlow();
        flow7.setId("flow7");
        flow7.setName("节点2->并行节点2");
        flow7.setSourceRef("userTask2");
        flow7.setTargetRef("parallel2");
        // 连接线：节点3->并行节点2
        SequenceFlow flow8 = new SequenceFlow();
        flow8.setId("flow8");
        flow8.setName("节点3->并行节点2");
        flow8.setSourceRef("userTask3");
        flow8.setTargetRef("parallel2");
        // 连接线：并行节点2->节点4
        SequenceFlow flow9 = new SequenceFlow();
        flow9.setId("flow9");
        flow9.setName("并行节点2->节点4");
        flow9.setSourceRef("parallel2");
        flow9.setTargetRef("userTask4");
        // 连接线：节点4->节点5
        SequenceFlow flow10 = new SequenceFlow();
        flow10.setId("flow10");
        flow10.setName("节点4->节点5");
        flow10.setSourceRef("userTask4");
        flow10.setTargetRef("serviceTask1");
        // 开始节点的封装
        StartEvent start = new StartEvent();
        start.setName("开始节点");
        start.setId("start1");
        start.setOutgoingFlows(Arrays.asList(flow0));
        // JAVA服务节点的封装
        ServiceTask serviceTask0 = new ServiceTask();
        serviceTask0.setId("serviceTask0");
        serviceTask0.setName("节点0");
        serviceTask0.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        serviceTask0.setImplementation("${sampleServiceManagerImpl}");
        serviceTask0.setIncomingFlows(Arrays.asList(flow0));
        serviceTask0.setOutgoingFlows(Arrays.asList(flow11));
        // 条件节点的封装
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId("check1");
        exclusiveGateway.setName("条件校验节点");
        exclusiveGateway.setIncomingFlows(Arrays.asList(flow11));
        exclusiveGateway.setOutgoingFlows(Arrays.asList(flow1, flow4));
        FlowableListener gateway1Listener = new FlowableListener();
        gateway1Listener.setEvent(ExecutionListener.EVENTNAME_START);
        gateway1Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        gateway1Listener.setImplementation("${sample1ExecutionListenerManagerImpl}");

        FlowableListener gateway2Listener = new FlowableListener();
        gateway2Listener.setEvent(ExecutionListener.EVENTNAME_END);
        gateway2Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        gateway2Listener.setImplementation("${sample2ExecutionListenerManagerImpl}");

        exclusiveGateway.setExecutionListeners(Arrays.asList(gateway1Listener, gateway2Listener));

        // 节点1
        UserTask userTask1 = new UserTask();
        userTask1.setName("节点1");
        userTask1.setId("userTask1");
        userTask1.setIncomingFlows(Arrays.asList(flow1));
        userTask1.setOutgoingFlows(Arrays.asList(flow2));
        userTask1.setCandidateGroups(Arrays.asList("test_group_001"));
        FlowableListener flowable1Listener = new FlowableListener();
        flowable1Listener.setEvent(TaskListener.EVENTNAME_CREATE);
        flowable1Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        flowable1Listener.setImplementation("${sample1TaskListenerManagerImpl}");

        FlowableListener flowable2Listener = new FlowableListener();
        flowable2Listener.setEvent(TaskListener.EVENTNAME_ASSIGNMENT);
        flowable2Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        flowable2Listener.setImplementation("${sample2TaskListenerManagerImpl}");

        userTask1.setTaskListeners(Arrays.asList(flowable1Listener, flowable2Listener));

        // 并行节点
        ParallelGateway parallelGateway1 = new ParallelGateway();
        parallelGateway1.setId("parallel1");
        parallelGateway1.setName("并行节点1");
        parallelGateway1.setIncomingFlows(Arrays.asList(flow2));
        parallelGateway1.setOutgoingFlows(Arrays.asList(flow5, flow6));

        // 节点2
        UserTask userTask2 = new UserTask();
        userTask2.setName("节点2");
        userTask2.setId("userTask2");
        userTask2.setIncomingFlows(Arrays.asList(flow5));
        userTask2.setOutgoingFlows(Arrays.asList(flow7));

        // 多实例设置
        MultiInstanceLoopCharacteristics mlc = new MultiInstanceLoopCharacteristics();
        mlc.setSequential(true);//isSequential为true是串行，isSequential为false是并行
        mlc.setLoopCardinality("5");

        // 节点3
        UserTask userTask3 = new UserTask();
        userTask3.setName("节点3");
        userTask3.setId("userTask3");
        userTask3.setLoopCharacteristics(mlc);
        userTask3.setIncomingFlows(Arrays.asList(flow6));
        userTask3.setOutgoingFlows(Arrays.asList(flow8));

        FlowableListener execution1Listener = new FlowableListener();
        execution1Listener.setEvent(ExecutionListener.EVENTNAME_START);
        execution1Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        execution1Listener.setImplementation("${sample1ExecutionListenerManagerImpl}");

        FlowableListener execution2Listener = new FlowableListener();
        execution2Listener.setEvent(ExecutionListener.EVENTNAME_START);
        execution2Listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        execution2Listener.setImplementation("${sample2ExecutionListenerManagerImpl}");

        userTask3.setExecutionListeners(Arrays.asList(execution1Listener, execution2Listener));

        // 并行节点
        ParallelGateway parallelGateway2 = new ParallelGateway();
        parallelGateway2.setId("parallel2");
        parallelGateway2.setName("并行节点2");
        parallelGateway2.setIncomingFlows(Arrays.asList(flow7, flow8));
        parallelGateway2.setOutgoingFlows(Arrays.asList(flow9));

        // 节点4
        UserTask userTask4 = new UserTask();
        userTask4.setName("节点4");
        userTask4.setId("userTask4");
        userTask4.setIncomingFlows(Arrays.asList(flow9));
        userTask4.setOutgoingFlows(Arrays.asList(flow10));

        // 节点5
        ServiceTask serviceTask1 = new ServiceTask();
        serviceTask1.setId("serviceTask1");
        serviceTask1.setName("节点5");

        // 使用delegate类型方式
        serviceTask1.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        serviceTask1.setImplementation("${sampleServiceManagerImpl}");

        // 使用class类型方式，则实现类无法使用spring进行管理
        //serviceTask1.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        //serviceTask1.setImplementation("my.sample.manager.workflow.SampleServiceManagerImpl");

        serviceTask1.setIncomingFlows(Arrays.asList(flow10));
        serviceTask1.setOutgoingFlows(Arrays.asList(flow3));

        // 结束节点
        EndEvent endEvent = new EndEvent();
        endEvent.setName("结束节点");
        endEvent.setId("endEvent");
        endEvent.setIncomingFlows(Arrays.asList(flow3, flow4));

        process.setId("my-process-test-cp");
        process.addFlowElement(start);
        process.addFlowElement(flow0);
        process.addFlowElement(serviceTask0);
        process.addFlowElement(flow11);
        process.addFlowElement(exclusiveGateway);
        process.addFlowElement(flow1);
        process.addFlowElement(userTask1);
        process.addFlowElement(flow2);
        process.addFlowElement(parallelGateway1);
        process.addFlowElement(flow5);
        process.addFlowElement(userTask2);
        process.addFlowElement(flow6);
        process.addFlowElement(userTask3);
        process.addFlowElement(flow7);
        process.addFlowElement(flow8);
        process.addFlowElement(parallelGateway2);
        process.addFlowElement(flow9);
        process.addFlowElement(userTask4);
        process.addFlowElement(flow10);
        process.addFlowElement(serviceTask1);
        process.addFlowElement(flow3);
        process.addFlowElement(flow4);
        process.addFlowElement(endEvent);
        model.addProcess(process);

        GraphicInfo g1 = new GraphicInfo();
        g1.setHeight(100);
        g1.setWidth(200);
        g1.setX(100);
        g1.setY(100);
        model.addGraphicInfo("start1", g1);
        GraphicInfo g2 = new GraphicInfo();
        g2.setHeight(100);
        g2.setWidth(200);
        g2.setX(600);
        g2.setY(100);
        model.addGraphicInfo("check1", g2);

        GraphicInfo g3 = new GraphicInfo();
        g3.setHeight(100);
        g3.setWidth(200);
        g3.setX(1100);
        g3.setY(100);
        model.addGraphicInfo("userTask1", g3);
        model.addGraphicInfo("serviceTask0", g3);
        GraphicInfo g4 = new GraphicInfo();
        g4.setHeight(100);
        g4.setWidth(200);
        g4.setX(1600);
        g4.setY(100);
        model.addGraphicInfo("parallel1", g4);
        GraphicInfo g5 = new GraphicInfo();
        g5.setHeight(100);
        g5.setWidth(200);
        g5.setX(2100);
        g5.setY(100);
        model.addGraphicInfo("userTask2", g5);
        GraphicInfo g6 = new GraphicInfo();
        g6.setHeight(100);
        g6.setWidth(200);
        g6.setX(2600);
        g6.setY(100);
        model.addGraphicInfo("userTask3", g6);
        GraphicInfo g7 = new GraphicInfo();
        g7.setHeight(100);
        g7.setWidth(200);
        g7.setX(3100);
        g7.setY(100);
        model.addGraphicInfo("parallel2", g7);
        GraphicInfo g8 = new GraphicInfo();
        g8.setHeight(100);
        g8.setWidth(200);
        g8.setX(3600);
        g8.setY(100);
        model.addGraphicInfo("userTask4", g8);
        GraphicInfo g9 = new GraphicInfo();
        g9.setHeight(100);
        g9.setWidth(200);
        g9.setX(4100);
        g9.setY(100);
        model.addGraphicInfo("serviceTask1", g9);
        GraphicInfo g10 = new GraphicInfo();
        g10.setHeight(100);
        g10.setWidth(200);
        g10.setX(4600);
        g10.setY(100);
        model.addGraphicInfo("endEvent", g10);


        //验证组装bpmnmodel是否正确
        ProcessValidator defaultProcessValidator = new ProcessValidatorFactory().createDefaultProcessValidator();
        List<ValidationError> validate = defaultProcessValidator.validate(model);
        if (validate.size() > 0) {
            throw new RuntimeException("流程有误，请检查后重试");
        }

        //生成流程图
        //BpmnModel bpmnModel = repositoryService.getBpmnModel(his.getProcessDefinitionId());

        ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
        //得到高亮的流程 processInstanceId这个应该是executionId
        //List<String> highLightedActivities=runtimeService.getActiveActivityIds(processInstanceId);
        List<String> highLightedActivities = new ArrayList<String>();
        List<String> highLightedFlows = new ArrayList<String>();
        //防止图片乱码
        InputStream in = diagramGenerator.generateDiagram(model, "png", highLightedActivities,
                highLightedFlows, "宋体", "宋体", "宋体", processEngine.getProcessEngineConfiguration().getClassLoader(), 1.0, true);
        File f = new File("C:\\Users\\bineea\\Desktop\\test.png");
        ImageIO.write(ImageIO.read(in), "png", f);

        Deployment deployment = repositoryService.createDeployment()
                .name("my-testDeploy-cp")
                // 此处命名必须以.bpmn20.xml作为结尾
                .addBpmnModel("my-testBpmnModel-cp.bpmn20.xml", model)
                .deploy();
        System.out.println("Deploy successfullly, deployId:"
                + deployment.getId() + "; deployName:" + deployment.getName());
        System.out.println("deployment:" + deployment.toString());
    }

    /*
     * 刪除流程部署
     */
    private void deleteDeployment() {
        //deploymentId:流程定义的ID
        //cascade:如果递归删除，会删除流程定义，流程实例以及各种历史实例……
        List<ProcessDefinition> list = repositoryService//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery().list();
        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                System.out.println("部署定义ID:" + pd.getDeploymentId());//流程定义的key+版本+随机生成数
                repositoryService.deleteDeployment(pd.getDeploymentId(), true);
            }
        }

    }

    /**
     * 查询流程定义
     */
    private void queryProcessDefinitionTest() {

        List<ProcessDefinition> list = repositoryService//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义查询
                /*指定查询条件,where条件*/
                //.deploymentId(deploymentId)//使用部署对象ID查询
                //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
                //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
                //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                /*排序*/
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
                //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                .list();//返回一个集合列表，封装流程定义
        //.singleResult();//返回唯一结果集
        //.count();//返回结果集数量
        //.listPage(firstResult, maxResults)//分页查询
        System.out.println("list size = " + list.size());
        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                System.out.println("部署定义ID:" + pd.getDeploymentId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义ID:" + pd.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义名称:" + pd.getName());//对应HelloWorld.bpmn文件中的name属性值
                System.out.println("流程定义的key:" + pd.getKey());//对应HelloWorld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:" + pd.getVersion());//当流程定义的key值相同的情况下，版本升级，默认从1开始
                System.out.println("部署对象ID:" + pd.getDeploymentId());
                System.out.println("################################");
            }
        }
    }

    /**
     * 启动流程
     */
    private void startProcessInstanceTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", "jack");
        variables.put("nrOfHolidays", 3);
        variables.put("description", "回家看看");

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("my-process-test-cp", variables);

    }

    /**
     * 查询并完成任务
     */
    private void queryAndCompleteTask() throws IOException {
        // 查询

        // 如果按照assignee查询，确实是查询指派用户的任务信息；但是如果全部查询仍然是可以全部查询到的，而且未申领任务的情况下也可以，任何用户都可以完成该节点任务

        List<Task> tasks = taskService.createTaskQuery()
                //.taskAssignee("test")
                .listPage(0, 8);
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName() + "; id:" + tasks.get(i).getId() +
                    "; task definitionid:" + tasks.get(i).getTaskDefinitionId() +
                    "; task description:" + tasks.get(i).getDescription() +
                    "; task executionid" + tasks.get(i).getExecutionId()
            );

            ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                    .executionId(tasks.get(i).getExecutionId()).singleResult();
            // 当前审批节点
            String crruentActivityId = ee.getActivityId();
            System.out.println("crruentActivityId:" + crruentActivityId);
        }

        // 选择
        if (tasks != null && tasks.size() > 0) {
            for (Task task : tasks) {
                Map<String, Object> processVariables = taskService.getVariables(task
                        .getId());
                System.out.println("result：" + processVariables.get("result"));
                System.out.println("公安交管审核结果：" + processVariables.get("gajgResult"));
                System.out.println("北京交通委审核结果：" + processVariables.get("jtwResult"));
                System.out.println("employee：" + processVariables.get("employee"));
                // 申领任务
                //taskService.claim(task.getId(), "fozzie");

                ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery()
                        .executionId(task.getExecutionId()).singleResult();


                // 当前审批节点
                String current = executionEntity.getActivityId();
                Map map = new HashMap<String, Object>();
                if (current.equals("userTask2")) {
                    map.put("employee", "123");
                    taskService.complete(task.getId(), map);
                } else if (current.equals("userTask3")) {
                    map.put("employee", "asd");
                    taskService.complete(task.getId(), map);
                } else {
                    // 完成任务
                    taskService.complete(task.getId(), map);
                }
            }
        } else {
            System.out.println("查询任务为空！！！！");
        }

        // 查询历史流程，验证是否结束
        List<HistoricProcessInstance> historicProcessInstanceList =
                historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance h : historicProcessInstanceList) {
            System.out.println("Process instance " + h.getId() + " end time: " + h.getEndTime());
            Map<String, Object> variables = h.getProcessVariables();
            System.out.println("result: " + variables.get("result") +
                    "; gajgResult: " + variables.get("gajgResult") +
                    "; jtwResult: " + variables.get("jtwResult")
            );


            BpmnModel bpmnModel = repositoryService.getBpmnModel(h.getProcessDefinitionId());

            ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //得到高亮的流程 processInstanceId这个应该是executionId
            //List<String> highLightedActivities=runtimeService.getActiveActivityIds(processInstanceId);
            List<String> highLightedActivities = new ArrayList<String>();
            List<String> highLightedFlows = new ArrayList<String>();
            //防止图片乱码
            InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities,
                    highLightedFlows, "宋体", "宋体", "宋体", processEngine.getProcessEngineConfiguration().getClassLoader(), 1.0, true);
            File f = new File("C:\\Users\\bineea\\Desktop\\test.png");
            ImageIO.write(ImageIO.read(in), "png", f);
        }
    }

    /**
     * 查询任务及变量值
     */
    private void queryTask() {
        TaskQuery taskQuery = taskService.createTaskQuery().processVariableValueEquals("employee", "jack");
        taskQuery.processVariableValueEquals("description", "回家看看");
        List<Task> tasks = taskQuery.orderByTaskCreateTime().taskAssignee("test").asc().listPage(0, 100);
        System.out.println("test 用户 have " + tasks.size() + " tasks:");
    }

    /*
     * 申领任务，并验证是否申领成功
     */
    private void claimAndCheckTask() throws IOException {
        // 获取第一个任务
        List<Task> user001tasks = taskService.createTaskQuery().taskCandidateUser("test_user_001").list();
        for (Task task : user001tasks) {
            System.out.println("Following task is available for test_user_001 task: " + task.getName());

            // 申领任务
            //taskService.claim(task.getId(), "fozzie");

            System.out.println("task :" + task.toString() +
                    "task process variables :" + JsonTools.writeValueAsString(task.getProcessVariables()) +
                    "task process variables size :" + task.getProcessVariables().size() +
                    "task local variables :" + JsonTools.writeValueAsString(task.getTaskLocalVariables()) +
                    "task local variables size :" + task.getTaskLocalVariables().size());

            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            System.out.println("variables :" + JsonTools.writeValueAsString(processVariables));

        }

        // 验证Fozzie获取了任务
        user001tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : user001tasks) {
            System.out.println("Task for fozzie: " + task.getName());
        }

        // 获取第一个任务
        List<Task> group001tasks = taskService.createTaskQuery().taskCandidateGroup("test_group_001").list();
        for (Task task : group001tasks) {
            System.out.println("Following task is available for test_group_001 group: " + task.getName());

            // 申领任务
            //taskService.claim(task.getId(), "fozzie");
        }

        // 验证Fozzie获取了任务
        group001tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : group001tasks) {
            System.out.println("Task for fozzie: " + task.getName());
        }

        // 获取第一个任务
        List<Task> fozzietasks = taskService.createTaskQuery().taskCandidateUser("fozzie").list();
        for (Task task : fozzietasks) {
            System.out.println("Following task is available for fozzie user: " + task.getName());

            // 申领任务
            //taskService.claim(task.getId(), "fozzie");
        }

        if (CollectionUtils.isEmpty(fozzietasks)) {
            updateIdentity();
            List<Task> fozzietasks2 = taskService.createTaskQuery().taskCandidateUser("fozzie").list();
            for (Task task : fozzietasks2) {
                System.out.println("Following task is available for fozzie user 2222: " + task.getName());

                // 申领任务
                //taskService.claim(task.getId(), "fozzie");
            }
        }

        // 验证Fozzie获取了任务
        fozzietasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : fozzietasks) {
            System.out.println("Task for fozzie: " + task.getName());
        }

        List<Task> allTasks = taskService.createTaskQuery()
                .processDefinitionKey("my-process-test-cp")
                .list();
        System.out.println("You have " + allTasks.size() + " tasks:");
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println((i + 1) + ") " + allTasks.get(i).getName() + "; id:" + allTasks.get(i).getId() +
                    "; task definitionid:" + allTasks.get(i).getTaskDefinitionId() +
                    "; task description:" + allTasks.get(i).getDescription() +
                    "; task executionid:" + allTasks.get(i).getExecutionId() +
                    "; processDefinitionId:" + allTasks.get(i).getProcessDefinitionId() +
                    "; definition key:" + allTasks.get(i).getTaskDefinitionKey() +
                    "; processInstanceId:" + allTasks.get(i).getProcessInstanceId()
            );

            ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                    .executionId(allTasks.get(i).getExecutionId()).singleResult();
            // 当前审批节点
            String crruentActivityId = ee.getActivityId();
            System.out.println("crruentActivityId:" + crruentActivityId);
        }


    }

    /**
     * 配置用户及用户组
     */
    private void handleIdentity() {
        Group group = identityService.newGroup("test_group_001");
        identityService.saveGroup(group);
        User user = identityService.newUser("test_user_001");
        identityService.saveUser(user);
        identityService.createMembership("test_user_001", "test_group_001");
    }

    /**
     * 更新用户及用户组
     */
    private void updateIdentity() {
        User user = identityService.newUser("fozzie");
        identityService.saveUser(user);
        identityService.createMembership("fozzie", "test_group_001");
    }

    private void handleDynamicBpmn() {
//        dynamicBpmnService.changeUserTaskCandidateGroups();
    }

}
