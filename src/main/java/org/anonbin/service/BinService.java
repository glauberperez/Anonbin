package org.anonbin.service;

import org.anonbin.model.BinModel;
import org.anonbin.repository.BinRepository;
import org.anonbin.utils.RandomSlugGenerator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BinService {

    private final BinRepository binRepository;

    public BinService(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    public BinModel getBinBySlug(String slug) {
        return binRepository.findBySlug(slug).orElse(null);
    }

    public BinModel createBin(BinModel binModel) {
        String slug = generateUniqueSlug(binModel.getTitle());
        binModel.setSlug(slug);

        return binRepository.save(binModel);
    }

    private String generateUniqueSlug(String title) {
        String randomPart = RandomSlugGenerator.generateRandomString(8);
        String sanitizedTitle = title != null ? title.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() : "bin";
        String slug = sanitizedTitle + "-" + randomPart;
        while (binRepository.existsBySlug(slug)) {
            randomPart = RandomSlugGenerator.generateRandomString(8);
            slug = sanitizedTitle + "-" + randomPart;
        }

        return slug;
    }

    @Scheduled(fixedRate = 60000) // 60 segundos aq
    public void deleteExpiredBins() {
        Iterable<BinModel> expiredBins = binRepository.findByExpirationTimeBeforeAndExpirationTimeIsNotNull(LocalDateTime.now());

        for (BinModel bin : expiredBins) {
            binRepository.delete(bin);
        }
    }


}