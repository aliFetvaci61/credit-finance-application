package com.alifetvaci.creditservice.elastic;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import java.util.function.Supplier;

@Configuration
@EnableElasticsearchRepositories(basePackages = "*")
public class ElasticSearchConfiguration extends ElasticsearchConfiguration {
    @Override
    public ClientConfiguration clientConfiguration() {

        Supplier<HttpHeaders> headersSupplier = () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Elastic-Product", "credit");
            return headers;
        };

        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withHeaders(headersSupplier)
                .build();
    }

}