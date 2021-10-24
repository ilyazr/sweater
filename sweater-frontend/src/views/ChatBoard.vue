<template>
  <div class="d-flex vh-100 flex-column">

    <div v-if="currentFriend == null"
         class="d-flex empty-block vh-100 w-100 align-items-center justify-content-center"
         style="background-color: #2C2A38">
      <div style="font-size: 1.5rem; color: #6c757d" >
        Your friends are waiting for you
      </div>
    </div>

<!--    user info-->

    <div v-if="currentFriend" class="user-info-holder"
         style="background-color: #2C2A38; border-bottom: 1px solid #6c757d; color: #6c757d">
      <b-row style="margin-left: 0; margin-right: 0;" class="d-flex">
        <b-col class="py-2 px-2 d-flex align-items-center">
          <b-avatar variant="light" size="45px" class="ml-2"
                    :src="currentFriend.avatarURI !== null? currentFriend.avatarURI : ''"></b-avatar>
          <div class="user-info d-flex align-items-start flex-column ml-3 text-left">
            <div>{{ `${getFriendFullName} @${currentFriend.username}` }} </div>
            <div>Last seen today at 10:13 PM</div>
          </div>
        </b-col>
        <b-col
               class="d-flex justify-content-end align-items-center">
          <div class="d-flex h-75 align-items-center">
            <b-icon icon="search" font-scale="1.2" class="mr-4"></b-icon>
            <b-icon icon="three-dots-vertical" font-scale="1.2" class=""></b-icon>
          </div>
        </b-col>
      </b-row>
    </div>

<!--    chat board-->
    <div v-if="currentFriend" class="message-holder d-flex flex-column flex-grow-1 pt-4"
         style="background-color: #3D3A4B;">

      <div class="d-flex flex-column mb-3 msg-outer"
           v-for="(msg, index) in messages"
           :key="index"
           :class="whoseThisMsg(msg.recipientId)? 'message-in' : 'message-out' " >

        <div v-if="shouldSetDateToMsg(messages[index - 1], msg)"
             class="mb-3 ml-auto mr-auto px-3 py-1"
             style="background-color: #6c757d; border-radius: 15px;">
          <div style="color: floralwhite">
            {{ getDateString(messages[index - 1], msg) }}
          </div>
        </div>

        <div class="message-payload d-flex py-2 px-4"
             :class="whoseThisMsg(msg.recipientId)? 'mr-auto' : 'ml-auto' "
             v-if="messages.length > 0">
          <span style="font-size: 0.9rem" class="text-break text-left">
            {{ readMessageInChatRoom(msg) }}
            <div class="msg-time text-right">
<!--              {{ msg.createdAt }}-->
              {{ `${getMessageTimeString(msg)}` }}
            </div>
          </span>
        </div>
      </div>

    </div>


<!--    small block-->
    <div v-if="currentFriend" class="d-flex justify-content-end"
         style="background-color: #3D3A4B;
         height: 3.5rem;
         border-top: 1px solid #6c757d">
      <div class="d-flex h-100 w-100 bottom-block align-items-center">
        <div class="ml-3">
          <b-icon icon="emoji-smile" font-scale="1.5" variant="secondary" class="send-icon"></b-icon>
        </div>
        <div>
          <b-icon icon="paperclip" rotate="45" font-scale="1.5" variant="secondary" class="send-icon"></b-icon>
        </div>
        <div class="d-flex w-100 pr-3 my-3">
          <b-form class="d-flex flex-grow-1">
            <b-form-input autocomplete="off" placeholder="Введите сообщение..." class="chat-input"
                          style="background-color: #2C2A38;
                          border-color: #6c757d;
                          color: white"
                          v-model="currentMessage">
            </b-form-input>
            <b-button variant="outline-warning"
                      class="ml-2"
                      style="border-radius: 15px"
                      @click="prepareAndSendMessage(currentMessage)">Отправить</b-button>
          </b-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { pushForce } from "@/util/routerUtil";
