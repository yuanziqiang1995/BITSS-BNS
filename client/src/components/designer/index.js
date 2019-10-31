import DrawNetwork from './FlowDesigner'

export default {
  install: (Vue) => {
    Vue.component('draw-network', DrawNetwork)
  }
}
