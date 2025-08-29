<template>
    <div class="profile-view">
        <div class="profile-header">
            <div class="profile-grid">
                <div class="field" style="grid-column: 1; grid-row: 1;">
                    <label>First Name:</label>
                    <input v-model="firstName" placeholder="Enter first name">
                </div>
                <div class="field" style="grid-column: 2; grid-row: 1;">
                    <label>Last Name:</label>
                    <input v-model="surname" placeholder="Enter last name">
                
                </div>
                <div class="field" style="grid-column: 3; grid-row: 1;">
                    <label>Followers:</label>
                    <p>{{ followers }}</p>
                </div>
                <div class="field" style="grid-column: 2; grid-row: 2;">
                    <label>Email:</label>
                    <input v-model="email" placeholder="Unesi email" readonly>
                    
                </div>
                <div class="field" style="grid-column: 1; grid-row: 3;">
                    <label>Password:</label>
                    <input v-model="password" type="password" placeholder="Enter password">
                    
                </div>
                <div class="field" style="grid-column: 2; grid-row: 3;">
                    <label>Confirm Password:</label>
                    <input v-model="confirmPassword" type="password" placeholder="Confirm password">
                    
                </div>
                <div class="field" style="grid-column: 3; grid-row: 3;">
                    <label>Address:</label>
                    <input v-model="address" placeholder="Enter address">
                    
                </div>
                <div class="centered" style="grid-column: 2; grid-row: 5;">
                    <button @click="updateProfile">Update Profile</button>
                </div>
                <div class="centered" style="grid-column: 1; grid-row: 7;">
                    <button @click="viewMyPosts">My Posts</button>
                </div>
                <div class="centered" style="grid-column: 2; grid-row: 7;">
                    <button @click="viewFollowers">Followers</button>
                </div>
                <div class="centered" style="grid-column: 3; grid-row: 7;">
                    <button @click="viewFollowing">Following</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            loggedInUserId: 0,
            firstName: '',
            surname: '',
            userName: '',
            followers: 0,
            email: '',
            password: '',
            confirmPassword: '',
            address: '',
            role: '',
            lastLogin: ''
        }
    },
    mounted() {
        this.loadUser().then(() => {
            this.fetchUserData();
        });
    },
    methods: {
        async fetchUserData() {
            try {
                
                console.log('Fetching data for user id: ' + this.loggedInUserId);
                const response = await axios.get(`http://localhost:8081/api/accounts/getById/${this.loggedInUserId}`,{
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                const data = response.data;
                this.firstName = data.firstName || '';
                this.surname = data.lastName || '';
                this.followers = data.followers || 0;
                this.email = data.email || '';
                this.password = data.password || '';
                this.confirmPassword = data.confirmPassword || '';
                this.address = data.address || '';
                this.userName = data.userName || '';
                this.role = data.role || '';
                this.lastLogin = data.lastLogin || '';

                console.log('Fetched user data:', data.lastName);
            } catch (error) {
                console.error('Greška pri dohvatanju podataka:', error);
            }
        },
        async updateProfile() {
            try {
                if (this.password !== this.confirmPassword || this.password==''
                    || this.firstName=='' || this.email=='' || this.address==''
                ) {
                    alert('Please fill in all fields correctly!');
                    return;
                }
                const response = await fetch('http://localhost:8081/api/accounts/update', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        id: this.loggedInUserId, 
                        firstName: this.firstName,
                        lastName: this.surname,
                        userName: this.userName, // treba dodati u data
                        email: this.email,
                        password: this.password,
                        address: this.address,
                        role: this.role, //treba dodati u data
                        followerCount: this.followers, // treba dodati u data
                        lastLogin: this.lastLogin // treba dodati u data
                    })
                });
                if (response.ok) {
                    const updatedAccount = await response.json();
                    alert('Profile successfully updated!');
                    console.log('Updated account:', updatedAccount); //provera da li je ispisan kako treba
                } else {
                    throw new Error('Error updating profile');
                }
            } catch (error) {
                console.error('Error saving profile:', error);
                alert('An error occurred while updating the profile.');
            }
        },
        async loadUser() {
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
        viewMyPosts() {
            console.log('Navigating to My Posts');
            this.$router.push({ name: 'MyPosts'});
        },
        viewFollowers() {
            console.log('Navigating to My Followers');
            this.$router.push({ name: 'MyFollowers'});
        },
        viewFollowing() {
            this.$router.push({ name: 'MyFollowing'});
        }
    }
}
</script>

<style>
.profile-view {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-header {
  text-align: center;
  margin-bottom: 20px;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field label {
  font-weight: 500;
  color: var(--clr-black);
}

.field input {
  padding: 10px;
  border: 1px solid var(--clr-primary);
  border-radius: 3px;
}

.field input:focus {
  outline: none;
  border-color: var(--clr-primary-dark);
}

.field p {
  margin: 0;
  font-size: 14px;
  color: #555;
}

.centered {
  display: flex;
  justify-content: center;
}

.centered button {
  background-color: var(--clr-primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 3px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.15s ease-in-out;
}

.centered button:hover:not(:disabled) {
  background-color: var(--clr-primary-dark);
}

@media (max-width: 768px) {
  .profile-view {
    padding: 15px;
    width: 100%;
  }

  .profile-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .centered button {
    width: 100%;
  }
}
</style>