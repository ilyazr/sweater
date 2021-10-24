<template>
  <b-container fluid>
    <b-row class="d-flex">
      <b-col cols="3" class="d-flex flex-column vh-100 px-0 all-chats-block"
             style="background-color: #3D3A4B; height: 100vh">
        <ChatListOfFriends />
      </b-col>
      <b-col class="vh-100 px-0">
        <ChatBoard
            :sendMsg="sendMsg"
            :userData="userData" />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import ChatListOfFriends from "@/views/ChatListOfFriends";
import ChatBoard from "@/views/ChatBoard";
export default {
  name: "Chat",
  components: {
    ChatListOfFriends, ChatBoard
  },
  props: ['sendMsg', 'userData'],
  data() {
    return {

    }
  }
}
</script>

<style scoped>
  .friend-block {
    height: 75px;
  }
  .all-chats-block {
    overflow-y: auto;
  }
  .stop-overflow {
    overflow-y: hidden;
  }
</style>