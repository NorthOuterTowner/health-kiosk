var jwt = require('jsonwebtoken')

const JWT_SECRET = process.env.JWT_SECRET;

function generateToken(data){
    const token = jwt.sign({data},JWT_SECRET,{expiresIn: '60min'});
    return token;
}

function verifyToken(data){
    console.log(data)
    try{
        jwt.verify(data,JWT_SECRET)
    }catch(error){
        throw new Error(error.message||'Invalid token')
    }
}

function decodeToken(data){
    return jwt.verify(data,JWT_SECRET);
}

module.exports = {generateToken,verifyToken,decodeToken}