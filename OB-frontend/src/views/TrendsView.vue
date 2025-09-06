<template>
  <div class="invisible-grid">
    <div class="grid-item trending-title" style="grid-column: 3; grid-row: 1;">
      <h1>Trending</h1>
    </div>

    <div class="grid-item trending-info" style="grid-column: 2; grid-row: 2;">
      <label>Total Posts Number: <strong>{{ totalPosts }}</strong></label>
    </div>

    <div class="grid-item trending-info" style="grid-column: 4; grid-row: 2;">
      <label>Posts in last 7 days: <strong>{{ postsLast7Days }}</strong></label>
    </div>

    <div class="grid-item trending-subtitle" style="grid-column: 2/5; grid-row: 3;">
      <label>Top 10 The Most Active Accounts in last 7 days:</label>
    </div>

    <div class="grid-item trending-table-wrapper" style="grid-column: 2/5; grid-row: 4;">
      <table class="trending-table">
        <thead>
          <tr>
            <th class="trending-th">First Name</th>
            <th class="trending-th">Last Name</th>
            <th class="trending-th">Email</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in mostActiveUsers" :key="user.id" class="trending-tr">
            <td class="trending-td">{{ user.firstName }}</td>
            <td class="trending-td">{{ user.lastName }}</td>
            <td class="trending-td">{{ user.email }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Najpopularnije objave (7 dana) -->
    <div class="grid-item trending-subtitle" style="grid-column: 2/5; grid-row: 5;">
      <label>Top 5 Most Popular Posts in last 7 days:</label>
    </div>

    <div class="grid-item post-container" style="grid-column: 1/5; grid-row: 6;">
      <div v-for="post in mostPopularPostsInLast7Days" :key="post.id" class="post-card" @click="navigateToPostDetails(post.id)">
        <div class="post-header">
          <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
          <div class="post-meta">
            <h3>{{ post.account.userName }}</h3>
            <p class="post-date">{{ formatDate(post.creationTime) }}</p>
          </div>
        </div>

        <div class="post-body">
          <p>{{ post.content }}</p>
          <img v-if="post.imagePath" :src="post.imagePath" alt="Post Image" class="post-image" />
        </div>

        <div class="post-footer">
          <span>{{ postLikes[post.id] || 0 }} Likes</span>
        </div>
      </div>
    </div>

    <!-- Najpopularnije objave (all time) -->
    <div class="grid-item trending-subtitle" style="grid-column: 2/5; grid-row: 7;">
      <label>Top 10 Most Popular Posts (All Time):</label>
    </div>

    <div class="grid-item post-container" style="grid-column: 2/5; grid-row: 8;">
      <div v-for="post in mostPopularPostsOfAllTime" :key="post.id" class="post-card" @click="navigateToPostDetails(post.id)">
        <div class="post-header">
          <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
          <div class="post-meta">
            <h3>{{ post.account.userName }}</h3>
            <p class="post-date">{{ formatDate(post.creationTime) }}</p>
          </div>
        </div>

        <div class="post-body">
          <p>{{ post.content }}</p>
          <img v-if="post.imagePath" :src="post.imagePath" alt="Post Image" class="post-image" />
        </div>

        <div class="post-footer">
          <span>{{ postLikes[post.id] || 0 }} Likes</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "InvisibleGrid",
  data() {
    return {
      totalPosts: 0,
      postsLast7Days: 0,
      mostActiveUsers: [],
      mostPopularPostsInLast7Days: [],
      mostPopularPostsOfAllTime: [],
      postLikes: {} // Dodato za broj lajkova
    };
  },
  mounted() {
    this.fetchTrendingData();
  },
  methods: {
    async fetchTrendingData() {
      try {
        const responseTotalPosts = await axios.get("http://localhost:8081/api/posts/getAllPostCount");
        this.totalPosts = responseTotalPosts.data;

        const responsePostsLast7Days = await axios.get("http://localhost:8081/api/posts/countPostsLast7Days");
        this.postsLast7Days = responsePostsLast7Days.data;

        const responseMostActiveUsers = await axios.get("http://localhost:8081/api/accounts/get10MostActiveAccount");
        this.mostActiveUsers = responseMostActiveUsers.data;

        while (this.mostActiveUsers.length < 10) {
          const copy = { ...this.mostActiveUsers[this.mostActiveUsers.length % 2] };
          copy.id = copy.id + "_copy" + this.mostActiveUsers.length;
          this.mostActiveUsers.push(copy);
        }

        const responseMostPopularPostsInLast7Days = await axios.get("http://localhost:8081/api/posts/get5MostPopularPosts");
        this.mostPopularPostsInLast7Days = responseMostPopularPostsInLast7Days.data;

        const responseMostPopularPostsOfAllTime = await axios.get("http://localhost:8081/api/posts/get10MostLikedPosts");
        this.mostPopularPostsOfAllTime = responseMostPopularPostsOfAllTime.data;

        // Fetch slike i lajkove za sve postove
        [...this.mostPopularPostsInLast7Days, ...this.mostPopularPostsOfAllTime].forEach(post => {
          this.fetchPostFile(post.id);
          this.fetchPostLikes(post.id);
        });

      } catch (error) {
        console.error("Error fetching trending data:", error);
      }
    },
    navigateToPostDetails(postId) {
      this.$router.push(`/PostDetails/${postId}`);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString() + " " + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    async fetchPostFile(postId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/posts/getFile/${postId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
        });

        let imagePath = response.data.imagePath;
        let compress = response.data.compress;
        imagePath = imagePath.replace(/\\/g, "/");

        const fullPath = compress === 1
          ? `http://localhost:8081/compressedImages/${imagePath}`
          : `http://localhost:8081/images/${imagePath}`;

       const targetInLast7Days = this.mostPopularPostsInLast7Days.find(p => p.id == postId);
       const targetInAllTime = this.mostPopularPostsOfAllTime.find(p => p.id == postId);
        if (targetInLast7Days) targetInLast7Days.imagePath = fullPath;
        if (targetInAllTime) targetInAllTime.imagePath = fullPath;
      } catch (error) {
        console.error("Error fetching file:", error);
      }
    },
    async fetchPostLikes(postId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/posts/likes/count/${postId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
        });
        this.postLikes[postId] = response.data;
      } catch (error) {
        console.error("Error fetching likes:", error);
        this.postLikes[postId] = 0;
      }
    }
  }
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
