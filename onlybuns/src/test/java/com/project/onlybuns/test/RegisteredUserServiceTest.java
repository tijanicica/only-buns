package com.project.onlybuns.test;
import com.project.onlybuns.dto.RegistrationDto;
import com.project.onlybuns.repository.RegisteredUserRepository;
import com.project.onlybuns.service.RegisteredUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RegisteredUserServiceTest {

    @Autowired
    private RegisteredUserService registeredUserService;

    @Test
    public void testConcurrentRegistration() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Void> task1 = () -> {
            registeredUserService.register(new RegistrationDto("ticatica1@mail.com", "password", "test2", "test", "ticatica", "test", "test", "test", "1"));
            return null;
        };

        Callable<Void> task2 = () -> {
            registeredUserService.register(new RegistrationDto("ticatica2@mail.com", "password", "test3", "test", "ticatica", "test", "test", "test", "1"));
            return null;
        };

        Future<Void> future1 = executor.submit(task1);
        Future<Void> future2 = executor.submit(task2);

        Exception exception1 = null;
        Exception exception2 = null;

        try {
            future1.get();
        } catch (ExecutionException e) {
            exception1 = (Exception) e.getCause(); // Get the actual cause of the exception
        }

        try {
            future2.get();
        } catch (ExecutionException e) {
            exception2 = (Exception) e.getCause();
        }

         // Asserting that one of the exceptions throws DataConstraintViolation
        Exception finalException = exception1;
        Exception finalException1 = exception2;
        assertThrows(DataIntegrityViolationException.class, () -> {
            if (finalException instanceof DataIntegrityViolationException) {
                throw finalException;
            } else if (finalException1 instanceof DataIntegrityViolationException) {
                throw finalException1;
            }
        });

        executor.shutdown();
    }

}

