<template>
  <!--
    参数说明：
      id: 每个组件要提供一个唯一的id，用以构建echarts图，
      loading：加载标志，为true则显示加载中动画
      labels: x与y坐标轴标签，例：['a','b','c'],
      copy: true则将像素图的右下部分复制到左上部分以实现对称
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

export default {
  name: "PixelMap",
  data() {
    return {
      resize: null,
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
          color: ["#ffffff", "#409eff"],
          show: false
        },
        series: [
          {
            type: "map",
            roam: true,
            map: this.id,
            aspectScale: 1,
            data: []
          }
        ]
      }
    };
  },
  props: {
    param: {
      type: Object,
      required: true
    },
    id: {
      type: String,
      default: ""
    }
  },
  watch: {
    param(to, from) {
      this.showMap();
    }
  },
  mounted() {
    console.log('hhh')
    this.chart = this.$echarts.init(document.getElementById(this.id));
    this.showMap();
    window.addEventListener("resize", () => {
      this.chart.resize();
    });
  },
  methods: {
    showMap() {
      this.option.series[0].data = this.formatValue();
      this.chart.setOption(this.option);
    },
    formatValue() {
      let vals = [];
      let mapdata = [];
      let values = this.param.values;
      let condition = this.param.condition;
      let noncondition = this.param.noncondition;
      let index = 0;
      for (let i = 0; i < values.length; i++) {
        let vali = values[i];
        for (let j = 0; j < vali.length; j++) {
          if(condition[i].name === noncondition[j].name){
            continue;
          }
          let valj = vali[j];
          let w = valj.length;
          let h = valj[0].length;
          let size = w > h ? w : h;
          let width = 0.9 / size;
          console.log(width)
          for (let k = 0; k < valj.length; k++) {
            let valk = valj[k];
            for (let z = 0; z < valk.length; z++) {
              mapdata.push(
                {
                  type: "Feature",
                  properties: {
                    name: ++index + ""
                  },
                  geometry: {
                    type: "Polygon",
                    coordinates: [
                      [
                        [
                          j + 0.5 - (w / 2 - k) * width,
                          -i - 0.5 + (h / 2 - z) * width
                        ],
                        [
                          j + 0.5 - (w / 2 - k - 1) * width,
                          -i - 0.5 + (h / 2 - z) * width
                        ],
                        [
                          j + 0.5 - (w / 2 - k - 1) * width,
                          -i - 0.5 + (h / 2 - z - 1) * width
                        ],
                        [
                          j + 0.5 - (w / 2 - k) * width,
                          -i - 0.5 + (h / 2 - z - 1) * width
                        ]
                      ]
                    ]
                  }
                }
              );
              vals.push({
                name: index + "",
                value: valk[z],
                 info: '概率：' +valk[z] + '<br>' + 
                    condition[i].name + "：" + condition[i].values[k] + '<br>'
                    + noncondition[j].name + "：" + noncondition[j].values[z]
              });
            }
          }
        }
      }
      console.log( {
        type: "FeatureCollection",
        features: mapdata
      })
      this.$echarts.registerMap(this.id, {
        type: "FeatureCollection",
        features: mapdata
      });
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
