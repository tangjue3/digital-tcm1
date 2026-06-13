package com.ruoyi.system.domain;




public class AllCourseStarts {
    private int courseId;           // 课程ID
    private String courseName;      // 课程名称
    private double avgReviewStars;  // 评分
    private int favoriteCount;      // 收藏数量
    private int cartCount;          // 加购数量
    private double compositeScore;  // 综合评分

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getAvgReviewStars() {
        return avgReviewStars;
    }

    public void setAvgReviewStars(double avgReviewStars) {
        this.avgReviewStars = avgReviewStars;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public double getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(double compositeScore) {
        this.compositeScore = compositeScore;
    }

    @Override
    public String toString() {
        return "AllCourseStarts{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", avgReviewStars=" + avgReviewStars +
                ", favoriteCount=" + favoriteCount +
                ", cartCount=" + cartCount +
                ", compositeScore=" + compositeScore +
                '}';
    }
}
