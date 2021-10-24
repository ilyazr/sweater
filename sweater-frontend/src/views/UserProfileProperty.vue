<template>
  <div>
    <b-list-group-item class="py-0 profile-list-item">
      <b-row class="h-100 my-0 custom-row" style="min-height: 3rem">
        <b-col class="d-flex col-4" >
          <div class="my-auto mx-auto text-center font-weight-bold">
            {{ properties[property].nominative }}
          </div>
        </b-col>
        <b-col class="d-flex py-2" style="padding-left: 0; padding-right: 0"
               @click.prevent="changeProperty(property)">
          <a class="d-flex w-100" :href="`/change/${property}`">
            <div v-if="property === 'avatarURI'" class="my-auto mx-auto py-2">
              <b-avatar variant="secondary"
                        :src="userData.avatarURI? userData.avatarURI : ''"
                        size="6rem" rounded></b-avatar>
            </div>
            <div v-else class="my-auto mx-auto">{{ getProperty(property) }}</div>
          </a>
        </b-col>
      </b-row>
    </b-list-group-item>
  </div>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
export default {
  name: "UserProfileProperty",
  props: ['property', 'userData'],
  data() {
    return {
      properties: {
        avatarURI: {prepared: 'аватар', nominative: 'Аватар'},
        username: {prepared: 'username', nominative: 'Username'},
        firstName: {prepared: 'имя', nominative: 'Имя'},
        lastName: {prepared: 'фамилию', nominative: 'Фамилия'},
        password: {prepared: 'пароль', nominative: 'Пароль'},
        email: {prepared: 'email', nominative: 'E-mail'},
        phoneNumber: {prepared: 'телефон', nominative: 'Телефон'},
        country: {prepared: 'страну', nominative: 'Страна'},
        dateOfBirth: {prepared: 'дату рождения', nominative: 'Дата рождения'}
      },
    }
  },
  methods: {
    pushForce,
    changeProperty(propName) {
      this.pushForce(`/change/${propName}`)
    },
    getProperty(property) {
      const obj = this.userData;
      if (obj[property] === undefined || obj[property] === null)
        return '<не указано>';
      if (property === 'dateOfBirth') {
        return new Date(this.userData[property]).toLocaleDateString();
      }
      return this.userData[property];
    }
  },
}
</script>

<style scoped>
.custom-row {
  padding-left: 0;
  padding-right: 0;
  word-break: break-word;
}
a {
  color: inherit;
  text-decoration: none;
  text-align: center;
  vertical-align: center;
}
a:hover {
  cursor: pointer;
}
.profile-list-item {
  background-color: #2C2A38;
  word-break: break-word;
}
</style>