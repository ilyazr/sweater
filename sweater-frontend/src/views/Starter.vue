<template>
  <b-container v-cloak fluid class="px-0" style="background-color: white; overflow-x: hidden">
      <b-row class="min-vh-100" align-v="stretch">
        <b-col class="col-md-6 col-sm-12 col-xs-12 vh-100 min-vh-100 px-0 d-flex align-items-center justify-content-center"
               style="background-color: #2C2A38; box-shadow: 5px 0 8px 2px #242424;">
          <b-img class="" style="width: 80%; height: 80%"
                 :src="require(`@/assets/sweater-logo.svg`)"></b-img>
        </b-col>

        <b-col class="col-md-6 d-flex flex-column">
          <b-row class="mx-auto mt-auto mb-5">
            <h1 class="font-weight-bolder ">Join the Sweater now!</h1>
          </b-row>

          <b-row class="d-flex inner mx-auto mt-4" style="margin-bottom: 25%" align-v="stretch" >
            <b-col class="d-flex justify-content-start align-items-end" cols="12">
              <b-button pill class="d-flex fb-100 justify-content-center h-100 align-items-center" variant="primary"
                v-b-modal.modal-login
              >Log In</b-button>
            </b-col>

            <b-col class="d-flex justify-content-center align-items-start mt-3" cols="12">
              <b-button pill class="d-flex fb-100 justify-content-center h-100 align-items-center" variant="dark"
                v-b-modal.modal-signup
              >Sign Up</b-button>
            </b-col>
          </b-row>
        </b-col>
      </b-row>

    <!-- Sign Up Form-->
    <b-modal
        id="modal-signup"
        ref="modal-signup"
        title="Регистрация"
        @show="resetModalSignup"
        @hidden="resetModalSignup"
        @ok="handleOkSignup"
        centered
    >
      <form ref="form" @submit.stop.prevent="handleSubmitSignup">
        <b-form-group
            label="Username"
            label-for="username-input"
            invalid-feedback="Username is required"
            :state="usernameStateSignup"
        >
          <b-form-input
              id="username-input"
              v-model="signupModalForm.username"
              placeholder="Enter username"
              required
          ></b-form-input>
        </b-form-group>

        <b-form-group
            id="input-group-2"
            label="Password"
            label-for="input-2"
            invalid-feedback="Password is required"
            :state="passwordStateSignup">
          <b-form-input
              id="input-2"
              v-model="signupModalForm.password"
              placeholder="Enter password"
              required
          ></b-form-input>
        </b-form-group>

        <b-form-group
            id="input-group-3"
            label="Password check"
            label-for="input-3"
            invalid-feedback="Passwords did not matches"
            :state="passwordCheckStatusSignup">
          <b-form-input
              id="input-3"
              v-model="signupModalForm.passwordCheck"
              placeholder="Enter password again"
              required
          ></b-form-input>
        </b-form-group>

        <b-form-group
            id="input-group-4"
            label="Email"
            label-for="input-4"
            invalid-feedback="Email is required"
            :state="emailStateSignup">
          <b-form-input
              id="input-4"
              v-model="signupModalForm.email"
              placeholder="Enter email"
              required
          ></b-form-input>
        </b-form-group>

      </form>

      <label for="datepicker">Date of birth</label>
      <b-form-datepicker id="datepicker"
                         v-model="signupModalForm.dateOfBirth"
                         locale="ru-RU"
                         class="mb-2"></b-form-datepicker>
    </b-modal>

    <!-- Log In Form-->
    <b-modal
        id="modal-login"
        ref="modal"
        title="Log In"
        @show="resetModalLogin"
        @hidden="resetModalLogin"
        @ok="handleOkLogin"
        centered
    >
      <form ref="form" @submit.stop.prevent="handleSubmitLogin">
        <b-form-group
            label="Name"
            label-for="name-input"
            invalid-feedback="Name is required"
            :state=usernameState
        >
          <b-form-input
              id="name-input"
              v-model="loginModalForm.username"
              placeholder="Enter name"
              required
          ></b-form-input>
        </b-form-group>

        <b-form-group
            id="input-group-2"
            label="Password"
            label-for="input-2"
            invalid-feedback="Password is required">
          <b-form-input
              id="input-2"
              type="password"
              v-model="loginModalForm.password"
              placeholder="Enter password"
              :state="passwordState"
              required
          ></b-form-input>
        </b-form-group>
      </form>
    </b-modal>
  </b-container>
