package work.sajor.zlink.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan("work.sajor")
//@MapperScan("work.sajor")
public class ZlinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZlinkAdminApplication.class, args);
    }

}
