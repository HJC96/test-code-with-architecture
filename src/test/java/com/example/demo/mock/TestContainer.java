package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.post.controller.PostController;
import com.example.demo.post.controller.PostCreateController;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.port.*;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {
    public final MailSender mailSender;
    public final UserReadService userReadService;
    public final UserUpdateService userUpdateService;
    public final UserCreateService userCreateService;
    public final AuthenticationService authenticationService;
    public final PostService postService;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final CertificationService certificationService;
    public final UserController userController;
    public final UserCreateController userCreateController;
    public final PostController postController;
    public final PostCreateController postcreateController;


    @Builder
    public TestContainer(ClockHolder clockHolder, UuidHolder uUidHolder) {
        this.mailSender = new FakeMailSender();
        this.userRepository = new FakeUserRepository();
        this.postRepository = new FakePostRepository();
        this.postService = PostServiceImpl
                .builder()
                .postRepository(postRepository)
                .userRepository(userRepository)
                .clockHolder(clockHolder)
                .build();
        this.certificationService = new CertificationService(mailSender);
        UserServiceImpl userService = UserServiceImpl
                .builder()
                .uuidHolder(uUidHolder)
                .userRepository(userRepository)
                .clockHolder(clockHolder)
                .certificationService(new CertificationService(mailSender))
                .build();
        this.userReadService = userService;
        this.userUpdateService = userService;
        this.userCreateService = userService;
        this.authenticationService = userService;
        this.userController = UserController.builder()
                .userReadService(userReadService)
                .userUpdateService(userUpdateService)
                .userCreateService(userCreateService)
                .authenticationService(authenticationService)
                .build();
        this.userCreateController = UserCreateController.builder()
                .userCreateService(userCreateService)
                .build();
        this.postController = PostController.builder()
                .postService(postService)
                .build();
        this.postcreateController = PostCreateController.builder()
                .postService(postService)
                .build();
    }
}
