/* eslint-disable no-undef */
import request from '@/utils/requestbn'

// 新建网络
export function createNetwork(data) {
  return request({
    datatype: 'json',
    url: '/bayes/static/discrete/create',
    method: 'post',
    data
  })
}

// 查询
export function queryInference(data) {
  return request({
    datatype: 'json',
    url: '/bayes/static/discrete/query',
    method: 'post',
    data
  })
}
