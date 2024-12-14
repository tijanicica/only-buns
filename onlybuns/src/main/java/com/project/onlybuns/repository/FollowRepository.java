package com.project.onlybuns.repository;

import com.project.onlybuns.model.Follow;
import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFollowedUserEmail(String email);
    List<Follow> findByfollowerEmail(String email);

    boolean existsByFollowedUserAndFollower(RegisteredUser followedUser, RegisteredUser follower);

    // Broj pratilaca za određenog korisnika
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followedUser.id = :userId")
    int countByFollowedUserId(Integer userId);

    // Broj korisnika koje prati određeni korisnik
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    int countByFollowerId(@Param("userId") Integer userId);


    // Provera da li korisnik prati drugog korisnika
    boolean existsByFollowedUserIdAndFollowerId(Integer followedUserId, Integer followerId);


    List<Follow> findByFollowerEmail(String email);

    Optional<Follow> findByFollowedUserAndFollower(RegisteredUser followedUser, RegisteredUser follower);

    @Query("SELECT f.followedUser FROM Follow f WHERE f.follower.id = :userId")
    List<RegisteredUser> findFollowedByUser(@Param("userId") Long userId);

    List<Follow> findAllByFollowerId(Integer followerId);

    List<Follow> findAllByFollowedUser(RegisteredUser user);
    List<Follow> findAllByFollower(RegisteredUser user);




}
