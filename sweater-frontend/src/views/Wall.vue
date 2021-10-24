<template>
  <div v-cloak class="mx-3 pl-4 my-3 pt-4 overflow-auto cards-holder">

<!--    User info-->
    <div v-cloak class="user-info-holder mx-auto mb-3 d-flex flex-column" style="color: #6C757D">
      <b-row class="my-0 flex-grow-1">
        <b-col class="col-4 d-flex flex-column"
               style="border-top-left-radius: 10px; border-bottom-left-radius: 10px">
          <b-row class="d-flex flex-column flex-grow-1">
            <b-col class="" style="background-color: #2C2A38; flex-grow: 3; border-top-left-radius: 10px">
              <div class="my-3 d-flex justify-content-center align-items-center avatar-holder">
                <b-avatar fluid style="height: 15rem; width: 15rem;border-radius: 50%" class=""
                       :src="getCurrentUserData().avatarURI"></b-avatar>
              </div>
            </b-col>
            <b-col class="" style="border-bottom-left-radius: 10px">
              <b-row class="d-flex h-100">
                <b-col class="d-flex justify-content-end col-6 py-2 px-0"
                       style="background-color: #2B2936;
                       border-bottom-left-radius: 10px;">
                  <div class="my-auto d-flex flex-column mx-auto w-100 pl-3"
                       style="word-break: break-word">
                    <div class="d-flex justify-content-center font-weight-bold mb-0 text-center" v-if="shouldSetNames">
                      {{ `${setUserProperty('firstName')} ${setUserProperty('lastName')}` }}
                    </div>
                    <div class="my-0 d-flex justify-content-center text-muted text-center">
                      {{ `@${setUserProperty('username')}` }}
                    </div>
                  </div>
                </b-col>

                <b-col class="col-6 d-flex flex-column px-0">
                  <div class="d-flex justify-content-center align-items-center"
                       style="background-color: #2B2936; flex-grow: 1">
                    <div class="mt-1" style="font-size: .95rem">
                      {{ `Посты: ${userInfo.amountOfPosts}` }}
                    </div>
                  </div>
                  <div class="d-flex justify-content-center align-items-center"
                       style="background-color: #2B2936; flex-grow: 1">
                    <a class="d-block link-to-friends mb-1"
                       style="font-size: .95rem"
                       :href="`/friends/${getCurrentUserData().username}`"
                       @click.prevent="pushForce(`/friends/${getCurrentUserData().username}`)">
                      {{ `Друзья: ${userInfo.amountOfFriends}` }}
                    </a>
                  </div>
                </b-col>
              </b-row>
            </b-col>
          </b-row>
        </b-col>

        <b-col class="col-8 d-flex flex-column px-0"
               style="background-color: #2B2936; border-left: 3px solid #2B2936;
               border-top-right-radius: 10px; border-bottom-right-radius: 10px">
          <div class="d-flex justify-content-end align-items-center"
               style="background-color: #2B2936; height: 50px; margin-left: 0px; border-top-right-radius: 10px">
            <div class="mr-4" :style="userInfo.isOnline? 'color: mediumpurple' : 'color: indianred'">
              {{ isUserOnline() }}
            </div>
          </div>
          <div class="d-flex justify-content-center align-items-center mb-2"
               style="background-color: #2C2A38; flex-grow: 1; ">

            <div class="user-data d-flex align-items-center justify-content-center my-2"
                 style="width: 80%; height: 50%">
              <b-list-group class="d-flex w-100 data-list" style="color: black">
                <b-list-group-item class="data-list-item d-flex">
                  <div style="flex: 1" class="text-center">Страна:</div>
                  <div style="flex: 1" class="text-left">{{ `${setUserProperty('country')}` }}</div>
                </b-list-group-item>
                <b-list-group-item class="data-list-item d-flex">
                  <div style="flex: 1" class="text-center">Email:</div>
                  <div style="flex: 1" class="text-left">{{ `${setUserProperty('email')}` }}</div>
                </b-list-group-item>
                <b-list-group-item class="data-list-item d-flex">
                  <div style="flex: 1" class="text-center">Телефон:</div>
                  <div style="flex: 1" class="text-left">{{ `${setUserProperty('phoneNumber')}` }}</div>
                </b-list-group-item>
                <b-list-group-item class="data-list-item d-flex">
                  <div style="flex: 1" class="text-center">Возраст:</div>
                  <div style="flex: 1" class="text-left">{{ `${setUserProperty('age')}` }}</div>
                </b-list-group-item>
              </b-list-group>
            </div>
          </div>
          <div class="mb-1 d-flex justify-content-center"
               style="height: 16%;">
            <b-button class="mt-1 mb-2"
                      variant="outline-warning"
                      size="sm"
                      style="border-radius: 10px"
                      v-if="$route.params.username !== userData.username"
                      :disabled="disableFriendButton"
                      @click="addFriend()">{{ getTextOfFriendshipButton }}</b-button>
          </div>
        </b-col>
      </b-row>
    </div>

