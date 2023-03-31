package ru.tinkoff.likeinscala.springexample;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringExampleApplication {

    @Value
    static class User {
        int id;
        String name;
    }

    interface UserDao {
        void save(User user);
    }

    @Component
    @Profile("test")
    static class TestUserDao implements UserDao {
        @Override
        public void save(User user) {
            System.out.printf("User %s should be saved into database here%n", user);
        }
    }

    @Component
    @Profile("prod")
    static class ProdUserDao implements UserDao {
        @Override
        public void save(User user) {
        }
    }

    @RequiredArgsConstructor
    @Component
    static class Service {
        private final UserDao dao;

        void save(User user) {
            System.out.println("Saving user " + user);
            dao.save(user);
        }
    }

    private final Service service;

    @PostConstruct
    public void saveUser() {
        var user = new User(1, "Andrey Golikov");
        service.save(user);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringExampleApplication.class, args);
    }

}
