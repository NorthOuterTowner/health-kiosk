import instance from "./axios";

export function getRoles () {
    return instance.get("/role/list");
}

export function addRole (id: number, name: string, remark: string, use: number) {
    return instance.post("/role/add",{
        role_id: id,
        role_name: name,
        remark: remark,
        use: use
    });
}

export function deleteRole (name: string) {
    return instance.post("/role/delete",{
        name
    })
}

export function updateRole (name: string) {
    return instance.post("/role/update",{
        role_name:name
    })
}