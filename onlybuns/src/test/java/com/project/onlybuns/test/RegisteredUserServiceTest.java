package com.project.onlybuns.test;
import com.project.onlybuns.dto.RegistrationDto;
import com.project.onlybuns.repository.RegisteredUserRepository;
import com.project.onlybuns.service.RegisteredUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
            registeredUserService.register(new RegistrationDto("ticatica@mail.com", "password", "test2", "test", "test1", "test", "test", "test", "1"));
            return null;
        };

        Callable<Void> task2 = () -> {
            registeredUserService.register(new RegistrationDto("ticatica@mail.com", "password", "test3", "test", "test2", "test", "test", "test", "1"));
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

        // Assert that one of the tasks threw an IllegalArgumentException
        Exception finalException = exception1;
        Exception finalException1 = exception2;
        assertThrows(IllegalArgumentException.class, () -> {
            if (finalException instanceof IllegalArgumentException) {
                throw finalException;
            } else if (finalException1 instanceof IllegalArgumentException) {
                throw finalException1;
            }
        });

        executor.shutdown();
    }

}

