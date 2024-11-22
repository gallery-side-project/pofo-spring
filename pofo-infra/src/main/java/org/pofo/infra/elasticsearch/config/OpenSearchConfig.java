package org.pofo.infra.elasticsearch.config;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.spring.boot.autoconfigure.RestClientBuilderCustomizer;
import org.pofo.infra.importer.PofoInfraConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.pofo.infra.elasticsearch.repository")
@Profile(value = "prod")
public class OpenSearchConfig implements PofoInfraConfig {
    @Bean
    public RestClientBuilderCustomizer clientConfiguration() {
        return new RestClientBuilderCustomizer() {
            @Override
            public void customize(RestClientBuilder builder) {}

            @Override
            public void customize(HttpAsyncClientBuilder builder) {
                try {
                    builder.setSSLContext(
                            new SSLContextBuilder()
                                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                    .build()
                    );
                } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
