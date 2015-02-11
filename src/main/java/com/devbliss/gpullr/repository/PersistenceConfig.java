package com.devbliss.gpullr.repository;

import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {
  
  @Profile("test")
  @Bean
  public DataSource createTestDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:mem:gpullrDb_test").driverClassName("org.h2.Driver").build();
  }
  
  @Profile({"prod", "dev"})
  @Bean
  public DataSource createDataSource() {
    return DataSourceBuilder.create().url("jdbc:h2:./gpullrDb").driverClassName("org.h2.Driver").build();
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
