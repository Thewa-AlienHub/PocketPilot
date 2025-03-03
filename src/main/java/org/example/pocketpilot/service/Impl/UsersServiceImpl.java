package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.dto.RequestDTO.LoginRequestDTO;
import org.example.pocketpilot.dto.ResponseDTO.LoginResponseDTO;
import org.example.pocketpilot.dto.RequestDTO.SignUpRequestDTO;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.example.pocketpilot.enums.UserRole;
import org.example.pocketpilot.model.UserModel;
import org.example.pocketpilot.repository.UserRepository;
import org.example.pocketpilot.service.UsersService;
import org.example.pocketpilot.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServiceImpl  extends ResponseController implements UsersService  {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public ResponseEntity<Object> authenticate(LoginRequestDTO dto) {
        try {
            // Authenticate user using repository
            UserModel user = userRepository.authenticateUser(dto);

            // Generate token
            String token = jwtUtil.generateToken(dto.getUsername(), user.getUserRole(), user.getId());

            // Create login response
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
            loginResponseDTO.setToken(token);
            loginResponseDTO.setUserRole(user.getUserRole());
            loginResponseDTO.setUserName(user.getUserName());

            return sendResponse(new Response(HttpStatus.OK, "Login successful", loginResponseDTO));

        } catch (ResponseStatusException ex) {

            return sendResponse(new ErrorMessage((HttpStatus) ex.getStatusCode(), ex.getReason()));

        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
        }
    }
    @Override
    public ResponseEntity<Object> signUp(SignUpRequestDTO dto) {
        try {
            // Check if the user already exists
            boolean userExist = userRepository.userExists(dto.getEmail());
            if (userExist) {
                return sendResponse(new ErrorMessage(HttpStatus.CONFLICT, "User with this email already exists"));
            }

            // Validate password confirmation
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                return sendResponse(new ErrorMessage(HttpStatus.BAD_REQUEST, "Passwords do not match"));
            }

            // Generate unique username
            String generatedUserName = genarateUserName(dto.getName());

            // Validate user role
            UserRole userRole;
            try {
                userRole = UserRole.fromCode(dto.getUserRole());
            } catch (IllegalArgumentException e) {
                return sendResponse(new ErrorMessage(HttpStatus.BAD_REQUEST, "Invalid role code"));
            }

            // Create and map UserModel
            UserModel userModel = new UserModel();
            userModel.setUserName(generatedUserName);
            userModel.setPassword(dto.getPassword());
            userModel.setEmail(dto.getEmail());
            userModel.setName(dto.getName());
            userModel.setUserRole(userRole.getRoleName());

            // Save user
            userRepository.save(userModel);

            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during signup"));
        }
    }

    @Override
    public ResponseEntity<Object> getUsers() {

        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userName = authentication.getName();
            String userRole = authentication.getAuthorities().toString();

            if (userName != null && userName != "" && userRole.equals("Admin")) {

                List<UserModel> user = userRepository.getUsers();
                return sendResponse(new Response(ResponseMessage.SUCCESS, "User Received Successfully", user));
            } else {

                return sendResponse(new Response(ResponseMessage.SUCCESS, "No permission to view all users", null));

            }

        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during get users"));
        }


    }

    @Override
    public ResponseEntity<Object> getUserPrfile() {

        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }

            String userName = authentication.getName();
            System.out.println(userName);

            if (StringUtils.hasText(userName)) {

                UserModel user = userRepository.getUserByUserName(userName);
                System.out.println(user);
                return sendResponse(new Response(HttpStatus.OK, "User Received Successfully", user));
            } else {

                return sendResponse(new Response(HttpStatus.NOT_FOUND, "Users Not found", null));

            }
        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during get users"));
        }
    }


    private String genarateUserName(String name){
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String sanitizedUsername = name.replaceAll("\\s+", "").toLowerCase();
        return sanitizedUsername + uniqueId;

    }





}
