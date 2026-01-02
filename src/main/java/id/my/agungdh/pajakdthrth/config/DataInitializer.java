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

    private final java.util.Random random = new java.util.Random();

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            KodePajakRepository kodePajakRepository,
            id.my.agungdh.pajakdthrth.repository.SkpdRepository skpdRepository,
            id.my.agungdh.pajakdthrth.repository.DthRepository dthRepository) {
        return args -> {
            // 1. Seed SKPD
            seedSkpd(skpdRepository);

            // 2. Seed Kode Pajak
            seedKodePajak(kodePajakRepository);

            // 3. Seed Users
            seedUsers(userRepository, skpdRepository, passwordEncoder);

            // 4. Seed DTH Data
            seedDth(dthRepository, skpdRepository, kodePajakRepository);
        };
    }

    private void seedSkpd(id.my.agungdh.pajakdthrth.repository.SkpdRepository skpdRepository) {
        List<String> skpdNames = List.of(
                "Dinas Pendidikan",
                "Dinas Kesehatan",
                "Dinas Pekerjaan Umum",
                "Badan Keuangan Daerah",
                "Inspektorat Daerah");

        skpdNames.forEach(nama -> {
            if (skpdRepository.findByNama(nama).isEmpty()) {
                id.my.agungdh.pajakdthrth.model.SKPD skpd = new id.my.agungdh.pajakdthrth.model.SKPD();
                skpd.setNama(nama);
                skpdRepository.save(skpd);
            }
        });
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

    private void seedUsers(UserRepository userRepository,
            id.my.agungdh.pajakdthrth.repository.SkpdRepository skpdRepository, PasswordEncoder passwordEncoder) {
        // Seed Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setNama("Administrator");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // Seed Regular Users for specific SKPDs
        createRegularUser(userRepository, skpdRepository, passwordEncoder, "user_diknas", "Dinas Pendidikan");
        createRegularUser(userRepository, skpdRepository, passwordEncoder, "user_dinkes", "Dinas Kesehatan");
        createRegularUser(userRepository, skpdRepository, passwordEncoder, "user_pu", "Dinas Pekerjaan Umum");
    }

    private void createRegularUser(UserRepository userRepository,
            id.my.agungdh.pajakdthrth.repository.SkpdRepository skpdRepository, PasswordEncoder passwordEncoder,
            String username, String skpdName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            skpdRepository.findByNama(skpdName).ifPresent(skpd -> {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode("password"));
                user.setNama("Admin " + skpdName);
                user.setRole(Role.USER);
                user.setSkpd(skpd);
                userRepository.save(user);
            });
        }
    }

    private void seedDth(id.my.agungdh.pajakdthrth.repository.DthRepository dthRepository,
            id.my.agungdh.pajakdthrth.repository.SkpdRepository skpdRepository,
            KodePajakRepository kodePajakRepository) {
        // Only seed if empty to prevent massive duplicates on restart
        if (dthRepository.count() > 0)
            return;

        List<id.my.agungdh.pajakdthrth.model.SKPD> allSkpd = skpdRepository.findAll();
        List<KodePajak> allKodePajak = kodePajakRepository.findAll();

        if (allSkpd.isEmpty() || allKodePajak.isEmpty())
            return;

        for (id.my.agungdh.pajakdthrth.model.SKPD skpd : allSkpd) {
            // Create 5-10 random DTH records for each SKPD
            int recordCount = 5 + random.nextInt(6);
            for (int i = 0; i < recordCount; i++) {
                id.my.agungdh.pajakdthrth.model.Dth dth = new id.my.agungdh.pajakdthrth.model.Dth();
                dth.setSkpd(skpd);

                dth.setNoSpm("SPM-" + skpd.getId() + "-" + (1000 + i));
                dth.setTglSpm(java.time.LocalDate.now().minusDays(random.nextInt(30)));
                dth.setNilaiBelanjaSpm(new java.math.BigDecimal(1000000 + random.nextInt(9000000)));

                dth.setNoSp2d("SP2D-" + skpd.getId() + "-" + (1000 + i));
                dth.setTglSp2d(dth.getTglSpm().plusDays(1 + random.nextInt(3)));
                dth.setNilaiBelanjaSp2d(dth.getNilaiBelanjaSpm()); // Usually matches

                KodePajak randomPajak = allKodePajak.get(random.nextInt(allKodePajak.size()));
                dth.setKodePajak(randomPajak);

                // Tax is roughly 10%
                dth.setJumlahPajak(dth.getNilaiBelanjaSp2d().multiply(new java.math.BigDecimal("0.1")));

                dth.setNamaRekanan("CV. Rekanan " + (char) ('A' + random.nextInt(26)));
                dth.setNpwp("00.123.456.7-" + (100 + random.nextInt(900)) + ".000");

                dthRepository.save(dth);
            }
        }
    }
}
