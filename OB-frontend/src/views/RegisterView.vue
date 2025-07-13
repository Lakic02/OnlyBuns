<template>
    <div class="registration-container">
      <h2 class="registration-title">Register</h2>
      <form @submit.prevent="submitForm">
        <div class="registration-input-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="form.email" class="registration-input" required />
        </div>
  
        <div class="registration-input-group">
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="form.username" class="registration-input" required />
        </div>
  
        <div class="registration-input-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="form.name" class="registration-input" required />
        </div>
  
        <div class="registration-input-group">
          <label for="password">Password:</label>
          <input type="password" id="password" v-model="form.password" class="registration-input" required />
        </div>
  
        <div class="registration-input-group">
          <label for="confirmPassword">Confirm Password:</label>
          <input type="password" id="confirmPassword" v-model="form.confirmPassword" class="registration-input" required />
          <div v-if="form.password !== form.confirmPassword" class="registration-error">
            Passwords do not match.
          </div>
        </div>
  
        <div class="registration-input-group">
          <label for="address">Address:</label>
          <input type="text" id="address" v-model="form.address" class="registration-input" required />
        </div>
  
        <button type="submit" class="registration-button">Register</button>
      </form>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { ref, computed } from 'vue';
  
  export default {
    name: 'RegistrationForm',
    setup() {
      const form = ref({
        email: '',
        username: '',
        name: '',
        password: '',
        confirmPassword: '',
        address: '',
      });
  
      const errors = ref({
        email: null,
        username: null,
        password: null,
        confirmPassword: null,
      });
  
      const validateEmail = () => {
        const emailPattern = /^[a-z][a-z0-9]*@[a-z0-9]+\.(com)$/;
        errors.value.email = emailPattern.test(form.value.email) ? null : 'Invalid email format';
      };
  
      const validateUsername = () => {
        errors.value.username = form.value.username.length > 0 ? null : 'Username is required';
      };
  
      const validatePassword = () => {
        errors.value.password =
          form.value.password.length >= 6 ? null : 'Password must be at least 6 characters';
      };
  
      const validateConfirmPassword = () => {
        errors.value.confirmPassword =
          form.value.password === form.value.confirmPassword
            ? null
            : 'Passwords do not match';
      };
  
      const submitForm = async () => {
        console.log('ALO');
        validateEmail();
        validateUsername();
        validatePassword();
        validateConfirmPassword();
  
        const payload = {
          firstName: form.value.name,
          userName: form.value.username,
          email: form.value.email,
          password: form.value.password,
          address: form.value.address,
        };
  
        console.log('Registration data:', payload);
        console.log('errori: ', hasErrors.value);
  
        if (!hasErrors.value) {
          try {
            const response = await axios.post('http://localhost:8081/api/authentication/register', payload, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
          });
            
            // Uspješan odgovor sa servera
            if (response.status === 200) {
              console.log('User registered successfully', response.data);
              alert('Registration successful, please check your email');
            }
          } catch (error) {
            // Obrada grešaka
            if (error.response) {
              // Greška je došla sa servera (status 4xx, 5xx)
              if (error.response.status === 500) {
                console.log('Server error occurred:', error.response.data);
                alert('Error: User already exist.');
              } else if (error.response.status === 400) {
                console.log('Bad request:', error.response.data);
                alert('Invalid registration details.');
              } else {
                console.log('Unexpected server error:', error.response.data);
                alert('An unexpected error occurred.');
              }
            } else if (error.request) {
              // Greška u request-u, može biti problem sa povezivanjem
              console.log('No response received from server');
              alert('No response from the server. Please check your connection.');
            } else {
              // Bilo kakva druga greška
              console.log('Error in axios request:', error.message);
              alert('An error occurred during registration.');
            }
          }
        }
      };
  
      const hasErrors = computed(() => {
        return (
          errors.value.email ||
          errors.value.username ||
          errors.value.password ||
          errors.value.confirmPassword
        );
      });
  
      return {
        form,
        errors,
        validateEmail,
        validateUsername,
        validatePassword,
        validateConfirmPassword,
        submitForm,
        hasErrors,
      };
    },
  };
</script>

  
  <style scoped>
  .registration-container {
    padding: 20px;
    max-width: 600px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .registration-title {
    color: var(--clr-black);
    text-align: center;
    margin-bottom: 20px;
    font-size: 24px;
    font-weight: 600;
  }
  
  .registration-input-group {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .registration-input {
    padding: 10px;
    border: 1px solid var(--clr-primary);
    border-radius: 3px;
    min-width: 200px;
  }
  
  .registration-input:focus {
    outline: none;
    border-color: var(--clr-primary-dark);
  }
  
  .registration-button {
    background-color: var(--clr-primary);
    color: white;
    border: none;
    padding: 10px 0;
    border-radius: 3px;
    cursor: pointer;
    font-weight: 600;
    transition: background-color 0.15s ease-in-out;
    width: 100%;
  }
  
  .registration-button:hover:not(:disabled) {
    background-color: var(--clr-primary-dark);
  }
  
  .registration-error {
    color: var(--clr-secondary);
    font-size: 14px;
    margin-top: -10px;
  }
  
  @media (max-width: 768px) {
    .registration-container {
      padding: 15px;
      width: 100%;
    }
  
    .registration-title {
      font-size: 20px;
    }
  }
  </style>
  