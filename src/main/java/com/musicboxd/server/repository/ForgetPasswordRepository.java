package com.musicboxd.server.repository;

import com.musicboxd.server.model.ForgetPassword;
import com.musicboxd.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword,Long> {
    ForgetPassword findByOtpAndUser(String otp, User user);
}
