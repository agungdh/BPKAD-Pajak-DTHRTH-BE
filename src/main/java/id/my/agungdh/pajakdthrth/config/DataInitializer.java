package id.my.agungdh.pajakdthrth.config;

import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@org.springframework.context.annotation.Profile("seeding")
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            KodePajakRepository kodePajakRepository) {
        return args -> {
            // Seed Admin User
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setNama("Administrator");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            // Seed Kode Pajak
            seedKodePajak(kodePajakRepository);
        };
    }

    private void seedKodePajak(KodePajakRepository kodePajakRepository) {
        // Concatenated "Code Name" as requested
        List<String> taxNames = List.of(
                "411211 PPN",
                "411121 PPh 21",
                "411122 PPh 22",
                "411124 PPh 23",
                "PPh 4/2 PPh 4/2");

        taxNames.forEach(nama -> {
            if (kodePajakRepository.findByNama(nama).isEmpty()) {
                KodePajak kp = new KodePajak();
                kp.setNama(nama);
                kodePajakRepository.save(kp);
            }
        });
    }
}
