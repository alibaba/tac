<template>
    <div>
        <div class="panel panel-warning">
            <div class="panel-heading">
                <h3 class="panel-title">服务列表</h3>
                <router-link :to="{ name: 'newMs'}" class="btn btn-warning">新建服务</router-link>
            </div>
            <div class="panel-body">
                <b-table striped hover :items="items" :fields="fields">
                    <template slot="operation" slot-scope="data">
                        <b-button-group size="sm" class="spanButtons">
                            <router-link :to="{ name: 'editMs',params:data.item} " class="btn btn-warning">编辑</router-link>
                            <router-link :to="{ name: 'msInstPublish',params:{msCode:data.item.code}} " class="btn btn-success">实例发布</router-link>
                            <!-- <b-button variant="danger" class="spanButton" v-on:click="offlineMs(data.item.code)">删除</b-button> -->
                        </b-button-group>
                    </template>
                </b-table>
            </div>
        </div>

    </div>
</template>

<script>
const items = [];
const fields = [
  {
    key: 'code',
    label: '服务编码'
  },
  {
    key: 'name',
    label: '服务名称'
  },
  'operation'
];
export default {
  name: 'TacMsList',
  data() {
    return {
      items: items,
      fields: fields
    };
  },
  methods: {
    offlineMs: function(msCode) {
      this.$http
        .post('/api/ms/offline', null, {
          params: {
            msCode: msCode
          }
        })
        .then(result => {
          this.loadAllMs();
        });
    },
    loadAllMs: function() {
      this.$http.get('/api/ms/list').then(resp => {
        resp.json().then(result => {
          if (result.success) {
            this.items = result.data;
          }
        });
      });
    }
  },

  mounted: function() {
    this.loadAllMs();
  }
};
</script>

<style scoped>

</style>






