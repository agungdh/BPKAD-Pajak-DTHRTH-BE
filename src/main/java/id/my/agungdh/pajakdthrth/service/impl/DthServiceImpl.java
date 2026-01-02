package id.my.agungdh.pajakdthrth.service.impl;

import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.mapper.DthMapper;
import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.repository.DthRepository;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.service.DthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DthServiceImpl implements DthService {

        private final DthRepository dthRepository;
        private final SkpdRepository skpdRepository;
        private final KodePajakRepository kodePajakRepository;
        private final id.my.agungdh.pajakdthrth.repository.UserRepository userRepository;
        private final DthMapper dthMapper;

        private id.my.agungdh.pajakdthrth.model.User getCurrentUser() {
                Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();
                if (principal instanceof id.my.agungdh.pajakdthrth.model.User) {
                        return (id.my.agungdh.pajakdthrth.model.User) principal;
                }
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "User not found or invalid token. Principal type: "
                                                + (principal != null ? principal.getClass().getName() : "null"));
        }

        @Override
        public List<DthResponse> findAll() {
                id.my.agungdh.pajakdthrth.model.User currentUser = getCurrentUser();
                List<Dth> dthList;

                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        dthList = dthRepository.findBySkpd(currentUser.getSkpd());
                } else {
                        dthList = dthRepository.findAll();
                }

                return dthList.stream()
                                .map(dthMapper::toResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public DthResponse findByUuid(String uuid) {
                id.my.agungdh.pajakdthrth.model.User currentUser = getCurrentUser();
                Dth dth = dthRepository.findByUuid(uuid)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));

                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        if (!dth.getSkpd().getId().equals(currentUser.getSkpd().getId())) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
                        }
                }

                return dthMapper.toResponse(dth);
        }

        @Override
        @Transactional
        public DthResponse create(DthRequest request) {
                id.my.agungdh.pajakdthrth.model.User currentUser = getCurrentUser();
                SKPD skpd;

                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        skpd = currentUser.getSkpd();
                        if (skpd == null) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "User (" + currentUser.getUsername() + ", ID: " + currentUser.getId()
                                                                + ") does not have an assigned SKPD. Principal class: "
                                                                + currentUser.getClass().getName());
                        }
                } else {
                        if (request.getSkpdId() == null) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "SKPD ID is required for Admins");
                        }
                        skpd = skpdRepository.findByUuid(request.getSkpdId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "SKPD not found"));
                }

                KodePajak kodePajak = kodePajakRepository.findByUuid(request.getKodePajakId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Kode Pajak not found"));

                Dth dth = dthMapper.toEntity(request);
                dth.setSkpd(skpd);
                dth.setKodePajak(kodePajak);

                Dth savedDth = dthRepository.save(dth);
                return dthMapper.toResponse(savedDth);
        }

        @Override
        @Transactional
        public DthResponse update(String uuid, DthRequest request) {
                id.my.agungdh.pajakdthrth.model.User currentUser = getCurrentUser();
                Dth dth = dthRepository.findByUuid(uuid)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));

                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        if (!dth.getSkpd().getId().equals(currentUser.getSkpd().getId())) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
                        }
                        // User cannot change SKPD
                        request.setSkpdId(dth.getSkpd().getUuid());
                }

                // Logic for changing SKPD is mainly for Admin, but if User passed it, we
                // effectively ignored it or reset it above.
                // Actually, if User passes a different SKPD ID, we should strictly prevent it
                // or just ignore it.
                // Let's stick to: If User, force SKPD to be current User's SKPD (which matches
                // DTH's SKPD verified above).

                SKPD targetSkpd;
                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        targetSkpd = currentUser.getSkpd();
                } else {
                        if (request.getSkpdId() == null) {
                                // If not provided in update, keep existing? Or required? Request says
                                // validation.
                                // Let's require it for consistency with Create if Admin.
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKPD ID is required");
                        }
                        targetSkpd = skpdRepository.findByUuid(request.getSkpdId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "SKPD not found"));
                }

                dth.setSkpd(targetSkpd);

                KodePajak kodePajak = kodePajakRepository.findByUuid(request.getKodePajakId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Kode Pajak not found"));
                dth.setKodePajak(kodePajak);

                dthMapper.updateEntityFromRequest(request, dth);

                Dth savedDth = dthRepository.save(dth);
                return dthMapper.toResponse(savedDth);
        }

        @Override
        @Transactional
        public void delete(String uuid) {
                id.my.agungdh.pajakdthrth.model.User currentUser = getCurrentUser();
                Dth dth = dthRepository.findByUuid(uuid)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DTH not found"));

                if (currentUser.getRole() == id.my.agungdh.pajakdthrth.model.Role.USER) {
                        if (!dth.getSkpd().getId().equals(currentUser.getSkpd().getId())) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
                        }
                }

                dthRepository.delete(dth);
        }
}
