import axios from "axios";
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from "axios";

const instance: AxiosInstance = axios.create({
  baseURL: "http://localhost:3000",
  timeout: 5000,
});

//Request Interceptor
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem("token");
    if (token && config.headers) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
