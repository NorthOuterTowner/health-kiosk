import express from 'express'
import crypto from 'crypto'
import { db, genid} from '../db/dbUtils.js'

class User {
    constructor({account,name,pwd}){
        this.account = account
        this.pwd = crypto.createHash('sha256').update(pwd.toString()).digest('hex');
    }

    async setInfo(account,name,age,gender,height,weight){
        const sql = "update `user` set `name` = ? , `age` = ?, `gender` = ?, `height` = ?, `weight` = ? where `account` = ?;"
        try{
            await db.async.run(sql,[name,age,gender,height,weight,account])
        }catch(e){
            console.log(e)
        }
    }

}

export default User;