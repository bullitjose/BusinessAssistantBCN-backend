package com.businessassistantbcn.opendata.helper;

import com.businessassistantbcn.opendata.config.PropertiesConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URL;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class HttpClientHelper {

    private final PropertiesConfig config;
    HttpClient httpClient;
    WebClient client;

    /**
     * Inicialización de objetos de conexión en constructor
     */
    @Autowired
    public HttpClientHelper(PropertiesConfig config){
        this.config = config;
        httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(config.getConnection_timeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(config.getConnection_timeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(config.getConnection_timeout(), TimeUnit.MILLISECONDS)));

        client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
                .build();

    }
    private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        clientCodecConfigurer.defaultCodecs().maxInMemorySize(config.getMaxBytesInMemory());
        clientCodecConfigurer.customCodecs().registerWithDefaultConfig(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.TEXT_PLAIN));
        clientCodecConfigurer.customCodecs().registerWithDefaultConfig(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.TEXT_PLAIN));
    }

    public String getStringRoot(URL url){
        return "";
    }


    public JsonNode getJsonRoot(URL url) {
        return null;
    }


    public Object getObjectRoot (URL url, Class clazz){

        return null;
    }

    /**
     * OJO RestTemplate pronto deprecada (ES BLOCKING), preferible uso de WebClient (reactive programming)
     * {@link} https://www.baeldung.com/spring-5-webclient
     * {@link} https://www.baeldung.com/spring-webclient-json-list
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    
    @SuppressWarnings("unchecked")
    public <T> Mono<T> getRequestData(URL url, Class clazz){
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(url.toString()));
        return bodySpec.retrieve().bodyToMono(clazz);
    }


}

    

