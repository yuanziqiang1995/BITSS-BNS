<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
    };
  }
};
</script>
<style>
.el-tabs__content {
  width: 100%;
  height: calc(100% - 39px);
  overflow-y: auto;
}
.el-tabs--border-card {
  box-shadow: none;
  height: 100%;
  border: none;
}
.el-steps--simple {
  background: transparent;
}
.el-step.is-simple:not(:last-of-type) .el-step__title {
  max-width: none;
}
</style>