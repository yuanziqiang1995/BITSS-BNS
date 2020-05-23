<template>
  <!--
  TODO
  克隆编辑
  推理
  -->
  <div class="app-container calendar-list-container">
    <div>
      <!-- <div style="margin-bottom: 10px;margin-top: 0px">
      <el-button  size="small" style="" @click="downloadInBatches()" type="primary" icon="el-icon-download">批量下载</el-button>
      </div>-->
      <el-table
        size="mini"
        :data="list"
        v-loading="listLoading"
        element-loading-text="给我一点时间"
        border
      >
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column align="center" label width="65" type="index" :index="indexMethod"></el-table-column>
        <el-table-column align="center" label="网络名称" prop="modelName" />
        <el-table-column align="center" label="网络描述" prop="description" />
        <el-table-column align="center" label="修改时间" prop="updateTime" />
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
        <el-table-column align="center" width="350" label="操作">
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
              @click="handleInference(scope.row)"
              style="margin-left:10px"
            >推理</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              style="margin-left:10px"
            >删除</el-button>
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
    <el-dialog title="数据基本信息" :visible.sync="dialogFormVisible" >
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
    <el-dialog title="编辑属性" :visible.sync="editFormVisible">
      <el-form ref="dataForm" :model="editDatasets" label-position="right" label-width="110px">
        <el-form-item label="网络名称 :">
          <el-input v-model="editDatasets.name" style="max-width:400px"></el-input>
        </el-form-item>
        <el-form-item label="网络描述 :">
          <el-input
            v-model="editDatasets.description"
            type="textarea"
            :rows="2"
            style="max-width:400px"
            maxlength="250"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">提交修改</el-button>
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
      page: 1,
      editFormVisible: false,
      pageSize: 10,
      tableKey: 0,
      fullHeight: document.documentElement.clientHeight,
      editDatasets: {
        name: "",
        description: "",
        id: ""
      },
      list: [],
      total: 0,
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
  mounted() {
    const that = this;
    this.queryData(1);
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
    handleDelete(row) {
      this.$request
        .post("/bayes/static/discrete/delete", {
          id: row.modelId
        })
        .then(res => {
          this.queryData(1);
        });
    },
    submitEdit() {
      this.$request
        .post("/bayes/static/discrete/update", {
          modelName: this.editDatasets.name,
          description: this.editDatasets.description,
          modelId: this.editDatasets.id
        })
        .then(res => {
          this.queryData(1);
          this.editFormVisible = false;
        });
    },
    queryData(page) {
      this.page = page;
      this.listLoading = true;
      this.$request
        .get(
          `/bayes/static/discrete/list?page=${page}&pageSize=${this.pageSize}`
        )
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
      //TODO
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
      this.listQuery.rows = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.pages = val;
      this.getList();
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
      if (row.datasetId) {
        this.$router.push({ path: "/draw/useData", query: {
          datasetId: row.datasetId,
          modelId: row.modelId
        } });
      } else {
        this.$router.push({
          path: "/draw/index",
          query: { modelId: row.modelId }
        });
      }
      //跳转到编辑网络
    },

    handleInference(row) {
      //跳转到网络推理

      this.$router.push({
        path: "/draw/inference",
        query: { modelId: row.modelId }
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
