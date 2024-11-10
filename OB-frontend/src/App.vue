<template>
  <header>
  <nav>
    <div class="nav-links nav-primary">
      <router-link to="/">Home</router-link>
    </div>
    <div class="nav-links nav-secondary">
      <router-link to="/LogIn" v-if="!IsLoggedIn">Log in</router-link>
      <router-link to="/Registration" v-if="!IsLoggedIn">Registration</router-link>
    </div>
    <div class="nav-links nav-secondary" v-if="IsLoggedIn">
      <router-link to="/CreatePost" v-if="isRegisteredLoggedIn">Create Post</router-link>
      <router-link to="/Posts">All Posts</router-link>
      <router-link to="/Users" v-if="isAdminLoggedIn">Check Users</router-link>
      
      <!-- Padajući meni za prijavljene korisnike -->
      <div class="dropdown" v-if="IsLoggedIn">
        <button class="dropbtn">Menu</button>
        <div class="dropdown-content">
          <router-link to="/FollowedPosts">Followed Posts</router-link>
          <router-link to="/Trends">Trends</router-link>
          <router-link to="/NearbyPosts">Nearby Posts</router-link>
          <router-link to="/Chat">Chat</router-link>
          <router-link to="/Profile">Profile</router-link>
        </div>
      </div>
    </div>
    <div class="nav-links nav-secondary" v-if="IsLoggedIn">
      <div class="header-user-info">
        <router-link class="userLogo" to="/User">
          <p class="username">{{ username }}</p>
          <p class="username">{{ role }}</p>
        </router-link>
      </div>
    </div>
    <div class="nav-links nav-secondary" v-if="IsLoggedIn">
      <router-link to="/" @click="LogOutClick()">Log Out</router-link>
    </div>
  </nav>
</header>

<router-view class="mainView" @LogInSuccess="HandleLogInSuccess(data)"/>

<footer>
  <div class="footer-links">
    <button @click="scrollToTop">Go To Top</button>
    <router-link to="/">Home</router-link>
    <router-link to="/User">User Profile</router-link>
  </div>
  <div class="footer-text">
    <p><b>Contact mail:</b> info@onlybuns.rs</p>
    <p><b>Tel/Fax:</b> 0800 100 100 / 024 225 883</p>
    <br>
    <p><b>© Only Buns.</b> Since 2024.</p>
  </div>
  <div class="footer-social">
    <p>Social media</p>
    <div class="social-icons">
      <a href="https://X.com" target="_blank">
        <img src="../src/images/X.png" alt="X Logo">
      </a>
      <a href="https://facebook.com" target="_blank">
        <img src="../src/images/facebook.png" alt="Fb Logo">
      </a>
      <a href="https://instagram.com" target="_blank">
        <img src="../src/images/instagram.png" alt="Ig Logo">
      </a>
      <a href="https://linkedin.com" target="_blank">
        <img src="../src/images/linkedin.png" alt="Li Logo">
      </a>
    </div>
  </div>
</footer>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router'
import axios from 'axios'

export default {
data(){
  return{
    IsLoggedIn:false,
    userId:0,
    username: '',
    role: '',
    isAdminLoggedIn:false,
    isRegisteredLoggedIn:false
  }
},
async mounted(){
  this.HandleLogInSuccess()
},
methods:{
  scrollToTop() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  },
  async HandleLogInSuccess(){
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token });
        if (response.status === 200) {
          const {id, username, role } = response.data;
          console.log('username')
          console.log(username)
          this.username = username;
          this.role = role;
          this.userId = id;
          this.LogInVisible=false
          this.IsLoggedIn=true
          this.CheckRole(this.role)
          this.$router.push('/')
        }
        else{
          this.SetDefaultVisibilities()
        }
      } catch (error) {
        console.error('Failed to decode token:', error);
      }
    }
  },
  LogOutClick(){
    this.SetDefaultVisibilities()
    localStorage.clear()
    window.location.reload()
  },
  CheckRole(role){
    if(role === 'admin'){
      this.isAdminLoggedIn=true
      this.isRegisteredLoggedIn=false
    }else if(role === 'registered'){
      this.isAdminLoggedIn=false
      this.isRegisteredLoggedIn=true
    }
    else{
      this.isAdminLoggedIn=false
      this.isRegisteredLoggedIn=false
    }
  },
  SetDefaultVisibilities(){
    this.IsLoggedIn=false
    this.isAdminLoggedIn=false
    this.isRegisteredLoggedIn=false
  }
}
}
</script>

