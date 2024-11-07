<template>
  <header>
      <nav>
        <div class="nav-links nav-primary">
          <router-link to="/">Home</router-link>
        </div>
        <div class="nav-links nav-secondary" v-if="!IsLoggedIn">
          <router-link to="/LogIn">Log in</router-link>
          <router-link to="/Registration">Registration</router-link>
        </div>
        <div class="nav-links nav-secondary">
          <router-link to="/CreatePost">Create Post</router-link>
          <router-link to="/Posts">All Posts</router-link>
        </div>
        <div class="nav-links nav-secondary">
          <div class="header-user-info">
            <router-link class="userLogo" to="/User">
              <img src="../src/images/logos/user.png" alt="User logo" width="50px" height="50">
              <p class="username">{{ username }}</p>
              <p class="username">{{ role }}</p>
            </router-link>
            <router-link to="/" @click="LogOutClick()">Log Out</router-link> 
          </div>
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
    <p><b>Contact mail:</b> info@chocolatefactory.rs</p>
    <p><b>Tel/Fax:</b> 0800 100 100 / 024 225 883</p>
    <br>
    <p><b>© Chocolate Factory.</b> Since 2024.</p>
  </div>
  <div class="footer-social">
    <p>Social media</p>
    <div class="social-icons">
      <a href="https://X.com" target="_blank">
        <img src="../src/images/logos/X.png" alt="X Logo">
      </a>
      <a href="https://facebook.com" target="_blank">
        <img src="../src/images/logos/facebook.png" alt="Fb Logo">
      </a>
      <a href="https://instagram.com" target="_blank">
        <img src="../src/images/logos/instagram.png" alt="Ig Logo">
      </a>
      <a href="https://linkedin.com" target="_blank">
        <img src="../src/images/logos/linkedin.png" alt="Li Logo">
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
    }
  },
  async mounted(){
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
  methods:{
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
      //this.$eventBus.emit('logOutSuccess')
    },
    SetDefaultVisibilities(){
      this.IsLoggedIn=false
      this.isAdminLoggedIn=false
    }
  }
}

</script>


<style>
:root {
  --clr-primary: #f44708;
  --clr-secondary: #ef233c;
  --clr-primary-dark: #bf3a0a;
  --clr-secondary-dark: #c40d22;
  --clr-primary-50: #f4470880;
  --clr-secondary-50: #ef233c80;
  --clr-black: #252321;

  --clr-succes: #00b548;
  --clr-danger: #ff0000;
}
* {
  margin: 0;
}
.toast {
  opacity: 1 !important;
}
.toast-close-button {
  font-size: 20px; /* Promenite veličinu fonta po potrebi */
  width: 25px; /* Promenite širinu po potrebi */
  height: 25px; /* Promenite visinu po potrebi */
  line-height: 25px; /* Uskladite line-height sa visinom dugmeta */
}

header{
  position: fixed !important;
  z-index: 555;
  background-color: var(--clr-black);
  margin-bottom: 3rem;
  width: calc(100% - var(--side-padding) * 2);
}
header nav {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
}
header nav .nav-links {
  display: flex;
  gap: 15px;
  align-items: center
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

.footer-links,
.footer-text,
.footer-social {
  flex: 1;
  text-align: center;
  padding: 10px;
}

.footer-links {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.footer-links a,
.footer-links button {
  color: #fff;
  text-decoration: none;
  margin: 5px 0;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1em;
}


.footer-links button{
  margin: 0px;
  padding-left: 0px;
  
}

.footer-text {
  text-align: center;
  margin-top: auto;
  padding-bottom: 10px;
}

.footer-social {
  text-align: center;
}
.footer-social p{
  display: flex;
  gap: 10px;
  justify-content: right;
  margin-top: 10px;
  margin-right: 15px;
}

.footer-social .social-icons {
  display: flex;
  gap: 10px;
  justify-content: right;
  margin-top: 10px;
}

.footer-social .social-icons img {
  width: 24px;
  height: 24px;
}
</style>
