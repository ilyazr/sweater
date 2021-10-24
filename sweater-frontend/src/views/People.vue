<template>
  <b-list-group class="people-list text-left mt-5">

    <b-list-group-item
        class="d-flex justify-content-center align-items-center"
        style="background-color: #2B2936; color: #6c757d;">
      <div class="font-weight-bold">Все пользователи сайта</div>
    </b-list-group-item>

    <b-list-group-item class="people-list-item" :href="`/wall/${user.username}`"
                       v-for="user in allUsers" :key="user.id"
                       @click.prevent="pushForce(`/wall/${user.username}`)">
      <b-row class="d-flex justify-content-start">
        <b-col class="d-flex col-12 align-items-center">
          <b-avatar variant="info" class=""
                    :src="user.avatarURI? user.avatarURI : ''"></b-avatar>
          <div class="ml-3 my-auto mr-auto">
            <div class="font-weight-bold mb-0" style="color: floralwhite">
              {{ `${user.firstName} ${user.lastName}` }}
            </div>
            <p class="my-0 text-muted text-left text-muted">@{{ `${user.username}` }}</p>
          </div>
        </b-col>
      </b-row>
    </b-list-group-item>
  </b-list-group>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
import axios from "axios";
export default {
  name: "People",
  async beforeMount() {
    try {
      let response = await axios.get(`${this.$hostname}/user/users`);
      this.allUsers = response.data.users;
    } catch (e) {
      console.log(e);
    }
  },
  data() {
    return {
      allUsers: []
    }
  },
  methods: {
    pushForce,
  },
}
</script>

<style scoped>
.people-list {
  width: 40%;
  min-width: 40%;
  box-shadow: 0 3px 8px #242424;
  border-radius: 20px;
}
.people-list-item {
  background-color: #2C2A38;
}
.people-list-item:hover {
  cursor: pointer;
}
</style>