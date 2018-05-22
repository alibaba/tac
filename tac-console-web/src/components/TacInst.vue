<template>
    <div class="tacInst">
        <h1>发布流程</h1>
        <div>
            <b-alert show variant="primary">
                <b-badge variant="danger">1</b-badge>
                上传编译好的zip文件
                <b-badge variant="danger">2</b-badge>
                预发布
                <b-badge variant="danger">3</b-badge>
                测试验证
                <b-badge variant="danger">4</b-badge>
                线上发布
                <b-badge variant="danger">5</b-badge>
                回归验证
            </b-alert>
        </div>
        <router-view></router-view>

    </div>

</template>


<script>
export default {
  name: 'TacInst'
};
</script>



<style scoped>
.tacInst {
  text-align: left;
  padding: 30px;
}
</style>

