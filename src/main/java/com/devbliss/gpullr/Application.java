package com.devbliss.gpullr;

import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Application entry point
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

  public static final String DRIVER_CLASS_NAME = "org.h2.Driver";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  @Profile({"dev", "test"})
  public DataSource createTestDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:mem:gpullr").driverClassName(DRIVER_CLASS_NAME).build();
  }

  @Bean
  @Profile("prod")
  public DataSource createDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:./gpullr").driverClassName(DRIVER_CLASS_NAME).build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
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
    adapter.setShowSql(true);
    adapter.setGenerateDdl(true);
    adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    return adapter;
  }

}
