<template>
  <div class="admin-users-view">
    <h1 class="admin-users-view-h1">Prikaz svih korisnika</h1>
    
    <!-- Pretraga -->
    <div>
      <input v-model="search.firstName" placeholder="Ime" @input="fetchUsers"/>
      <input v-model="search.lastName" placeholder="Prezime" @input="fetchUsers"/>
      <input v-model="search.email" placeholder="Email" @input="fetchUsers"/>
      <input v-model.number="search.minPosts" placeholder="Minimalan broj objava" @input="fetchUsers"/>
      <input v-model.number="search.maxPosts" placeholder="Maksimalan broj objava" @input="fetchUsers"/>
    </div>
    
    <!-- Tabela korisnika -->
    <table>
      <thead>
        <tr>
          <th @click="sort('firstName')">Ime</th>
          <th @click="sort('lastName')">Prezime</th>
          <th @click="sort('email')">Email</th>
          <th @click="sort('postCount')">Broj objava</th>
          <th @click="sort('followingCount')">Broj pratioca</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users.content" :key="user.id">
          <td>{{ user.firstName }}</td>
          <td>{{ user.lastName }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.postCount }}</td>
          <td>{{ user.followingCount }}</td>
        </tr>
      </tbody>
    </table>
    
    <!-- Paginacija -->
    <div>
      <button :disabled="users.first" @click="changePage(users.pageable.pageNumber - 1)">Prethodna</button>
      <button :disabled="users.last" @click="changePage(users.pageable.pageNumber + 1)">Sledeća</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      users: { content: [], pageable: {} }, // Podaci o korisnicima
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
    };
  },
  mounted(){
    this.fetchUsers()
  },
  methods: {
    // Funkcija za dohvatanje korisnika
    fetchUsers() {
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
          'Authorization': `Bearer ${localStorage.getItem('token')}` // Ovde dodaj token iz localStorage
        },
      })
      .then(response => {
        this.users = response.data;
      })
      .catch(error => {
        console.error('Greška prilikom dohvatanja korisnika:', error);
      });
    },
    // Funkcija za sortiranje po odabranom polju
    sort(field) {
      if (this.sortField === field) {
        this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc'; // Ako je već sortirano po ovom polju, menja pravac sortiranja
      } else {
        this.sortField = field;
        this.sortDir = 'asc'; // Početni pravac sortiranja
      }
      this.fetchUsers();
    },
    // Funkcija za promenu stranice
    changePage(pageNumber) {
      if (pageNumber >= 0 && pageNumber < this.users.pageable.totalPages) {
        this.currentPage = pageNumber;
        this.fetchUsers();
      }
    },
  }
};
</script>

<style>
.admin-users-view-h1{
  margin-bottom: 20vh;
}
table {
  width: 100%;
  border-collapse: collapse;
}
table, th, td {
  border: 1px solid black;
}
th, td {
  padding: 10px;
  text-align: left;
}
input {
  margin-right: 10px;
  padding: 5px;
}
button {
  padding: 5px 10px;
  margin-top: 10px;
}
</style>
