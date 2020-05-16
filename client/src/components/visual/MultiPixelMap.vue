<template>
  <!--
    多变量联合概率像素图，上左下右，5种data
    参数说明：
      id: 每个组件要提供一个唯一的id，用以构建echarts图，
      loading：加载标志，为true则显示加载中动画
      on-submit：数据提交，响应函数。
      vars: 变量列表（数组），对于每项，name表示名称,id是唯一标识，values表示所有取值，ps对应values每个值的概率
        例：
            [{name:'a',id:1,values:[1,2,3],ps:[0.5,0.2,0.3]},
            {name:'b',id:2,values:['sd','dd','assd'],ps:[0.3,0.4,0.3]}]
      values: 像素图数据，数组的每项表示像素图的一个区域（表示两个变量的关系），例如：
      [
        {
          x: 0, y: 0,//x,y表示该区域在图中的位置
          w: 2, h: 3,//w,h表示该区域的宽与高
          vx: [0,1],//vx表示该区域对应横坐标变量的所有取值
          vy: [-1,0,1],vy表示该区域对应纵坐标变量的所有取值
          p: [//该数组表示这个区域的所有像素点的概率值，依次对应vx和vy的每个组合
            0.5,0.3,
            0.6,0.5,
            0.2,0.7
          ]
        }
      ]
      values的样例：
     [{"x":1,"y":0,"h":1,"w":6,"p":[0.90,0.92,0.7782147880768684,0.41971960710478373,0.46346138181725793,0.8138803008726139],"vx":[-1,0,1,2,3,4],"vy":[-1]},
     {"x":2,"y":0,"h":1,"w":7,"p":[0.52,0.79,0.8434874536639436,0.6506222161706117,0.1359123381170695,0.5681127408381585,0.8592592963773573],"vx":[-1,0,1,2,3,4,5],"vy":[-1]},
     {"x":2,"y":1,"h":2,"w":7,"p":[0.83,0.98,0.6851006410505089,0.35197044086699925,0.48078281359007313,0.5902255429377656,0.9499852802834596,0.20803383803786235,0.5139056770071386,0.5235830772566532,0.6354050222712235,0.21757675105507301,0.2959415525657527,0.808400028547031],"vx":[-1,0,1,2,3,4,5],"vy":[-1,0]},
     {"x":3,"y":0,"h":1,"w":8,"p":[0.98,0.46,0.8700352116998193,0.4585151456853811,0.49024103132914343,0.5238624284876143,0.4686784205851229,0.9981796808576386],"vx":[-1,0,1,2,3,4,5,6],"vy":[-1]},
     {"x":3,"y":1,"h":2,"w":8,"p":[0.33,0.6598340284803363,0.6289109420156767,0.279444254183552,0.25815351522723673,0.15210284452727252,0.6802530040792112,0.1376027669404234,0.8163229664317908,0.42691023579914855,0.6966902091687881,0.8953315290412642,0.9295414363013959,0.13440825541794532,0.7514116837448552,0.9622686004210925],"vx":[-1,0,1,2,3,4,5,6],"vy":[-1,0]},
     {"x":3,"y":2,"h":3,"w":8,"p":[0.29,0.29503428472935345,0.48301130346952337,0.06712283243373429,0.01525395057942891,0.6871342347609934,0.367950790674473,0.48383039702859376,0.3978743852153499,0.9470814375467094,0.9913117189101142,0.4413495656503419,0.03846234310594543,0.28572585434002207,0.4790497097981148,0.800965070044684,0.2046481984662265,0.30715629076882434,0.2031396994185819,0.8902440784841701,0.986529728126031,0.08799398407602843,0.8900885627797188,0.36428363998827074],"vx":[-1,0,1,2,3,4,5,6],"vy":[-1,0,1]}]

  -->
  <div class="echart" style="width:100%;height:100%;" :id="id"></div>
</template>
<script>
import echarts from "echarts";
// import {mapState} from 'vuex'
const colors = [
  // 'rgb(255,255,0)','rgb(227,207,87)','rgb(255,153,18)','rgb(124,252,0)',
  // 'rgb(34,139,34)']
  '#000','#888','#444','#aaa','#666'
]
//[变量名，位置，值,x,y,h,w,cellWidth,deltaX,cellHeight,deltaY,p]

//[x,y,w,h,cellWidth,deltaX,cellHeight,deltaY,p]

