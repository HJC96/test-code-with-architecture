package com.example.demo.mock;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {

    /*
        테스트는 단일 스레드로 실행되므로 동기화가 필요없다.
    private final AtomicLong autoGenerateId = new AtomicLong(0);
    private final List<User> data = Collections.synchronizedList(new ArrayList<>());
    */

    private Long autoGenerateId = 1L;
    private List<User> data = new ArrayList<>();


    @Override
    public Optional<User> findById(long id) {
        return data.stream().filter(item->item.getId().equals(id)).findAny();
    }

    @Override
    public User getById(long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return data.stream().filter(item-> item.getId().equals(id) && item.getStatus() == userStatus).findAny();
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return data.stream().filter(item->item.getEmail().equals(email) && item.getStatus() == userStatus).findAny();
    }

    @Override
    public User save(User user) {
        if(user.getId() == null || user.getId() == 0) {
            User newUser = User.builder()
                    .id(autoGenerateId++)
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .address(user.getAddress())
                    .certificationCode(user.getCertificationCode())
                    .status(user.getStatus())
                    .lastLoginAt(user.getLastLoginAt())
                    .build();
            data.add(newUser);
            return newUser;
        }else{
            data.removeIf(item -> Objects.equals(item.getId(), user.getId()));
            data.add(user);
            return user;
        }
    }

}
