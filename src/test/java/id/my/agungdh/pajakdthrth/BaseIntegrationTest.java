package id.my.agungdh.pajakdthrth;

import id.my.agungdh.pajakdthrth.config.TestFlywayConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestFlywayConfig.class)
public abstract class BaseIntegrationTest {
}
