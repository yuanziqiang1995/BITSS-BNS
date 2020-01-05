<template>
  <!--
    使用说明：
      传入labels和values，
      labels: 对应x,y坐标轴，eps: ['a','b','c']
      values: 像素图数据：数组的每项标识一个九宫格，x,y标识该九宫格在图中的位置。
      eps: [{
        x:3,
        y:0,
        value:[
          0,0.2,0.4,
          1,0.5,0.1,
          0.5,0.7,0.21
        ]
        },
        {
        x:0,
        y:1,
        value:[
          0,0.2,0.4,
          1,0.5,0.1,
          0.5,0.7,0.21
        ]
        },
     ]
  -->
  <div class='echart' :id='id'></div>
</template>
<script>
  // import {mapState} from 'vuex'

  function renderItem(params, api) {
    console.log(api)

    let height = api.size([1,1])[1];
    let width = api.size([1,1])[0];

    let x = (api.value(0)+0.05+api.value(2)*0.3)*width  + params.coordSys.x;

    let y = (api.value(1)+0.05+api.value(3)*0.3)*height + params.coordSys.y;
    width *= 0.3;
    height *=0.3;
    console.log(width,height,x,y)
    var rectShape = echarts.graphic.clipRectByRect({
      x,
      y,
      width,
      height
    }, {
      x: params.coordSys.x,
      y: params.coordSys.y,
      width: params.coordSys.width,
      height: params.coordSys.height
    });


    return rectShape && {
      type: 'rect',
      shape: rectShape,
      style: api.style()
    };
  }

  export default {
    name: 'PixelMap',
    data () {
      // let yData = []
      //
      // for(let k = 0; k < 3;k++){
      //   for(let j=0;j<3;j++){
      //     for(let i = 0;i<3;i++){
      //       for(let z = 0;z<3;z++){
      //         yData.push([i,z,k,j,Math.random()])
      //       }
      //     }
      //   }
      // }
      let vals = this.values.map(x => {
        return x.values.map(y => [x.x,x.y,...y])
      }).flatMap()
      return {
        option : {
          tooltip: {
            position: 'top'
          },
          animation: false,
          backgroundColor: 'transparent',
          xAxis: {
            type: 'category',
            axisTick: {
              show: false
            },
            interval: 20,
            data: this.labels,
            splitArea: {
              show: true
            }
          },
          yAxis: {
            interval: 20,
            type: 'category',
            data: this.labels,
            splitArea: {
              show: true
            },
            axisTick: {
              show: false
            },
          },
          visualMap: {
            min: 0,
            max: 1,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '15%',
            color: ['#fff','#000'],
            show:false
          },
          series: [{
            type: 'custom',
            renderItem,
            data:vals
          }]
        }
      }
    },
    computed: {
      // ...mapState([
      //   'resizeVersion'
      // ])
    },
    props: {
      labels: {
        type: Array,
        required: true
      },
      values: {
        type: Array,
        required: true
      },
      id: {
        type: String,
        default: ''
      },
      loading: {
        type: Boolean,
        default: false
      }
    },
    watch: {
      values (to, from) {
        let vals = to.map(x => {
          return x.values.map(y => [x.x,x.y,...y])
        }).flatMap()
        this.option.series[0].data = vals
        this.chart.clear()
        this.chart.setOption(this.option)
      },
      // resizeVersion (to, from) {
      //   this.chart.resize()
      // },
      loading (to, from) {
        if (to !== from) {
          if (to === true) {
            this.showLoading()
          } else if (to === false) {
            this.hideLoading()
          }
        }
      }
    },
    mounted () {
      this.chart = this.$echarts.init(
        document.getElementById(this.id)
      )
      this.chart.setOption(this.option)
    },
    methods: {
      showLoading () {
        this.chart.showLoading({
          text: 'loading',
          color: '#fff',
          textColor: '#fff',
          maskColor: 'rgba(0, 0, 0, 0.3)'
        })
      },
      hideLoading () {
        this.chart.hideLoading()
      }
    }
  }
</script>
<style scoped>
  .echart{
    width:100%;
    height:100%;
    margin: 0;
    padding: 0;
  }
</style>