<style>
html {
scroll-behavior: smooth;
height: 100%;
}

:root {
--clr-primary: #e56b6b;
--clr-secondary: #e56b6b;
--clr-primary-dark: #e56b6b;
--clr-secondary-dark:  #e97777;
--clr-primary-50: #e56b6b;
--clr-secondary-50: #e56b6b;
--clr-black: #984f4f;

--clr-succes: #00b548;
--clr-danger: #ff0000;
}

* {
margin: 0;
padding: 0;
box-sizing: border-box;
}

body {
margin: 0;
min-height: 100vh;
display: flex;
flex-direction: column;
}

#app {
display: flex;
flex-direction: column;
min-height: 100vh;
}

header {
position: sticky;
top: 0;
left: 0;
width: 100%;
background-color: var(--clr-black);
z-index: 1000;
}

header nav {
display: flex;
justify-content: space-between;
padding: 12px;
}

header nav .nav-links {
display: flex;
gap: 15px;
align-items: center;
}

header nav a {
--bg: var(--clr-primary);
text-decoration: none;
font-weight: 600;
background-color: var(--bg);
padding: 7px 20px;
border-radius: 3px;
transition: background 0.15s ease-in-out;
font-size: 18px;
}

header nav a:hover {
--bg: var(--clr-primary-dark);
}

header nav .nav-secondary a {
--bg: var(--clr-secondary);
}

header nav .nav-secondary a:hover {
--bg: var(--clr-secondary-dark);
}

.header-user-info {
display: flex;
flex-direction: column;
align-items: center;
justify-content: center;
}

.nav-links a{
color: white;
}

.mainView {
flex: 1 0 auto;
padding: 20px;
width: 100%;
max-width: 1200px;
margin: 0 auto;
min-height: calc(100vh - 160px); /* Adjust based on header/footer height */
}

footer {
position: relative;
width: 100%;
background-color: var(--clr-black);
padding: 10px 0;
display: grid;
grid-template-columns: 1fr 1fr 1fr;
align-items: center;
flex-shrink: 0;
}

.footer-links {
display: flex;
flex-direction: column;
align-items: flex-start;
gap: 5px;
padding-left: 20px;
}

.footer-links a,
.footer-links button {
color: #fff;
text-decoration: none;
background: none;
border: none;
cursor: pointer;
font-size: 1em;
}

.footer-links button {
padding-left: 0;
}

.footer-text {
text-align: center;
color: white;
}

.footer-social {
display: flex;
flex-direction: column;
align-items: flex-end;
gap: 5px;
color: white;
padding-right: 20px;
}

.footer-social p {
margin-top: 0;
}

.footer-social .social-icons {
display: flex;
gap: 10px;
}

.footer-social .social-icons img {
width: 24px;
height: 24px;
}

.dropdown {
position: relative;
display: inline-block;
}

.dropbtn {
background-color: var(--clr-primary);
color: white;
padding: 10px 20px;
font-size: 18px;
border: none;
cursor: pointer;
border-radius: 3px;
text-decoration: none;
}

.dropbtn:hover {
background-color: #e97777;
}

.dropdown-content {
display: none;
position: absolute;
background-color: #f9f9f9;
min-width: 160px;
box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.2);
z-index: 1;
border-radius: 3px;
}

.dropdown:hover .dropdown-content {
display: block;
}

.dropdown-content a {
color: white;
padding: 12px 16px;
text-decoration: none;
display: block;
border-radius: 3px;
}

.dropdown-content a:hover {
background-color: #e97777;
}

@media (max-width: 768px) {
header nav {
  flex-direction: column;
  gap: 10px;
}

footer {
  grid-template-columns: 1fr;
  gap: 20px;
  text-align: center;
}

.footer-links,
.footer-social {
  align-items: center;
  padding: 0 20px;
}
}
</style>