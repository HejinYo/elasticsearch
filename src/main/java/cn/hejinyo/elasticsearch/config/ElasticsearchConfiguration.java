package cn.hejinyo.elasticsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * elasticsearch配置
 *
 * @author flybear.c.xiong
 */
@Configuration
@Slf4j
public class ElasticsearchConfiguration {
    @Value("${elasticsearch.cluster-nodes}")
    private String clusterNodeStr;

    @Bean("myRestClient")
    public RestHighLevelClient buildClient() {
        try {
            String[] clusterNodes = clusterNodeStr.split(",");
            HttpHost[] hosts = new HttpHost[clusterNodes.length];
            for (int i = 0; i < clusterNodes.length; i++) {
                String node = clusterNodes[i];
                hosts[i] = new HttpHost(
                        node.split(":")[0],
                        Integer.parseInt(node.split(":")[1]),
                        "http");
            }
            RestClientBuilder builder = RestClient.builder(hosts);
            builder.setMaxRetryTimeoutMillis(60000);
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setSocketTimeout(60000);
                }
            });
            return new RestHighLevelClient(builder);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}