<template>
  <div>
    <!-- 面包屑导航区域-->
    <el-breadcrumb separator-class='el-icon-arrow-right'>
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>元数据中心</el-breadcrumb-item>
      <el-breadcrumb-item>元数据中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-row :gutter="24">
        <el-col :span="10">
          <div class="grid-content bg-purple">
            源端数据库
            <el-select v-model="sourceId" filterable placeholder="请选择" @change="getSchemaAndTable">
              <el-option v-for="item in dataSourceList" :key="item.id" :label="item.databaseName"
                         :value="item.id"></el-option>
            </el-select>
          </div>
          <el-row :gutter="24">
            <el-col :span="14">
              <el-input placeholder="输入关键字进行过滤" v-model="filterText" label-width="140px"></el-input>
              <el-tree
                class="filter-tree"
                :data="metaDataList"
                show-checkbox
                :props="defaultProps"
                :filter-node-method="filterNode"
                ref="tree"
                node-key="name"
                @node-click="handleNodeClick"
              >
              </el-tree>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="4">
          <el-button @click="syncTableStruct" type="primary" style="margin-left: 16px;">创建表结构</el-button>
        </el-col>
        <el-col :span="10">
          <div class="grid-content bg-purple">
            目标数据库
            <el-cascader :options="options" :props="casProps" @change="handleChange"></el-cascader>
          </div>
        </el-col>
      </el-row>
    </el-card>

  </div>
</template>

<script>
export default {
  name: 'MetaData',
  watch: {
    filterText(val) {
      console.log(val)
      this.$refs.tree.filter(val);
    }
  },
  data() {
    return {
      options: [],
      casProps: {
        lazy: true, // 此处必须为true
        lazyLoad: async (node, resolve) => {
          let {level} = node;
          if (node.data === undefined) {
            return
          }
          const {data: res} = await this.$http.get('metadata/getSchemaAndTable', {params: {id: node.data.value}});
          if (res.code !== 200) {
            return this.$message.error('获取元数据失败！')
          }
          const nodes = res.data.map(item => ({
            value: item.name,
            label: item.name,
            leaf: level >= 1
          }));
          resolve(nodes);
        }
      },
      filterText: '',
      dataSourceList: [],
      sourceId: '',
      targetData: {
        targetId: '',
        targetSchema: ''
      },
      metaDataList: [],
      // 要同步表的信息
      metaInfos: [],
      defaultProps: {
        children: 'tables',
        label: 'name'
      }
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getDataSourceList()
  },
  methods: {
    async localFlinkCDC() {
      const {data: res} = await this.$http.post('metadata/localFlinkCDC', {
        'sourceId': this.sourceId,
        'targetData': this.targetData,
        'tables': this.$refs.tree.getCheckedNodes(true)
      })
    },
    handleChange(value) {
      console.log(value);
      this.targetData.targetId = value[0]
      this.targetData.targetSchema = value[1]
    },
    async handleNodeClick(data) {
      const node = this.$refs.tree.getNode(data.name);
      // 证明是叶子节点
      if (node.childNodes.length === 0) {
        const {data: res} = await this.$http.get('metadata/listColumns', {
          params: {
            id: this.sourceId,
            schemaName: data.schema,
            tableName: data.name
          }
        })
        if (res.code !== 200) {
          return this.$message.error('获取列元数据失败！')
        }
        console.log(res)
      }
    },
    // 同步表结构
    async syncTableStruct() {
      if (this.targetData.targetId === "") {
        return this.$message.error("未选择目标数据库")
      }
      const {data: res} = await this.$http.post('metadata/syncTableStruct', {
        'sourceId': this.sourceId,
        'targetData': this.targetData,
        'tables': this.$refs.tree.getCheckedNodes(true)
      })
      console.log(res)
      if (res.code !== 200) return this.$message.error('同步表结构失败，失败信息 : ' + res.message);
      return this.$message.success('同步表结构成功！')
    },
    async getDataSourceList() {
      const {data: res} = await this.$http.get('datasource/listDataSource')
      if (res.code !== 200) {
        return this.$message.error('获取数据源列表失败！')
      }
      this.dataSourceList = res.data.records
      this.dataSourceList.map(item => {
        this.options.push({
          value: item.id,
          label: item.databaseName
        })
      })
      this.total = res.data.total
      console.log(this.dataSourceList)
    },
    async getSchemaAndTable() {
      const {data: res} = await this.$http.get('metadata/getSchemaAndTable', {params: {id: this.sourceId}})
      if (res.code !== 200) {
        return this.$message.error('获取元数据失败！')
      }
      this.metaDataList = res.data
      console.log(res)
    },
    filterNode(value, metaDataList) {
      if (!value) return true;
      return metaDataList.name.indexOf(value) !== -1;
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
