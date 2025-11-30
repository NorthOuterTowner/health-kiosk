import instance from "../axios";

export function getInfoApi (page:Number | undefined | null, 
    limit:Number | undefined | null) {
        if(page == null) page = 1;
        if(limit == null) limit = 10;
        return instance.get(`/examData/userId`,{
            params:{
                page,
                limit
            }
        })
}
