import req from './http.js'

export const CREATE = params => req('post', '/bayes/static/discrete', params)