<!--    Cards are below-->
    <div class="test-holder flex-column mx-auto mb-3" style="color: white" v-if="isPrincipal">
      <b-row class="ml-2 mr-2 text-row">
        <b-col class="text-holder col-12 d-flex mt-3 mb-2 h-75">
          <b-form-textarea style="background-color: floralwhite"
              id="textarea-auto-height"
              v-model="postText"
              placeholder="What is happening?"
              rows="5"
              no-resize
              @keyup.enter="addNewPost"
          ></b-form-textarea>
        </b-col>
        <b-col class="col-bottom mt-2 mb-2">
          <div class="text-holder-bottom ml-auto">
            <b-row class="h-100" style="margin-right: 0; margin-left: 0">
              <b-col class="col-7 w-100"></b-col>
              <b-col class="col-5 d-flex">
                <b-button @click="addNewPost" class="ml-auto mr-3 px-4" variant="outline-warning">Tweet</b-button>
              </b-col>
            </b-row>
          </div>
        </b-col>
      </b-row>
    </div>

    <b-card-group columns class="b" v-if="posts.length > 0">

      <b-card class="card my-4" header-tag="header" footer-tag="footer"
              v-for="post in posts"
              :key="post.id">
        <template #header>
          <b-row class="d-flex justify-content-start">
            <b-col class="d-flex align-items-center">
              <b-avatar variant="info"
                        :src="isPrincipal? userData.avatarURI : otherUserData.avatarURI"></b-avatar>
              <div class="ml-3 my-auto">
                <p class="font-weight-bold mb-0 text-left" v-if="post.authorFirstName && post.authorLastName">
                  {{ `${post.authorFirstName} ${post.authorLastName}` }}
                </p>
                <p class="my-0 text-muted text-left">@{{ post.authorUsername }}</p>
              </div>
            </b-col>
            <b-col class="d-flex align-items-center justify-content-end">
              <div class="mr-2">
                <b-icon icon="plus" class="del-msg-button"
                        rotate="45"
                        font-scale="1.8rem"
                        style="color: #6C757D"
                        @click="deletePost(post)"
                        v-b-tooltip.hover title="Удалить сообщение"></b-icon>
              </div>
            </b-col>
          </b-row>
        </template>

        <b-card-text>{{ post.text }}</b-card-text>
        <template #footer>
          <b-row class="d-flex justify-content-center">
            <b-col>
              <b-icon style="color: #6c757d;" icon="chat"></b-icon>
            </b-col>
            <b-col>
              <b-icon style="color: #6c757d;"
                      :icon="post.amountOfLikes > 0? 'heart-fill' : 'heart'"
                      :variant="changeHeart(post)"
                      @click="processLike(post)" class="like"></b-icon>
              <span class="ml-1" v-if="post.amountOfLikes > 0">{{ post.amountOfLikes }}</span>
            </b-col>
            <b-col>
              <b-icon style="color: #6c757d;" icon="share"></b-icon>
            </b-col>
            <b-col>
              <b-icon style="color: #6c757d;" icon="three-dots"></b-icon>
            </b-col>
          </b-row>
        </template>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
