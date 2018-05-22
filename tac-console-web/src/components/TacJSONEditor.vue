<template>
    <div ref="jsoneditor" :style="styleObj"></div>
</template>


<script>
import JSONEditor from 'JSONEditor';

export default {
  name: 'TacJSONEditor',
  props: {
    styleObj: {
      type: Object,
      default: function() {
        return {
          width: '100%',
          height: '500px'
        };
      }
    },
    options: {
      type: Object,
      default: function() {
        return { modes: ['tree', 'view', 'code'] };
      }
    }
  },

  editor: null,
  data() {
    return {};
  },
  methods: {
    setJSON: function(json) {
      this.editor.set(json);
      console.log(this);
    },
    getJSON: function() {
      return this.editor.get();
    }
  },

  mounted: function() {
    let container = this.$refs.jsoneditor;

    this.editor = new JSONEditor(container, this.options);
  }
};
</script>


<style scoped>

</style>




