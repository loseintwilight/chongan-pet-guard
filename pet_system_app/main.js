import Vue from 'vue';
import App from './App';
import store from './store'; // store
import plugins from './plugins'; // plugins
import './permission'; // permission
import { getDicts } from '@/api/system/dict/data';
import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html';

Vue.component('mp-html', mpHtml);

Vue.use(plugins);

Vue.config.productionTip = false;
Vue.prototype.$store = store;
Vue.prototype.getDicts = getDicts;

App.mpType = 'app';

const app = new Vue({
	...App
});

app.$mount();