</template>

<script>

import axios from "axios";
import { pushForce } from "@/util/routerUtil";
export default {
  name: "Starter",
  props: ['login', 'signUp', 'getUserData'],
  data() {
    return {
      //secondary. if you won't use these - delete
      isLoginModalShow: false,
      isSignupModalShow: false,
      usernameState: null,
      passwordState: null,

      usernameStateSignup: null,
      passwordStateSignup: null,
      passwordCheckStatusSignup: null,
      emailStateSignup: null,

      signupModalForm: {
        username: '',
        password: '',
        passwordCheck: '',
        email: '',
        dateOfBirth: null,
      },

      loginModalForm: {
        username: '',
        password: ''
      },

    }
  },
  methods: {
    pushForce,

    checkFormValidity() {
      const usernameValidate = this.usernameState = this.loginModalForm.username.length > 0;
      const passwordValidate = this.passwordState = this.loginModalForm.password.length > 0;
      return usernameValidate & passwordValidate;
    },

    checkSignupFormValidity() {
      const usernameValidation = this.usernameStateSignup = this.signupModalForm.username.length > 0;
      const passwordValidation = this.passwordStateSignup = this.signupModalForm.password.length > 0;
      const passwordCheckValidation = this.passwordCheckStatusSignup =
          this.signupModalForm.password.length > 0 && this.signupModalForm.passwordCheck.length > 0?
              this.signupModalForm.password === this.signupModalForm.passwordCheck : false;

      //TODO: add regex
      const emailValidation = this.emailStateSignup = this.signupModalForm.email.length > 0;
      return usernameValidation & passwordValidation & passwordCheckValidation & emailValidation;
    },

    resetModalSignup() {
      this.signupModalForm = {
        username: '',
        password: '',
        passwordCheck: '',
        email: '',
        dateOfBirth: null,
      }
    },

    handleOkSignup(okModalEvt) {
      okModalEvt.preventDefault();
      this.handleSubmitSignup();
    },

    handleSubmitSignup() {
      const signUpValidation = this.checkSignupFormValidity();
      if (!signUpValidation) {
        return;
      }
      console.log(`User has been created!\n ${JSON.stringify(this.signupModalForm)}`);
      this.signUp(this.signupModalForm)
      this.$nextTick(() => {
        this.$bvModal.hide('modal-signup')
      });
    },

    resetModalLogin() {
      this.loginModalForm.username = '';
      this.loginModalForm.password = '';
      this.usernameState = null;
      this.passwordState = null;
    },

    handleOkLogin(okModalEvent) {
      //prevent modal from closing
      okModalEvent.preventDefault();
      this.handleSubmitLogin();
    },

    handleSubmitLogin() {
      this.checkCredentials().then(hasBeenLoggedIn => {
        let formValidity = this.checkFormValidity();
        if (!formValidity) return;
        if (hasBeenLoggedIn) {
          this.$nextTick(() => {
            this.$bvModal.hide('modal-login')
          })
          setTimeout(() => {
            this.pushForce(`/wall/${this.getUserData.username}`);
          }, 100)
        }
        else {
          this.$nextTick(() => {
            this.$bvModal.hide('modal-login')
          });
          setTimeout(() => {alert('Wrong credentials!')}, 500);
        }
      }).catch(err => {
        this.$bvToast.toast(`${err.message}`, {
          title: `Error`,
          variant: 'danger',
          toaster: 'b-toaster-top-center',
          solid: true
        })
      })
    },

    async checkCredentials() {
      let hasBeenLoggedIn = false;
      const { username, password } = this.loginModalForm
      try {
        let response = await axios.post(`${this.$hostname}/login`, {username, password});
        hasBeenLoggedIn = true
        await this.login(response.headers['authorization'])
      } catch (err) {
        throw new Error('Logging error! Wrong credentials or maybe you did not verified your account.')
      }
      return hasBeenLoggedIn;
    }
  }
}
</script>

<style scoped>
  #col1 {
    background-color: lightgreen;
  }
  #col2 {
    background-color: lightcoral;
  }
  .col {
    margin: 0px;
  }
  .inner {
    padding: 3px;
    min-width: 35%;
    min-height: 11%;
  }
  .inner-top {
    background-color: greenyellow;
  }
  .fb-100 {
    flex: 1;
    flex-basis: 100%;
  }
  img {
    height: 100vh;
  }
</style>