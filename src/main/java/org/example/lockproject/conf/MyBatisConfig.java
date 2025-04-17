package org.example.lockproject.conf;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
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

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(
            // 設置mybatis的xml所在位置，這裏使用mybatis註解方式，沒有配置xml文件
            new PathMatchingResourcePatternResolver().getResources(mapperLocations));

        // 設定 MyBatis-Plus 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true); // 自動將下劃線轉駝峰
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

}
