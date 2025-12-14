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

export function deleteExamDataApi (record_id: number) {
    return instance.post("/examData/delete", {
        record_id
    })
}

export function downloadDataApi(start_date: String, end_date: String, 
    file_type: String | null | undefined) {
        let fact_type = file_type ?? "csv";
  return instance.post("/examData/download", {
    start_date,
    end_date,
    file_type: fact_type
  }, {
    responseType: "blob"
  });
}