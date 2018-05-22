import Vue from 'vue';
import Router from 'vue-router';
import TacConsole from '@/components/TacConsole';
import Home from '@/components/Home';
import TacMs from '@/components/TacMs';
import TacMsList from '@/components/TacMsList';
import TacMsEdit from '@/components/TacMsEdit';
import TacInst from '@/components/TacInst';
import TacInstPublish from '@/components/TacInstPublish';
import TacInstPublishCheck from '@/components/TacInstPublishCheck';

Vue.component('Home', Home);
Vue.component('TacMs', TacMs);

Vue.use(Router);

export default new Router({
    routes: [
        { path: '/', redirect: '/home' },
        {
            path: '/home',
            name: 'home',
            component: Home
        },
        {
            path: '/tacMs',
            redirect: '/tacMs/list',
            component: TacMs,
            children: [
                {
                    path: 'list',
                    component: TacMsList
                },
                {
                    path: 'new',
                    name: 'newMs',
                    component: TacMsEdit
                },
                {
                    path: 'edit/:code',
                    name: 'editMs',
                    component: TacMsEdit
                }
            ]
        },
        {
            path: '/tacInst',
            component: TacInst,
            redirect: '/tacMs/list',
            children: [
                {
                    name: 'msInstPublish',
                    path: 'publish/:msCode',
                    component: TacInstPublish
                },
                {
                    path: 'publishcheck',
                    name: 'instTest',
                    component: TacInstPublishCheck
                }
            ]
        }
    ]
});
