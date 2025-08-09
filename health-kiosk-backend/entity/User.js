const express = require('express');
const crypto = require('crypto');

class User {
    constructor({id,name,pwd}){
        this.id = id
        this.name = name
        this.pwd = sha256Hash(pwd)
    }
    
    sha256Hash(password) {
        return crypto.createHash('sha256').update(password).digest('hex');
    }
}

module.exports = User