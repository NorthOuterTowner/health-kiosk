import instance from "../axios";

export function searchUser (keyword: string) {
    return instance.get("/search/user",{
        params: {
            keyword
        }
    })
}

export function searchItem (keyword: string) {
    return instance.get("/search/item",{
        params: {
            keyword
        }
    })
}

export function searchDevice (keyword: string) {
    return instance.get("/search/device",{
        params: {
            keyword
        }
    })
}