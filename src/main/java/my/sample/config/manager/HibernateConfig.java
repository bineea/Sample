package my.sample.config.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//注解开启对Spring Data JPA Repostory的支持
@EnableJpaRepositories(basePackages ={ AppConfig.APP_NAME + ".dao.repo.jpa"}, entityManagerFactoryRef = "entityManager")
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;

    @Value("#{databaseProperties.dialect}")
    private String dialect;

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager()
    {
        LocalContainerEntityManagerFactoryBean entityFactory = new LocalContainerEntityManagerFactoryBean();
        entityFactory.setDataSource(dataSource);
        entityFactory.setPackagesToScan(new String[] {AppConfig.APP_NAME+".dao.entity"});
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto","update");
        entityFactory.setJpaProperties(jpaProperties);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(dialect);

        entityFactory.setJpaVendorAdapter(vendorAdapter);
        return entityFactory;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager()
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(this.entityManager().getObject());
        return transactionManager;
    }

    @Bean(name = "annotationTransactionAspect")
    public AnnotationTransactionAspect annotationTransactionAspect() {
        AnnotationTransactionAspect aspect = AnnotationTransactionAspect.aspectOf();
        aspect.setTransactionManager(transactionManager());
        return aspect;
    }
}
