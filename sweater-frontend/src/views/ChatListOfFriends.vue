<template>
  <div>
    <div class="form-holder mt-3 mx-3">

      <b-input-group size="sm" style="margin-bottom: 18px">
        <b-form-input type="search" placeholder="Поиск пользователей"
                      style="border-top-left-radius: 15px;
                      border-bottom-left-radius: 15px; border-right: none; background-color: #2C2A38;
                      border-color: #6c757d;
                      color: #6c757d"
                      v-model="findChatRoom">
        </b-form-input>
        <b-input-group-append>
          <b-input-group-text
                              style="border-bottom-right-radius: 15px;
                              border-top-right-radius: 15px;
                              background-color: #2C2A38; border-color: #6c757d">
            <b-icon icon="search"></b-icon>
          </b-input-group-text>
        </b-input-group-append>

      </b-input-group>
    </div>

<!--    list of friends-->

    <div>
      <div class="border-top friend-block d-flex py-2 px-2 align-items-center"
           v-for="(chatWithCount, index) in getChatRoomListUnreadMsg" :key="index"
           @click="openChatRoom(chatWithCount.chatRoom.id, chatWithCount.chatRoom)">
        <b-avatar variant="light" size="45px" :src="setAvatarToChat(chatWithCount.chatRoom)" ></b-avatar>
        <div class="d-flex friend-block-data flex-column align-items-start ml-2">
          <div class="font-weight-bold mx-0 my-0" style="font-size: 0.9rem">{{ getChatName(chatWithCount) }}</div>
          <div style="font-size: 0.8rem" class="font-italic">{{ getUsernameOfFriend(chatWithCount) }}</div>
          <div v-if="chatWithCount.countOfUnreadMessages > 0"
               style="font-size: 0.8rem; color: #42b983" class="font-italic">
            {{ chatWithCount.countOfUnreadMessages }} new messages
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ChatListOfFriends",

  data() {
    return {
      findChatRoom: '',
      userData: JSON.parse(localStorage.getItem('user-data')),
      chatRoomListUnreadMsg: null,
      countOfUnreadMessages: 0
    }
  },

  async created() {
    this.$root.$refs.ChatListOfFriends = this
    if (!this.chatRoomListUnreadMsg) {
      let response = await axios.get(`${this.$hostname}/chat/getAll?userId=${this.userData.id}`)
      this.chatRoomListUnreadMsg = response.data
    }
  },

  computed: {
    getCountOfUnreadMessage() {
      return this.countOfUnreadMessages
    },

    getChatRoomListUnreadMsg() {
      if (this.findChatRoom === '') return this.chatRoomListUnreadMsg;
      else return this.chatRoomListUnreadMsg
          .filter(chat => {
            const friendUsername = this.getUsernameOfFriend(chat).split("@")[1];
            return friendUsername.toLowerCase().startsWith(this.findChatRoom.toLowerCase());
          });
    }
  },
  methods: {
    setAvatarToChat(chatRoom) {
      if (chatRoom.capacity === 2) {
        const friend = chatRoom.users.filter(user => user.id !== this.userData.id)[0];
        return friend.avatarURI !== null? friend.avatarURI : ''
      } else {
        return ''
      }
    },

    addOneToCounterOfUnreadMessages(chatRoomId) {
      console.log(`Add counter to chat #${chatRoomId}`)
      if (this.chatRoomListUnreadMsg) {
        this.chatRoomListUnreadMsg.filter(chatRoomWithCounter => {
          if (chatRoomWithCounter.chatRoom.id === chatRoomId) {
            console.log(`chat was found: ${JSON.stringify(chatRoomWithCounter, undefined, 2)}`)
            chatRoomWithCounter.countOfUnreadMessages++
          }
        })
      }
    },

    getChatName({chatRoom: chat}) {
      if (chat.capacity === 2) {
        const friend = chat.users.filter(user => user.id !== this.userData.id)[0];
        const {firstName, lastName} = friend
        return `${firstName} ${lastName}`
      }
    },

    getUsernameOfFriend({chatRoom: chat}) {
      if (chat.capacity === 2) {
        const friend = chat.users.filter(user => user.id !== this.userData.id)[0];
        return `@${friend.username}`
      }
    },

    async checkNewMessages(chatId) {
      this.countOfUnreadMessages = 0
      let response = await axios.get(`${this.$hostname}/msg/chat/${chatId}/sent/count`)
      let count = response.data
      this.countOfUnreadMessages = count
      return count
    },

    async openChatRoom(chatRoomId, chatRoom) {
      this.$root.$refs.ChatBoard.openChatRoom(chatRoomId, chatRoom)
      this.readUnreadMessages(chatRoomId)
    },

    readUnreadMessages(chatRoomId) {
      let chatRoomWithCount = this.chatRoomListUnreadMsg
          .filter(chatRoomWithCount => {
            if (chatRoomWithCount.chatRoom.id === chatRoomId) {
              return chatRoomWithCount
            }
          })[0];
      chatRoomWithCount.countOfUnreadMessages = 0
    },
  }
}
</script>

<style scoped>
.appended-icon {
  border-bottom-right-radius: 15px;
  border-top-right-radius: 15px;
}
.friend-block {
  height: 75px;
  color: #7e8992;
  background-color: #3D3A4B;
}
.friend-block:hover {
  cursor: pointer;
}
.border-top {
  border-top: 1px solid #6c757d !important;
}
</style>