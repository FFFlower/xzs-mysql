package com.mindskip.xzs.configuration.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

/**
 * <p><一句话功能简述>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/10/12, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/10/12]
 * @see [相关类/方法]
 */
@Slf4j
@Configuration
public class ElasticSearchConfig {

    /**
     * elk集群地址
     */
    @Value("${elasticsearch.ip}")
    private String hostName;

    /**
     * 端口
     */
    @Value("${elasticsearch.port}")
    private String port;

    /**
     * 集群名称
     */
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    /**
     * 连接池
     */
    @Value("${elasticsearch.pool}")
    private String poolSize;

    /**
     * Bean name default  函数名字
     *
     * @return
     */

    @PostConstruct
    public void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    /**
     * Bean name default  函数名字
     *
     * @return
     */
    @Bean(name = "transportClient")
    public TransportClient transportClient() {
        log.info("Elasticsearch初始化开始。。。。。");
        TransportClient transportClient = null;
        try {
            // 配置信息
            Settings esSetting = Settings.builder()
                    .put("cluster.name", clusterName) //集群名字
                    .put("client.transport.sniff", true)//增加嗅探机制，找到ES集群
                    .put("thread_pool.search.size", Integer.parseInt(poolSize))//增加线程池个数，暂时设为5
                    .build();
            //配置信息Settings自定义
            transportClient = new PreBuiltTransportClient(esSetting);
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port));
            transportClient.addTransportAddresses(transportAddress);
        } catch (Exception e) {
            log.error("elasticsearch TransportClient create error!!", e);
        }
        return transportClient;
    }
}
