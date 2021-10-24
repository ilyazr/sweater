<template>
  <b-list-group class="change-group text-left mt-5">
    <b-list-group-item class="change-info d-flex">
      <a class="d-block"
         @click.prevent="pushForce('/profile')"
         :href="'/profile'">
        <b-icon font-scale="1.4rem" icon="arrow-left" style="color: #6C757D"></b-icon>
      </a>
      <div class="ml-3">
        Изменить {{ properties[$route.params.property].prepared }}
      </div>
    </b-list-group-item>

<!--    if this is avatar-->
    <b-list-group-item v-if="$route.params.property === 'avatarURI'" class="d-flex flex-column">
      <div class="mb-2">
        <b-input-group class="mt-3" style="padding-right: 8px">
          <b-input-group-prepend class="d-flex">
            <div class="d-flex align-items-center pr-2 user-form__prepend"></div>
          </b-input-group-prepend>
          <b-form-file
              accept="image/*"
              v-model="file"
              placeholder="Choose a file"
              drop-placeholder="Drop file here..."
          ></b-form-file>
        </b-input-group>
      </div>
      <div class="ml-auto mr-2 mt-3 mr-auto">
        <b-button variant="outline-primary" @click="setNewAvatar">Сохранить</b-button>
      </div>
    </b-list-group-item>

    <b-list-group-item v-else class="d-flex flex-column">
      <div v-if="$route.params.property === 'dateOfBirth'" class="mb-4 mt-3">
        <b-form-datepicker
            v-model="inputData"
            locale="ru-RU"
            placeholder="Укажите дату"></b-form-datepicker>
      </div>
      <div v-else class="mt-3 mb-2">
        <b-form-group
            id="input-group-1"
            label-for="input-1"
        >
          <b-form-input
              id="input-1"
              v-model="inputData"
              :placeholder="`Введите ${properties[$route.params.property].prepared}`"
              required
          ></b-form-input>
        </b-form-group>
      </div>
      <div class="ml-auto mr-2 mr-auto">
        <b-button variant="outline-primary" @click="changeUserProperty">Сохранить</b-button>
      </div>
    </b-list-group-item>

  </b-list-group>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
import axios from "axios";
export default {
  name: "UserProfileChange",
  props: ['userData'],

  data() {
    return {
      inputData: '',
      file: null,
      properties: {
        avatarURI: {prepared: 'аватар', nominative: 'аватар'},
        username: {prepared: 'username', nominative: 'username'},
        firstName: {prepared: 'имя', nominative: 'имя'},
        lastName: {prepared: 'фамилию', nominative: 'фамилия'},
        password: {prepared: 'пароль', nominative: 'Пароль'},
        email: {prepared: 'email', nominative: 'email'},
        phoneNumber: {prepared: 'телефон', nominative: 'телефон'},
        country: {prepared: 'страну', nominative: 'страна'},
        dateOfBirth: {prepared: 'дату рождения', nominative: 'дата рождения'}
      },
    }
  },

  methods: {
    pushForce,

    async changeUserProperty() {
      if (this.inputData.length < 2)
        alert('Input data should consist of at least 2 characters')
      else {
        const propName = this.$route.params.property;
        const response =
            await axios.post(`${this.$hostname}/user/change?property=${propName}&value=${this.inputData}`);
        console.log(JSON.stringify(response.data));
        this.recreateTokenAndUserData(response);
        this.pushForce('/profile');
      }
      this.cleanForm();
    },

    cleanForm() {
      this.inputData = '';
      this.file = null;
    },

    recreateTokenAndUserData(response) {
      const { property, newValue, newAvatarUri } = response.data;
      this.userData[property] = newValue;
      if (newAvatarUri) this.userData.avatarURI = newAvatarUri;
      localStorage.setItem('user-data', JSON.stringify(this.userData));
      localStorage.setItem('user-token', response.headers['authorization']);
      axios.defaults.headers['Authorization'] = localStorage.getItem('user-token');
    },

    async setNewAvatar() {
      if (this.file === null)
        alert('Input data should not be null')
      else {
        let formData = new FormData();
        formData.append('file', this.file);
        console.log('appended to the form data');
        const response = await axios.post(
            `${this.$hostname}/user/changeAvatar`,
            formData,
            {
              headers: { "Content-Type": "multipart/form-data" }
            });
        this.recreateTokenAndUserData(response);
        this.pushForce('/profile');
      }
      this.cleanForm();
    },

  },
}
</script>

<style scoped>
.change-group {
  width: 40%;
  min-width: 33%;
  box-shadow: 0 3px 8px #242424;
  border-radius: 20px;
}
.change-info {
  background-color: #2B2936;
  color: floralwhite;
}
.change-list {
  background-color: #2C2A38;
  word-break: break-word;
}
</style>