import instance from "./axios"

export function getPermissions(){
    return instance.get("/permission/config");
}

export function reassign(role: string, permissions: string[]) {
    // properties of `role` and `permissions` are all the id of them
    return instance.post("/permission/reassign",{
        roleId: role,
        permissions
    });
}