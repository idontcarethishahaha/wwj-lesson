package org.example.config;
/**
 * 类说明：
 *
 * @author WuWenJin
 * @date 2026-05-24 15:04
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
public class BarrageServerConfig {

    /** 该bean会自动扫描@ServerEndpoint注解并使其生效 */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
