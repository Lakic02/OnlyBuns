<template>
  <div class="register-container-login">
    <div class="logInTitle">
      <h1>Log in</h1>
    </div>
    <form class="form-login" @submit.prevent="Submit">
      <div class="form-group-login">
        <label for="username">Email</label>
        <input type="text" id="username" v-model="username" class="login-input">
      </div>

      <div class="form-group-login">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" class="login-input">
      </div>

      <div class="form-group-login" v-if="errorVisible">
        <label class="error">All fields are required!</label>
      </div>
      
      <div class="form-group-login" v-if="errorLogIn">
        <label class="error">Email or password is not correct.</label>
      </div>
      
      <button id="LogInButton" type="submit">LogIn</button>
    </form>
  </div>
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
            console.log('ALO')
            this.errorVisible = false
            this.errorLogIn = true
            this.showError('Log-in failed')
            alert('Login failed');
          }
        }).catch(err => {
          if (err.response) {
      if (err.response.status === 401) {
        // Ako je status 401 - Unauthorized
       alert('Login failed: Unauthorized access');
        this.errorVisible = true;
        this.errorLogIn = true;
      } else {
        // Za sve ostale greške sa statusnim kodovima
        this.showError(`Login failed: ${err.response.status} error`);
        this.errorVisible = true;
        this.errorLogIn = true;
      }
    } else {
      // Ako je neka druga greška (npr. mrežna greška)
      this.showError('Login failed: Network error');
      this.errorVisible = true;
      this.errorLogIn = true;
    }
        })
      }
    }
  }
</script>

<style>
.register-container-login {
  padding: 20px;
  max-width: 400px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logInTitle {
  color: var(--clr-black);
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.form-login {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group-login {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group-login label {
  font-weight: 500;
  color: var(--clr-black);
}

.form-group-login input {
  padding: 10px;
  border: 1px solid var(--clr-primary);
  border-radius: 3px;
}

.form-group-login input:focus {
  outline: none;
  border-color: var(--clr-primary-dark);
}

.error {
  color: var(--clr-secondary);
  font-size: 14px;
}

#LogInButton {
  background-color: var(--clr-primary);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 3px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.15s ease-in-out;
  width: 100%;
}

#LogInButton:hover:not(:disabled) {
  background-color: var(--clr-primary-dark);
}

@media (max-width: 768px) {
  .register-container-login {
    padding: 15px;
    width: 100%;
  }

  .logInTitle {
    font-size: 20px;
  }
}



</style>