export default {
  name: "MultiPixelMap",
  data() {
    return {
      option: {
        tooltip: {
          position: "inside",
          formatter: function(params, ticket, callback) {
            return `${params.data.info}`;
          }
        },
        animation: false,
        backgroundColor: "transparent",
        xAxis: {
          type: "category",
          axisTick: {
            show: false
          },
          interval: 20,
          data: [],
          axisLine: {
            show: false
          }
        },
        yAxis: {
          interval: 20,
          type: "category",
          data: [],
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          }
        },
         visualMap: {
          min: 0,
          max: 1,
          calculable: true,
          orient: "horizontal",
          left: "center",
          bottom: "15%",
          color: ["#409eff","#f8f8f8"],
          show: false
        },
        series: [
          {
            type: "map",
            roam: true,
            map: this.id,
            aspectScale: 1,
            data: []
          }]
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
      default() {
        return {
          left: [],
          right: [],
          top: [],
          bottom: [],
          map: []
        };
      }
    }
  },
  watch: {
    values(to, from) {
      this.option.series[0].data = this.formatValue();
      this.chart.clear();
      this.chart.setOption(this.option);
    },
    loading(to, from) {
      if (to !== from) {
        if (to === true) {
          this.showLoading();
        } else if (to === false) {
          this.hideLoading();
        }
      }
    }
  },
  mounted() {
    this.chart = this.$echarts.init(document.getElementById(this.id));
    let geo = [];
    let geo2 = [];
    let data = [];
    // let data2 = [];
    this.formatBorder(geo, data);
    this.formatValue(geo,data);
    this.$echarts.registerMap(this.id, {
      type: "FeatureCollection",
      features: geo
    });
    // this.$echarts.registerMap(this.id + "_2", {
      // type: "FeatureCollection",
      // features: geo2
    // });
    this.option.series[0].data = data;
    // this.option.series[1].data = data2;
    // console.log(geo, data1,data2);
    console.log(this.param)
    this.chart.setOption(this.option);

    window.addEventListener("resize", () => {
      this.chart.resize();
    });
  },
  methods: {
    formatBorder(geo, data) {
      let xlength = 1;
      let top = this.param.top;
      let left = this.param.left;
      let right = this.param.right;
      let bottom = this.param.bottom;
      for (let i of top) {
        xlength *= i.values.length;
      }
      for (let i of bottom) {
        xlength *= i.values.length;
      }
      let ylength = 1;
      for (let i of left) {
        ylength *= i.values.length;
      }
      for (let i of right) {
        ylength *= i.values.length;
      }

      let deltaX = 0.1;
      let deltaY = 0.1;
      let cellWidth = 1;
      let cellHeight = 1;
      let values = [];
      let cnt = 1;
      let index = 0;
      let offset = ylength * (1 + deltaY);
      for (let i = 0; i < top.length; i++) {
        let item = top[i];
        let cellN = xlength / cnt / item.values.length;
        let w = cellN + (cellN - 1) * deltaX;
        for (let j = 0; j < cnt; j++) {
          for (let k = 0; k < item.values.length; k++) {
            let x = (j * item.values.length + k) * (w + deltaX);
            let y = offset + (top.length - 1 - i) * (1 + deltaY);

            let h = 1;
            geo.push({
              type: "Feature",
              properties: {
                name: ++index + ""
              },
              geometry: {
                type: "Polygon",
                coordinates: [
                  [
                    [x, y + 0.3],
                    [x, y + h],
                    [x + w, y + h],
                    [x + w, y + 0.3]
                  ]
                ]
              }
            });
            data.push({
              name: index + "",
              value: k / item.values.length * 0.8 + 0.2,

              info:
                top[i].name +
                "：" +
                top[i].values[k].name
            });
          }
        }
        cnt *= item.values.length;
      }

      for (let i = 0; i < bottom.length; i++) {
        let item = bottom[i];
        let cellN = xlength / cnt / item.values.length;
        let w = cellN + (cellN - 1) * deltaX;
        for (let j = 0; j < cnt; j++) {
          for (let k = 0; k < item.values.length; k++) {
            let x = (j * item.values.length + k) * (w + deltaX);
            let y = (i - bottom.length) * (1 + deltaY);

            let h = 1;
            geo.push({
              type: "Feature",
              properties: {
                name: ++index + ""
              },
              geometry: {
                type: "Polygon",
                coordinates: [
                  [
                    [x, y],
                    [x, y + h - 0.3],
                    [x + w, y + h - 0.3],
                    [x + w, y]
                  ]
                ]
              }
            });
            data.push({
              name: index + "",
              value: k / item.values.length * 0.8 + 0.2,
              info:
                bottom[i].name +
                "：" +
                bottom[i].values[k].name
            });
          }
        }
        cnt *= item.values.length;
      }
      cnt = 1;
      let ox = offset;
      for (let i = 0; i < left.length; i++) {
        let item = left[i];
        let cellN = ylength / cnt / item.values.length;
        let h = cellN + (cellN - 1) * deltaY;
        for (let j = 0; j < cnt; j++) {
          for (let k = 0; k < item.values.length; k++) {
            let x = (i - left.length) * (1 + deltaX);
            let y = ox - (j * item.values.length + k) * (h + deltaY);
            let w = 1;

            geo.push({
              type: "Feature",
              properties: {
                name: ++index + ""
              },
              geometry: {
                type: "Polygon",
                coordinates: [
                  [
                    [x, y],
                    [x, y - h],
                    [x + w - 0.3, y - h],
                    [x + w -0.3, y]
                  ]
                ]
              }
            });
            data.push({
              name: index + "",
              value: k / item.values.length * 0.8 + 0.2,
              info:
                left[i].name +
                "：" +
                left[i].values[k].name
            });
          }
        }
        cnt *= item.values.length;
      }

      offset = xlength * (1 + deltaX);
      for (let i = 0; i < right.length; i++) {
        let item = right[i];
        let cellN = ylength / cnt / item.values.length;
        let h = cellN + (cellN - 1) * deltaY;
        for (let j = 0; j < cnt; j++) {
          for (let k = 0; k < item.values.length; k++) {
            let x = offset + (right.length - 1 - i) * (1 + deltaX);
            let y = ox - (j * item.values.length + k) * (h + deltaY);
            let w = 1;

            geo.push({
              type: "Feature",
              properties: {
                name: ++index + ""
              },
              geometry: {
                type: "Polygon",
                coordinates: [
                  [
                    [x + 0.3, y],
                    [x + 0.3, y - h],
                    [x + w, y - h],
                    [x + w, y]
                  ]
                ]
              }
            });
            data.push({
              name: index + "",
              value: k / item.values.length * 0.8 + 0.2,
              info:
                right[i].name +
                "：" +
                right[i].values[k].name
            });
          }
        }
        cnt *= item.values.length;
      }
    },
    formatValue(geo,data) {
      let map = this.param.map
      let xlength = 1;
      let top = this.param.top;
      let left = this.param.left;
      let right = this.param.right;
      let bottom = this.param.bottom;
      for (let i of top) {
        xlength *= i.values.length;
      }
      for (let i of bottom) {
        xlength *= i.values.length;
      }
      let ylength = 1;
      for (let i of left) {
        ylength *= i.values.length;
      }
      for (let i of right) {
        ylength *= i.values.length;
      }
      let index = -1;
       let offset = ylength * (1 + 0.1);
      for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[0].length; j++) {
          --index;
          let cnt = xlength;
          let info = "概率：" + map[i][j];
          for(let k of top){
            cnt /= k.values.length;
            let idx = Math.floor(i / cnt)
            idx %= k.values.length;
            info = info + "<br>" + k.name + "：" + k.values[idx].name
          }
          for(let k of bottom){
            cnt /= k.values.length;
            let idx = Math.floor(i / cnt)
            idx %= k.values.length;
            info = info + "<br>" + k.name + "：" + k.values[idx].name
          }
          cnt = ylength;
          for(let k of left){
            cnt /= k.values.length;
            let idx = Math.floor(i / cnt)
            idx %= k.values.length;
            info = info + "<br>" + k.name + "：" + k.values[idx].name
          }
          for(let k of right){
            cnt /= k.values.length;
            let idx = Math.floor(i / cnt)
            idx %= k.values.length;
            info = info + "<br>" + k.name + "：" + k.values[idx].name
          }
          let p = map[i][j];
          data.push({
            name: index + "",
            value: map[i][j],
            info: info,
            // itemStyle: {
                // areaColor : `rgb(${p*64 + (1-p)*255},${p*159 + (1-p)*255},255)`
            // }
          })
          let x = j *(1+0.1)
          let y = offset - i*(1+0.1)
          geo.push({
              type: "Feature",
              properties: {
                name: index + ""
              },
              geometry: {
                type: "Polygon",
                coordinates: [
                  [
                    [x, y],
                    [x, y - 1],
                    [x + 1, y - 1],
                    [x + 1, y]
                  ]
                ]
              }
            });
        }
      }
    },
    showLoading() {
      this.chart.showLoading({
        text: "loading",
        color: "#fff",
        textColor: "#fff",
        maskColor: "rgba(0, 0, 0, 0.3)"
      });
    },
    hideLoading() {
      this.chart.hideLoading();
    }
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
