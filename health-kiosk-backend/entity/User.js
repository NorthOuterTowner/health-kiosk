const express = require('express');
const crypto = require('crypto');
const {db,genid} = require('../db/dbUtils')

class User {
    constructor({account,name,pwd}){
        this.account = account
        this.pwd = sha256Hash(pwd)
    }

    async setInfo(account,name,age,gender,height,weight){
        const sql = "update `user` set `name` = ? , `age` = ?, `gender` = ?, `height` = ?, `weight` = ? where `account` = ?;"
        try{
            await db.async.run(sql,[name,age,gender,height,weight,account])
        }catch(e){
            console.log(e)
        }
    }
    sha256Hash(password) {
        return crypto.createHash('sha256').update(password).digest('hex');
    }

}

module.exports = User