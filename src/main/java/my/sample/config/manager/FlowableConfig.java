package my.sample.config.manager;

import org.flowable.engine.*;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;

@Configuration
public class FlowableConfig {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JpaTransactionManager transactionManager;

    //配置流程引擎，并创建流程引擎
    @Bean(name = "processEngineConfiguration")
    public SpringProcessEngineConfiguration initProcessEngineConfig() {
        SpringProcessEngineConfiguration processConfig = new SpringProcessEngineConfiguration();
        processConfig.setDataSource(dataSource);
        processConfig.setTransactionManager(transactionManager);
        processConfig.setDatabaseSchemaUpdate(SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processConfig.setAsyncExecutorActivate(false);
        return processConfig;
    }

    //ProcessEngineFactoryBean已经实现接口ApplicationContextAware，必须将该类注册为Spring的bean，Spring才能进行应用上下文ApplicationContext的注入。
    @Bean(name = "processEngineFactoryBean")
    public ProcessEngineFactoryBean initProcessEngineFactory() throws Exception {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(initProcessEngineConfig());
        return processEngineFactoryBean;
    }

    //主要是关于静态信息（数据不会改变，或者至少不是很多）
    //查询引擎已知的部署和流程定义。
    //暂停和激活整个部署或特定流程定义。暂停意味着不能对它们执行进一步的操作，而激活则相反并且再次启用操作。
    @Bean(name = "repositoryService")
    public RepositoryService getRepositoryService() throws Exception { return initProcessEngineFactory().getObject().getRepositoryService(); }

    //启动流程定义的新流程实例
    @Bean(name = "runtimeService")
    public RuntimeService getRuntimeService() throws Exception {
        return initProcessEngineFactory().getObject().getRuntimeService();
    }

    //查询分配给用户或组的任务
    //创建新的独立任务。这些是与流程实例无关的任务。
    //操作分配任务的用户或以某种方式参与任务的用户。
    //声称并完成任务。声称意味着某人决定成为该任务的受让人，这意味着该用户将完成该任务。完成意味着完成任务的工作。通常，这是填写各种形式。
    @Bean(name = "taskService")
    public TaskService getTaskService() throws Exception {
        return initProcessEngineFactory().getObject().getTaskService();
    }

    //暴露了可流动的引擎收集的所有历史数据例如流程实例启动时间，执行哪些任务，完成任务所需的时间，每个流程实例中遵循的路径，等等
    @Bean(name = "historyService")
    public HistoryService getHistoryService() throws Exception {
        return initProcessEngineFactory().getObject().getHistoryService();
    }

    //ManagementService通常在用Flowable编写用户应用时不需要使用。它可以读取数据库表与表原始数据的信息，也提供了对作业(job)的查询与管理操作
    @Bean(name = "managementService")
    public ManagementService getManagementService() throws Exception {
        return initProcessEngineFactory().getObject().getManagementService();
    }

    //组和用户的管理（创建，更新，删除，查询......）
    @Bean(name = "identityService")
    public IdentityService getIdentityService() throws Exception {
        return initProcessEngineFactory().getObject().getIdentityService();
    }

    @Bean(name = "formService")
    public FormService getFormService() throws Exception {
        return initProcessEngineFactory().getObject().getFormService();
    }

    //可以用来改变流程定义的一部分，而无需重新部署
    @Bean(name = "dynamicBpmnService")
    public DynamicBpmnService getDynamicBpmnService() throws Exception {
        return initProcessEngineFactory().getObject().getDynamicBpmnService();
    }
}
