package org.example.splitbooks.services.impl;

import org.example.splitbooks.entity.Genre;
import org.example.splitbooks.entity.Language;
import org.example.splitbooks.entity.ReadingFormat;
import org.example.splitbooks.repositories.*;
import org.example.splitbooks.services.ProfileSetupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileSetupServiceImpl implements ProfileSetupService {
    private GenreRepository genreRepository;
    private LanguageRepository languageRepository;
    private ReadingFormatRepository readingFormatRepository;

    public ProfileSetupServiceImpl(GenreRepository genreRepository, LanguageRepository languageRepository,
                                   ReadingFormatRepository readingFormatRepository) {
        this.genreRepository = genreRepository;
        this.languageRepository = languageRepository;
        this.readingFormatRepository = readingFormatRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public List<ReadingFormat> getAllReadingFormats() {
        return readingFormatRepository.findAll();
    }
}
