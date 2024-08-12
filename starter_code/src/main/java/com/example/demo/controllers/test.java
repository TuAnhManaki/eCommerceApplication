package com.example.demo.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test {

	 private static BCryptPasswordEncoder bCryptPasswordEncoder;

	    public static void main(String[] args) {
	        // Khởi tạo đối tượng BCryptPasswordEncoder
	        bCryptPasswordEncoder = new BCryptPasswordEncoder();

	        // Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
	        String encodedPassword = bCryptPasswordEncoder.encode("1234w");

	        System.out.println("Mật khẩu đã mã hóa: " + encodedPassword);
	    }
}
