package com.cvgenerator.service;

import com.cvgenerator.domain.dto.UserCvDto;
import com.cvgenerator.domain.dto.UserDto;
import com.cvgenerator.domain.entity.Language;
import com.cvgenerator.domain.entity.User;
import com.cvgenerator.domain.enums.LanguageLevel;
import com.cvgenerator.exceptions.notfound.LanguageNotFoundException;
import com.cvgenerator.repository.LanguageRepository;
import com.cvgenerator.service.implementation.LanguageServiceImpl;
import com.cvgenerator.service.implementation.UserCvServiceImpl;
import com.cvgenerator.service.implementation.UserServiceImpl;
import com.cvgenerator.utils.service.implementation.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LanguageServiceTest {

    @Autowired private LanguageServiceImpl languageService;
    @Autowired private LanguageRepository repository;
    @Autowired private UserServiceImpl userService;
    @Autowired private UserCvServiceImpl userCvService;
    @MockBean private MailServiceImpl mailService;
    private Language language;

    @BeforeEach
    void setUp() {

        BDDMockito.doNothing().when(mailService).sendConfirmationEmail(ArgumentMatchers.any(User.class));

        UserCvDto userCvDto = new UserCvDto.UserCvDtoBuilder()
                .setId(1L)
                .setName("Dominik cv")
                .setTemplateName("Aquarius")
                .buildUserCvDto();

        UserDto userDto = new UserDto.UserDtoBuilder()
                .setId(1L)
                .setFirstName("Dominik")
                .setLastName("Janiga")
                .setEmail("dominikjaniga91@gmail.com")
                .setPassword("dominik123")
                .setRole("USER")
                .setActive(true)
                .buildUserDto();

        language = Language.builder()
                    .id(1L)
                    .name("English")
                    .level(LanguageLevel.B2)
                    .build();

        userService.saveUser(userDto);
        userCvService.saveUserCv(1L, userCvDto);
        languageService.createLanguage(1L, language);
    }


    @Test
    @DisplayName("Should return appropriate language from database")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnAppropriateLanguageFromDatabase(){
        Language foundedLanguage = repository.findById(1L).orElseThrow();
        assertEquals("English", foundedLanguage.getName());
    }

    @Test
    @DisplayName("Should return new language level after update")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnNewLanguageLevelAfterUpdate(){

        LanguageLevel expected = LanguageLevel.C1;
        language.setLevel(expected);
        languageService.updateLanguage(language);
        Language foundedLanguage = repository.findById(1L).orElseThrow();
        assertEquals(expected, foundedLanguage.getLevel());
    }

    @Test
    @DisplayName("Should thrown an exception after try to delete non existing language ")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldThrownAnException_afterTryToDeleteNonExistingLanguage(){

        LanguageNotFoundException exception = assertThrows(LanguageNotFoundException.class, () -> languageService.deleteLanguageById(10L));
        assertEquals("Language does not exist", exception.getMessage());
    }
}