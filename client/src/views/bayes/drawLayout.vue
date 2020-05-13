<template>
  <div class="fit" style="display:flex;flex-direction:column;position:relative">
    <div style="flex-grow:4;display:flex;flex-direction:row;width:100%;height:0;">
      <div style="flex-grow:4;height:100%;border-right:1px solid rgba(0,0,0,0.13);position:relative;">
        <slot name="left"></slot>
        <div style="position:absolute;top:10px;right:10px;">
          <slot name="buttons"></slot>
        </div>
        <div style="position:absolute;top:10px;left:10px;">
          <slot name="infos"></slot>
        </div>
      </div>
      <div style="flex-grow:0;height:100%;width:350px;">
        <slot name="right"></slot>
      </div>
    </div>
  </div>
</template>
<script>
import echarts from "echarts";
// import {mapState} from 'vuex'

export default {
  name: "drawLayout",
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
