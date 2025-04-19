//package com.example.demo.user.controller.request;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Builder;
//import lombok.Getter;
//
//@Getter
//public class UserUpdateRequest {
//    private final String nickname;
//    private final String address;
//
//    @Builder
//    public UserUpdate(
//            @JsonProperty("nickname") String nickname,
//            @JsonProperty("address") String address) {
//        this.nickname = nickname;
//        this.address = address;
//    }
//
//    public UserUpdateRequest(String nickname, String address) {
//        this.nickname = nickname;
//        this.address = address;
//    }
//}

/*이런식으로 사용자에게 노출되는 request 요청만 따로 뺄수도 있음. */