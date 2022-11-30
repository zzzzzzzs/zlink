<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>元数据中心</el-breadcrumb-item>
      <el-breadcrumb-item>元数据中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="grid-content bg-purple">
            源端数据库
            <el-select v-model="sourceId" filterable placeholder="请选择" @change="getSchemaAndTable">
              <el-option
                v-for="item in dataSourceList"
                :key="item.id"
                :label="item.databaseName"
                :value="item.id">
              </el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="grid-content bg-purple">
            目标数据库
            <el-select v-model="targetId" filterable placeholder="请选择">
              <el-option
                v-for="item in dataSourceList"
                :key="item.id"
                :label="item.databaseName"
                :value="item.id">
              </el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </el-card>


  </div>
</template>

<script>
export default {
  name: 'MetaData',
  data() {
    return {
      dataSourceList: [],
      sourceId: '',
      targetId: '',
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getDataSourceList()
  },
  methods: {
    async getDataSourceList() {
      const {data: res} = await this.$http.get('datasource/listDataSource')
      if (res.code !== 200) {
        return this.$message.error('获取数据源列表失败！')
      }
      this.dataSourceList = res.data.records
      this.total = res.data.total
      console.log(this.dataSourceList)
    },
    async getSchemaAndTable() {
      console.log(this.sourceId)
      const {data: res} = await this.$http.get('metadata/getSchemaAndTable', {params: {id: this.sourceId}})
    },
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
.datasource-title {
  position: relative;
  height: 40px;

  .add-btn {
    position: absolute;
    right: 10px;
    bottom: 15px;
  }
}

</style>
