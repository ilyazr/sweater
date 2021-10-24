<template>
  <b-list-group class="people-list text-left">

    <b-list-group-item class="friends-info">
      <b-row class="d-flex align-items-center" style="word-break: break-word">
        <b-col class="col-4 ml-auto text-center">
          {{ whoseFriends }}
        </b-col>
        <b-col class="col-3">
          {{ getAmountOfFriends }}
        </b-col>
        <b-col style="width: 100%" class="px-0 mr-auto">
          <b-form-select class="friends-selector"
                         v-model="selected"
                         :options="options" size="sm"></b-form-select>
        </b-col>
      </b-row>
    </b-list-group-item>

    <b-list-group-item class="people-list-item"
                       v-for="user in filterFriends" :key="user.id">
      <b-row class="d-flex justify-content-start">
        <b-col class="d-flex col-6 align-items-center">
          <b-avatar variant="info" class=""
                    :src="user.avatarURI? user.avatarURI : ''"></b-avatar>
          <div class="ml-3 my-auto mr-auto">
            <div class="font-weight-bold mb-0" style="color: floralwhite">
              {{ `${user.firstName} ${user.lastName}` }}
            </div>
            <p class="my-0 text-muted text-left text-muted">@{{ `${user.username}` }}</p>
          </div>
        </b-col>
        <b-col class="col-6">
          <b-row class="h-100">

            <b-col class="d-flex justify-content-center align-items-center" style="">
              <a class="d-flex friends-icon justify-content-center align-items-center h-100 w-100"
                 :href="`/wall/${user.username}`"
                 @click.prevent="pushForce(`/wall/${user.username}`)"
                 v-b-tooltip.hover title="Страница пользователя">
                <b-icon icon="house-fill" style="color: #6C757D"></b-icon>
              </a>
            </b-col>

            <b-col v-if="isPrincipal && selected === 'accepted'"
                   class="d-flex justify-content-center align-items-center"
                   style="border-left: 2px solid #5A5C62FF">
              <a class="d-flex friends-icon justify-content-center align-items-center h-100 w-100"
                 :href="`/chat`"
                 @click.prevent="goToChatWithUser(user)"
                 v-b-tooltip.hover title="Чат с пользователем">
                <b-icon icon="chat-text-fill" style="color: #6C757D"></b-icon>
              </a>
            </b-col>

            <b-col v-if="isPrincipal && selected === 'requested'"
                   class="d-flex justify-content-center align-items-center"
                   style="border-left: 2px solid #5A5C62FF">
              <a class="d-flex friends-icon justify-content-center align-items-center h-100 w-100"
                 @click.prevent="addFriend(user)"
                 v-b-tooltip.hover title="Принять заявку">
                <b-icon icon="check2" style="color: #6C757D"></b-icon>
              </a>
            </b-col>

            <b-col v-if="isPrincipal"
                   class="friends-icon d-flex justify-content-center align-items-center" style="border-left: 2px solid #5A5C62FF">
              <a class="d-flex friends-icon justify-content-center align-items-center h-100 w-100"
                 @click.prevent="deleteFriend(user)"
                 v-b-tooltip.hover="setTooltipTextForRemove()">
                <b-icon icon="plus" rotate="45" font-scale="1.5rem" style="color: #6C757D"></b-icon>
              </a>
            </b-col>

          </b-row>
        </b-col>
      </b-row>
    </b-list-group-item>

  </b-list-group>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
import axios from "axios";
export default {
  name: "Friends",
  props: ['userData'],

  async beforeMount() {
    try {
      let response = await axios.get(`${this.$hostname}/friends`, {
        params: { username: this.$route.params.username }
      });
      this.allUsers = response.data;
      this.currentList = this.allUsers.accepted;
    } catch (e) {
      console.log(e);
    }
  },

  data() {
    return {
      allUsers: {
        accepted: [],
        requested: [],
        outgoingRequests: [],
      },
      currentList: [],
      selected: 'accepted',
      options: [
        { value: 'accepted', text: 'Все друзья' },
        { value: 'requested', text: 'Заявки в друзья' },
        { value: 'outgoingRequests', text: 'Исходящие заявки' },
      ],
    }
  },

  computed: {
    isPrincipal: function () {
      return this.$route.params.username === this.userData.username;
    },

    filterFriends: function () {
      return this.allUsers[this.selected];
    },

    getAmountOfFriends: function () {
      if (this.selected === 'accepted') return this.allUsers.accepted.length;
      else if (this.selected === 'requested') return this.allUsers.requested.length;
      else return this.allUsers.outgoingRequests.length;
    },

    whoseFriends() {
      if (this.selected === 'requested') {
        return this.isPrincipal? "Заявки в друзья" : `Заявки в друзья ${this.getUsername()}`;
      } else if (this.selected === 'accepted') {
        return this.isPrincipal? 'Ваши друзья' : `Друзья ${this.$route.params.username}`;
      } else {
        return this.isPrincipal? 'Исходящие заявки' : `Исходящие заявки ${this.$route.params.username}`;
      }
    },
  },

  methods: {
    pushForce,

    async goToChatWithUser(user) {
      const response = await axios.get(`${this.$hostname}/chat/get?friendId=${user.id}`);
      const chatRoom = response.data.chatRoom;
      setTimeout(() => this.$root.$refs.ChatBoard.openChatRoom(chatRoom.id, chatRoom))
      this.pushForce('/chat')
    },

    async addFriend(user) {
      await axios.post(`${this.$hostname}/friends/add/${user.id}`);
      this.allUsers.requested = this.allUsers.requested.filter(u => u.id !== user.id);
      this.allUsers.accepted.push(user);
    },

    setTooltipTextForRemove() {
      if (this.selected === 'accepted') return 'Удалить из друзей';
      else if (this.selected === 'requested') return 'Отказать в дружбе';
      else if (this.selected === 'outgoingRequests') return 'Отменить запрос в друзья';
    },

    getUsername() {
      return this.isPrincipal? this.userData.username : this.$route.params.username;
    },

    deleteFriend(friend) {
      try {
        axios.delete(`${this.$hostname}/friends/del/${friend.id}`, {
          params: {status: this.selected}
        });
        this.allUsers[this.selected] =
            this.allUsers[this.selected].filter(e => e.id !== friend.id);
      } catch (e) {
        alert(e);
      }
    },
  },
}
</script>

<style scoped>
.people-list {
  width: 45%;
  min-width: 33%;
  box-shadow: 0 3px 8px #242424;
  border-radius: 20px;
}
.people-list-item {
  background-color: #2C2A38;
}
.friends-info{
  background-color: #2B2936;
  color: floralwhite;
}
.friends-selector {
  background-color: #2C2A38;
  color: floralwhite;
  border: 2px solid #5A5C62FF;
}
.friends-icon {
  cursor: pointer;
}
</style>