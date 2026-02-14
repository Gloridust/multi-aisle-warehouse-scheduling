package com.example.warehouse.service;

import com.example.warehouse.dto.ChangePasswordRequest;
import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.model.AdminUser;
import com.example.warehouse.repository.AccountLogRepository;
import com.example.warehouse.repository.AdminUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthService {
    private final AdminUserRepository adminUserRepository;
    private final AccountLogRepository accountLogRepository;

    public AuthService(AdminUserRepository adminUserRepository, AccountLogRepository accountLogRepository) {
        this.adminUserRepository = adminUserRepository;
        this.accountLogRepository = accountLogRepository;
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请输入账号与密码");
        }
        AdminUser user = adminUserRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "账号或密码错误"));
        String hash = hash(request.getPassword().trim());
        if (!hash.equals(user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账号或密码错误");
        }
        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setToken("admin-token");
        return response;
    }

    public void changePassword(ChangePasswordRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账号不能为空");
        }
        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请输入旧密码与新密码");
        }
        AdminUser user = adminUserRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "账号不存在"));
        String oldHash = hash(request.getOldPassword().trim());
        if (!oldHash.equals(user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "旧密码不正确");
        }
        String newHash = hash(request.getNewPassword().trim());
        String updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        adminUserRepository.updatePassword(user.getId(), newHash, updatedAt);
        com.example.warehouse.model.AccountLog log = new com.example.warehouse.model.AccountLog();
        log.setUsername(user.getUsername());
        log.setAction("修改密码");
        log.setDetail("成功");
        log.setCreatedAt(updatedAt);
        accountLogRepository.insert(log);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : encoded) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "密码处理失败");
        }
    }
}
