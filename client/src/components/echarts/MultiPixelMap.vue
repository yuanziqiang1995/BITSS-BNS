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
  <div style="display:flex;flex-direction:column">
    <!-- <div style="flex-grow:0;height:50px;width:100%;"></div>
    <div style="flex-grow:0;height:50px;width:100%;"></div>   -->
    <div style='display:flex;flex-direction:row;flex-grow:1;height:300px;'>
        <div style='flex-grow:0;width:100px;'></div>
        <div class="echart" style='width:500px;height:300px;;' :id="id"></div>
        <div style='flex-grow:0;width:100px;'></div>
    </div>
    <!-- <div style='flex-grow:0;height:50px;'></div> -->
  </div>
</template>
<script>
import echarts from "echarts";
// import {mapState} from 'vuex'

//[变量名，位置，值,x,y,h,w,cellWidth,deltaX,cellHeight,deltaY,p]
function renderBorder(params, api) {
  let height = api.size([1, 1])[1];
  let width = api.size([1, 1])[0];
  let pos = api.value(1);
  let x, y, w, h;
  let xx = api.value(3);

  let yy = api.value(4);
  let hh = api.value(5);
  let ww = api.value(6);
  let cellWidth = api.value(7)
  let deltaX = api.value(8)
  let cellHeight = api.value(9)
  let deltaY = api.value(10)
  if (pos === 0) {
    x = 0.1 + ww * xx * (cellWidth + deltaX);
    h = 1 / 10 / hh / 2;
    y = h * (yy * 2);
    w = ww * cellWidth + (ww - 1) * deltaX;
    console.log(w + "w", ww, xx, x);
  } else if (pos === 2) {
    x = 0.1 + ww * xx * (cellWidth + deltaX);
    h = 1 / 10 / hh / 2;
    y = h * (yy * 2);
    w = ww * cellWidth + (ww - 1) * deltaX;
    x = x;
    h = h;
    y = 1 - y - h;
    w = w;
  } else if (pos === 1) {
    y = 0.1 + hh * yy * (cellHeight + deltaY);
    w = 1 / 10 / ww / 2;
    x = w * (xx * 2);
    h = hh * cellHeight + (hh - 1) * deltaY;
  } else if (pos === 3) {
    y = 0.1 + hh * yy * (cellHeight + deltaY);
    w = 1 / 10 / ww / 2;
    x = w * (xx * 2);
    h = hh * cellHeight + (hh - 1) * deltaY;
    x = 1 - x - w;
    h = h;
    y = y;
    w = w;
  }
  //  console.log(x,y,w,h)
  x *= width;
  y *= height;
  h *= height;
  w *= width;
  x += params.coordSys.x;
  y += params.coordSys.y;

  var rectShape = echarts.graphic.clipRectByRect(
    {
      x,
      y,
      width: w,
      height: h
    },
    {
      x: params.coordSys.x,
      y: params.coordSys.y,
      width: params.coordSys.width,
      height: params.coordSys.height
    }
  );

  return (
    rectShape && {
      type: "rect",
      shape: rectShape,
      style: api.style()
    }
  );
}

//[x,y,w,h,cellWidth,deltaX,cellHeight,deltaY,p]
function renderItem(params, api) {
  let height = api.size([1, 1])[1];
  let width = api.size([1, 1])[0];
  let cellWidth = api.value(4)
  let deltaX = api.value(5)
  let cellHeight = api.value(6)
  let deltaY = api.value(7)
  let x =
    width * (0.1 + api.value(0) * (cellWidth + deltaX)) +
    params.coordSys.x;
  let y =
    height * (0.1 + api.value(1) * (cellHeight + deltaY)) +
    params.coordSys.y;

  let w = cellWidth * width;
  let h = cellHeight * height;

  var rectShape = echarts.graphic.clipRectByRect(
    {
      x,
      y,
      width: w,
      height: h
    },
    {
      x: params.coordSys.x,
      y: params.coordSys.y,
      width: params.coordSys.width,
      height: params.coordSys.height
    }
  );

  return (
    rectShape && {
      type: "rect",
      shape: rectShape,
      style: api.style()
    }
  );
}

