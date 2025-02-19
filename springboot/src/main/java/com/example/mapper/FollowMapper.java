package com.example.mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper {

    @Select("SELECT COUNT(*) FROM Follow WHERE user_id = #{userId} AND business_id = #{businessId}")
    int isFollowing(@Param("userId") Integer userId, @Param("businessId") Integer businessId);

    @Insert("INSERT INTO Follow (user_id, business_id) VALUES (#{userId}, #{businessId})")
    void follow(@Param("userId") Integer userId, @Param("businessId") Integer businessId);

    @Delete("DELETE FROM Follow WHERE user_id = #{userId} AND business_id = #{businessId}")
    void unfollow(@Param("userId") Integer userId, @Param("businessId") Integer businessId);


    @Select("SELECT COUNT(*) FROM Follow WHERE business_id = #{businessId} and DATE(created_at) = CURDATE()")
    Long countTodayFollows(@Param("businessId") Integer businessId);

    @Select("SELECT COUNT(*) FROM Follow WHERE business_id = #{businessId} and DATE(created_at) = CURDATE() - INTERVAL 1 DAY")
    Long countYesterdayFollows(@Param("businessId") Integer businessId);

    @Select("SELECT COUNT(*) FROM Follow WHERE business_id = #{businessId} and MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())")
    Long countMonthlyFollows(@Param("businessId") Integer businessId);

    @Select("SELECT COUNT(*) FROM Follow WHERE business_id = #{businessId}")
    Long count(@Param("businessId") Integer businessId);
}