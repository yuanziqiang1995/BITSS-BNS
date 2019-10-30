module.exports = {
  proxy: {
        '/apis': {    //将www.exaple.com印射为/apis
            target: 'https://10.4.20.23',  // 接口域名
            secure: false,  // 如果是https接口，需要配置这个参数
            changeOrigin: true  //是否跨域

        }
  }
}