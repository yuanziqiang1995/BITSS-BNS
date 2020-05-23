<template>
  <div class="fit" style="display:flex;flex-direction:column;min-width:850px;">
    <div
      style="flex-grow:0; height:46px;margin-top:5px;padding:0 5px 5px 5px; width: 100%; border-bottom:1px solid rgba(0,0,0,0.13)"
    >
      <slot name="top"></slot>
    </div>
    <div style="flex-grow:1; height: 0; width: 100%;position:relative;">
      <slot name="bottom"></slot>
      <div style="position:absolute;top:10px;left:10px;">
        <slot name="icons"></slot>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  name: "bayesLayout",
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
