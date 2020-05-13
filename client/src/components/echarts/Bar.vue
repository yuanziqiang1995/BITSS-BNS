<template>
  <div class="echart" style="width:100%;height:300px;" :id="id"></div>
</template>
<script>
import echarts from "echarts";
// import {mapState} from 'vuex'

export default {
  name: "PixelMap",
  data() {
    return {
      resize: null,
      option: {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: "shadow" // 默认为直线，可选为：'line' | 'shadow'
          },
          formatter(data){
              return data.reduce((a,b)=>{
                  return a + b.data.info
              },data[0].axisValue)
          }
        },
        visualMap: {
          min: 0,
          max: 1,
          calculable: true,
          orient: "horizontal",
          left: "center",
          bottom: "15%",
          color: ["#409eff", "#d0e0f0"],
          show: false
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        xAxis: [
          {
            type: "category",
            data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
          }
        ],
        yAxis: [
          {
            type: "value",
            min: 0,
            max: 1
          }
        ],
        series: [
          {
            name: "直接访问",
            type: "bar",
            data: [0.1, 0.2, 0.5, 0.4, 0.2, 0.7]
          },
          {
            name: "邮件营销",
            type: "bar",
            data: [0.9, 0.2, 0.5, 0.4, 0.2, 0.7]
          },
          {
            name: "联盟广告",
            type: "bar",
            data: [0.6, 0.4, 0.6, 0.1, 0.2, 0.3]
          }
        ]
      }
    };
  },
  props: {
    id: {
      type: String,
      default: ""
    },
    param: {
      type: Object,
      required: true
    }
  },
  watch: {
    param(to, from) {
      this.setOption(to);
    }
  },
  mounted() {
    this.chart = this.$echarts.init(document.getElementById(this.id));
    this.setOption(this.param);
    window.addEventListener("resize", () => {
      this.chart.resize();
    });
  },
  methods: {
    setOption(param) {
      let labels = param.labels;
      let series = param.series;
      this.option.xAxis[0].data = labels;
      let ssss = [];
      for (let i = 0; i < series.length; i++) {
        ssss.push({
          name: "_" + i,
          type: "bar",
          data: series[i]
        });
      }
      this.option.series = ssss;
      console.log(ssss)
      this.chart.setOption(this.option);
    },
    formatValue() {
      let vals = [];
      let values = this.param.values;
      let condition = this.param.condition;
      let noncondition = this.param.noncondition;
      for (let i of values) {
        for (let j = 0; j < i.w; j++) {
          for (let k = 0; k < i.h; k++) {
            vals.push([
              i.x,
              condition.length - 1 - i.y,
              j,
              k,
              i.w,
              i.h,
              noncondition[i.x],
              condition[i.y],
              i.vx[j],
              i.vy[k],
              i.p[j * i.h + k],
              1 - i.p[j * i.h + k]
            ]);
          }
        }
      }
      return vals;
    }
    // showLoading() {
    //   this.chart.showLoading({
    //     text: "loading",
    //     color: "#fff",
    //     textColor: "#fff",
    //     maskColor: "rgba(0, 0, 0, 0.3)"
    //   });
    // },
    // hideLoading() {
    //   this.chart.hideLoading();
    // }
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
</style>
