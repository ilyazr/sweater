package ru.zakharov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import ru.zakharov.configs.FileStorageProperties;
import ru.zakharov.listeners.CustomServletContextListener;

import javax.servlet.ServletContextListener;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
//@EnableAsync
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> servletListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new CustomServletContextListener());
        return srb;
    }

}
