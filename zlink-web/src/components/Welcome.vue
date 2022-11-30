<template>
  <div>welcome</div>
</template>

<script>
export default {
  name: 'Welcome',
  data() {
    return {
      // 左侧菜单数据
      menulist: [],
      // 图标绑定字典
      iconsDict: {
        '1': 'iconfont icon-user',
        '2': 'iconfont icon-tijikongjian',
        '3': 'iconfont icon-shangpin',
        '102': 'iconfont icon-danju',
        '145': 'iconfont icon-baobiao'
      },
      // 是否折叠
      isCollapse: false
    }
  },
  // 刚进入界面首先调用这里的函数
  created() {
    this.getMenuList()
  },
  methods: {
    // TODO 退出登录
    // 基于token 的方式实现退出比较简单，只需要销毁本地的 token即可。这样，后续的请求就不会携带token，必须里新登录生成一个新的 token之后才可以访问页面。
    logout() {
      window.sessionStorage.clear()
      // 跳转登录页
      this.$router.push('/login')
    },
    // 获取所有菜单
    async getMenuList() {
      const {data: res} = await this.$http.get('menus/listMenus')
      console.log(res)
      if (res.code !== 200) return this.$message.error(res.meta.msg)
      this.menulist = res.data
    },
    // 点击按钮, 切换
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    }
  }
}
</script>

<!--scoped 只在当前组件中生效，去掉在全局生效。一个组件中的样式不应该影响其他地方-->
<style lang='less' scoped>
.home-container {
  height: 100%;
}


.el-header {
  background-color: #c8c4cc;
  display: flex;
  justify-content: space-between;
  padding: 0;
  align-items: center;
  color: #fff;
  font-size: 20px;

  > div {
    display: flex;
    align-items: center;

    span {
      margin-left: 15px;
    }
  }
}

.el-aside {
  background-color: #1eacda;
}

.el-main {
  background-color: #f8f8f4;

  .el-menu {
    border-right: none;
  }
}

.iconfont {
  margin-right: 10px;
}

.toggle-button {
  background-color: #fda252;
  font-size: 10px;
  line-height: 24px;
  color: #55a532;
  text-align: center;
}
</style>
