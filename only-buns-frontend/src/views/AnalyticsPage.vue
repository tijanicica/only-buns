<template>
    <div class="container mt-5 d-flex">
        <!-- Admin Side Menu -->
        <div class="side-menu">
            <h1 class="onlybuns-title">OnlyBuns</h1>
            <ul class="menu-list">
                <li @click="$router.push('/admin/trends')" class="menu-item">
                    <i class="fas fa-chart-line"></i> Trends
                </li>
                <li @click="$router.push('/admin/analytics')" class="menu-item">
                    <i class="fas fa-chart-pie"></i> Analytics
                </li>
                <li @click="$router.push('/admin/users')" class="menu-item">
                    <i class="fas fa-users"></i> User Profiles
                </li>
            </ul>
            <button @click="logout" class="logout-btn">
                <i class="fas fa-sign-out-alt"></i> Logout
            </button>
        </div>

        <!-- Main Content -->
        <div class="content">
            <h2 class="text-center">Application Analytics</h2>

            <div v-if="analytics">
                <h3>Number of Posts</h3>
                <ul>
                    <li>Weekly: {{ analytics.weeklyPosts || 0 }}</li>
                    <li>Monthly: {{ analytics.monthlyPosts || 0 }}</li>
                    <li>Yearly: {{ analytics.yearlyPosts || 0 }}</li>
                </ul>

                <h3>Number of Comments</h3>
                <ul>
                    <li>Weekly: {{ analytics.weeklyComments || 0 }}</li>
                    <li>Monthly: {{ analytics.monthlyComments || 0 }}</li>
                    <li>Yearly: {{ analytics.yearlyComments || 0 }}</li>
                </ul>

                <h3>User Activity</h3>
                <p>Users with posts only: {{ analytics.usersWithPostsOnly || 0 }}%</p>
                <p>Users with comments only: {{ analytics.usersWithCommentsOnly || 0 }}%</p>
                <p>Inactive users: {{ analytics.inactiveUsers || 0 }}%</p>

                <canvas id="activity-chart"></canvas>
            </div>

            <div v-else>
                <p>Loading analytics...</p>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import Chart from "chart.js/auto";

export default {
    data() {
        return {
            analytics: null,
        };
    },
    created() {
        this.fetchAnalytics();
    },
    methods: {
        fetchAnalytics() {
            const token = localStorage.getItem("token");
            if (token) {
                axios
                    .get("http://localhost:8080/api/analytics/posts-comments", {
                        headers: { Authorization: `Bearer ${token}` },
                    })
                    .then((response) => {
                        console.log("Raw API Response:", response.data);

                        // Total Users and Calculations
                        const totalUsers = response.data.totalUsers || 1; // Avoid division by zero
                        const usersWithPostsOnlyPercent = (response.data.usersWithPostsOnly / totalUsers).toFixed(2);
                        const usersWithCommentsOnlyPercent = (response.data.usersWithCommentsOnly / totalUsers).toFixed(2);
                        const inactiveUsersPercent = (response.data.inactiveUsers / totalUsers).toFixed(2);

                        console.log("Calculated Percentages:", {
                            usersWithPostsOnlyPercent,
                            usersWithCommentsOnlyPercent,
                            inactiveUsersPercent,
                        });

                        this.analytics = {
                            ...response.data,
                            usersWithPostsOnly: usersWithPostsOnlyPercent,
                            usersWithCommentsOnly: usersWithCommentsOnlyPercent,
                            inactiveUsers: inactiveUsersPercent,
                        };

                        console.log("Updated Analytics Object:", this.analytics);
                        this.renderChart();
                    })
                    .catch((error) => {
                        console.error("Error fetching analytics:", error);
                    });
            } else {
                console.error("No token found. Please login.");
            }
        },
        renderChart() {
            this.$nextTick(() => {
                const ctx = document.getElementById("activity-chart");
                if (!ctx) {
                    console.error("Canvas element not found!");
                    return;
                }

                const data = [
                    this.analytics?.usersWithPostsOnly || 0,
                    this.analytics?.usersWithCommentsOnly || 0,
                    this.analytics?.inactiveUsers || 0,
                ];

                new Chart(ctx, {
                    type: "doughnut",
                    data: {
                        labels: [
                            "Users with Posts Only",
                            "Users with Comments Only",
                            "Inactive Users",
                        ],
                        datasets: [
                            {
                                data: data,
                                backgroundColor: ["#007bff", "#28a745", "#dc3545"],
                            },
                        ],
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: "top",
                            },
                            tooltip: {
                                callbacks: {
                                    label: function (tooltipItem) {
                                        const value = data[tooltipItem.dataIndex];
                                        return `${tooltipItem.label}: ${value}%`;
                                    },
                                },
                            },
                        },
                    },
                });
            });
        },
        logout() {
            localStorage.removeItem("token");
            this.$router.push("/login");
        },
    },
};
</script>

<style scoped>
.container {
    display: flex;
    flex-wrap: nowrap;
}

.side-menu {
    position: fixed;
    top: 0;
    left: 0;
    width: 250px;
    height: 100vh;
    background-color: #f9f9f9;
    padding: 20px;
    border-right: 1px solid #ddd;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.onlybuns-title {
    font-family: "Pacifico", cursive;
    font-size: 2rem;
    font-weight: bold;
    color: #007bff;
    text-align: center;
    margin-bottom: 20px;
}

.menu-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    color: #333;
    cursor: pointer;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.menu-item:hover {
    background-color: #007bff;
    color: white;
}

.logout-btn {
    background-color: #ff4d4d;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 10px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.logout-btn:hover {
    background-color: #cc0000;
}

.content {
    flex: 1;
    margin-left: 250px;
    padding: 20px;
}
</style>
