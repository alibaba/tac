<template>
    <div>
        <b-form @submit="onSubmit" @reset="onReset" v-if="show">
            <b-form-group label="服务ID" v-show="false">
                <b-form-input id="msId" type="text" v-model="form.id" readonly>
                </b-form-input>
            </b-form-group>
            <b-form-group id="msCode" label="服务编码" description="服务编码，唯一">
                <b-form-input id="msCode" type="text" v-model="form.code" required placeholder="msCode" v-bind:readonly="isEdit">
                </b-form-input>
            </b-form-group>
            <b-form-group id="name" label="名称" description="服务名称">
                <b-form-input id="name" type="text" v-model="form.name" required placeholder="name">
                </b-form-input>
            </b-form-group>
            <b-form-group id="gitRepo" label="git仓库" description="git仓库地址">
                <b-form-input id="gitRepo" type="text" v-model="form.gitRepo" placeholder="">
                </b-form-input>
            </b-form-group>
            <b-button type="submit" variant="primary">Submit</b-button>
            <b-button type="reset" variant="danger">Reset</b-button>
        </b-form>
    </div>
    <!-- b-form-1.vue -->
</template>


<script>
export default {
  data() {
    return {
      form: {
        id: 0,
        code: '',
        name: '',
        gitRepo: ''
      },
      show: true,
      isEdit: false
    };
  },
  mounted: function() {
    let editParams = this.$route.params;

    if (editParams && editParams.code) {
      this.isEdit = true;
      this.form = { ...this.$route.params };
    }
  },
  methods: {
    handleSave: function() {
      let tacMc = {
        id: this.form.id,
        code: this.form.code,
        name: this.form.name,
        gitRepo: this.form.gitRepo
      };

      this.$http.post('/api/ms/update', tacMc).then(resp => {
        resp.json().then(data => {
          if (data.success) {
            this.$toastr.s('保存成功');
            this.$router.push({ path: '/tacMs/list' });
          } else {
            this.$toastr.e(data.msgInfo);
          }
        });
      });
    },
    handleCreate: function() {
      let tacMc = {
        code: this.form.code,
        name: this.form.name
      };

      this.$http.post('/api/ms/create', tacMc).then(resp => {
        resp.json().then(data => {
          if (data.success) {
            this.$toastr.s('新增成功');
            this.$router.push({ path: '/tacMs/list' });
          } else {
            this.$toastr.e(data.msgInfo);
          }
        });
      });
    },
    onSubmit(evt) {
      evt.preventDefault();

      if (this.isEdit) {
        this.handleSave();
      } else {
        this.handleCreate();
      }
    },
    onReset(evt) {
      evt.preventDefault();
      /* Reset our form values */
      this.form.code = '';
      this.form.name = '';
      this.form.gitRepo = '';
      this.$nextTick(() => {
        this.show = true;
      });
    }
  }
};
</script>

<style  scoped>

</style>


