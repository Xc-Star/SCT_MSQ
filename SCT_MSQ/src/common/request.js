//定制请求的实例

//导入axios  npm install axios
import axios from 'axios';
import { ElMessage } from 'element-plus'
import { useTokenStore } from '../stores/token'

/* import { useRouter } from 'vue-router'
const router = useRouter(); */

import router from '@/router';

//定义一个变量,记录公共的前缀  ,  baseURL
// const baseURL = 'http://127.0.0.1:4523/m1/6495443-6195651-default';
const baseURL = '/api';
const instance = axios.create({
  baseURL,
  timeout: 10000, // 设置10秒超时
  headers: {
    'Content-Type': 'application/json'
  }
})

//添加请求拦截器
instance.interceptors.request.use(
    config => {
        let tokenStore = useTokenStore();
        // 判断token是否存在
        if(tokenStore.token){
            config.headers.Authorization = tokenStore.token;
        }
        return config;
    },
    err => {
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

//添加响应拦截器
instance.interceptors.response.use(
    result => {
        // 如果是文件流，直接返回
        if (result.config && result.config.responseType === 'blob') {
            return result.data;
        }
        // 判断业务状态码
        if(result.data.code === 0){
            // 操作成功
            return result.data;
        } else {
            // 操作失败
            // alert(result.data.msg ? result.data.msg : '服务异常');
            ElMessage.error(result.data.message ? result.data.message : '服务异常');
            return Promise.reject(result.data);//异步的状态转化成失败的状态
        }
    },
    err => {
        // 判断响应状态码
        if(err.code === 'ECONNABORTED') {
            ElMessage.error('请求超时，请检查网络连接');
        } else if(err.response) {
            if(err.response.status === 401){
                router.push('/admin/login');
            } else if(err.response.status === 404) {
                ElMessage.error('请求的资源不存在');
            } else if(err.response.status === 500) {
                ElMessage.error('服务器内部错误');
            } else {
                ElMessage.error(err.response.data?.message || '服务异常');
            }
        } else if(err.request) {
            ElMessage.error('网络连接失败，请检查网络设置');
        } else {
            ElMessage.error('请求配置错误');
        }
        return Promise.reject(err);
    }
)

export default instance;