export default {
  name: "MultiPixelMap",
  data() {
    return {
      option: {
        tooltip: {
          position: "top",
        //   formatter: function(params, ticket, callback) {
        //     return `<div>${params.data[6]}：${params.data[8]}<br>\
        //         ${params.data[7]}：${params.data[9]}<br>\
        //         概率：${params.data[10]}</div>`;
        //   }
        },
        // dataZoom:[
        //     {
        //         id:'x',
        //         show: true,
        //         type: 'slider',
        //         orient:"horizontal", 
        //         start:0,
        //         end:100
        //     },
        //     {
        //         id:'y',
        //         show: true,
        //         type: 'slider',
        //         orient: 'vertical',
        //         start:0,
        //         end:100
        //     }
        // ],
        animation: false,
        backgroundColor: "transparent",
        xAxis: {
          type: "category",
          axisTick: {
            show: false
          },
          show:false,
          interval: 20,
          data: ['b'],
        },
        yAxis: {
            show:false,
          interval: 20,
          type: "category",
          data: ['a'],
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
          color: ["#fff", "#409eff"],
          show: false
        },
        series: [
          {
            type: "custom",
            renderItem: renderBorder,
            data: this.formatBorder()
          },{
              type:'custom',
              renderItem,
              data:this.formatValue()
          }
        ]
      }
    };
  },
  props: {
    copy: false,
    id: {
      type: String,
      default: ""
    },
    loading: {
      type: Boolean,
      default: false
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
    this.chart.setOption(this.option);
    if (this.loading) {
      this.showLoading();
    }
    window.addEventListener("resize", () => {
        this.chart.resize();
      });
  },
  methods: {
    formatBorder() {
      let left = //this.left;
      [{name:'a',id:1,values:[1,2],ps:[0.3,0.4,0.3]},
      {name:'e',id:5,values:[1,2],ps:[0.3,0.4,0.3]},
      {name:'a',id:1,values:[1,2],ps:[0.3,0.4,0.3]},
      {name:'e',id:5,values:[1,2],ps:[0.3,0.4,0.3]}]
      let right = //this.right;
       [{name:'b',id:2,values:[1,2],ps:[0.1,0.9]}]
      let top = //this.top;
       [{name:'c',id:3,values:[1,2],ps:[0.3,0.4,0.3]}]
      let bottom = //this.bottom;
       [{name:'d',id:4,values:[1,2],ps:[0.3,0.4,0.3]}]
      let xlength = 1;
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

        console.log('x',xlength,'y',ylength)

      let deltaX = 0.8 / 3 / (xlength - 1);
      let deltaY = 0.8 / 3 / (ylength - 1);
      let cellWidth = 1.6 / 3 / xlength;
      let cellHeight = 1.6 / 3 / ylength;
      let values = []
      let cnt = 1;
      for(let i =0;i<top.length;i++){
          let item = top[i];
          for(let j=0;j<cnt;j++){
              for(let k = 0;k<item.values.length;k++){
                  values.push([
                      item.name,
                      0,
                      item.values[k],
                      j*item.values.length+k,
                      i,
                      top.length,
                      xlength / cnt / item.values.length,
                      cellWidth,
                      deltaX,
                      cellHeight,
                      deltaY,
                      item.ps[k]])
              }
          }
          cnt *= item.values.length;
      }
      for(let i =0;i<bottom.length;i++){
          let item = bottom[i];
          for(let j=0;j<cnt;j++){
              for(let k = 0;k<item.values.length;k++){
                  values.push([
                      item.name,
                      2,
                      item.values[k],
                      j*item.values.length+k,
                      i,
                      bottom.length,
                      xlength / cnt / item.values.length,
                      cellWidth,
                      deltaX,
                      cellHeight,
                      deltaY,
                      item.ps[k]])
              }
          }
          cnt *= item.values.length;
      }
      cnt = 1;
      for(let i =0;i<left.length;i++){
          let item = left[i];
          for(let j=0;j<cnt;j++){
              for(let k = 0;k<item.values.length;k++){
                  values.push([
                      item.name,
                      1,
                      item.values[k],
                      i,
                      j*item.values.length+k,
                      ylength / cnt / item.values.length,
                      left.length,
                      cellWidth,
                      deltaX,
                      cellHeight,
                      deltaY,
                      item.ps[k]])
              }
          }
          cnt *= item.values.length;
      }
      for(let i =0;i<right.length;i++){
          let item = right[i];
          for(let j=0;j<cnt;j++){
              for(let k = 0;k<item.values.length;k++){
                  values.push([
                      item.name,
                      3,
                      item.values[k],
                      i,
                      j*item.values.length+k,
                      ylength / cnt / item.values.length,
                      right.length,
                      cellWidth,
                      deltaX,
                      cellHeight,
                      deltaY,
                      item.ps[k]])
              }
          }
          cnt *= item.values.length;
      }
      console.log(values)
      return values;
    },
    formatValue() {
        let vs = []
        let xlength = 4;
        let ylength = 32;
          let deltaX = 0.8 / 3 / (xlength - 1);
      let deltaY = 0.8 / 3 / (ylength - 1);
      let cellWidth = 1.6 / 3 / xlength;
      let cellHeight = 1.6 / 3 / ylength;
      for(let i=0;i<4;i++){
          for(let j=0;j<32;j++){
            vs.push([i,j,4,32,cellWidth,deltaX,cellHeight,deltaY,Math.random()])
          }
      }
      return vs;
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
