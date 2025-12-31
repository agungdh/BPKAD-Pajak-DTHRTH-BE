package id.my.agungdh.pajakdthrth.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class TestFlywayConfig {

    @Bean
    public BeanPostProcessor flywayDataSourceMigrator() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource) {
                    Flyway.configure()
                            .dataSource((DataSource) bean)
                            .locations("classpath:db/migration")
                            .baselineOnMigrate(true)
                            .cleanDisabled(false)
                            .load()
                            .migrate();
                }
                return bean;
            }
        };
    }
}
