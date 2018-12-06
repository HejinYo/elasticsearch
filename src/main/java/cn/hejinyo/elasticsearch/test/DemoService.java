package cn.hejinyo.elasticsearch.test;

import cn.hejinyo.elasticsearch.config.ElasticsearchConfiguration;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: anthony.s.he
 * @Email: hejinyo@gmail.cn
 * @Date: 2018-12-06 09:25
 */
@Service
public class DemoService {

    @Resource(name = "myRestClient")
    private RestHighLevelClient client;

    /**
     * 创建索引
     *
     * @param indexName 索引名称
     */
    public void createIndex(String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
        request.mapping("tweet",
                "  {\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }",
                XContentType.JSON);
        request.alias(new Alias("test_1_alias"));

        GetIndexRequest getRequest = new GetIndexRequest();
        getRequest.indices(indexName);
        try {
            boolean exists = client.indices().exists(getRequest, RequestOptions.DEFAULT);
            if (exists) {
                System.out.println("索引已经存在");
                return;
            }

            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            //指示是否所有节点都已确认请求
            boolean acknowledged = createIndexResponse.isAcknowledged();
            //指示是否在超时之前为索引中的每个分片启动了必需的分片副本数
            boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
            System.out.println("acknowledged:"+acknowledged);
            System.out.println("shardsAcknowledged:"+shardsAcknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void get() {
        SearchRequest searchRequest = new SearchRequest("skye");
        searchRequest.types("person");
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : hits) {
                System.out.println(searchHit);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
