<template>
  <div class="container mt-5 d-flex">
    <!-- Admin Side Menu -->
    <div class="side-menu">
        <h1 class="onlybuns-title">OnlyBuns</h1>
        <ul class="menu-list">
            <li @click="$router.push('/admin/trends')" class="menu-item">
                <i class="fas fa-chart-line"></i> Trends
            </li>
            <li @click="$router.push('/admin/analytics')" class="menu-item active">
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

      <!-- Forma za izbor perioda -->
      <div class="filter-form">
        <div class="form-group">
          <label for="year">Year:</label>
          <select id="year" v-model="selectedYear" @change="fetchAnalytics">
            <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>
        <div class="form-group">
          <label for="month">Month:</label>
          <select id="month" v-model="selectedMonth" @change="fetchAnalytics">
            <option :value="null">All Year</option>
            <option v-for="m in 12" :key="m" :value="m">{{ m }}</option>
          </select>
        </div>
        <div class="form-group" v-if="selectedMonth">
          <label for="week">Week:</label>
          <select id="week" v-model="selectedWeek" @change="fetchAnalytics">
            <option :value="null">All Month</option>
            <option v-for="w in 5" :key="w" :value="w">{{ w }}</option>
          </select>
        </div>
      </div>

      <div v-if="analyticsData" class="analytics-results">
        <div class="stats-container">
          <h3>Analytics for Selected Period</h3>
          <p><strong>Number of Posts:</strong> {{ analyticsData.posts }}</p>
          <p><strong>Number of Comments:</strong> {{ analyticsData.comments }}</p>
        </div>
        <hr>
        <div class="chart-container">
          <h3>User Activity in Selected Period</h3>
          <p>Users with posts: {{ analyticsData.usersWithPosts.toFixed(2) }}%</p>
          <p>Users with comments only: {{ analyticsData.usersWithCommentsOnly.toFixed(2) }}%</p>
          <p>Inactive users (in this period): {{ analyticsData.inactiveUsers.toFixed(2) }}%</p>
          <canvas id="activity-chart"></canvas>
        </div>
      </div>
      <div v-else class="loading-text">Loading analytics...</div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Chart from "chart.js/auto";

export default {
    data() {
        const currentYear = new Date().getFullYear();
        return {
            analyticsData: null,
            selectedYear: currentYear,
            selectedMonth: null,
            selectedWeek: null,
            years: Array.from({length: 5}, (_, i) => currentYear - i),
            activityChart: null,
        };
    },
    created() {
        this.fetchAnalytics();
    },
    watch: {
        selectedMonth(newVal) {
            if (!newVal) {
                this.selectedWeek = null;
            }
        }
    },
    methods: {
        async fetchAnalytics() {
            this.analyticsData = null;
            const token = localStorage.getItem("token");
            if (!token) return;

            const params = { year: this.selectedYear };
            if (this.selectedMonth) params.month = this.selectedMonth;
            if (this.selectedWeek && this.selectedMonth) params.week = this.selectedWeek;
            
            try {
                const response = await axios.get("http://localhost:8080/api/analytics/all", {
                    params,
                    headers: { Authorization: `Bearer ${token}` },
                });
                this.analyticsData = response.data;
                this.renderChart();
            } catch (error) {
                console.error("Error fetching analytics:", error);
                // Opciono: prikaži poruku o grešci korisniku
            }
        },
        renderChart() {
            this.$nextTick(() => {
                const ctx = document.getElementById("activity-chart");
                if (!ctx || !this.analyticsData) return;
                if (this.activityChart) {
                    this.activityChart.destroy();
                }

                this.activityChart = new Chart(ctx, {
                    type: "doughnut",
                    data: {
                        labels: ["Users with Posts", "Users with Comments Only", "Inactive Users (in period)"],
                        datasets: [{ 
                            data: [
                                this.analyticsData.usersWithPosts,
                                this.analyticsData.usersWithCommentsOnly,
                                this.analyticsData.inactiveUsers,
                            ], 
                            backgroundColor: ["#007bff", "#28a745", "#dc3545"] 
                        }],
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'bottom',
                            },
                            tooltip: {
                                callbacks: {
                                    label: (tooltipItem) => `${tooltipItem.label}: ${tooltipItem.raw.toFixed(2)}%`,
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
.container { display: flex; flex-wrap: nowrap; }
.side-menu { position: fixed; top: 0; left: 0; width: 250px; height: 100vh; background-color: #f9f9f9; padding: 20px; border-right: 1px solid #ddd; display: flex; flex-direction: column; justify-content: space-between; box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1); }
.onlybuns-title { font-family: "Pacifico", cursive; font-size: 2rem; font-weight: bold; color: #007bff; text-align: center; margin-bottom: 20px; }
.menu-list { list-style: none; padding: 0; margin: 0; }
.menu-item { display: flex; align-items: center; gap: 10px; padding: 10px; border-radius: 8px; font-size: 16px; font-weight: 500; color: #333; cursor: pointer; transition: background-color 0.3s ease, color 0.3s ease; }
.menu-item:hover, .menu-item.active { background-color: #007bff; color: white; }
.logout-btn { background-color: #ff4d4d; color: white; border: none; border-radius: 8px; padding: 10px; font-size: 16px; font-weight: 500; cursor: pointer; transition: background-color 0.3s ease; }
.logout-btn:hover { background-color: #cc0000; }
.content { flex: 1; margin-left: 250px; padding: 20px; }
.filter-form { display: flex; gap: 20px; margin-bottom: 20px; align-items: center; background-color: #f8f9fa; padding: 15px; border-radius: 8px; }
.form-group { display: flex; flex-direction: column; }
.form-group label { margin-bottom: 5px; font-weight: bold; font-size: 0.9em; }
.form-group select { padding: 8px; border-radius: 5px; border: 1px solid #ccc; }
.stats-container { margin-bottom: 20px; }
.chart-container { max-width: 400px; margin: auto; }
.loading-text { color: #888; }
.analytics-results hr { margin: 30px 0; }
</style>