import instance from "./axios";
import dayjs from "dayjs";

export function UserListApi (page:Number,limit:Number) {
    return instance.get(`/user/list`,{
        params:{
            page,
            limit
        }
    })
}

export function editApi (User: any){
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

export function deleteUserApi(account: string){
    return instance.post("/user/delete",{
        account:account
    });
}

export function resetPasswordApi(password: string){
    return instance.post("/user/reset/pwd",{
        newPassword: password
    })
}

interface ChangeInfo {
  username?: string | null;
  pwd?: string | null;
  email?: string | null;
  gender?: string | null;
  age?: number | null;
  birthday?: number | null;
  height?: string | null;
  weight?: string | null;
}
export function changeInfoApi(data: Partial<ChangeInfo>){
    const { username, gender, age, height, weight, birthday, ...rest} = data;
    return instance.post("/user/change",{
        account: localStorage.getItem("account"),
        name: username,
        age,
        gender,
        height,
        weight,
        birthday: birthday ? dayjs(birthday).format("YYYY-MM-DD") : null
    });
}

export function setEmailApi(email: string){
    return instance.post("/user/setEmail",{
        email
    })
}
