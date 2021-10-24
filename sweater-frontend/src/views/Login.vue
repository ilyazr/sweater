<template>
  <div class="home">
    Login Form

    <b-container class="login-form">
      <b-form @submit.prevent="submitLoginForm" @reset="resetLoginForm">
        <b-form-group
            id="input-group-1"
            label="Your name:"
            label-for="input-1"
        >
          <b-form-input
              id="input-1"
              v-model="formData.username"
              placeholder="Enter your name"
              required
          ></b-form-input>
        </b-form-group>

        <b-form-group id="input-group-2" label="Your Password:" label-for="input-2">
          <b-form-input
              id="input-2"
              v-model="formData.password"
              placeholder="Enter password"
              required
          ></b-form-input>
        </b-form-group>

        <b-button type="submit" variant="primary">Submit</b-button>
        &nbsp;
        <b-button type="reset" variant="danger">Reset</b-button>
      </b-form>
    </b-container>

  </div>
</template>

<script>
// @ is an alias to /src
import axios from 'axios';
//import jwt_decode from 'jwt-decode'
export default {
  name: 'Home',
  props: ['changeHasToken'],
  data() {
    return {
      formData: {
        username: '',
        password: ''
      }
    }
  },
  components: {
  },
  methods: {
    resetLoginForm() {
      this.formData = {
        username: '',
        password: ''
      }
    },
    async submitLoginForm() {
      try {
        let response = await axios.post(`${this.$hostname}/login`, {
          username: this.formData.username,
          password: this.formData.password
        });

        console.log(response.headers);

        let token = response.headers['authorization'].split(' ')[1];
        console.log(`token: ${token}`);
        localStorage.setItem('user-token', token);
        this.changeHasToken();
      } catch (e) {
        console.log(e);
      } finally {
        this.resetLoginForm();
      }

    },
    checkLocalStorage() {
      for(let i=0; i<localStorage.length; i++) {
        let key = localStorage.key(i);
        alert(`${i}. ${key}: ${localStorage.getItem(key)}`);
      }
    }
  }
}
</script>

<style scoped>
.login-form {
  max-width: 55% !important;
  margin-top: 15px;
}
</style>
