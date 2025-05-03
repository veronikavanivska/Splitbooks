package org.example.splitbooks.services;

import org.example.splitbooks.entity.Genre;
import org.example.splitbooks.entity.Language;
import org.example.splitbooks.entity.ReadingFormat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProfileSetupService {

    public List<Genre> getAllGenres();
    public List<Language> getAllLanguages();
    public List<ReadingFormat> getAllReadingFormats();
}
