<template>
  <div class="invisible-grid">
    <div class="grid-item trending-title" style="grid-column: 3; grid-row: 1;">
      <h1>Trending</h1>
    </div>

    <div class="grid-item trending-info" style="grid-column: 2; grid-row: 2;">
      <label>Total Posts Number: <strong>{{ totalPosts }}</strong></label>
    </div>

    <div class="grid-item trending-info" style="grid-column: 4; grid-row: 2;">
      <label>Posts Number in last 7 days: <strong>{{ postsLast7Days }}</strong></label>
    </div>

    <div class="grid-item trending-subtitle" style="grid-column: 2/4; grid-row: 3;">
      <label>Top 10 Most Active Accounts in last 7 days:</label>
    </div>

    <div class="grid-item trending-table-wrapper" style="grid-column: 2/4; grid-row: 4;">
      <table class="trending-table">
        <thead>
          <tr>
            <th class="trending-th">First Name</th>
            <th class="trending-th">Last Name</th>
            <th class="trending-th">Number of Likes</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in mostActiveUsers" :key="user.id" class="trending-tr">
            <td class="trending-td">{{ user.firstName }}</td>
            <td class="trending-td">{{ user.lastName }}</td>
            <td class="trending-td">{{ user.numberOfLike}}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "InvisibleGrid",
  data() {
    return {
      totalPosts: 0,
      postsLast7Days: 0,
      mostActiveUsers: [],
    };
  },
  mounted() {
    this.fetchMostActiveUsers();
  },
  methods: {
    async fetchMostActiveUsers() {
      try {
        const response = await axios.get();
      } catch (error) {
        console.error('Error fetching trending data:', error);
      }
    },
  },
};
</script>

<style>
.invisible-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  grid-template-rows: repeat(10, auto);
  gap: 15px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 12px; /* malo vazduha oko sadržaja */
}

.grid-item {
  pointer-events: auto;
  visibility: visible;
}

/* Naslov */
.trending-title h1 {
  margin: 0;
  text-align: center;
  color: var(--clr-black, #1f1f1f);   /* fallback ako nema var */
  font-size: 2rem;
  font-weight: 700;
}

/* Info kartice (Total / Last 7 days) */
.grid-item.trending-info {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.05rem;
  color: var(--clr-black, #222);
  background-color: #f7f7f7;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 12px 14px;
}

/* Podnaslov */
.grid-item.trending-subtitle label {
  display: block;
  text-align: center;
  font-weight: 600;
  font-size: 1.15rem;
  color: var(--clr-black, #1f1f1f);
  margin: 4px 0 2px;
}

/* Omotač tabele (kartica) */
.grid-item.trending-table-wrapper {
  overflow-x: auto;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
  border: 1px solid #eee;
  padding: 0; /* tabela će popuniti */
}

/* Tabela */
.trending-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #fff;
}

/* Header ćelije – namećemo i preko klase i preko elementa radi sigurnosti */
.trending-table thead th,
.trending-th {
  background-color: var(--clr-black, #2b2b2b); /* fallback crna */
  color: #fff;
  padding: 12px 14px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap; /* da naslovi ne pucaju u više redova */
}

/* Telo tabele */
.trending-table tbody td,
.trending-td {
  padding: 12px 14px;
  border-bottom: 1px solid #eee;
  color: var(--clr-black, #333);
  background: #fff;
}

/* Hover redova */
.trending-table tbody tr:hover,
.trending-tr:hover {
  background-color: #f9f9f9;
}

/* Malo više “mesa” na širim ekranima */
@media (min-width: 1024px) {
  .grid-item.trending-info { font-size: 1.1rem; }
  .trending-title h1 { font-size: 2.1rem; }
}

</style>
