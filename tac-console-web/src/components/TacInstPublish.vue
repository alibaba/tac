<template>
    <div class="tacInstPublish">

        <b-row>

            <b-col>
                <h3>服务编码:{{msCode}} 实例ID: {{instId}} </h3>
            </b-col>
        </b-row>
        <hr>
        <b-row>
            <b-col>
                <b-row>
                    <b-col>
                        <h4>预发布</h4> 版本: {{prePublish.jarVersion}} </b-col>
                    <b-col>
                        <b-form-file v-model="file" :state="Boolean(file)" placeholder="Choose a file..."></b-form-file>
                    </b-col>
                    <b-col>
                        <b-button size="sm" variant="warning" v-on:click="onPrePublish(msCode)">
                            预发布
                        </b-button>
                        <router-link :to="{path:'/tacInst/publishcheck',query:{
                        msCode:msCode,
                        instId:instId,
                        action:'preTest'
                    }}" target="_blank">
                            <b-button size="sm" variant="warning">
                                预发布测试
                            </b-button>
                        </router-link>

                    </b-col>
                </b-row>
            </b-col>
            <b-col>
                <b-row>

                    <b-col>
                        <h4>线上发布:</h4> 版本: {{publish.jarVersion}} </b-col>

                    <b-col>
                        <b-button size="sm" variant="danger" v-on:click="onPublish(msCode,instId)">
                            正式发布
                        </b-button>
                        <router-link :to="{path:'/tacInst/publishcheck',query:{
                        msCode:msCode,
                        instId:instId,
                        action:'onlineTest'
                    }}" target="_blank">
                            <b-button size="sm" variant="danger">
                                线上回归
                            </b-button>
                        </router-link>

                    </b-col>
                </b-row>
            </b-col>
        </b-row>
        <hr>
        <div>
            <b-row>
                <b-col>
                    <h4>Git分支实例
                        <b-button size="sm" variant="warning" v-b-modal.modal-inst>
                            新建实例
                        </b-button>
                    </h4>

                </b-col>

            </b-row>
            <b-row>
                <b-col>
                    <div class="panel panel-warning">
                        <div class="panel-body">
                            <b-table striped hover :items="msInstListItems" :fields="fields">
                                <template slot="status" slot-scope="data">
                                    <span v-if="data.item.status==0">新建</span>
                                    <span v-else-if="data.item.status==1">预发布</span>
                                    <span v-else-if="data.item.status==2">正式发布</span>
                                </template>
                                <template slot="operation" slot-scope="data">
                                    <b-button-group size="sm" class="spanButtons">
                                        <b-button size="sm" variant="warning" v-on:click="onClickIntEdit(data.item)">
                                            编辑
                                        </b-button>
                                    </b-button-group>
                                    <b-button-group size="sm" class="spanButtons">
                                        <b-button size="sm" variant="warning" v-on:click="onGitPrePublish(data.item.id)">
                                            预发布
                                        </b-button>
                                        <router-link :to="{path:'/tacInst/publishcheck',query:{msCode:msCode,instId:data.item.id, action:'preTest' }}" target="_blank">
                                            <b-button size="sm" variant="warning">
                                                预发布测试
                                            </b-button>
                                        </router-link>
                                    </b-button-group>
                                    <b-button-group size="sm" class="spanButtons">
                                        <b-button size="sm" variant="danger" v-on:click="onPublish(msCode,data.item.id)">
                                            正式发布
                                        </b-button>
                                        <router-link :to="{path:'/tacInst/publishcheck',query:{ msCode:msCode,instId:data.item.id, action:'onlineTest'}}" target="_blank">
                                            <b-button size="sm" variant="danger">
                                                线上回归
                                            </b-button>
                                        </router-link>
                                    </b-button-group>
                                </template>
                            </b-table>
                        </div>
                    </div>
                </b-col>

            </b-row>
        </div>
        <!-- Modal Component -->
        <b-modal id="modal-inst" ref="modalinst" title="实例" @ok="handleInstOk">
            <form @submit.stop.prevent="handleInstSubmit">
                <b-form-group id="name" label="实例名称" description="">
                    <b-form-input type="text" placeholder="实例名称" v-model="currentInst.name"></b-form-input>
                </b-form-group>
                <b-form-group id="gitBranch" label="git分支" description="">
                    <b-form-input type="text" placeholder="git分支" v-model="currentInst.gitBranch"></b-form-input>
                </b-form-group>
            </form>
        </b-modal>
    </div>

