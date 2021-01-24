package aolshevskiy.rdsiam;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.SQLException;

@SpringBootApplication
public class RdsIamApplication {
    private static final Logger logger = LoggerFactory.getLogger(RdsIamApplication.class);

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(RdsIamApplication.class, args);
    }

    @Bean
    public DataSource dataSource(RegionProvider regionProvider, AWSCredentialsProvider credentialsProvider, DataSourceProperties properties) {
        RdsIamAuthTokenGenerator rdsTokenGenerator = RdsIamAuthTokenGenerator.builder()
          .credentials(credentialsProvider)
          .region(regionProvider.getRegion())
          .build();

        URI uri = URI.create(properties.getUrl().substring(5));

        GetIamAuthTokenRequest tokenRequest = GetIamAuthTokenRequest.builder()
          .hostname(uri.getHost())
          .port(uri.getPort())
          .userName(properties.getUsername())
          .build();

        String rdsToken = rdsTokenGenerator.getAuthToken(tokenRequest);

        return properties.initializeDataSourceBuilder()
          .password(rdsToken)
          .build();
    }
}
