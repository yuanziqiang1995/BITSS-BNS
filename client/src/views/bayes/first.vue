<template>
  <firstLayout>
    <mynetwork id='firstNetwork' ref='firstNetwork' slot='left' :model='model'></mynetwork>
    <div slot="right" style="width:100%;height:100%;display:flex;flex-direction:column;">
      <div
        style="flex-grow:0;color:rgb(144,147,153);cursor:default;font-weight:700"
        class="item"
      >节点信息</div>
      <div style="flex-grow:1;overflow-y:auto;width:100%;height:0;">
        <el-popover
          placement="left"
          :title="item.name + '取值分布'"
          width="350"
          trigger="click"
          v-for="(item,index) in variables"
          :key="index"
        >
          <div style='max-height:400px;overflow-y:auto;'>
            <el-table :data="item.values" style="width: 100%">
              <el-table-column prop="value" label="取值"></el-table-column>
              <el-table-column prop="count" label="数量"></el-table-column>
              <el-table-column width='60' prop="percent" label="概率"></el-table-column>
            </el-table>
          </div>
          <div slot="reference" class="variable item">{{item.name }}</div>
        </el-popover>
      </div>
    </div>
   
   
  </firstLayout>
</template>
<script>
import echarts from "echarts";
import firstLayout from "./firstLayout";
import mynetwork from './mynetwork'
// import {mapState} from 'vuex'

export default {
  name: "first",
  components: {
    firstLayout,
    mynetwork
  },
  props: {
    nodeInfo: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data() {
    return {
    };
  },
  computed: {
    model () {
      return {
        node: this.nodeInfo,
        link: []
      }
    },
    variables () {
      return this.nodeInfo.map(x => {
        return {
          name: x.nodeName,
          id: x.nodeId,
          valueNum: x.valueNum,
          values: x.value.map(y => {
            return {
              value: y.name,
              count: y.count,
              percent: y.probability.toFixed(3),
              id: y.id
            }
          })
        }
      })
    }
  },
  methods: {
    getModel(){
      return this.$refs.firstNetwork.getModel();
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
.fit {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
.item {
  line-height: 23px;
  font-size: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.13);
  padding: 9px 10px;
}
.variable {
  color: rgb(96, 98, 102);
  cursor: pointer;
}
.variable:hover {
  transition: background-color 0.25s ease;
  background-color: #ecf5ff;
}
</style>
