/* tip:
 * `type` is enum of '1' and '0',
 * release type representative by '1' and debug type representative by '0'
 */

import instance from "./axios";

export function getDeviceInfoApi(page: Number, limit: Number){
    return instance.get("/device/list",{
        params:{
            page,
            limit
        }
    });
}

export function downloadDevice(version: string, type: string){
    return instance.get("/device/download",{
        params: {
            version,
            type
        }
    })
}

export function addDeviceApi(version: string, type: string, 
        description: string | null, apk: File){
    const formData = new FormData();
    formData.append("version",version);
    formData.append("type",type);
    if(description){
        formData.append("description",description);
    }
    formData.append("apk",apk);
    return instance.post("/device/add",formData,{
        headers: {
            "Content-Type": "multipart/form/data"
        }
    })
}

export function deleteDeviceApi(version: string, type: string){
    instance.post("/device/delete",{
        version,
        type
    })
}