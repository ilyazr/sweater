<template>
  <b-container fluid class="px-0">
<!--  Navbar  -->
    <b-navbar class="custom-nav" sticky fixed="top" toggleable="lg" type="dark">

      <div>
        <b-button @click="toggleSidebar" variant="outline-warning">
          Menu
        </b-button>
      </div>

      <b-navbar-brand href="#" class="ml-3">
        <img :src="require(`@/assets/sweater-logo.svg`)" />
      </b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>

        <b-navbar-nav class="ml-auto">

          <b-navbar variant="faded" type="light"
                    class="px-0 py-0 d-flex mr-2"
                    style="max-height: 40px; max-width: 40px">
            <b-avatar variant="secondary" :src="userData.avatarURI? userData.avatarURI : ''"></b-avatar>
          </b-navbar>

          <b-nav-item-dropdown right>
            <template #button-content>
              <em> {{ userData.username }}</em>
            </template>
            <b-dropdown-item :href="'/profile'" @click.prevent="pushForce('/profile')">Профиль</b-dropdown-item>
            <b-dropdown-divider></b-dropdown-divider>
            <b-dropdown-item href="#" @click="logout">Выйти</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <b-container fluid>
      <b-row class="d-flex min-vh-100 h-100">

          <b-col lg="3" md="12" style="min-height: 100%; background-color: #2C2A38;
box-shadow: 5px 0 8px 0 #242424; " v-if="!sidebarIsHidden"
                 class="d-flex px-0 mr-3">
            <b-row class="d-flex mx-auto mb-auto mt-auto btn-row">
              <b-col cols="12">
                <b-button variant="outline-secondary" pill class="side-bar-btn mt-1"
                          @click="pushForce(`/wall/${userData.username}`)">
                  <b-icon icon="house-fill"></b-icon>
                  Главная
                </b-button>
              </b-col>
              <b-col cols="12">
                <b-button variant="outline-secondary" pill class="side-bar-btn mt-1"
                          @click="pushForce('/profile')">
                  <b-icon icon="person-fill"></b-icon>
                Профиль
               </b-button>
              </b-col>
              <b-col cols="12"><b-button variant="outline-secondary" pill class="side-bar-btn mt-1"
                                         @click="pushForce('/chat')">
                <b-icon icon="envelope-fill"></b-icon>
                Сообщения
              </b-button></b-col>
              <b-col cols="12"><b-button variant="outline-secondary" pill class="side-bar-btn mt-1"
                                         @click="pushForce('/people')">
                <b-icon icon="globe2"></b-icon>
                Люди
              </b-button></b-col>
              <b-col cols="12"><b-button variant="outline-secondary" pill class="side-bar-btn mt-1"
                                         @click="pushForce(`/friends/${userData.username}`)">
                <b-icon icon="people-fill"></b-icon>
                Друзья
              </b-button></b-col>
              <b-col cols="12"><b-button variant="outline-secondary" pill class="side-bar-btn mt-1">
                <b-icon icon="gear-fill"></b-icon>
                Настройки
              </b-button></b-col>
              <b-col cols="12"><b-button variant="outline-secondary" pill class="side-bar-btn mt-1">
                <b-icon icon="three-dots"></b-icon>
                Еще
              </b-button></b-col>
            </b-row>
          </b-col>
        <b-col class="d-flex pl-0 ml-0 pr-0 w-75" style="background-color: #3D3A4B;">

          <router-view class="mx-auto my-auto" :key="$route.fullPath"
                       :sendMsg="sendMsg"
                       :userData="userData"
                       :mockUserData="mockUserData"/>

        </b-col>
      </b-row>
    </b-container>

  </b-container>
</template>

<script>
import { pushForce } from "@/util/routerUtil";
import WallMock from "@/views/WallMock";
export default {
  name: "Home",
  props: ['mockUserData', 'logout', 'sendMsg'],
  components: {
    // eslint-disable-next-line vue/no-unused-components
    WallMock
  },

  data() {
    return {
      hasToken: !!localStorage.getItem('user-token'),
      userData: null,
      isLogin: false,
      sidebarIsHidden: true,
      findByTagsValues: []
    }
  },

  created: function() {
    this.userData = JSON.parse(localStorage.getItem('user-data'))
  },

  methods: {
    pushForce,

    changeHasToken() {
      this.hasToken = !this.hasToken;
    },

    toggleSidebar() {
      this.sidebarIsHidden = !this.sidebarIsHidden;
    },
  },
}
</script>

<style scoped>
 .container, .container-fluid {
   font-family: Avenir, Helvetica, Arial, sans-serif;
   -webkit-font-smoothing: antialiased;
   -moz-osx-font-smoothing: grayscale;
   text-align: center;
   color: #2c3e50;
 }
 .side-bar-btn {
   height: 40px;
   width: 90%
 }
 .btn-row {
   width: 90%;
 }
 .fade-enter-active,
 .fade-leave-active {
   transition: .3s;
 }
 .fade-enter-to {
   transform: translateX(0);
 }

 .fade-leave-to {
   transform: translateX(-100%);
 }
 .custom-nav {
   background-color: #2C2A38 !important;
   box-shadow: 0 3px 8px #242424;
 }
</style>