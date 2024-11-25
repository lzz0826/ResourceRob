package org.example.lockproject.conf;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;


@Configuration
@EnableConfigurationProperties
//@MapperScan("org.server.mapper")
public class MyBatisConfig {

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;



    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(
            // 设置mybatis的xml所在位置，这里使用mybatis注解方式，没有配置xml文件
            new PathMatchingResourcePatternResolver().getResources(mapperLocations));


        return bean.getObject();
    }

}
