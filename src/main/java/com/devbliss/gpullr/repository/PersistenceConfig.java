package com.devbliss.gpullr.repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class PersistenceConfig {

  private static final String DRIVER_CLASS_NAME = "org.h2.Driver";

  @Autowired
  private ConfigurableBeanFactory beanFactory;

  @PostConstruct
  public void registerThreadScope() {
    Scope threadScope = new SimpleThreadScope();
    beanFactory.registerScope("thread", threadScope);
  }

  @Profile("test")
  @Bean
  public DataSource createTestDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:mem:gpullrDb_test").driverClassName(DRIVER_CLASS_NAME).build();
  }

  @Profile({
    "prod", "dev"
  })
  @Bean
  public DataSource createDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:./gpullrDb;AUTO_SERVER=TRUE").driverClassName(DRIVER_CLASS_NAME).build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    DataSource dataSource,
    JpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
    emfb.setDataSource(dataSource);
    emfb.setPackagesToScan("com.devbliss.gpullr");
    emfb.setJpaVendorAdapter(jpaVendorAdapter);
    return emfb;
  }

  @Bean
  public JpaVendorAdapter createJpaVendorAdapter() {
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabase(Database.H2);
    adapter.setShowSql(false);
    adapter.setGenerateDdl(false);
    adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    return adapter;
  }
}
