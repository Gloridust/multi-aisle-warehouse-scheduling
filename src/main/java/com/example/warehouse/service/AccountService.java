package com.example.warehouse.service;

import com.example.warehouse.dto.AccountProfileRequest;
import com.example.warehouse.model.AccountLog;
import com.example.warehouse.model.AccountProfile;
import com.example.warehouse.repository.AccountLogRepository;
import com.example.warehouse.repository.AccountProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private final AccountProfileRepository accountProfileRepository;
    private final AccountLogRepository accountLogRepository;

    public AccountService(AccountProfileRepository accountProfileRepository, AccountLogRepository accountLogRepository) {
        this.accountProfileRepository = accountProfileRepository;
        this.accountLogRepository = accountLogRepository;
    }

    public AccountProfile getProfile(String username) {
        String normalized = normalize(username);
        return accountProfileRepository.findByUsername(normalized)
                .orElseGet(() -> createDefaultProfile(normalized));
    }

    public AccountProfile updateProfile(AccountProfileRequest request) {
        String normalized = normalize(request.getUsername());
        AccountProfile existing = accountProfileRepository.findByUsername(normalized)
                .orElseGet(() -> createDefaultProfile(normalized));
        AccountProfile updated = new AccountProfile();
        updated.setId(existing.getId());
        updated.setUsername(existing.getUsername());
        updated.setNickname(request.getNickname());
        updated.setEmail(request.getEmail());
        updated.setPhone(request.getPhone());
        updated.setAvatarBase64(request.getAvatarBase64());
        updated.setUpdatedAt(now());
        accountProfileRepository.update(updated);
        logProfileUpdate(existing, updated);
        return updated;
    }

    public List<AccountLog> getLogs(String username, int limit) {
        String normalized = normalize(username);
        int safeLimit = limit <= 0 ? 50 : Math.min(limit, 200);
        return accountLogRepository.findByUsername(normalized, safeLimit);
    }

    private String normalize(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账号不能为空");
        }
        return username.trim();
    }

    private AccountProfile createDefaultProfile(String username) {
        AccountProfile profile = new AccountProfile();
        profile.setUsername(username);
        profile.setNickname("管理员");
        profile.setEmail("");
        profile.setPhone("");
        profile.setAvatarBase64("");
        profile.setUpdatedAt(now());
        Long id = accountProfileRepository.insert(profile);
        profile.setId(id);
        return profile;
    }

    private void logProfileUpdate(AccountProfile before, AccountProfile after) {
        List<String> changes = new ArrayList<>();
        if (!safeEquals(before.getNickname(), after.getNickname())) {
            changes.add("昵称");
        }
        if (!safeEquals(before.getEmail(), after.getEmail())) {
            changes.add("邮箱");
        }
        if (!safeEquals(before.getPhone(), after.getPhone())) {
            changes.add("电话");
        }
        if (!safeEquals(before.getAvatarBase64(), after.getAvatarBase64())) {
            changes.add("头像");
        }
        String detail = changes.isEmpty() ? "无变化" : String.join("、", changes);
        AccountLog log = new AccountLog();
        log.setUsername(after.getUsername());
        log.setAction("更新资料");
        log.setDetail(detail);
        log.setCreatedAt(now());
        accountLogRepository.insert(log);
    }

    private boolean safeEquals(String left, String right) {
        if (left == null) {
            return right == null || right.isEmpty();
        }
        if (right == null) {
            return left.isEmpty();
        }
        return left.equals(right);
    }

    private String now() {
        return ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
