
<script>
import TacJSONEditor from '@/components/TacJSONEditor';

export default {
  name: 'TacInstPublishCheck',
  mounted: function() {
    this.paramsEditor = this.$refs.checkParamsEditor;

    this.resultEditor = this.$refs.checkResultEditor;

    let data = this.$route.query;

    let { instId, msCode, action } = data;

    this.instId = instId;
    this.msCode = msCode;
    this.action = action;
  },
  methods: {
    test: function() {
      let params = this.paramsEditor.getJSON();
      if (this.action == 'preTest') {
        this.handlePreTest(params);
      } else {
        this.handleOnlineTest(params);
      }
    },
    handlePreTest: function(params) {
      let data = {
        instId: this.instId,
        msCode: this.msCode,
        params: params
      };
      this.$http.post('/api/inst/preTest', data).then(resp => {
        resp.json().then(tacResult => {
          if (tacResult.success) {
            console.log(tacResult);
            let msgInfo = tacResult.data.msgInfo;
            tacResult.data.msgInfo = '';
            this.logResult = msgInfo;
            this.resultEditor.setJSON(tacResult.data);
          } else {
            this.$toastr.e(tacResult.msgInfo);
          }
        });
      });
    },
    handleOnlineTest: function(params) {
      let data = {
        instId: this.instId,
        msCode: this.msCode,
        params: params
      };
      this.$http.post('/api/inst/onlineTest', data).then(resp => {
        resp.json().then(tacResult => {
          if (tacResult.success) {
            console.log(tacResult);
            let msgInfo = tacResult.msgInfo;
            tacResult.msgInfo = '';
            this.logResult = msgInfo;
            this.resultEditor.setJSON(tacResult);
          } else {
            this.$toastr.e(tacResult.msgInfo);
          }
        });
      });
    }
  },
  data() {
    return {
      instId: 0,
      msCode: '',
      action: '',
      logResult: ''
    };
  }
};
</script>





<template>
    <div>
        <span>服务编码: {{msCode}}</span>
        <span>实例ID: {{instId}}</span>
        <b-row>
            <b-col cols="3">
                <span>
                    请求参数
                </span>
                <TacJSONEditor ref="checkParamsEditor"></TacJSONEditor>
            </b-col>
            <b-col cols="1">
                <b-button size="sm" variant="danger" v-on:click="test()" :style="{width: '100%'}">
                    测试
                </b-button>
            </b-col>
            <b-col cols="8">
                <span>
                    结果
                </span>
                <TacJSONEditor ref="checkResultEditor"></TacJSONEditor>
            </b-col>
        </b-row>
        <hr>
        <div>
            <h5>日志</h5>
            <p class="logResult">
                {{logResult}}
            </p>

        </div>

    </div>

</template>




<style scoped>
.logResult {
  background-color: black;
  color: yellow;
  padding: 30px;
  max-height: 600px;
  overflow: scroll;
}
</style>
