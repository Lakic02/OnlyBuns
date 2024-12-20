<template>
  <div>
    <h2>Analytics Dashboard</h2>
    <div>
      <p>Weekly Posts: {{ weeklyPosts }}</p>
      <p>Monthly Posts: {{ monthlyPosts }}</p>
      <p>Yearly Posts: {{ yearlyPosts }}</p>
      <p>Weekly Comments: {{ weeklyComments }}</p>
      <p>Monthly Comments: {{ monthlyComments }}</p>
      <p>Yearly Comments: {{ yearlyComments }}</p>
    </div>

    <div>
      <h2>Users Activity Analytics</h2>
      <canvas id="userActivityChart"></canvas>
    </div>

    <div>
      <h2>Sales Chart</h2>
      <canvas id="salesChart"></canvas>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

export default {
  data() {
    return {
      weeklyPosts: 0,
      monthlyPosts: 0,
      yearlyPosts: 0,
      weeklyComments: 0,
      monthlyComments: 0,
      yearlyComments: 0,
      chart: null,
      salesChart: null,
    };
  },
  mounted() {
    this.fetchAnalytics();
    this.fetchData();
  },
  methods: {
    async fetchAnalytics() {
      try {
        const postResponse = await axios.get('http://localhost:8081/api/analytics/posts');
        this.weeklyPosts = postResponse.data.weeklyPosts;
        this.monthlyPosts = postResponse.data.monthlyPosts;
        this.yearlyPosts = postResponse.data.yearlyPosts;

        const commentResponse = await axios.get('http://localhost:8081/api/analytics/comments');
        this.weeklyComments = commentResponse.data.weeklyComments;
        this.monthlyComments = commentResponse.data.monthlyComments;
        this.yearlyComments = commentResponse.data.yearlyComments;
      } catch (error) {
        console.error('Failed to fetch analytics', error);
      }
    },

    async fetchData() {
      try {
        const response = await axios.get('http://localhost:8081/api/analytics/user-activity');
        const data = response.data;
        this.renderActivityChart(data);
        this.renderSalesChart(data);  // Render sales chart with the same data
      } catch (error) {
        console.error('Greška pri dobavljanju podataka', error);
      }
    },

    renderActivityChart(data) {
      const ctx = document.getElementById('userActivityChart').getContext('2d');
      if (this.chart) this.chart.destroy();

      this.chart = new Chart(ctx, {
        type: 'pie',  // Pie chart for user activity
        data: {
          labels: ['Korisnici sa objavama', 'Samo komentari', 'Neaktivni korisnici'],
          datasets: [
            {
              data: [
                data.usersWithPostsPercentage,
                data.usersWithOnlyCommentsPercentage,
                data.inactiveUsersPercentage,
              ],
              backgroundColor: ['#36A2EB', '#FFCE56', '#FF6384'],
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
            },
          },
        },
      });
    },

    renderSalesChart(data) {
      const ctx = document.getElementById('salesChart').getContext('2d');
      if (this.salesChart) this.salesChart.destroy();

      this.salesChart = new Chart(ctx, {
        type: 'polarArea',  // Polar chart for sales analytics (can change type as needed)
        data: {
          labels: ['Korisnici sa objavama', 'Samo komentari', 'Neaktivni korisnici'],
          datasets: [
            {
              data: [
                data.usersWithPostsPercentage,
                data.usersWithOnlyCommentsPercentage,
                data.inactiveUsersPercentage,
              ],
              backgroundColor: ['#36A2EB', '#FFCE56', '#FF6384'],
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    },
  },
};
</script>

<style>
canvas {
  max-width: 400px;
  margin: auto;
}
</style>
