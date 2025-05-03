package org.example.splitbooks.controllers;

import org.example.splitbooks.entity.Genre;
import org.example.splitbooks.entity.Language;
import org.example.splitbooks.entity.ReadingFormat;
import org.example.splitbooks.services.ProfileSetupService;
import org.example.splitbooks.services.impl.ProfileSetupServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/profile-setup")
public class ProfileSetupController {

    private  ProfileSetupServiceImpl profileSetupServiceImpl;

    public ProfileSetupController(ProfileSetupServiceImpl profileSetupServiceImpl) {
        this.profileSetupServiceImpl = profileSetupServiceImpl;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return profileSetupServiceImpl.getAllGenres();
    }

    @GetMapping("/languages")
    public List<Language> getAllLanguages() {
        return profileSetupServiceImpl.getAllLanguages();
    }

    @GetMapping("/reading-format")
    public List<ReadingFormat> getReadingFormat() {
        return profileSetupServiceImpl.getAllReadingFormats();
    }

}
