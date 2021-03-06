package com.cvgenerator.controller;

import com.cvgenerator.domain.entity.Token;
import com.cvgenerator.domain.entity.User;
import com.cvgenerator.service.implementation.TokenServiceImpl;
import com.cvgenerator.service.implementation.UserServiceImpl;
import com.cvgenerator.utils.service.implementation.MailServiceImpl;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Optional;

@Api(tags = "Confirmation token controller")
@RestController
@RequestMapping("/api")
public class ConfirmTokenController {

    private final TokenServiceImpl tokenService;
    private final UserServiceImpl userService;
    private final MailServiceImpl mailService;

    @Autowired
    public ConfirmTokenController(TokenServiceImpl tokenService,
                                  UserServiceImpl userService,
                                  MailServiceImpl mailService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @ApiOperation(value = "Check confirmation link. More precisely method check if " +
                            "token value exist in database and its expiry time. " +
                            "When token exist and is not expired then user account is activate otherwise s" +
                            "end error in response")
    @ApiResponses(value = {
            @ApiResponse(code=200, message = "Your account is active"),
            @ApiResponse(code=400, message = "Confirmation link doesn't exist"),
            @ApiResponse(code=401, message = "Confirmation link expired")
    })
    @GetMapping("/token")
    public ResponseEntity<?> checkConfirmationEmailToken(@ApiParam(value = "Value of token which was send with confirmation link")
                                                         @RequestParam("value") String tokenValue){

        Optional<Token> token = tokenService.findTokenByValue(tokenValue);
        if (token.isPresent()){

            Token foundedToken = token.get();
            if(isNotExpired(foundedToken)){
                User user = foundedToken.getUser();
                user.setActive(true);
                userService.updateUser(user);
                mailService.sendWelcomeEmail(user);
                return new ResponseEntity<>("Your account is active", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Confirmation link expired", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Confirmation link doesn't exist", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isNotExpired(Token foundedToken){
        LocalDateTime expirationDate = foundedToken.getExpiryDate();
        LocalDateTime now = LocalDateTime.now();
        return expirationDate.isAfter(now);

    }
}
