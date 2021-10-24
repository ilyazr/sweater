<template>
  <b-container fluid class="px-0">
    <div id="app" v-cloak style="background-color: #3D3A4B;">

      <router-view :login="login"
                   :signUp="signUp"
                   :logout="logout"
                   :sendMsg="sendMsg"
                   :getUserData="userData"
                   :changeHasToken="changeHasToken"
                   :mockUserData="mockUserData"/>
    </div>

  </b-container>
</template>

<script>
import axios from "axios";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

export default {
  name: 'App',

  beforeDestroy() {
    this.disconnectWS();
  },

  components: {
  },
  computed: {
    getUserData() {
      return this.userData
    }
  },
  data() {
    return {
      host: `${this.$hostname}/ws`,
      socket: null,
      stompClient: null,
      isConnected: false,
      hasToken: !!localStorage.getItem('user-token'),
      isLogin: false,
      userData: {
        avatarURI: null,
        id: null,
        username: null,
        password: null,
        firstName: null,
        lastName: null,
        email: null,
        phoneNumber: null,
        country: null,
      },
      mockUserData: {
        avatarURI: 'https://i.pinimg.com/originals/46/da/e5/46dae512e375bee2664a025507da8795.jpg',
        username: 'dev123',
        password: '123',
        firstName: 'Ilya',
        lastName: 'Zakharov',
        dateOfBirth: new Date(1999, 2, 9),
        country: 'Russia'
      }
    }
  },

  async created() {
    let hasToken = !!localStorage.getItem('user-token')
    if (hasToken) {
      this.userData = JSON.parse(localStorage.getItem('user-data'))
      await this.connectWS()
    }
  },

  methods: {
    async signUp(userData) {
      console.log("try to signup...")
      let endpoint = `${this.$hostname}/registration`
      let response = await axios.post(endpoint, {
        username: userData.username,
        password: userData.password,
        email: userData.email,
        dateOfBirth: userData.dateOfBirth
      });
      if (response.status !== 200) alert("Error while register. Try again")
      else {
        alert("All that remains is to verify your account. Follow the link that came to your email")
      }
    },

    async login(token) {
      localStorage.setItem('user-token', token);
      axios.defaults.headers['Authorization'] = `${token}`;
      let response = await axios.get(`${this.$hostname}/user/get`);
      let userData = response.data;
      this.userData = userData
      localStorage.setItem('user-data', JSON.stringify(userData))
      await this.connectWS()
    },

    logout() {
      localStorage.removeItem('user-token')
      localStorage.removeItem('user-data')
      delete axios.defaults.headers['Authorization']
      this.disconnectWS()
      this.changeHasToken()
      this.$router.push('/login')
    },

    changeHasToken() {
      this.hasToken = !this.hasToken;
    },

    //------------------------ WEB SOCKET METHODS ------------------------//
    async connectWS() {
      this.socket = new SockJS(this.host);
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({token: localStorage.getItem('user-token')}, frame => {
        console.log('=====> WebSocket: ' + frame);

        this.stompClient.subscribe(`/user/${this.getUserData.id}/queue/pm`, msg => {
          console.log(`Current path: ${this.$router.currentRoute.fullPath}`)
          const bodyMsg = JSON.parse(msg.body);

          if (this.$router.currentRoute.fullPath !== '/chat') {
            this.processIncomeMessage(bodyMsg);
            this.$root.$refs.ChatListOfFriends.addOneToCounterOfUnreadMessages(bodyMsg.chatRoomId)
          } else {
            if (this.$root.$refs.ChatBoard.isInChatRoomWithId(bodyMsg.chatRoomId)) {
              this.$root.$refs.ChatBoard.receiveIncomeMessage(bodyMsg)
            } else {
              this.$root.$refs.ChatListOfFriends.addOneToCounterOfUnreadMessages(bodyMsg.chatRoomId)
            }
          }
        });

      }, error => console.log('=====> ERROR WS: ' + JSON.stringify(error)));

      this.isConnected = true;

    },

    disconnectWS() {
      if (this.stompClient) {
        this.stompClient.disconnect();
      }
      this.isConnected = false;
      console.log('=====> WS disconnect');
    },

    sendMsg(message) {
      console.log(`Send message: ${JSON.stringify(message)}`)
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send('/app/pm', JSON.stringify(message));
      }
    },

    processIncomeMessage(msg) {
    this.$bvToast.toast(msg.text, {
      title: `Сообщение от ${msg.senderUsername}`,
      variant: 'secondary',
      solid: true
    });
    }

  },

}
</script>

<style>
#app, #app2 {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

[v-cloak] {
  display: none;
}

</style>