import axios from "axios";
import { pushForce } from "@/util/routerUtil";
export default {
  name: "Wall",
  props: ['userData'],
  async beforeMount() {
    // fetch all posts
    try {
      // 1. fetch wall messages of user
      // 2. if user not principal => fetch user data
      let response = await axios.get(`${this.$hostname}/wall/messages/${this.$route.params.username}`);
      this.posts = response.data.wallMessages;
      if (this.userData.username !== this.$route.params.username) {
        response = await axios.get(`${this.$hostname}/user/${this.$route.params.username}`);
        this.otherUserData = response.data;
      } else this.isPrincipal = true;

      this.getCurrentUserData()['age'] = this.calculateAge(this.getCurrentUserData().dateOfBirth);

      if (!this.isPrincipal) {
        response = await axios.get(`${this.$hostname}/friends/checkStatus?userId=${this.otherUserData.id}`);
        this.userInfo.friendshipStatus = response.data.friendshipStatus;
      }

      // 1. fetch amount of
      //    1.1. posts
      //    1.2. friends
      // 2. check online
      const getUserId = () => {
        return this.isPrincipal? this.userData.id : this.otherUserData.id
      };

      response = await axios.get(`${this.$hostname}/wall/messages/count`,{
        params: {userId: getUserId()}
      });
      this.userInfo.amountOfPosts = response.data;

      response = await axios.get(`${this.$hostname}/friends/count`,{
        params: {userId: getUserId()}
      });
      this.userInfo.amountOfFriends = response.data;

      response = await axios.get(`${this.$hostname}/user/checkOnline`,{
        params: {userId: getUserId()}
      });
      this.userInfo.isOnline = response.data;

    } catch (e) {
      alert(e);
    }
  },
  data() {
    return {
      postText: '',
      isPrincipal: false,
      posts: [],
      // not principal user
      otherUserData: {},
      // additional info
      userInfo: {
        amountOfPosts: -1,
        amountOfFriends: -1,
        isOnline: false,
        friendshipStatus: null,
      },
    }
  },

  computed: {
    changeHeart() {
      return (post) => {
        return post.isLiked? 'danger' : ''
      }
    },

    shouldSetNames() {
      const obj = this.getCurrentUserData();
      return obj.firstName && obj.lastName;
    },

    disableFriendButton() {
      return !(this.getTextOfFriendshipButton === 'Добавить в друзья' ||
          this.getTextOfFriendshipButton === 'Принять заявку');
    },

    getTextOfFriendshipButton() {
      const status = this.userInfo.friendshipStatus;
      if (status === 'accepted')
        return 'Ваш друг';
      else if (status === 'outgoingRequests')
        return 'Заявка отправлена';
      else if (status === 'requested')
        return 'Принять заявку';
      else return 'Добавить в друзья';
    },
  },

  methods: {
    pushForce,

    isUserOnline() {
      if (this.userData.username === this.$route.params.username) return 'online';
      else return this.userInfo.isOnline? 'online' : 'offline'
    },

    async addFriend() {
      const response = await axios.post(`${this.$hostname}/friends/add/${this.otherUserData.id}`);
      this.userInfo.friendshipStatus = response.data.friendshipStatus;
    },

    getCurrentUserData() {
      return this.isPrincipal? this.userData : this.otherUserData;
    },

    setUserProperty(property) {
      const obj = this.getCurrentUserData();
      let isNullOrUndefined = obj[property] === undefined || obj[property] === null;
      return isNullOrUndefined? '<не указано>' : obj[property];
    },

    addNewPost() {
      this.createNewPost(this.postText)
          .then(post => {
            this.posts.unshift(post);
            console.log(post);
            this.userInfo.amountOfPosts++;
          })
          .catch(alert)
          .finally(() => this.postText = '');
    },

    deletePost(post) {
      axios.delete(`${this.$hostname}/wall/message/${post.id}`);
      this.posts = this.posts.filter(p => p.id !== post.id);
    },

    async createNewPost(text) {
      const endpoint = `${this.$hostname}/wall/message`;
      let response = await axios.post(endpoint, {text});
      return response.data;
    },

    addNewLikeLocally(post) {
      post.isLiked = !post.isLiked;
      post.amountOfLikes++;
    },

    removeLikeLocally(post) {
      post.isLiked = !post.isLiked;
      post.amountOfLikes--;
    },

    async processLike(post) {
      try {
        const endpoint = `${this.$hostname}/wall/message/${post.id}/like`;
        const response = await axios.get(endpoint);
        const data = response.data;
        console.log(data);
        switch (data.operation) {
          case "add":
            this.addNewLikeLocally(post);
            break;
          case "remove":
            this.removeLikeLocally(post)
            break;
          default:
            throw new Error("Wrong like operation")
        }
      } catch (e) {
        throw new Error(e.message);
      }
    },
    calculateAge() {
      // console.log(`Date of birth: ${new Date(this.getCurrentUserData().dateOfBirth).toDateString()}`)
      const birthDate = new Date(this.getCurrentUserData().dateOfBirth);
      const otherDate = new Date();
      let years = (otherDate.getFullYear() - birthDate.getFullYear());
      if (otherDate.getMonth() < birthDate.getMonth() ||
          otherDate.getMonth() == birthDate.getMonth() && otherDate.getDate() < birthDate.getDate()) {
        years--;
      }
      return years;
    },
  },
}
</script>

<style scoped>
  .test-holder {
    background-color: #2C2A38;
    width: 70%;
    min-height: 13rem;
    border-radius: 10px;
    box-shadow: 0 3px 8px #242424;
  }
  .cards-holder {
    height: 100vh;
    min-width: 100%;
    padding-right: 55px;
  }
  .card {
    background-color: #2C2A38;
    color: floralwhite;
    border-radius: 15px !important;
    box-shadow: #242424 0px 2px 8px 0px;
  }
  .card-header {
    border-bottom: none;
  }
  .card-footer {
    border-top: none;
  }
  .text-holder {
    width: 98%;
    height: 35%;
  }
  .text-holder-bottom {
    height: 50%;
  }
  .col-bottom {
    height: 40% !important;
  }
  [v-cloak] {
    display: none;
  }
  .user-info-holder {
    color: white;
    /*margin-top: 3em !important;*/
    width: 70%;
    min-height: 17rem;
    border-radius: 10px;
    box-shadow: 0 3px 8px #242424;
  }
  .user-info-holder > * {
    border-radius: 50px !important;
  }
  .user-info-holder>.row {
    margin-left: 0;
    margin-right: 0;
  }
  .data-list-item {
    padding: .2rem .2rem;
  }
  .list-group {
    box-shadow: 0 3px 8px #242424;
    border-radius: 10px;
  }
  .list-group-item {
    background-color: #2C2A38;
    color: #6C757D;
  }
  .link-to-friends {
    color: inherit;
    cursor: pointer;
  }
  .del-msg-button:hover {
    cursor: pointer;
  }
  .like:hover {
    cursor: pointer;
  }
</style>