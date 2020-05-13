<template>
  <div class="app-container calendar-list-container">
    <div>
      <div style="margin-bottom: 10px;margin-top: 0px">
        <el-button size="medium" style @click="handleNotUse()" type="primary">不使用数据集</el-button>
      </div>
      <el-table
        :key="tableKey"
        :data="list"
        v-loading="listLoading"
        element-loading-text="给我一点时间"
        border
        highlight-current-row
        style="width: 100%"
        @selection-change="handleDownloads"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column align="center" label width="65" type="index" :index="indexMethod"></el-table-column>
       <el-table-column align="center" label="数据集名称" prop="name" />
        <el-table-column align="center" label="数据集描述" prop="description" />
        <el-table-column align="center" label="数据集格式" prop="format" />
        <el-table-column align="center" label="上传时间" prop="updateTime" />
        <!-- <el-table-column align="center" label="数据描述" >
        <template slot-scope="scope">
          <span>{{scope.row.description}}</span>
        </template>
      </el-table-column>
      <el-table-column width="150px" align="center" label="时间">
        <template slot-scope="scope">
          <span>{{scope.row.dataset.updateDate | formatTimes }}</span>
        </template>
        </el-table-column>-->
        <el-table-column
          align="center"
          label="操作"
          width="400"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleEdit(scope.row)"
              style="margin-left:10px"
            >编辑</el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleDownload(scope.row)"
              style="margin-left:10px"
            >下载</el-button>
            <el-button
              size="small"
              type="success"
              @click="handleUse(scope.row)"
              style="margin-left:10px"
            >使用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[10,20,30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
    <el-dialog title="数据基本信息" :visible.sync="dialogFormVisible" v-dialogDrag>
      <el-form
        :rules="rules"
        ref="dataForm"
        :model="temp"
        label-position="right"
        label-width="110px"
        style=" margin-left:50px;margin-right:50px; height:350px; "
      >
        <el-form-item label="数据名称 :">
          <el-input v-model="temp.dataset.name" disabled style="width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据格式 :">
          <el-input v-model="temp.format" disabled style="width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据类型 :">
          <el-input v-model="temp.category" disabled style="width:400px"></el-input>
        </el-form-item>
        <el-form-item label="数据描述 :">
          <el-input
            v-model="temp.dataset.description"
            disabled
            type="textarea"
            :rows="2"
            style="width:400px"
            maxlength="250"
          ></el-input>
        </el-form-item>
        <el-form-item label="数据使用说明 :">
          <el-input
            v-model="temp.dataset.description"
            disabled
            type="textarea"
            :rows="2"
            style="width:400px"
            maxlength="250"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleDownload(r)">下载</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
//import { fetchDataList,fetchDataTypeList , fetchDataFormatList,download} from '@/api/data'
import waves from "@/directive/waves"; // 水波纹指令
import { parseTime, arrayToTree1 } from "@/utils";
//import { fetchFileInfo,downloadFile} from '@/api/file'

export default {
  directives: {
    waves
  },
  data() {
    return {
      tableKey: 0,
      fullHeight: document.documentElement.clientHeight,
      list:[],
      page: 1,
      pageSize: 20,
      total: 4,
      listLoading: true,
      listQuery: {
        pages: 1,
        rows: 10,
        category: "",
        name: "",
        format: "",
        formatId: "",
        categoryId: ""
      },
      dataTypeData: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      dataformatOptions: [],
      temp: {
        format: "",
        category: "",
        dataset: {
          name: "",
          description: ""
        }
      },
      tempDown: {
        datasetId: "",
        formatId: "",
        categoryId: "",
        name: ""
      },
      dialogFormVisible: false,
      dataTypeDialogFormVisible: false,
      dialogStatus: "",
      multipleSelection: [],
      downloadLoading: false,
      r: ""
    };
  },
  filters: {
    formatTimes(updateDate) {
      const y = new Date(updateDate).getFullYear();
      const m = new Date(updateDate).getMonth() + 1;
      const d = new Date(updateDate).getDate();
      const h = new Date(updateDate).getHours();
      const i = new Date(updateDate).getMinutes();
      const s = new Date(updateDate).getSeconds();
      var t = y + "-" + m + "-" + d + " " + h + ":" + i + ":" + s;
      // var t = Y + '-' + m + '-' + d;
      return t;
    }
  },
  created() {
    this.getList();
    fetchDataFormatList(this.listQuery).then(response => {
      this.dataformatOptions = response.data.format.map(item => {
        return { value: item.id, label: item.name };
      });
    });
    fetchDataTypeList().then(response => {
      // this.data = response.data.data
      for (const v of response.data.data) {
        v.label = v.name;
        this.dataTypeData.push(v);
      }
      this.dataTypeData = arrayToTree1(this.dataTypeData);
      console.log("树状结构");
      console.log(this.dataTypeData);
    });
  },
  mounted() {
   this.queryData(1);
    const that = this;
    window.onresize = () => {
      return (() => {
        window.clientHeight = document.body.clientHeight;
        that.fullHeight = window.clientHeight;
      })();
    };
  },
  watch: {
    fullHeight(val) {
      // if(!this.timer) {
      this.fullHeight = val;
      //   this.timer = true
      //   let that = this
      //   setTimeout(function (){
      //     that.timer = false
      //   },400)
      // }
    }
  },
  methods: {
    queryData(page) {
      this.page = page;
      this.listLoading = true;
      this.$request
        .get(`/bayes/datasets/list?page=${page}&pageSize=${this.pageSize}`)
        .then(res => {
          this.listLoading = false;
          this.total = res.data.data.count;
          this.list = res.data.data.list;
        });
    },
    indexMethod(index) {
      if (this.listQuery.pages == 1) {
        return index + 1;
      } else {
        return index + (this.listQuery.pages - 1) * this.listQuery.rows + 1;
      }
    },

    getList() {
      this.listLoading = false;
      //   fetchDataList(this.listQuery).then(response => {
      //     this.list = response.data.list
      //     for(var i=0;i < this.list.length;i++){
      //       this.list[i] = Object.assign({},this.list[i],this.list[i].dataset)
      //     }
      //     this.total = response.data.total
      //     this.listLoading = false
      //   })
    },
    handleNotUse() {
      this.$router.push("/draw/index");
    },
    handleFilter() {
      this.listQuery.pages = 1;
      this.getList();
    },
    handleNodeClick(data) {
      this.listQuery.category = data.label;
      this.listQuery.categoryId = data.id;
      this.getList();
    },
   handleSizeChange(val) {
      this.pageSize = val
      this.queryData(1)
    },
    handleCurrentChange(val) {
      this.page = val;
     this.queryData(val)
    },

    resetListQuery() {
      (this.listQuery = {
        rows: 10,
        category: ""
      }),
        this.handleFilter();
    },
    // handleDownloads(val) {
    //   const arr = val
    //   this.multipleSelection = []
    //   for(const v of arr){
    //     this.multipleSelection.push(v.position)
    //   }
    // },
    // downloadInBatches(){
    //   for(var p of this.multipliteSelection){
    //     window.open('http://10.4.20.151:8101/govern-spark/hdfs/downloadFile?filePath='+p)
    //   }
    // },

    handleEdit(row) {
      //跳转到编辑网络
    },

    handleInference(row) {
      //跳转到网络推理
    },
    handleUse(row) {
      this.$router.push({
        path: "/draw/useData",
        query: { datasetId: row.id }
      });
    },
    handleDownload(row) {
      this.downloadLoading = true;
      this.temp = Object.assign({}, row);
      window.open(
        "http://10.4.20.151:8101/govern-spark/hdfs/downloadFile?filePath=" +
          row.position
      );
      this.tempDown.datasetId = this.temp.id;
      this.tempDown.formatId = this.temp.formatId;
      this.tempDown.categoryId = this.temp.categoryId;
      this.tempDown.name = this.temp.name;
      download(this.tempDown).then(response => {
        this.$notify({
          title: "成功",
          message: response.data.msg,
          type: "success",
          duration: 2000
        });
        this.downloadLoading = false;
      });
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v =>
        filterVal.map(j => {
          if (j === "timestamp") {
            return parseTime(v[j]);
          } else {
            return v[j];
          }
        })
      );
    }
  }
};
</script>