export default {
  name: "ChatBoard",
  props: ['sendMsg'],
  data() {
    return {
      lastMsgDate: null,
      userData: null,
      messages: [],
      currentChatRoom: null,
      currentMessage: '',
      currentFriend: null,
    }
  },

  computed: {
    getUserFullName() {
      return `${this.userData.firstName} ${this.userData.lastName}`
    },

    getFriendFullName() {
      return `${this.currentFriend.firstName} ${this.currentFriend.lastName}`
    },
  },
  methods: {
    shouldSetDateToMsg(prevMsg, msg) {
      if (!prevMsg) return true;
      else {
        return new Date(prevMsg.createdAt).toLocaleDateString() !== new Date(msg.createdAt).toLocaleDateString();
      }
    },

    getMessageTimeString(msg) {
      return new Date(msg.createdAt).toLocaleTimeString();
    },

    getDateString(prevMsg, msg) {
      const now = new Date().toLocaleDateString();
      if (!prevMsg) {
        const date = new Date(msg.createdAt).toLocaleDateString();
        return now === date? 'Сегодня' : date;
      }
      else {
        const prevDate = new Date(prevMsg.createdAt).toLocaleDateString();
        const newDate = new Date(msg.createdAt).toLocaleDateString();
        if (prevDate !== newDate) {
          return now === newDate? 'Сегодня' : newDate;
        }
        else return prevDate;
      }
    },

    pushForce,

    scrollBottom() {
      const msgBlocks = this.$el.querySelector('.message-holder');
      msgBlocks.lastElementChild.scrollIntoView({behavior: 'smooth'});
    },

    readMessageInChatRoom(msg) {
      if (this.userData.id === msg.recipientId) {
        axios.get(`${this.$hostname}/msg/read/${msg.id}`)
      }
      return msg.text
    },

    isInChatRoomWithId(chatRoomId) {
      console.log(`Call isInChatRoomWithId with arg = ${chatRoomId}`)
      if (this.currentChatRoom === null) {
        return false
      } else {
        return this.currentChatRoom.id === chatRoomId
      }
    },

    receiveIncomeMessage(message) {
      this.messages.push(message)
    },

    addTimestampToMessage(message) {
      message.createdAt = +new Date();
      return message
    },

    prepareAndSendMessage(messageText) {
      if (messageText.length !== 0) {
        console.log(`Prepare: ${JSON.stringify(messageText)}`)
        let message = {
          senderId: this.userData.id,
          senderUsername: this.userData.username,
          senderFullName: this.getUserFullName,
          recipientId: this.currentFriend.id,
          chatRoomId: this.currentChatRoom.id,
          text: messageText
        }
        this.sendMsg(message)
        this.messages.push(this.addTimestampToMessage(message))
        this.currentMessage = ''
        this.$nextTick(() => this.scrollBottom());
      }
    },

    findFriendInPrivateChat(chatRoom) {
      this.currentFriend = chatRoom.users.find(user => user.id !== this.userData.id)
    },

    async openChatRoom(chatRoomId, chatRoom) {
      console.log(chatRoom)
      this.currentChatRoom = chatRoom
      this.findFriendInPrivateChat(chatRoom)
      let response = await axios.get(`${this.$hostname}/msg/chat/${chatRoomId}/delivered`)
      this.messages = response.data
      await this.readUnreadMessages(chatRoomId)
    },

    async readUnreadMessages(chatId) {
      let response = await axios.get(`${this.$hostname}/msg/chat/${chatId}/sent`)
      let unreadMessages = response.data
      if (unreadMessages && unreadMessages.length > 0) {
        this.messages.push(...unreadMessages)
      }
    },

    whoseThisMsg(recipientId) {
      return this.userData.id === recipientId
    },

  },

  created() {
    this.userData = JSON.parse(localStorage.getItem('user-data'))
    this.$root.$refs.ChatBoard = this
    this.currentFriend = null
    this.currentChatRoom = null
  }
}
</script>

<style scoped>
  .empty-block {
    border-left: 1px solid #6c757d;
  }
  .msg-time {
    font-size: 0.9rem;
    margin-left: 3rem;
    color: lightgrey;
  }
  .message-in {
    justify-content: flex-start;
  }
  .message-in>.message-payload {
    margin-left: 2rem;
    background-color: #F38E33;
    color: white;
  }
  .message-out {
    justify-content: flex-end;
  }
  .message-out>.message-payload {
    margin-right: 2rem;
    background-color: #53A77D;
    color: white;
  }
  .message-holder {
    border-left: 1px solid #6c757d;
    overflow-y: auto;
  }
  .message-payload {
    max-width: 60%;
    border-radius: 15px;
  }
  .bottom-block {
    margin-left: 10%;
  }
  .bottom-block>div {
    margin-left: 10px;
  }
  .chat-input {
    border-radius: 15px;
    width: 100%;
  }
  .send-icon:hover {
    cursor: pointer;
  }
  .user-info-icon:hover {
    cursor: pointer;
  }
</style>