</template>


<script>
const fields = [
  {
    key: 'id',
    label: '实例ID'
  },
  {
    key: 'name',
    label: '实例名称'
  },
  {
    key: 'gitBranch',
    label: 'git分支'
  },
  {
    key: 'status',
    label: '状态'
  },
  'operation'
];
export default {
  name: 'TacInstPublish',
  data() {
    return {
      instId: 0,
      msCode: '',
      prePublish: { jarVersion: '' },
      publish: { jarVersion: '' },
      file: null,
      fields: fields,
      msInstListItems: [],
      currentInst: {
        id: 0,
        name: '',
        gitBranch: '',
        action: 1
      }
    };
  },
  mounted: function() {
    this.msCode = this.$route.params.msCode;

    if (!this.msCode) {
      this.$router.push({ path: '/tacMs/list' });
      return;
    }

    this.getMsInstInfo(this.msCode);
    this.getMsInstList(this.msCode);
  },
  methods: {
    onGitPrePublish: function(instId) {
      this.$http
        .get('/api/inst/gitPrePublish', {
          params: {
            instId
          }
        })
        .then(resp => {
          resp.json().then(result => {
            console.log(result);
            if (result.success) {
              this.$toastr.s('预发布成功');
              this.getMsInstList(this.msCode);
            } else {
              this.$toastr.e(data.msgInfo);
            }
          });
        });
    },
    onClickIntEdit: function(inst) {
      this.currentInst = {
        name: inst.name,
        gitBranch: inst.gitBranch,
        action: 2,
        id: inst.id
      };
      this.$refs.modalinst.show();
    },
    handleInstOk: function(evt) {
      evt.preventDefault();
      let { name, gitBranch, id } = { ...this.currentInst };

      // 参数校验
      if (!name || !gitBranch) {
        return;
      }

      let tacInst = { name, gitBranch, msCode: this.msCode, id };

      if (this.currentInst.action == 1) {
        // create
        this.$http.post('/api/inst/create', tacInst).then(resp => {
          resp.json().then(data => {
            if (data.success) {
              this.$toastr.s('创建成功');
              this.$refs.modalinst.hide();
              this.getMsInstList(this.msCode);
            } else {
              this.$toastr.e(data.msgInfo);
            }
          });
        });
      } else {
        tacInst.id = id;
        this.$http.post('/api/inst/update', tacInst).then(resp => {
          resp.json().then(data => {
            if (data.success) {
              this.$refs.modalinst.hide();
              this.getMsInstList(this.msCode);
            } else {
              this.$toastr.e(data.msgInfo);
            }
          });
        });
      }
    },
    getMsInstInfo: function(msCode) {
      this.$http.get('/api/inst/info/' + msCode).then(resp => {
        resp.json().then(result => {
          let data = result.data;
          if (data != null) {
            this.instId = data.id;
            this.prePublish.jarVersion = data.prePublishJarVersion;
            this.publish.jarVersion = data.jarVersion;
          }
        });
      });
    },
    getMsInstList: function(msCode) {
      this.$http.get('/api/inst/list/' + msCode).then(resp => {
        resp.json().then(result => {
          this.msInstListItems = result.data;

          console.log(result.data);
        });
      });
    },
    onPrePublish: function(msCode) {
      if (this.file == null) {
        this.$toastr.e('缺少文件');
        return;
      }
      let data = new FormData();
      data.append('msCode', msCode);
      data.append('file', this.file);
      data.append('instId', this.instId);
      let config = {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      };
      this.$http.post('/api/inst/prePublish', data, config).then(resp => {
        resp.json().then(result => {
          if (result.success) {
            this.getMsInstInfo(msCode);
            this.$toastr.s('预发布成功');
          } else {
            this.$toastr.e(result.msgInfo);
          }
        });
      });
    },
    onPublish: function(msCode, instId) {
      this.$http
        .post('/api/inst/publish', null, {
          params: {
            msCode,
            instId
          }
        })
        .then(resp => {
          resp.json().then(result => {
            if (result.success) {
              this.$toastr.s('发布成功');
              this.getMsInstList(this.msCode);
            } else {
              this.$toastr.e(result.msgInfo);
            }
          });
        });
    }
  }
};
</script>



<style scoped>

</style>

