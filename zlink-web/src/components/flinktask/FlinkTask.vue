<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>flink-task</el-breadcrumb-item>
      <el-breadcrumb-item>flink-task</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-row>
        flink-task 信息

        <el-tooltip
          class="item"
          effect="dark"
          content="刷新 flink-task 信息"
          placement="top">
          <el-button type="success" plain icon="el-icon-refresh" circle @click='getLocalFlinkInfo'></el-button>
        </el-tooltip>

        <el-tooltip
          class="item"
          effect="dark"
          content="停止任务"
          placement="top">
          <el-button type="danger" icon="el-icon-close" circle @click="stopFlinkTask"></el-button>
        </el-tooltip>

        <el-tooltip
          class="item"
          effect="dark"
          placement="top">
          <div slot="content">推送任务<br/>目前支持 {{ pushModel.toString() }}</div>
          <el-button type="success" icon="el-icon-s-promotion" circle @click="pushTaskVisible=true"></el-button>
        </el-tooltip>

      </el-row>

      <el-table ref="multipleTable" :data='flinkInfos' border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label='jobId' prop='jobId'></el-table-column>
        <el-table-column label='模式' prop='model'>
          <template slot="header">
            <el-tooltip
              class="item"
              effect="dark"
              content="目前支持 local, yarn, session, k8s"
              placement="top">
              <span> 模式 <i class="el-icon-question"></i>  </span>
            </el-tooltip>
          </template>
          <template slot-scope="scope">
            <el-tooltip
              class="item"
              effect="dark"
              content="local 模式下并行度为1"
              :disabled="scope.row.model !== 'local'"
              placement="top">
              <el-tag type="danger" v-if="scope.row.model === 'local'">{{ scope.row.model }}</el-tag>
              <el-tag type="success" v-else>{{ scope.row.model }}</el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label='url'>
          <template slot-scope="scope">
            <a :href="'http://'+scope.row.url+'/#/overview'" target="_blank">flink-web</a>
          </template>
        </el-table-column>
        <el-table-column label='任务状态' prop='status'>
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 'RUNNING'">{{ scope.row.status }}</el-tag>
            <el-tag type="danger" v-else>{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title='推送任务' :visible.sync="pushTaskVisible" width="50%"
               @close="pushTaskClosed">
      <!-- 添加数据源表单 -->
      <el-form :model="pushTaskForm" ref="pushTaskFormRef"
               label-width="140px">
        <el-form-item label="推送模式：">
          <el-select v-model="pushModel.model" placeholder="请选择" @change="selectBtn">
            <el-option
              v-for="item in pushModel"
              :key="item"
              :label="item"
              :value="item"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="并行度：">
          <el-input v-model="pushTaskForm.parallelism"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pushTaskVisible = false">取 消</el-button>
        <el-button type="primary" @click="pushTask">推 送</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>

export default {
  name: 'FlinkTask',
  components: {},
  watch: {},
  data() {
    return {
      flinkModel: [],
      pushModel: [],
      pushTaskForm: {
        model: '',
        parallelism: '',
        jobIds: [],
      },
      pushTaskVisible: false,
      multipleSelection: [],
      flinkInfos: [],
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.listFlinkModel()
    this.getLocalFlinkInfo()
  },
  methods: {
    async listFlinkModel() {
      const {data: res} = await this.$http.get('flink-model/listFlinkModel')
      if (res.code !== 200) return this.$message.error("获取 flink model 失败！")
      this.$message.success("获取 flink model 成功！")
      // console.log(res.data)
      this.flinkModel = res.data
      console.log(this.flinkModel)
      this.flinkModel.map(it => {
        this.pushModel.push(it.flinkModel)
      })
    },
    pushTask() {
      this.multipleSelection.map(it => {
        this.pushTaskForm.jobIds.push(it.jobId)
        // this.pushModel.push(it.flinkModel)
      })
      console.log(this.pushTaskForm)
    },
    pushTaskClosed() {

    },
    selectBtn(value) {
      this.pushModel.forEach(item => {
        if (item === value) this.pushTaskForm.model = item
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    async stopFlinkTask() {
      if (this.multipleSelection.length === 0) return this.$message.error("请勾选任务！")
      const {data: res} = await this.$http.post('cdc/stopFlinkTask', this.multipleSelection)
      if (res.code !== 200) return this.$message.error("停止任务失败！")
      this.$message.success("停止任务成功！")
      await this.getLocalFlinkInfo();
    },
    async getLocalFlinkInfo() {
      const {data: res} = await this.$http.get('cdc/getLocalFlinkInfo')
      console.log(res)
      if (res.code !== 200) return this.$message.error("获取 flink info 失败！")
      this.$message.success("获取 flink info 成功！")
      this.flinkInfos = res.data
      console.log(this.flinkInfos)
    },
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
</style>
