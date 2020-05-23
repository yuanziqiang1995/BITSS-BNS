<template>
  <div class="fit" style="display:flex;flex-direction:column;position:relative">
    <div style="flex-grow:4;display:flex;flex-direction:row;width:100%;height:0;">
      <div style="flex-grow:4;height:100%;border-right:1px solid rgba(0,0,0,0.13);">
        <slot name="left"></slot>
      </div>
      <div style="flex-grow:2;height:100%;max-width: 200px;">
        <slot name="right"></slot>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "firstLayout",
  data() {
    return {};
  }
};
</script>
<style scoped>
.fit {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
