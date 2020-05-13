<template>
  <div class="fit" style="display:flex;flex-direction:row;position:relative;">
    <div
      style="position:relative;flex-grow:4;height:100%;display:flex;width:0;flex-direction:column;border-right:1px solid rgba(0,0,0,0.13)"
    >
      <div style="flex-grow:4;height:0;width:100%;">
        <slot name="left"></slot>
        <div style="position:absolute;top:10px;right:10px;">
          <slot name="buttons"></slot>
        </div>
      </div>
      <div
        style="display:flex;flex-direction:row;flex-grow:2;height:0;width:100%;border-top:1px solid rgba(0,0,0,0.13)"
      >
        <div style="height:100%;flex-grow:1;width:0;border-right:1px solid rgba(0,0,0,0.13)">
          <slot name="b1"></slot>
        </div>
        <div style="height:100%;flex-grow:1;width:0;">
          <slot name="b2"></slot>
        </div>
      </div>
    </div>
    <div style="flex-grow:2;height:100%;max-width: 300px;min-width:300px;">
      <slot name="right"></slot>
    </div>
  </div>
</template>
<script>
import echarts from "echarts";
// import {mapState} from 'vuex'

export default {
  name: "thirdLayout",
  data() {
    return {};
  }
};
</script>
<style scoped>
.echart {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
.fit {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
