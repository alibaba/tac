<template>
    <div class="">
        <b-navbar toggleable="md" type="dark" variant="info">

            <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

            <b-navbar-brand href="#">TacAdmin</b-navbar-brand>

            <b-collapse is-nav id="nav_collapse">

                <b-navbar-nav>

                    <b-nav-item>
                        <router-link to="/">Home</router-link>
                    </b-nav-item>
                    <b-nav-item>
                        <router-link to="/tacMs">TacMicroService</router-link>
                    </b-nav-item>
                    <b-nav-item href="#">Github</b-nav-item>
                </b-navbar-nav>

                <!-- Right aligned nav items -->
                <!-- <b-navbar-nav class="ml-auto">

      <b-nav-form>
        <b-form-input size="sm" class="mr-sm-2" type="text" placeholder="Search"/>
        <b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>
      </b-nav-form>



    </b-navbar-nav> -->

            </b-collapse>
        </b-navbar>

        <!-- navbar-1.vue -->

        <div class="app-container">
            <router-view></router-view>
        </div>

    </div>
</template>

<script>
import Home from '@/components/Home';

export default {
  name: 'TacConsole',
  data() {
    return {
      msg: 'Welcome to Your Vue.js App'
    };
  },
  mounted() {
    this.$toastr.defaultPosition = 'toast-top-center';
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1,
h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
