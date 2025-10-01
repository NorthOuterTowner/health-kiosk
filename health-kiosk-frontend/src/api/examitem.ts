import instance from "./axios";
import { version } from 'vue';

export function getExamItemInfoApi(page: Number, limit: Number){
    return instance.get("/examitem/list",{
        params:{
            page,
            limit
        }
    });
}

export function addExamItemApi(name: string, abbreviation: string, 
        description: string | null, status: number){
    return instance.post("/examitem/add",{
        name,
        abbreviation,
        description,
        status
    })
}

export function deleteExamItemApi(id: string | null, name: string | null){
    if(id == null && name == null ){
        return {
            status: 400,
            data: {
                code: 400,
                msg: "未选择项目"
            }
        }
    }
    return instance.post("/examitem/delete",{
        id,
        name
    })
}

export function updateExamItemApi(id: string, name: string | null, 
    status: number | null, description: string | null, 
    abbreviation: string | null){
    return instance.post("/examitem/update",{
        id,
        name,
        status,
        abbreviation,
        description
    });
}