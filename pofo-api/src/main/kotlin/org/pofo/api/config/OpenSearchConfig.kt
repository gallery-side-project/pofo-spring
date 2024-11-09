package org.pofo.api.config

import com.pofo.elasticsearch.repository.TechStackAutoCompleteRepository
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.ssl.SSLContextBuilder
import org.opensearch.client.RestClientBuilder
import org.opensearch.spring.boot.autoconfigure.RestClientBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = [TechStackAutoCompleteRepository::class])
@ComponentScan(basePackageClasses = [OpenSearchConfig::class])
class OpenSearchConfig {
    @Bean
    fun customizer(): RestClientBuilderCustomizer =
        object : RestClientBuilderCustomizer {
            override fun customize(builder: HttpAsyncClientBuilder) {
                try {
                    builder.setSSLContext(
                        SSLContextBuilder()
                            .loadTrustMaterial(null, TrustSelfSignedStrategy())
                            .build(),
                    )
                } catch (ex: KeyManagementException) {
                    throw RuntimeException("Failed to initialize SSL Context instance", ex)
                } catch (ex: NoSuchAlgorithmException) {
                    throw RuntimeException("Failed to initialize SSL Context instance", ex)
                } catch (ex: KeyStoreException) {
                    throw RuntimeException("Failed to initialize SSL Context instance", ex)
                }
            }

            override fun customize(builder: RestClientBuilder) {
                // nothing
            }
        }
}
