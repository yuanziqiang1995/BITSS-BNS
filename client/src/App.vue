<template>
  <div id="app">
    <!-- <MultiPixelMap id='a' :param='param2'/> -->
    <!-- <PixelMap id="a" :param="param" /> -->
    <router-view />
    <!-- <bayes></bayes> -->
  </div>
</template>

<script>
import PixelMap from "./components/visual/PixelMap";
import MultiPixelMap from "./components/visual/MultiPixelMap";
import bayes from "./views/bayes/bayes";
import axios from "axios";
export default {
  name: "App",
  components: {
    PixelMap,
    MultiPixelMap,
    bayes
  },
  data() {
    let www = [5, 6, 7, 8];
    let ddd = [1, 2, 3, 4];
    let labels = ["a", "b", "c", "d"];
    let d = [];
    let vv = [-1, 0, 1, 2, 3, 4, 5, 6, 7];
    for (let i = 0; i < 4; i++) {
      for (let j = 0; j < 4; j++) {
        if (i <= j) {
          continue;
        }

        let w = www[i];
        let h = ddd[j];
        let p = [];
        for (let k = 0; k < w; k++) {
          for (let z = 0; z < h; z++) {
            p.push(Math.random());
          }
        }
        d.push({
          x: i,
          y: j,
          h,
          w,
          p,
          vx: vv.slice(0, w),
          vy: vv.slice(0, h)
        });
      }
    }
    console.log(JSON.stringify(d));
    let pp = []
    for(let i = 0;i<24;i++){
      let t = []
      for(let j = 0;j<36;j++){
        t.push(Math.random())
      }
      pp.push(t)
    }
    return {
      labels: ["a", "b", "c", "d"],
      param: {
        noncondition: [
          {
            name: 'aa',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
          {
            name: 'bb',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
          {
            name: 'cc',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
        ],
        condition: [
          {
            name: 'aa',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
          {
            name: 'bb',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
          {
            name: 'cc',
            values: [
              '取值a1',
              '取值a2',
              '取值a3'
            ]
          },
        ],
        values: [
          [
            [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ],
            [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ]
          ],
          [
             [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ],
            [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ]
          ],
          [
             [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ],
            [
              [0.2, 0.5, 0.3],
              [0.1, 0.5, 0.6],
              [0.6, 0.2, 0.7]
            ]
          ]
        ]
      },
      param2: {
        map: pp,
        left: [
          {
            name: 'aa',
            values: [
              {
                name: '取值a1',
                p: 0.5
              },
              {
                name: '取值a2',
                p: 0.3
              }
            ]
          },
          {
            name: 'bb',
            values: [
              {
                name: '取值b1',
                p: 0.5
              },
              {
                name: '取值b2',
                p: 0.2
              }
            ]
          }
        ],
        right: [
          {
            name: 'cc',
            values: [
              {
                name: '取值c1',
                p: 0.3
              },
              {
                name: '取值c2',
                p: 0.2
              }
            ]
          },
          {
            name: 'dd',
            values: [
              {
                name: '取值d1',
                p: 0.5
              },
              {
                name: '取值d2',
                p: 0.3
              },
              {
                name: '取值d3',
                p: 0.2
              }
            ]
          }
        ],
        top: [
          {
            name: 'ee',
            values: [
              {
                name: '取值e1',
                p: 0.3
              },
              {
                name: '取值e2',
                p: 0.2
              }
            ]
          },
          {
            name: 'ff',
            values: [
              {
                name: '取值f1',
                p: 0.5
              },
              {
                name: '取值f2',
                p: 0.3
              },
              {
                name: '取值f3',
                p: 0.2
              }
            ]
          }
        ],
        bottom: [
          {
            name: 'gg',
            values: [
              {
                name: '取值g1',
                p: 0.3
              },
              {
                name: '取值g2',
                p: 0.2
              }
            ]
          },
          {
            name: 'hh',
            values: [
              {
                name: '取值h1',
                p: 0.5
              },
              {
                name: '取值h2',
                p: 0.3
              },
              {
                name: '取值h3',
                p: 0.2
              }
            ]
          }
        ],
      }
    };
  },
  mounted() {
    axios.get("/dataset/use").then(console.log);
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
</style>