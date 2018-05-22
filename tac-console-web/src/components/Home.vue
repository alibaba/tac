<template>
    <b-jumbotron header="TAC-Console" lead="The Tangram App Container">

        <router-link to="/tacMs" class="btn btn-primary">TacMicroService</router-link>
    </b-jumbotron>
</template>


<script>
export default {
  name: 'Home',
  data() {
    return {
      msg: 'Welcome to Tac App'
    };
  }
};
</script>


<style scoped>

</style>
