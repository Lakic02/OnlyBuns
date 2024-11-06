<template>
  <body>
    <div class="register-container-login">
      <div class="logInTitle">
        <h1>Log in</h1>
      </div>
      <form class="form-login">
        <div class="form-group-login">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="username">
        </div>

        <div class="form-group-login">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password">
        </div>

        <div class="form-group-login" v-if="errorVisible">
          <label class="error">All fields are required!</label>
        </div>
        <div class="form-group-login" v-if="errorLogIn">
          <label class="error">Username or password is not correct.</label>
        </div>
        <button id="LogInButton" type="submit" @click="Submit">LogIn</button>
      </form>
    </div>
  </body>
</template>

<script>
  import axios from 'axios'
  export default {
  emits: ['logInSuccess'],
    data(){
      return{
          username: "",
          password: "",
          errorVisible: false,
          errorLogIn: false
      }
    },
    methods:{
      Submit(){
        event.preventDefault()
        let error = false
        this.errorVisible=false
        this.errorLogIn=false

        if(!document.getElementById("username").value) error=true
        if(!document.getElementById("password").value) error=true

        if(error){
          this.errorLogIn = false
          this.errorVisible=true
          return
        }
        
        axios.get("http://localhost:8081/api/authentication/logIn/"+this.username+"/"+this.password).then(response => {
          if(response.data){
            this.errorVisible = false
            this.errorLogIn = false
            //console.log(response)
            localStorage.setItem('token', response.data)
            //console.log(localStorage.getItem('token'))
            this.$eventBus.emit('logInSuccess');
            this.$emit('LogInSuccess')
          }else{
            this.errorVisible = false
            this.errorLogIn = true
            this.showError('Log-in failed')
          }
        }).catch(err => {
          if (err.data) {
          this.showError('Log-in failed')
          }
        })
      }
    }
  }
</script>

<style>
.register-container-login {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 60px;
  height: 60vh;
}

.form-login {
  display: flex;
  flex-direction: column;
  width: 550px;
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.form-group-login {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}



#LogInButton{
  background-color: var(--clr-primary);
  border: none;
  padding: 10px;
  font-size: 1.2em;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 100%;
}


#LogInButton:hover {
  background-color: var(--clr-primary);
}

.error-login{
  text-align: center;
  color: red;
}
.logInTitle{
  text-align: center;
  padding-bottom: 30px;
}
</style>