package org.project.sembs.evntmngmtbksytm;

import org.project.sembs.evntmngmtbksytm.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class EvntMngmtBkgSytmApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvntMngmtBkgSytmApplication.class, args);
    }

}
