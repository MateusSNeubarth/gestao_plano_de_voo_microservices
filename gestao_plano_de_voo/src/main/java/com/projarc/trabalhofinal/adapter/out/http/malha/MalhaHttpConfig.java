package com.projarc.trabalhofinal.adapter.out.http.malha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class MalhaHttpConfig {

    // sem bean RestClient.Builder @LoadBalanced: o cliente do Eureka usaria o builder e tentaria resolver o próprio servidor pelo load balancer
    @Bean
    public RestClient malhaRestClient(DeferringLoadBalancerInterceptor loadBalancer,
                                      @Value("${malha.timeout.connect}") Duration connect,
                                      @Value("${malha.timeout.read}") Duration read) {
        return builder(connect, read)
                .requestInterceptor(loadBalancer)
                .baseUrl("http://malha-aerea")
                .build();
    }

    static RestClient.Builder builder(Duration connect, Duration read) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connect);
        requestFactory.setReadTimeout(read);
        return RestClient.builder().requestFactory(requestFactory);
    }
}
