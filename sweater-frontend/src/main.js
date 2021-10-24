import '@babel/polyfill'
import 'mutationobserver-shim'
import Vue from 'vue'
import './plugins/bootstrap-vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from "axios";

Vue.config.productionTip = false
Vue.prototype.$hostname = 'http://localhost:9090/api'

const token = localStorage.getItem('user-token')
if (token) {
  axios.defaults.headers['Authorization'] = `${token}`;
}

new Vue({
  axios,
  router,
  store,
  render: h => h(App),
}).$mount('#app')
