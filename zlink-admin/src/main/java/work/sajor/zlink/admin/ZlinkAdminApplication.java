package work.sajor.zlink.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@ComponentScan("work.sajor")
@MapperScan("work.sajor")
public class ZlinkAdminApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext run = SpringApplication.run(ZlinkAdminApplication.class, args);
        ConfigurableEnvironment env = run.getEnvironment();

        String envPort = env.getProperty("server.port");
        String envContext = env.getProperty("server.servlet.context-path");
        String port = envPort == null ? "8080" : envPort;
        String context = envContext == null ? "" : envContext;
        String path = port + "" + context + "/swagger-ui/index.html";
        String externalAPI = InetAddress.getLocalHost().getHostAddress();
        log.info("Access URLs:\n----------------------------------------------------------\n\t"
                        + "Local-API: \t\thttp://127.0.0.1:{}\n\t"
                        + "External-API: \thttp://{}:{}\n\t"
                        + "web-URL: \t\thttp://127.0.0.1:{}/index.html\n----------------------------------------------------------",
                path, externalAPI, path, port);
    }

}
