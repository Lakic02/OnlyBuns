<template>
  <div class="admin-users-container">
    <h1 class="admin-users-title">All My Followers</h1>
    
    <!-- Pretraga 
    <div class="admin-users-search">
      <input v-model="search.firstName" placeholder="First Name" @input="fetchUsers" class="admin-users-input"/>
      <input v-model="search.lastName" placeholder="Last Name" @input="fetchUsers" class="admin-users-input"/>
      <input v-model="search.email" placeholder="Email" @input="fetchUsers" class="admin-users-input"/>
      <input v-model.number="search.minPosts" placeholder="Min. number of posts" @input="fetchUsers" class="admin-users-input"/>
      <input v-model.number="search.maxPosts" placeholder="Max. number of posts" @input="fetchUsers" class="admin-users-input"/>
    </div> -->

    <!-- Sortiranje 
    <div class="admin-users-sort-wrapper">
      <select v-model="sortField" @change="fetchUsers" class="admin-users-select">
        <option value="email">Sort by Email</option>
        <option value="followingCount">Sort by Following Number</option>
      </select>
      <select v-model="sortDir" @change="fetchUsers" class="admin-users-select">
        <option value="asc">Ascending (ASC)</option>
        <option value="desc">Descending (DESC)</option>
      </select>
    </div> -->
    
    <!-- Tabela korisnika -->
    <div class="admin-users-table-wrapper">
      <table class="admin-users-table">
        <thead>
          <tr>
            <th class="admin-users-th">First Name</th>
            <th class="admin-users-th">Last Name</th>
            <th class="admin-users-th">Email</th>
            
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users.content" :key="user.id" class="admin-users-tr" 
          @click="navigateToUser(user.id)" style="cursor: pointer;">
            <td class="admin-users-td">{{ user.firstName }}</td>
            <td class="admin-users-td">{{ user.lastName }}</td>
            <td class="admin-users-td">{{ user.email }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Paginacija 
    <div class="admin-users-pagination"> 
      <button 
        :disabled="users.first" 
        @click="changePage(users.pageable.pageNumber - 1)"
        class="admin-users-pagination-btn"
      >Prethodna</button>
      <button 
        :disabled="users.last" 
        @click="changePage(users.pageable.pageNumber + 1)"
        class="admin-users-pagination-btn"
      >Sledeća</button> 
    </div> -->
  </div>
</template>

<script>
// Script deo ostaje isti
import axios from 'axios';
import { get } from 'ol/proj';

export default {
  data() {
    return {
      users: { content: [], pageable: {} },
      search: {
        firstName: '',
        lastName: '',
        email: '',
        minPosts: null,
        maxPosts: null,
      },
      sortField: 'email',
      sortDir: 'asc',
      currentPage: 0,
      pageSize: 5,
      loggedInUserId: null
    };
  },
  mounted() {
    this.loadUserId().then(() => {
      this.loadAllFollowers();
    });
  },
  methods: {
    fetchUsers() {

      console.log(this.sortField);
      console.log(this.sortDir);
      axios.get('http://localhost:8081/api/accounts/getAll', {
        params: {
          firstName: this.search.firstName,
          lastName: this.search.lastName,
          email: this.search.email,
          minPosts: this.search.minPosts,
          maxPosts: this.search.maxPosts,
          page: this.currentPage,
          size: this.pageSize,
          sortField: this.sortField,
          sortDir: this.sortDir,
        },
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
      })
      .then(async (response) => {
        this.users = response.data;

        for (let user of this.users.content) {
          const postResponse = await axios.get(`http://localhost:8081/api/accounts/countPosts/${user.id}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          });
          user.postCount = postResponse.data; 

          const followingResponse = await axios.get(`http://localhost:8081/api/accounts/countFollowing/${user.id}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          });
          user.followingCount = followingResponse.data;
        }
      })
      .catch(error => {
        console.error('Greška prilikom dohvatanja korisnika:', error);
      });
    },
    changePage(pageNumber) {
      if (pageNumber >= 0 && pageNumber < this.users.totalPages) {
        this.currentPage = pageNumber;
        this.fetchUsers();
      }
    },
    navigateToUser(userId) {
      this.$router.push({ name: 'CheckUser', params: { userId } });
    },
    async loadUserId() {
      const token = localStorage.getItem('token');
            if (token) {
                try {
                    const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token }, {
                        headers: {
                            'Authorization': `Bearer ${localStorage.getItem('token')}`
                        },
                    });
          if (response.status === 200) {
            const { id, username, role } = response.data;
            this.loggedInUserId = parseInt(id);
            console.log('User id is: ' + this.loggedInUserId);
          }
        } catch (error) {
          console.error('Failed to decode token:', error);
        }
      } else {
        this.loggedInUserId = null;
        console.log('User id is: ' + this.loggedInUserId);
      }
    },
    async loadAllFollowers(){
      axios.get(`http://localhost:8081/api/follow/getFollowing/${this.loggedInUserId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      .then(response => {
        // Handle the response data
        console.log('Followers:', response.data);
        this.users.content = response.data;


        
        
      })
      .catch(error => {
        console.error('Error loading followers:', error);
      });
    }
  }
};
</script>

<style>
.admin-users-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.admin-users-title {
  color: var(--clr-black);
  text-align: center;
  margin-bottom: 30px;
}

.admin-users-search {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.admin-users-input {
  padding: 7px 15px;
  border: 1px solid var(--clr-primary);
  border-radius: 3px;
  flex: 1;
  min-width: 200px;
}

.admin-users-input:focus {
  outline: none;
  border-color: var(--clr-primary-dark);
}

.admin-users-sort-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.admin-users-select {
  padding: 7px 15px;
  border: 1px solid var(--clr-primary);
  border-radius: 3px;
  background-color: white;
  color: var(--clr-black);
  cursor: pointer;
}

.admin-users-select:hover {
  border-color: var(--clr-primary-dark);
}

.admin-users-table-wrapper {
  overflow-x: auto;
  margin-bottom: 20px;
  border-radius: 3px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.admin-users-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

.admin-users-th {
  background-color: var(--clr-black);
  color: white;
  padding: 12px 15px;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.15s ease-in-out;
}

.admin-users-th:hover {
  background-color: var(--clr-secondary-dark);
}

.admin-users-td {
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
}

.admin-users-tr:hover {
  background-color: #f9f9f9;
}

.admin-users-pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.admin-users-pagination-btn {
  background-color: var(--clr-primary);
  color: white;
  border: none;
  padding: 7px 20px;
  border-radius: 3px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.15s ease-in-out;
}

.admin-users-pagination-btn:hover:not(:disabled) {
  background-color: var(--clr-primary-dark);
}

.admin-users-pagination-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .admin-users-search {
    flex-direction: column;
  }
  
  .admin-users-input {
    width: 100%;
  }
  
  .admin-users-sort-wrapper {
    flex-direction: column;
  }
  
  .admin-users-select {
    width: 100%;
  }
}
</style>