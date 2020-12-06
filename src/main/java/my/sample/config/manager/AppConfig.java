package my.sample.config.manager;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.alibaba.druid.pool.DruidDataSource;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Import({HibernateConfig.class, MybatisConfig.class, FlowableConfig.class, RedisConfig.class})
//注解开启注解式事务的支持，通知Spring，@Transactional注解的类被事务的切面包围
/*AdviceMode共有两种模式：
PROXY(代理模式，jdk动态代理和cglib动态代理)
ASPECTJ(编译时增强模式，编译时对类进行增强生成新的AOP类)，需要配置AnnotationTransactionAspect
*/
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ, proxyTargetClass = true)
@ComponentScan(basePackages = {AppConfig.APP_NAME + ".manager"})
public class AppConfig 
{
	public static final String APP_NAME = "my.sample";

	@Bean(name = "databaseProperties")
	public PropertiesFactoryBean loadDatabaseProperties()
	{
		PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
		propertiesFactory.setLocation(new ClassPathResource("config/hibernate-database.properties"));
		propertiesFactory.setFileEncoding("utf-8");
		return propertiesFactory;
	}

	@Value("#{databaseProperties.driverClassName}")
	private String driverClassName;
	@Value("#{databaseProperties.url}")
	private String jdbcUrl;
	@Value("#{databaseProperties.username}")
	private String username;
	@Value("#{databaseProperties.password}")
	private String password;

	@Value("#{databaseProperties.validationQuery}")
	private String validationQuery;
	@Value("#{databaseProperties.databaseType}")
	private String databaseType;
	
	@Bean(name = "dataSource")
	public DataSource initDataSource()
	{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		//初始化连接数
		dataSource.setInitialSize(5);
		//最小空闲连接数
		dataSource.setMinIdle(1);
		//最大活动连接数
		dataSource.setMaxActive(100);
		//最大连接等待时间
		dataSource.setMaxWait(60000);
		//SQL查询,用来验证从连接池取出的连接,在将连接返回给调用者之前.如果指定,则查询必须是一个SQL SELECT并且必须返回至少一行记录 
		dataSource.setValidationQuery(validationQuery);
		//是否在从连接池中取出连接前进行检验,如果检验失败,则从连接池中去除连接并尝试取出另一个；若设置为true，validationQuery参数必须设置为非空字符串
		dataSource.setTestOnBorrow(false);
		//是否在归还到池中前进行检验；若设置为true，validationQuery参数必须设置为非空字符串
		dataSource.setTestOnReturn(false);
		//连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除；若设置为true，validationQuery参数必须设置为非空字符串
		dataSource.setTestWhileIdle(true);
		// 打开PSCache，并且指定每个连接上PSCache的大小
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

		return dataSource;
	}

	@Bean
	public ThreadPoolExecutor mySelfThreadPoolExecutor() {
		return new ThreadPoolExecutor(80, 90,
				10L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(100));
	}
}
