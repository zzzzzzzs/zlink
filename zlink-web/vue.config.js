module.exports = {
  devServer: {
    // https:false,
    open: true,
    //以上的ip和端口是我们本机的;下面为需要跨域的
    proxy: { //配置跨域
      '/api': {
        target: 'http://localhost:5465/',
        ws: true,
        changeOrigin: true,//允许跨域
        pathRewrite: {
          '^/api': ''   //请求的时候使用这个api就可以
        }
      }
    }
  },

  chainWebpack: config => {
    // 发布模式
    config.when(process.env.NODE_ENV === 'production', config => {
      config
        .entry('app')
        .clear()
        .add('./src/main-prod.js')

      config.set('externals', {
        vue: 'Vue',
        'vue-router': 'VueRouter',
        axios: 'axios',
        lodash: '_',
        echarts: 'echarts',
        nprogress: 'NProgress',
        'vue-quill-editor': 'VueQuillEditor'
      })

      config.plugin('html').tap(args => {
        args[0].isProd = true
        return args
      })
    })

    // 开发模式
    config.when(process.env.NODE_ENV === 'development', config => {
      config
        .entry('app')
        .clear()
        .add('./src/main-dev.js')

      config.plugin('html').tap(args => {
        args[0].isProd = false
        return args
      })
    })
  }
}
