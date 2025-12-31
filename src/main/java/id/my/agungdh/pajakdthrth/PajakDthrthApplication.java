package id.my.agungdh.pajakdthrth;

import id.my.agungdh.pajakdthrth.repository.SoftDeleteRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = SoftDeleteRepositoryImpl.class)
public class PajakDthrthApplication {

    public static void main(String[] args) {
        SpringApplication.run(PajakDthrthApplication.class, args);
    }

}
