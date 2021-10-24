<template>
  <b-list-group class="profile-list text-left mt-5">
    <b-list-group-item class="profile-info">
    </b-list-group-item>
    <UserProfileList :userData="userData" />
  </b-list-group>
</template>

<script>
import UserProfileList from "./UserProfileList";
import { pushForce } from "@/util/routerUtil";
export default {
  name: "UserProfileNew",
  props: ['userData'],
  components: {
    UserProfileList,
  },
  methods: {
    pushForce,
  },
}
</script>

<style scoped>
.profile-list {
  width: 43%;
  min-width: 33%;
  box-shadow: 0 3px 8px #242424;
  border-radius: 20px;
  overflow: hidden;
  color: floralwhite;
}
.profile-info {
  background-color: #2B2936;
  color: floralwhite;
}
</style>