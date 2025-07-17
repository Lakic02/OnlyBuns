<template>
  <div class="analytics-container">
    <h1 class="dashboard-title">Analytics Dashboard</h1>
    
    <!-- General Analytics Cards -->
    <div class="analytics-cards">
      <div class="analytics-card">
        <h3>Posts</h3>
        <div class="card-content">
          <div class="stat-item">
            <span class="label">Weekly:</span>
            <span class="value">{{ weeklyPosts }}</span>
          </div>
          <div class="stat-item">
            <span class="label">Monthly:</span>
            <span class="value">{{ monthlyPosts }}</span>
          </div>
          <div class="stat-item">
            <span class="label">Yearly:</span>
            <span class="value">{{ yearlyPosts }}</span>
          </div>
        </div>
      </div>
      
      <div class="analytics-card">
        <h3>Comments</h3>
        <div class="card-content">
          <div class="stat-item">
            <span class="label">Weekly:</span>
            <span class="value">{{ weeklyComments }}</span>
          </div>
          <div class="stat-item">
            <span class="label">Monthly:</span>
            <span class="value">{{ monthlyComments }}</span>
          </div>
          <div class="stat-item">
            <span class="label">Yearly:</span>
            <span class="value">{{ yearlyComments }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Year Selector -->
    <div class="selector-section">
      <div class="selector-card">
        <h3>📅 Year Analysis</h3>
        <div class="selector-row">
          <label for="yearSelect">Select Year:</label>
          <select id="yearSelect" v-model="selectedYear" @change="onYearChange" class="custom-select">
            <option v-for="year in availableYears" :key="year" :value="year">
              {{ year }}
            </option>
          </select>
        </div>
        
        <div v-if="selectedYear" class="results-grid">
          <div class="result-item posts">
            <div class="result-number">{{ postsForSelectedYear }}</div>
            <div class="result-label">Posts in {{ selectedYear }}</div>
          </div>
          <div class="result-item comments">
            <div class="result-number">{{ commentsForSelectedYear }}</div>
            <div class="result-label">Comments in {{ selectedYear }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Month Selector -->
    <div class="selector-section">
      <div class="selector-card">
        <h3>📊 Month Analysis</h3>
        <div class="selector-row">
          <label for="monthSelect">Select Month:</label>
          <select id="monthSelect" v-model="selectedMonth" @change="onMonthChange" class="custom-select">
            <option v-for="month in availableMonths" :key="month.value" :value="month.value">
              {{ month.name }}
            </option>
          </select>
        </div>
        
        <div v-if="selectedMonth" class="results-grid">
          <div class="result-item posts">
            <div class="result-number">{{ postsForSelectedMonth }}</div>
            <div class="result-label">Posts in {{ getMonthName(selectedMonth) }} {{ selectedYear }}</div>
          </div>
          <div class="result-item comments">
            <div class="result-number">{{ commentsForSelectedMonth }}</div>
            <div class="result-label">Comments in {{ getMonthName(selectedMonth) }} {{ selectedYear }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Weekly Data Table -->
    <div v-if="weeklyDataForMonth.length > 0" class="table-section">
      <div class="table-card">
        <h3>📈 Weekly Breakdown for {{ getMonthName(selectedMonth) }} {{ selectedYear }}</h3>
        <div class="table-container">
          <table class="modern-table">
            <thead>
              <tr>
                <th>Week</th>
                <th>Date Range</th>
                <th>Posts</th>
                <th>Comments</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="week in weeklyDataForMonth" :key="week.weekNumber">
                <td>
                  <span class="week-badge">Week {{ week.weekNumber }}</span>
                </td>
                <td class="date-range">
                  {{ formatDate(week.startDate) }} - {{ formatDate(week.endDate) }}
                </td>
                <td>
                  <span class="count-badge posts-badge">{{ week.posts }}</span>
                </td>
                <td>
                  <span class="count-badge comments-badge">{{ week.comments }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>👥 User Activity Distribution</h3>
        <canvas id="userActivityChart"></canvas>
      </div>

      <div class="chart-card">
        <h3>📊 Activity Overview</h3>
        <canvas id="salesChart"></canvas>
      </div>
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
      selectedYear: 2025,
      availableYears: [2024, 2025],
      postsForSelectedYear: 0,
      commentsForSelectedYear: 0,
      
      selectedMonth: new Date().getMonth() + 1,
      availableMonths: [
        { value: 1, name: 'January' },
        { value: 2, name: 'February' },
        { value: 3, name: 'March' },
        { value: 4, name: 'April' },
        { value: 5, name: 'May' },
        { value: 6, name: 'June' },
        { value: 7, name: 'July' },
        { value: 8, name: 'August' },
        { value: 9, name: 'September' },
        { value: 10, name: 'October' },
        { value: 11, name: 'November' },
        { value: 12, name: 'December' }
      ],
      postsForSelectedMonth: 0,
      commentsForSelectedMonth: 0,
      weeklyDataForMonth: [],
    };
  },
  mounted() {
    this.fetchAnalytics();
    this.fetchData();
    this.fetchDataByYear();
    this.fetchDataByMonth();
    this.fetchWeeklyDataForMonth();
  },
  methods: {
    async onYearChange() {
      // Kada se promeni godina, pozovi sve fetch metode
      await this.fetchDataByYear();
      await this.fetchDataByMonth();
      await this.fetchWeeklyDataForMonth();
    },
    async onMonthChange() {
      // Kada se promeni mesec, pozovi fetch za mesec i nedelje
      await this.fetchDataByMonth();
      await this.fetchWeeklyDataForMonth();
    },
    async fetchDataByYear() {
      if (!this.selectedYear) return;
      
      try {
        const [postsResponse, commentsResponse] = await Promise.all([
          axios.get(`http://localhost:8081/api/analytics/posts-by-year/${this.selectedYear}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
          }),
          axios.get(`http://localhost:8081/api/analytics/comments-by-year/${this.selectedYear}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
          })
        ]);

        this.postsForSelectedYear = postsResponse.data;
        this.commentsForSelectedYear = commentsResponse.data;
      } catch (error) {
        console.error('Failed to fetch data by year', error);
      }
    },
    async fetchDataByMonth() {
      if (!this.selectedYear || !this.selectedMonth) return;
      
      try {
        const [postsResponse, commentsResponse] = await Promise.all([
          axios.get(`http://localhost:8081/api/analytics/posts-by-month/${this.selectedYear}/${this.selectedMonth}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
          }),
          axios.get(`http://localhost:8081/api/analytics/comments-by-month/${this.selectedYear}/${this.selectedMonth}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
          })
        ]);

        this.postsForSelectedMonth = postsResponse.data;
        this.commentsForSelectedMonth = commentsResponse.data;
      } catch (error) {
        console.error('Failed to fetch data by month', error);
      }
    },
    getMonthName(monthNumber) {
      const month = this.availableMonths.find(m => m.value === monthNumber);
      return month ? month.name : '';
    },
    async fetchWeeklyDataForMonth() {
      if (!this.selectedYear || !this.selectedMonth) return;
      
      try {
        const response = await axios.get(`http://localhost:8081/api/analytics/weeks-in-month/${this.selectedYear}/${this.selectedMonth}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
        });

        this.weeklyDataForMonth = response.data;
      } catch (error) {
        console.error('Failed to fetch weekly data for month', error);
        this.weeklyDataForMonth = [];
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric' 
      });
    },
    async fetchAnalytics() {
      try {
        const postResponse = await axios.get('http://localhost:8081/api/analytics/posts', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
        });
        this.weeklyPosts = postResponse.data.weeklyPosts;
        this.monthlyPosts = postResponse.data.monthlyPosts;
        this.yearlyPosts = postResponse.data.yearlyPosts;

        const commentResponse = await axios.get('http://localhost:8081/api/analytics/comments', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
        });
        this.weeklyComments = commentResponse.data.weeklyComments;
        this.monthlyComments = commentResponse.data.monthlyComments;
        this.yearlyComments = commentResponse.data.yearlyComments;
      } catch (error) {
        console.error('Failed to fetch analytics', error);
      }
    },

    async fetchData() {
      try {
        const response = await axios.get('http://localhost:8081/api/analytics/user-activity', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
        });
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
.analytics-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.dashboard-title {
  text-align: center;
  color: #1a202c;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Analytics Cards */
.analytics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.analytics-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 16px;
  padding: 25px;
  color: white;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.analytics-card:hover {
  transform: translateY(-5px);
}

.analytics-card h3 {
  margin: 0 0 20px 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.value {
  font-size: 1.2rem;
  font-weight: 700;
}

/* Selector Sections */
.selector-section {
  margin-bottom: 30px;
}

.selector-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.selector-card h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 1.3rem;
  font-weight: 600;
}

.selector-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.selector-row label {
  font-weight: 500;
  color: #4a5568;
  min-width: 120px;
}

.custom-select {
  padding: 10px 15px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 1rem;
  background: white;
  color: #2d3748;
  transition: all 0.3s ease;
  min-width: 150px;
}

.custom-select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.result-item {
  text-align: center;
  padding: 20px;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.result-item:hover {
  transform: scale(1.02);
}

.result-item.posts {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.result-item.comments {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.result-number {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.result-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

/* Table Section */
.table-section {
  margin-bottom: 30px;
}

.table-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.table-card h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 1.3rem;
  font-weight: 600;
}

.table-container {
  overflow-x: auto;
}

.modern-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.modern-table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px;
  text-align: left;
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.modern-table td {
  padding: 15px;
  border-bottom: 1px solid #f1f5f9;
  transition: background-color 0.3s ease;
}

.modern-table tr:hover td {
  background-color: #f8fafc;
}

.week-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.date-range {
  color: #4a5568;
  font-weight: 500;
}

.count-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.posts-badge {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.comments-badge {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

/* Charts Section */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 30px;
  margin-top: 30px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  text-align: center;
}

.chart-card h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 1.3rem;
  font-weight: 600;
}

canvas {
  max-width: 100%;
  height: auto !important;
}

/* Responsive */
@media (max-width: 768px) {
  .analytics-container {
    padding: 15px;
  }
  
  .dashboard-title {
    font-size: 2rem;
  }
  
  .selector-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .selector-row label {
    min-width: auto;
    margin-bottom: 5px;
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
}
</style>