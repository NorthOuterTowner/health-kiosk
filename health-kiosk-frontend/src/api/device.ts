import instance from "./axios";

export function getDeviceInfoApi(page: Number, limit: Number){
    return instance.get("/device/list",{
        params:{
            page,
            limit
        }
    });
}