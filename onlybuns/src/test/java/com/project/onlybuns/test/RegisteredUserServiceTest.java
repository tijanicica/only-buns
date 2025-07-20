package com.project.onlybuns.test;
import com.project.onlybuns.dto.RegistrationDto;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.RegisteredUserRepository;
import com.project.onlybuns.service.RegisteredUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegisteredUserServiceTest {

    @Autowired
    private RegisteredUserService registeredUserService;

    @Test
    public void testConcurrentRegistrationWithLatch() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        Callable<Void> task1 = () -> {
            try {
                readyLatch.countDown();
                readyLatch.await();

                registeredUserService.register(new RegistrationDto("ticaticatest8@mail.com", "password", "test8", "test8", "ticaticatest12345", "test", "test", "test", "1", 19.8489, 45.239594));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                doneLatch.countDown();
            }
            return null;
        };

        Callable<Void> task2 = () -> {
            try {
                readyLatch.countDown();
                readyLatch.await();

                registeredUserService.register(new RegistrationDto("ticaticatest7@mail.com", "password", "test7", "test7", "ticaticatest12345", "test", "test", "test", "1", 19.8489, 45.239594));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                doneLatch.countDown();
            }
            return null;
        };

        executor.submit(task1);
        executor.submit(task2);

        doneLatch.await(5, TimeUnit.SECONDS);

        executor.shutdown();

        assertEquals(1, exceptions.size(), "Expected one registration failure.");

        Exception thrownException = exceptions.get(0);
        assertInstanceOf(
                org.springframework.dao.DataIntegrityViolationException.class,
                thrownException,
                "Expected exception is DataIntegrityViolationException."
        );
    }

    @Test
    public void testConcurrentFollow() throws InterruptedException, ExecutionException {
        // Create a user to be followed
        Integer userToFollowId = createTestUser("followedUser@mail.com", "followedUser");

        // Create follower users
        createTestUser("follower1@mail.com", "follower1");
        createTestUser("follower2@mail.com", "follower2");

        // Define concurrent tasks for following
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Void> followTask1 = () -> {
            registeredUserService.followUser(userToFollowId, "follower1@mail.com");
            return null;
        };

        Callable<Void> followTask2 = () -> {
            registeredUserService.followUser(userToFollowId, "follower2@mail.com");
            return null;
        };

        // Execute the tasks concurrently
        Future<Void> future1 = executor.submit(followTask1);
        Future<Void> future2 = executor.submit(followTask2);

        Exception exception1 = null;
        Exception exception2 = null;

        try {
            future1.get();
        } catch (ExecutionException e) {
            exception1 = (Exception) e.getCause();
        }

        try {
            future2.get();
        } catch (ExecutionException e) {
            exception2 = (Exception) e.getCause();
        }

        // Verify no exceptions occurred
        Assertions.assertNull(exception1, "Task 1 threw an exception");
        Assertions.assertNull(exception2, "Task 2 threw an exception");

        // Verify the follower count was incremented correctly
        int updatedFollowerCount = registeredUserService.getNumberOfFollowers(userToFollowId);
        assertEquals(2, updatedFollowerCount, "The follower count should be 2 after concurrent follows");

        executor.shutdown();
    }

    /**
     * Helper method to create a test user using RegisteredUserService.
     */
    private Integer createTestUser(String email, String username) {
        // Create a registration DTO with the user data
        var registrationDto = new com.project.onlybuns.dto.RegistrationDto(
                email, "password", "FirstName", "LastName", username,
                "City", "Country", "StreetName", "1", 19.8489, 45.239594
        );

        // Register the user and return its ID
        registeredUserService.register(registrationDto);
        return registeredUserService.findByEmail(email).getId();
    }




}

