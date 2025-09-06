import instance from "./axios";

export function UserListApi (page:Number,limit:Number) {
    return instance.get(`/user/list`,{
        params:{
            page,
            limit
        }
    })
}

export function editApi (User: any){
    console.log(User)
    return instance.post("/user/change",{
        ...User
    })
}

export function authApi(authUser:String,roleLevel:String){
    return instance.post("/user/authorization",{
        authUser,
        roleLevel
    })
}

export function userChartApi(){
    return instance.get("/statistics/userRegister");
}

export function addUserApi(User: any){
    return instance.post("/user/add",{
        ...User
    });
}

export function deleteUserApi(account: String){
    return instance.post("/user/delete",{
        account:account
    });
}
