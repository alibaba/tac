// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import VueResource from 'vue-resource';
import Toastr from 'vue-toastr';
import App from './App';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import 'jsoneditor/dist/jsoneditor.css';

import 'vue-toastr/dist/vue-toastr.css';

import TacConsole from '@/components/TacConsole';
import TacJSONEditor from '@/components/TacJSONEditor';

import '../static/main.css';

Vue.use(Toastr);
Vue.use(BootstrapVue);
Vue.use(VueResource);

Vue.component('TacConsole', TacConsole);
Vue.component('TacJSONEditor', TacJSONEditor);

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    template: '<App/>',
    components: { App }
});
