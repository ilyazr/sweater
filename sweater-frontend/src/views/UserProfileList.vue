<template>
  <div style="color: #6C757D">
    <UserProfileProperty v-for="(property, idx) in properties" :key="idx"
                         :property="property"
                         :userData="userData"/>
  </div>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
import UserProfileProperty from "./UserProfileProperty";
export default {
  name: "UserProfileList",
  props: ['userData'],
  data() {
    return {
      properties: [
          'avatarURI', 'username', 'firstName', 'lastName', 'password', 'email',
          'phoneNumber', 'country', 'dateOfBirth'
      ]
    }
  },
  components: {UserProfileProperty},
  methods: {
    pushForce,
    changeProperty(propName) {
      this.pushForce(`/change/${propName}`)
    },
  }
}
</script>

<style scoped>
.profile-list-item {
  background-color: #2C2A38;
  word-break: break-word;
}
.custom-row {
  padding-left: 0;
  padding-right: 0;
  word-break: break-word;
}
a {
  text-align: center;
  vertical-align: center;
}
a:hover {
  cursor: pointer;
}
</style>