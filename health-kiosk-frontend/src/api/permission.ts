import instance from "./axios"

export function getPermissions(){
    return instance.get("/permission/config");
}

export function reassign(role: string, permissions: number[]) {
    // properties of `role` and `permissions` are all the id of them
    return instance.post("/permission/reassign",{
        assignId: role,
        permissions
    });
}

export const getPermissionTree = () => {
  return instance.get("/permission/functionTree");
};

export const getPermissionConfig = () => {
  return instance.get("/permission/config");
};

export function getPermissionsConfig() {
  return instance.get("/permission/config");
}