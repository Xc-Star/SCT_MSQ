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
const instance = axios.create({baseURL})

//添加请求拦截器
instance.interceptors.request.use(
    config=>{
        let tokenStore = useTokenStore();
        // 判断token是否存在
        if(tokenStore.token){
            config.headers.Authorization = tokenStore.token;
        }
        return config;
    },
    err=>{
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

//添加响应拦截器
instance.interceptors.response.use(
    result=>{
        // 判断业务状态码
        if(result.data.code===0){
            // 操作成功
            return result.data;
        } else {
            // 操作失败
            // alert(result.data.msg ? result.data.msg : '服务异常');
            ElMessage.error(result.data.message ? result.data.message : '服务异常');
            return Promise.reject(result.data);//异步的状态转化成失败的状态
        }
    },
    err=>{
        // 判断响应状态码, 如果为401, 则跳转到登录页面
        if(err.response.status===401){
            //跳转到登录页面
            ElMessage.error('请先登录');
            router.push('/admin/login')
        } else {
            ElMessage.error('服务异常');
            return Promise.reject(err);//异步的状态转化成失败的状态
        }
    }
)

export default instance;